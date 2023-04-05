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


import org.dcom.core.compliancedocument.*;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import java.util.HashMap;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*A helper class that takes an JSON string and produces an in memory compliance document.
*/
public class JSONComplianceDocumentDeserialiser {

  private static final Logger LOGGER = LoggerFactory.getLogger( XMLComplianceDocumentDeserialiser.class );

  private JSONComplianceDocumentDeserialiser() {
    
  }
  
  public static ComplianceDocument parseComplianceDocument(String docString) {
    try {
      Genson genson=new GensonBuilder().create();
      HashMap<String, Object> data = genson.deserialize(docString, HashMap.class);
      ComplianceDocument document = new ComplianceDocument();
      parseMetaData(document,data);
      if (data.containsKey("sections")) {
        ArrayList<Object> sections =(ArrayList<Object>)data.get("sections");
        for (int i=0; i < sections.size();i++) document.addSection(parseSection((HashMap<String,Object>)sections.get(i),document));
      }
      LOGGER.trace("Deserialising "+document);
      return document;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static Section parseSection(HashMap<String,Object> data, ComplianceItem _parent) {
    Section section=new Section(_parent);
    parseMetaData(section,data);

    if (data.containsKey("paragraphs")) {
      ArrayList<Object> paragraphs =  (ArrayList<Object>)data.get("paragraphs");
      for (int i=0; i < paragraphs.size();i++) section.addParagraph(parseParagraph((HashMap<String,Object>)paragraphs.get(i),section));
    }
    
    if (data.containsKey("sections")) {
      ArrayList<Object> sections = (ArrayList<Object>)data.get("sections");
      for (int i=0; i < sections.size();i++) section.addSection(parseSection((HashMap<String,Object>)sections.get(i),section));
    }

    
    LOGGER.trace("Deserialising "+section);
    return section;
  }

  private static Paragraph parseParagraph(HashMap<String,Object> e,ComplianceItem _parent) {
    Paragraph paragraph=new Paragraph(_parent);
    parseMetaData(paragraph,e);
    if (e.containsKey("paragraphs")) {
      ArrayList<Object> paragraphs =  (ArrayList<Object>)e.get("paragraphs");
      for (int i=0; i < paragraphs.size();i++) paragraph.addParagraph(parseParagraph((HashMap<String,Object>)paragraphs.get(i),paragraph));
    }
    if (e.containsKey("rules")) {
      ArrayList<Object> rules =  (ArrayList<Object>)e.get("rules");
      for (int i=0; i < rules.size();i++) paragraph.addRule(parseRule((HashMap<String,Object>)rules.get(i),paragraph));
    }

    if (e.containsKey("inserts")) {
      ArrayList<Object> inserts =  (ArrayList<Object>)e.get("inserts");
      for (int i=0; i < inserts.size();i++) paragraph.addInsert(parseInsert((HashMap<String,Object>)inserts.get(i),paragraph));
    }
    LOGGER.trace("Deserialising "+paragraph);
    return paragraph;
  }

  private static Insert parseInsert(HashMap<String,Object> e,ComplianceItem _parent){
      if (e.containsKey("imageData")) {
        //its an image
        Figure i=new Figure(_parent);
        parseMetaData(i,e);
        i.setImageData(e.get("imageData").toString());
        LOGGER.trace("Deserialising "+i);
        return i;
      } else {
        //its a table
        Table t=new Table(_parent);
        parseMetaData(t,e);
        if (e.containsKey("header")) t.setHeader(parseTableGroup((HashMap<String,Object>)e.get("header"),new TableHeader(t)));
        if (e.containsKey("footer")) t.setFooter(parseTableGroup((HashMap<String,Object>)e.get("footer"),new TableFooter(t)));
        if (e.containsKey("body")) t.setBody(parseTableGroup((HashMap<String,Object>)e.get("body"),new TableBody(t)));
        LOGGER.trace("Deserialising "+t);
        return t;
      }
  }


  private static <T extends TableGroup> T parseTableGroup(HashMap<String,Object> e,T tg) {

    parseMetaData(tg,e);
    if (e.containsKey("rows")) {
        ArrayList<Object> listRows=(ArrayList<Object>)e.get("rows");
        for (int i=0; i < listRows.size();i++) {
          HashMap<String,Object> rowData=(HashMap<String,Object>)(listRows.get(i));
          Row row=new Row(tg);
          parseMetaData(row,rowData);
          tg.addRow(row);
          ArrayList<Object> listCells=(ArrayList<Object>)rowData.get("cells");
          for (int x=0; x < listCells.size();x++) {
            HashMap<String,Object> cellData=(HashMap<String,Object>)(listCells.get(x));
            Cell c;
            if (cellData.get("cellType").equals("TitleCell")) {
              c=new TitleCell(row);
            } else {
              c=new DataCell(row);
            }
            parseMetaData(c,cellData);
            row.addCell(c);
          }

        }
    }
    LOGGER.trace("Deserialising "+tg);
    return tg;
  }

  private static Rule parseRule(HashMap<String,Object> e,ComplianceItem _parent) {
    Rule rule =new Rule(_parent);
    parseMetaData(rule,e);
    LOGGER.trace("Deserialising "+rule);
    return rule;
  }

  private static void parseMetaData(ComplianceItem item, HashMap<String,Object> data) {
        for (String mD:data.keySet()) {
            if (mD.equals("sections") || mD.equals("paragraphs") || mD.equals("inserts") || mD.equals("rules")) continue;
            if (mD.equals("imageData") || mD.equals("header") || mD.equals("footer") || mD.equals("rows") || mD.equals("cells")  || mD.equals("cellType") ) continue;
            if (data.get(mD) instanceof HashMap) continue;
            if (data.get(mD) instanceof ArrayList) {
              for (Object o : (ArrayList<Object>)data.get(mD)) {
                  item.setMetaData(mD,castMetaData(o));
              }
            } else {
              item.setMetaData(mD,castMetaData(data.get(mD)));
            }
        }
  }

  private static String castMetaData(Object o) {
      if (o instanceof Long) {
        return ""+o;
      }
      if (o instanceof Boolean) {
        return ""+o;
      }
      if (o instanceof Float) {
        return ""+o;
      }
      if (o instanceof Double) {
        return ""+o;
      }
      if (o instanceof Integer) {
        return ""+o;
      }
      return (String)o;
  }


}
