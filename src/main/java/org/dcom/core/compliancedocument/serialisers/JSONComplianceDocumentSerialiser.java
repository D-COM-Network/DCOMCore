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
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.stream.ObjectWriter;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dcom.core.compliancedocument.utils.TextExtractor;

/**
*A helper class that takes an in memory compliance document and produces a JSON string.
*/
public class JSONComplianceDocumentSerialiser {

    private static ObjectWriter writer;
    private static final Logger LOGGER = LoggerFactory.getLogger( JSONComplianceDocumentSerialiser.class );

    private JSONComplianceDocumentSerialiser() {
      
    }

    public static String serialise(ComplianceDocument document) {
        LOGGER.trace("Serialising "+document);
        Genson genson=new GensonBuilder().create();
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        writer=genson.createWriter(outputStream);

        writer.beginObject();
        serialiseMetadata(document);
        writer.writeName("sections").beginArray();
        for (int i=0; i < document.getNoSections();i++) serialiseSection(document.getSection(i));
        writer.endArray();
        writer.endObject();
        writer.flush();
        return outputStream.toString();
    }


    private static void serialiseParagraph(Paragraph p) {
        LOGGER.trace("Serialising "+p);
        writer.beginObject();
        if (p.hasMetaData("inserted")) {
          p.setInlineItems(TextExtractor.extractStructure("<ins>"+p.getBodyText(false)+"</ins>"));
        }
        if (p.hasMetaData("deleted")) {
          p.setInlineItems(TextExtractor.extractStructure("<ins>"+p.getBodyText(false)+"</ins>"));
        }
        writer.writeString("body",p.getBodyText(false));
        serialiseMetadata(p);
        if (p.getNoParagraphs()> 0) {
          writer.writeName("paragraphs").beginArray();
          for (int i=0; i < p.getNoParagraphs();i++) {
            if (p.hasSubItem(p.getParagraph(i))) serialiseParagraph(p.getParagraph(i));
          }
          writer.endArray();
        }
        if (p.getNoRules()> 0) {
          writer.writeName("rules").beginArray();
          for (int i=0; i < p.getNoRules();i++) serialiseRule(p.getRule(i));
          writer.endArray();
        }
        if (p.getNoInserts()> 0) {
          writer.writeName("inserts").beginArray();
          for (int i=0; i < p.getNoInserts();i++) serialiseInsert(p.getInsert(i));
          writer.endArray();
        }

        writer.endObject();
    }

    private static void serialiseTableGroup(String name,TableGroup g) {
        LOGGER.trace("Serialising "+g);
        writer.writeName(name);
        writer.beginObject();
        serialiseMetadata(g);
        writer.writeName("rows");
        writer.beginArray();
        for (int i=0; i < g.getNoRows();i++) {
          Row r=g.getRow(i);
          writer.beginObject();
          serialiseMetadata(r);
          writer.writeName("cells");
          writer.beginArray();
          for (int z=0; z< r.getNoCells();z++) {
            writer.beginObject();
            serialiseMetadata(r.getCell(z));
            if (r.getCell(z) instanceof TitleCell) writer.writeString("cellType","TitleCell");
            else if (r.getCell(z) instanceof DataCell) writer.writeString("cellType","DataCell");
            writer.writeString("body",r.getCell(z).getBody().getBodyText(false));
            writer.endObject();
          }
          writer.endArray();
          writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }

    private static void serialiseInsert(Insert i) {
        LOGGER.trace("Serialising "+i);
        if (i instanceof Table) {
            writer.beginObject();
            serialiseMetadata(i);
            Table t=(Table)i;
            if (t.getHeader()!=null) serialiseTableGroup("header",t.getHeader());
            if (t.getBody()!=null) serialiseTableGroup("body",t.getBody());
            if (t.getFooter()!=null) serialiseTableGroup("footer",t.getFooter());
            writer.endObject();
        } else if (i instanceof Figure) {
          writer.beginObject();
          serialiseMetadata(i);
          String imageData=((Figure)i).getImageDataString();
          if (imageData!=null) writer.writeString("imageData",imageData);
          writer.endObject();
        }
    }

    private static void serialiseRule(Rule r) {
        LOGGER.trace("Serialising "+r);
        writer.beginObject();
        serialiseMetadata(r);
        writer.endObject();
    }

    private static void serialiseSection(Section s) {
        LOGGER.trace("Serialising "+s);
        if (s.getNoSubItems()==0) return;
        writer.beginObject();
        serialiseMetadata(s);
        if (s.getNoSections()> 0) {
          writer.writeName("sections").beginArray();
          for (int i=0; i < s.getNoSections();i++) {
            if (s.hasSubItem(s.getSection(i))) serialiseSection(s.getSection(i));
          }
          writer.endArray();
        }
        if (s.getNoParagraphs()> 0) {
          writer.writeName("paragraphs").beginArray();
          for (int i=0; i < s.getNoParagraphs();i++) {
            if (s.hasSubItem(s.getParagraph(i))) serialiseParagraph(s.getParagraph(i));
          }
          writer.endArray();
        }
        writer.endObject();
    }

    private static void serialiseMetadata(ComplianceItem i) {
        for(String mDName: i.getMetaDataList()) {
          if (i.isListMetadata(mDName)){
            writer.writeName(mDName).beginArray();
            ArrayList<String> data=i.getMetaDataList(mDName);
            for (String d:data) writer.writeString(d);
            writer.endArray();
          }else {
            writer.writeString(mDName,i.getMetaDataString(mDName));
          }
        }

    }

}
