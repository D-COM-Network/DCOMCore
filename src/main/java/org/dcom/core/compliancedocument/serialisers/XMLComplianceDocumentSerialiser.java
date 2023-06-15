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

package org.dcom.core.compliancedocument.serialisers;

import org.dcom.core.compliancedocument.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import javax.xml.transform.OutputKeys;
import java.io.StringWriter;
import org.w3c.dom.Element;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.JavaUnicodeEscaper;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.NumericEntityEscaper;
import org.apache.commons.text.translate.UnicodeUnpairedSurrogateRemover;
import org.apache.commons.lang3.StringUtils;
import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
*A helper class that takes an in memory compliance document and produces a XML string.
*/
public class XMLComplianceDocumentSerialiser {

    private static Document xmlDocument;
    private static final Logger LOGGER = LoggerFactory.getLogger( XMLComplianceDocumentSerialiser.class );

    private XMLComplianceDocumentSerialiser() {
      
    }
    
    public static String serialise(ComplianceDocument document) {
        StringWriter writer = new StringWriter();
        writer.write("<!DOCTYPE html>");
        try {
            LOGGER.trace("Serialising "+document);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            xmlDocument = dBuilder.newDocument();

            Element rootElement = xmlDocument.createElement("html");
            xmlDocument.appendChild(rootElement);
            Element head = xmlDocument.createElement("head");
            rootElement.appendChild(head);
          //  Element item = xmlDocument.createElement("meta");
          //  head.appendChild(item);
          //  item.setAttribute("charset","UTF-8");
            Element item = xmlDocument.createElement("meta");
            head.appendChild(item);
            item.setAttribute("content","text/html; charset=UTF-8");
            item.setAttribute("http-equiv","Content-Type");
            
            item = xmlDocument.createElement("link");
            head.appendChild(item);
            item.setAttribute("type","text/css");
            item.setAttribute("rel","stylesheet");
            item.setAttribute("href","https://www.dcom.org.uk/dcom.css");
          
            item = xmlDocument.createElement("link");
            head.appendChild(item);
            item.setAttribute("type","text/css");
            item.setAttribute("rel","stylesheet");
            item.setAttribute("href","https://www.dcom.org.uk/rase.css");
            
            serialiseMetadata(document,head);
            
            Element body = xmlDocument.createElement("body");
            rootElement.appendChild(body);
            for (int i=0; i < document.getNoSections();i++) serialiseSection(document.getSection(i),body);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            DOMSource source = new DOMSource(xmlDocument);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
        } catch (Exception e) {
          LOGGER.error("[Error] Error Generating HTML:" +e);
        }
        
        return removeUTFCharacters(writer.toString().replaceAll("\0xFB02","fl").replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&amp;","&"));
    }

    

    private static ArrayList<Insert> serialiseParagraph(Paragraph p,Element body) {
      LOGGER.trace("Serialising "+p);
      ArrayList<Insert> inserts=new ArrayList<Insert>();
      Element para = xmlDocument.createElement("p");
      body.appendChild(para);
      if (p.hasMetaData("inserted")) {
        p.setBodyText("<ins>"+p.getBodyText()+"</ins>");
      }
      if (p.hasMetaData("deleted")) {
        p.setBodyText("<del>"+p.getBodyText()+"</del>");
      }
      para.setTextContent(removeUTFCharacters(p.getBodyText()));
      serialiseInlineMetadata(p,para);
      if (p.getNoRules()>0) {
          for (int i=0; i < p.getNoRules();i++) serialiseRule(p.getRule(i),para,i);
      }
      if (p.getNoParagraphs()> 0) {
          Element list;
          if (p.getParagraph(0).hasMetaData("numbered") && !p.getParagraph(0).getMetaDataString("numbered").equalsIgnoreCase("none")) {
            list = xmlDocument.createElement("ol");
          } else {
            list = xmlDocument.createElement("ul");
          }
          
          if (p.getParagraph(0).hasMetaData("numberedstyle")) {
            String style=p.getParagraph(0).getMetaDataString("numberedstyle");
            list.setAttribute("type",style);
          }
      
          body.appendChild(list);
          for (int i=0; i < p.getNoParagraphs();i++) {
            Element li = xmlDocument.createElement("li");
            list.appendChild(li);
            inserts.addAll(serialiseParagraph(p.getParagraph(i),li));
          }
      }
      for (int i=0; i < p.getNoInserts();i++) {
          inserts.add(p.getInsert(i));
      }
      return inserts;
    }

    private static void serialiseTableGroup(String name,TableGroup g,Element body) {
        LOGGER.trace("Serialising "+g);
        Element tableGroup = xmlDocument.createElement(name);
        body.appendChild(tableGroup);
        serialiseInlineMetadata(g,tableGroup);
        for (int i=0; i < g.getNoRows();i++) {
          Row r=g.getRow(i);
          Element row = xmlDocument.createElement("tr");
          tableGroup.appendChild(row);
          serialiseInlineMetadata(r,row);
          for (int z=0; z< r.getNoCells();z++) {
            Element cell=null;
            if (r.getCell(z) instanceof TitleCell) cell=xmlDocument.createElement("th");
            else if (r.getCell(z) instanceof DataCell) cell=xmlDocument.createElement("td");
            row.appendChild(cell);
            serialiseInlineMetadata(r.getCell(z),cell);
            cell.setTextContent(removeUTFCharacters(r.getCell(z).getBody().getBodyText()));
          }

        }
    }

    private static void serialiseInsert(Insert i,Element body) {
        LOGGER.trace("Serialising "+i);
        if (i instanceof Table) {
            Element table = xmlDocument.createElement("table");
            body.appendChild(table);
            if (i.getMetaDataString("caption")!=null) {
                Element caption = xmlDocument.createElement("caption");
                table.appendChild(caption);
                caption.setTextContent(removeUTFCharacters(i.getMetaDataString("caption")));
            }
            Table t=(Table)i;
            if (t.getHeader()!=null) serialiseTableGroup("thead",t.getHeader(),table);
            if (t.getBody()!=null) serialiseTableGroup("tbody",t.getBody(),table);
            if (t.getFooter()!=null) serialiseTableGroup("tfoot",t.getFooter(),table);

        } else if (i instanceof Figure) {
          Element figure = xmlDocument.createElement("figure");
          body.appendChild(figure);
          Figure f=(Figure)i;
          if (f.getMetaDataString("caption")!=null) {
              Element caption = xmlDocument.createElement("figcaption");
              figure.appendChild(caption);
              caption.setTextContent(removeUTFCharacters(f.getMetaDataString("caption")));
          }
          Element image = xmlDocument.createElement("img");
          image.setAttribute("style","max-width:100%");
          figure.appendChild(image);
          if (f.getMetaDataString("alternativetext")!=null){
              image.setAttribute("alt",f.getMetaDataString("alternativetext"));
          }
          String imgData=f.getImageDataString();
          if (imgData!=null) {
            String fl=""+imgData.charAt(0);
            String fileType="";
            if (fl.equals("/")) fileType="jpg";
            else if (fl.equals("i")) fileType="png";
            else if (fl.equals("R")) fileType="gif";
            else if (fl.equals("U")) fileType="webp";
            else {
              System.out.println("Unknown File Format:"+fl);
            }
            image.setAttribute("src","data:image/"+fileType+";base64, "+imgData);
          }
        }
    }

    private static void serialiseRule(Rule r, Element body, int number) {
          LOGGER.trace("Serialising "+r);
          for(String mDName: r.getMetaDataList()) {
              String item=r.getMetaDataString(mDName);
              body.setAttribute("data-el"+number+mDName,item);
          }
    }



    private static void serialiseSection(Section s,Element body) {
      LOGGER.trace("Serialising "+s);
      if (s.getNoSubItems()==0) return;
      Element section;
      if ( (!s.hasMetaData("numbered") || !s.getMetaDataString("numbered").equals("global")) && (!s.hasMetaData("dcterms:title")) ) {
        section = xmlDocument.createElement("div");
      } else {
        section = xmlDocument.createElement("section");
     }
      body.appendChild(section);
      serialiseInlineMetadata(s,section);
      if (s.getNoSubItems()> 0) {
          for (int i=0; i < s.getNoSubItems();i++) {
            ComplianceItem item=s.getSubItem(i);
            if (item instanceof Section) serialiseSection((Section)item,section);
            else {
              ArrayList<Insert> insertList=serialiseParagraph((Paragraph)item,section);
              for (int x=0; x < insertList.size();x++) serialiseInsert(insertList.get(x),section);
          }
        }
      }
    }


    private static void serialiseInlineMetadata(ComplianceItem i,Element item) {
      for(String mDName: i.getMetaDataList()) {
        if (i.isListMetadata(mDName)){
          ArrayList<String> data=i.getMetaDataList(mDName);
          StringBuffer str=new StringBuffer();
          for (String d:data) {
            if (str.length()!=0) str.append("||");
            str.append(d);
          }
          String newMDName=mapMetaDataName(mDName);
          if (newMDName!=null) item.setAttribute(newMDName,str.toString());
        }else {
          String newMDName=mapMetaDataName(mDName);
          if (newMDName!=null && !newMDName.equals("type")) item.setAttribute(newMDName,i.getMetaDataString(mDName));
        }
      }
    }

    private static String mapMetaDataName(String oName) {
      if (oName.equals("rules") || oName.equals("caption")) return null;
      if (oName.equals("imagedata") || oName.equals("alternativetext")) return null;
      if (oName.equals("colspan")) return "colspan";
      if (oName.equals("rowspan")) return "rowspan";
      if (oName.equals("deleted")) return null;
      if (oName.equals("inserted")) return null;
      if (oName.equals("id")) return "id";
      if (oName.equals("numbered")) return "data-numbered";
      if (oName.equals("numberedstyle")) return "type";
      if (oName.equals("dcterms:identifier")) return "data-identifier";
      if (oName.equals("dcterms:relation")) return "data-relation";
      if (oName.equals("dcterms:replaces")) return "data-replaces";
      if (oName.equals("dcterms:isReplacedBy")) return "data-isReplacedBy";
      if (oName.equals("dcterms:title")) return "title";
      if (oName.equals("ckterms:accessLocation")) return "data-accessLocation";
      if (oName.equals("raseType")) return "data-raseType";
      if (oName.equals("raseId")) return "id";
      System.out.println("Unrecognised:"+oName);
      return null;
    }
    
    private static String removeUTFCharacters(String data){
      if (data==null) return "";
      data=data.replaceAll("\u201C","\"").replaceAll("\u201D","\"").replaceAll("\u2013","-").replaceAll("\u2019","'").replaceAll("\u2018","'");
      data = Normalizer.normalize(data,Normalizer.Form.NFKD);
      data=ESCAPE_XML10.translate(data);
      String nonAscii = "[^\\p{ASCII}]+";
      return data.replaceAll(nonAscii, "");
        
    }

    private static void serialiseMetadata(ComplianceItem i,Element head) {
        for(String mDName: i.getMetaDataList()) {

          if (i.isListMetadata(mDName)){
            ArrayList<String> data=i.getMetaDataList(mDName);
            for (String d:data) {
              Element item = xmlDocument.createElement("meta");
              head.appendChild(item);
              item.setAttribute("name",mDName);
              item.setAttribute("content",d);
            }
          }else {
            if (!mDName.equals("dcterms:title")) {
                Element item = xmlDocument.createElement("meta");
                head.appendChild(item);
                item.setAttribute("name",mDName);
                item.setAttribute("content",i.getMetaDataString(mDName));
            } else {
              Element item = xmlDocument.createElement("title");
              head.appendChild(item);
              item.setTextContent(removeUTFCharacters(i.getMetaDataString(mDName)));
            }
          }
        }

    }

    private static final CharSequenceTranslator ESCAPE_XML10;
    static {
      final Map<CharSequence, CharSequence> escapeXml10Map = new HashMap<>();
      escapeXml10Map.put("\u0000", StringUtils.EMPTY);
      escapeXml10Map.put("\u0001", StringUtils.EMPTY);
      escapeXml10Map.put("\u0002", StringUtils.EMPTY);
      escapeXml10Map.put("\u0003", StringUtils.EMPTY);
      escapeXml10Map.put("\u0004", StringUtils.EMPTY);
      escapeXml10Map.put("\u0005", StringUtils.EMPTY);
      escapeXml10Map.put("\u0006", StringUtils.EMPTY);
      escapeXml10Map.put("\u0007", StringUtils.EMPTY);
      escapeXml10Map.put("\u0008", StringUtils.EMPTY);
      escapeXml10Map.put("\u000b", StringUtils.EMPTY);
      escapeXml10Map.put("\u000c", StringUtils.EMPTY);
      escapeXml10Map.put("\u000e", StringUtils.EMPTY);
      escapeXml10Map.put("\u000f", StringUtils.EMPTY);
      escapeXml10Map.put("\u0010", StringUtils.EMPTY);
      escapeXml10Map.put("\u0011", StringUtils.EMPTY);
      escapeXml10Map.put("\u0012", StringUtils.EMPTY);
      escapeXml10Map.put("\u0013", StringUtils.EMPTY);
      escapeXml10Map.put("\u0014", StringUtils.EMPTY);
      escapeXml10Map.put("\u0015", StringUtils.EMPTY);
      escapeXml10Map.put("\u0016", StringUtils.EMPTY);
      escapeXml10Map.put("\u0017", StringUtils.EMPTY);
      escapeXml10Map.put("\u0018", StringUtils.EMPTY);
      escapeXml10Map.put("\u0019", StringUtils.EMPTY);
      escapeXml10Map.put("\u001a", StringUtils.EMPTY);
      escapeXml10Map.put("\u001b", StringUtils.EMPTY);
      escapeXml10Map.put("\u001c", StringUtils.EMPTY);
      escapeXml10Map.put("\u001d", StringUtils.EMPTY);
      escapeXml10Map.put("\u001e", StringUtils.EMPTY);
      escapeXml10Map.put("\u001f", StringUtils.EMPTY);
      escapeXml10Map.put("\ufffe", StringUtils.EMPTY);
      escapeXml10Map.put("\uffff", StringUtils.EMPTY);
      ESCAPE_XML10 = new AggregateTranslator(new LookupTranslator(EntityArrays.APOS_ESCAPE),new LookupTranslator(Collections.unmodifiableMap(escapeXml10Map)),NumericEntityEscaper.between(0x7f, 0x84),NumericEntityEscaper.between(0x86, 0x9f), new UnicodeUnpairedSurrogateRemover());
    }
}
