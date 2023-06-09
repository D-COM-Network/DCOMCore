/*
Copyright [2022] [Cardiff University]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.dcom.core.compliancedocument.deserialisers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.StringReader;
import org.dcom.core.compliancedocument.*;
import org.xml.sax.InputSource;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.io.File;
import java.nio.file.Files;
import org.dcom.core.compliancedocument.utils.GuidHelper;
import java.util.HashMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import java.util.regex.Pattern;

/**
*A helper class that takes an XML string and produces an in memory compliance document.
*/
public class XMLComplianceDocumentDeserialiser {

    private static final Logger LOGGER = LoggerFactory.getLogger( XMLComplianceDocumentDeserialiser.class );

    private XMLComplianceDocumentDeserialiser() {
      
    }

  public static ComplianceDocument parseComplianceDocument(String docString) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      docString=docString.replace("<!DOCTYPE html>","");
      docString=docString.replace("<!doctype html>","");
      Document document = builder.parse(new InputSource(new StringReader(docString)));
      
      XPath xp = XPathFactory.newInstance().newXPath();
      NodeList nl = (NodeList) xp.evaluate("//text()[normalize-space(.)='']", document, XPathConstants.NODESET);

      for (int i=0; i < nl.getLength(); ++i) {
          Node node = nl.item(i);
          node.getParentNode().removeChild(node);
      }

      ComplianceDocument complianceDocument=new ComplianceDocument();

      Element root = (Element) document.getDocumentElement();
      Element head = (Element) root.getElementsByTagName("head").item(0);
      Element title = (Element)head.getElementsByTagName("title").item(0);
      if (title!=null) {
        complianceDocument.setMetaData("dcterms:title",title.getTextContent());
      } else {
        throw new Exception("No Title on Compliance Document");
      }
      NodeList metaLst=head.getElementsByTagName("meta");
      for (int i=0; i < metaLst.getLength();i++) {
        Element meta=(Element) metaLst.item(i);
        if (meta.getAttribute("name").equals("")) continue;
        complianceDocument.setMetaData(meta.getAttribute("name"),meta.getAttribute("content"));
      }
      Element body = (Element) root.getElementsByTagName("body").item(0);
      NodeList sections=body.getElementsByTagName("section");
      for (int i=0; i < sections.getLength();i++) {
          Element s=(Element)sections.item(i);
          if (s.getParentNode().isSameNode(body)) {
            complianceDocument.addSection(parseSection(s,complianceDocument));
          }
      }
      LOGGER.trace("Deserialising "+complianceDocument);
      return complianceDocument;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Section parseSection(Element e,ComplianceItem _parent) {
      Section s=new Section(_parent);

      getMetaData("numbered","data-numbered",e,s);
      getMetaData("numberedstyle","type",e,s);
      getMetaData("dcterms:title","title",e,s);
      getMetaData("dcterms:identifier","data-identifier",e,s);
      getMetaData("dcterms:relation","data-relation",e,s);
      getMetaData("dcterms:replaces","data-replaces",e,s);
      getMetaData("dcterms:isReplacedBy","data-isReplacedBy",e,s);
      getMetaData("ckterms:accessLocation","data-accessLocation",e,s);
      getMetaData("raseType","data-raseType",e,s);
      getMetaData("raseId","id",e,s);

      ArrayList<Insert> inserts=new ArrayList<Insert>();
      //keep a temp list of paragraphs to aid placement of figures
      ArrayList<Paragraph> paragraphs=new ArrayList<Paragraph>();

      NodeList children=e.getChildNodes();
      HashMap<Insert,Paragraph> lastParaForInsert=new HashMap<Insert,Paragraph>();
      Paragraph lastPara=null;
      for (int i=0; i < children.getLength();i++) {
            if (children.item(i).getNodeType()==Node.ELEMENT_NODE) {
                Element e1=(Element)children.item(i);
                if (e1.getTagName().equals("section") || e1.getTagName().equals("div")) {
                  Section newSec = parseSection(e1,s);
                  s.addSection(newSec);
                  if (newSec.getNoParagraphs() > 0) lastPara = newSec.getParagraph(newSec.getNoParagraphs()-1);
                } else if (e1.getTagName().equals("p") || e1.getTagName().equals("span")) {
                  Paragraph p=parseParagraph(e1,s);
                  paragraphs.addAll(p.getAllSubParagraphs());
                  s.addParagraph(p);
                  lastPara=p;
                } else if (e1.getTagName().equals("figure") || e1.getTagName().equals("table") ) {
                  Insert theInsert=parseInsert(e1,s);
                  inserts.add(theInsert);
                  if (lastPara!=null) lastParaForInsert.put(theInsert,lastPara); 
                  
                }
            }
      }
      //now allocate the inserts to a paragraphs
      int noFound=0;
      for (Insert i: inserts) {
        String id=i.getMetaDataString("identifier");
        boolean found=false;
        for (Paragraph p: paragraphs) {
          ArrayList<String> related=p.getMetaDataList("related");
          if (related!=null )  {
              for (String relatedItem:related) {
                if (relatedItem.equals(id)) {
                  noFound++;
                  found=true;
                  p.addInsert(i);
                  break;
                }
              }
            }
            if (found==true) break;
        }
        if (!found) {
          if (lastParaForInsert.containsKey(i)) {
              lastParaForInsert.get(i).addInsert(i);
              noFound++;
          }else {
            LOGGER.error("Error Inserting Insert)");
          }
        }
      }
      if (noFound < inserts.size()) {
        LOGGER.error("Dangling Inserts Found!");
      }
      

      LOGGER.trace("Deserialising "+s);
      return s;
  }

  private static Paragraph parseParagraph(Element e,ComplianceItem _parent) {
    Paragraph p=new Paragraph(_parent);

    getMetaData("numbered","data-numbered",e,p);
    getMetaData("dcterms:identifier","data-identifier",e,p);
    getMetaData("dcterms:relation","data-relation",e,p);
    getMetaData("dcterms:replaces","data-replaces",e,p);
    getMetaData("dcterms:isReplacedBy","data-isReplacedBy",e,p);
    getMetaData("ckterms:accessLocation","data-accessLocation",e,p);
    getMetaData("raseType","data-raseType",e,p);
    getMetaData("raseId","id",e,p);
    //if no identifier than allocate one
    if (!p.hasMetaData("dcterms:identifier")) p.setMetaData("dcterms:identifier",GuidHelper.generateGuid());
  

    p.setBodyText(innerXml(e));
  
    //loop through text nodes until we find an element
    Node n=e.getNextSibling();

    if (n!=null && n.getNodeType()==Node.ELEMENT_NODE) {
      Element e1=(Element)n;
      if (e1.getTagName().equalsIgnoreCase("ol") || e1.getTagName().equalsIgnoreCase("ul")) {
          NodeList para=e1.getChildNodes();
          for (int i=0; i < para.getLength();i++) {
              if (para.item(i) instanceof Element && ((Element)para.item(i)).getTagName().equalsIgnoreCase("li")) {
                Element innerPara=(Element)((Element)para.item(i)).getFirstChild();
                if (innerPara.getTagName().equals("div")) {
                  //sometimes we get a missplaced div here
                  String id=innerPara.getAttribute("id");
                  String raseType=innerPara.getAttribute("rase-type");
                  innerPara=(Element)innerPara.getFirstChild();
                  innerPara.setAttribute("rase-type",raseType);
                  innerPara.setAttribute("id",id);
                }
                p.addParagraph(parseParagraph(innerPara,p));
              }
          }
      }
    }
    
    int i=0;
    while (e.hasAttribute("data-el"+i+"ruleid")) {
      Rule r=parseRule(e,i,p);
      p.addRule(r);
      i++;
    }
    LOGGER.trace("Deserialising "+p);
    return p;
  }

  private static Insert parseInsert(Element e,ComplianceItem _parent) {

      if (e.getTagName().equals("figure")) {
          Figure f=new Figure(_parent);
          getMetaData("dcterms:identifier","data-identifier",e,f);
          NodeList nl=e.getElementsByTagName("figcaption");
          if (nl.getLength()>0) {
            String caption=innerXml(nl.item(0));
            f.setMetaData("caption",caption);
          }
          nl=e.getElementsByTagName("img");
          if (nl.getLength()>0) {
              Element imgE=(Element)nl.item(0);
              if (imgE.hasAttribute("src")) {
                String imgData=imgE.getAttribute("src");
                if (imgData.contains("base64")) {
                  imgData=imgData.substring(imgData.indexOf("base64,")+7).trim();
                  f.setImageData(imgData);
                } else {
                  try {
                    byte [] imageByteArray = Files.readAllBytes(new File(imgData).toPath());
                    imgData=Base64.getEncoder().encodeToString(imageByteArray);
                    f.setImageData(imgData);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
            
              }
              if (imgE.hasAttribute("alt")) {
                getMetaData("alt","alternativetext",imgE,f);
              }
          }
          LOGGER.trace("Deserialising "+f);
          return f;
      } else if (e.getTagName().equals("table")) {
          Table t= new Table(_parent);
          NodeList nl=e.getElementsByTagName("caption");
          String caption="";
          if (nl.getLength()>0) caption=innerXml(nl.item(0));
          t.setMetaData("caption",caption);
          getMetaData("dcterms:identifier","data-identifier",e,t);
          nl=e.getElementsByTagName("thead");
          if (nl.getLength()>0) {
            Element tg=(Element)nl.item(0);
             t.setHeader(parseTableGroup(tg,new TableHeader(t)));
          }
          nl=e.getElementsByTagName("tbody");
          if (nl.getLength()>0) {
            Element tg=(Element)nl.item(0);
            t.setBody(parseTableGroup(tg,new TableBody(t)));
          }
          nl=e.getElementsByTagName("tfoot");
          if (nl.getLength()>0) {
            Element tg=(Element)nl.item(0);
            t.setFooter(parseTableGroup(tg,new TableFooter(t)));
          }
          LOGGER.trace("Deserialising "+t);
          return t;
      }
      return null;
  }

  private static <T extends TableGroup> T parseTableGroup(Element e,T tg) {
      NodeList nl=e.getElementsByTagName("tr");
      for (int i=0; i < nl.getLength();i++) {
        if (nl.item(i).getNodeType()==Node.ELEMENT_NODE) {
          Element row=(Element)nl.item(i);
          Row r=new Row(tg);
          tg.addRow(r);
          NodeList nl2=row.getElementsByTagName("th");
          for (int x=0; x < nl2.getLength();x++) {
            if (nl2.item(x).getNodeType()==Node.ELEMENT_NODE) {
              Element cellE=(Element)nl2.item(x);
              TitleCell cell=new TitleCell(r);
              r.addCell(cell);
              getMetaData("colspan","colspan",cellE,cell);
              getMetaData("rowspan","rowspan",cellE,cell);
              cell.setMetaData("body",innerXml(cellE));
            }
          }
          nl2=row.getElementsByTagName("td");
          for (int x=0; x < nl2.getLength();x++) {
            if (nl2.item(x).getNodeType()==Node.ELEMENT_NODE) {
              Element cellE=(Element)nl2.item(x);
              DataCell cell=new DataCell(r);
              r.addCell(cell);
              getMetaData("colspan","colspan",cellE,cell);
              getMetaData("rowspan","rowspan",cellE,cell);
              cell.setMetaData("body",innerXml(cellE));
            }
          }

        }
      }
      LOGGER.trace("Deserialising "+tg);
      return tg;
  }


  private static Rule parseRule(Element e,int i,ComplianceItem _parent) {
    String d="data-el"+i;
    Rule r=new Rule(_parent);
    getMetaData("ruleid",d+"ruleid",e,r);
    getMetaData("command",d+"command",e,r);
    getMetaData("rulephrase",d+"rulephrase",e,r);
    getMetaData("trigger",d+"trigger",e,r);
    getMetaData("value",d+"value",e,r);
    getMetaData("lowerlimit",d+"lowerlimit",e,r);
    getMetaData("upperlimit",d+"upperlimit",e,r);
    getMetaData("unit",d+"unit",e,r);
    getMetaData("comparator",d+"comparator",e,r);
    LOGGER.trace("Deserialising "+r);
    return r;
  }

  private static void getMetaData(String title,String n,Element e,ComplianceItem p) {
    if (e.hasAttribute(n)) {
      String data;
      if (e.getAttribute(n).contains("||")) {
          String[] listData=e.getAttribute(n).toString().split(Pattern.quote("||"));
          for (int i=0; i < listData.length;i++) p.setMetaData(title,listData[i]);
      } else {
        data=e.getAttribute(n).toString();
        p.setMetaData(title,data);
      }
    }
  }

  private static String innerXml(Node node) {
    DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
    LSSerializer lsSerializer = lsImpl.createLSSerializer();
    lsSerializer.getDomConfig().setParameter("xml-declaration", false);
    NodeList childNodes = node.getChildNodes();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < childNodes.getLength(); i++) {
       Node innerNode = childNodes.item(i);
      if (innerNode!=null) {
        if (innerNode.hasChildNodes()) {
          sb.append(lsSerializer.writeToString(innerNode));
        } else {
          sb.append(innerNode.getNodeValue());
        }
      }
    }   
    return sb.toString().trim().replaceAll(" +", " ").replaceAll("[\\n\\t]", "").replace("&lt;","<").replace("&gt;",">");
  }

}
