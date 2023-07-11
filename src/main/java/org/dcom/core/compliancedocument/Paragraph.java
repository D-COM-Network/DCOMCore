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
package org.dcom.core.compliancedocument;

import java.util.ArrayList;
import java.util.List;
import org.dcom.core.compliancedocument.inline.InlineItem;
import org.dcom.core.compliancedocument.inline.RASEBox;

/**
*The programmatic representation and helper functionality for managing Paragraphs in a Compliance Document. A paragraph is made up of Paragraphs and Inserts.
*/
public class Paragraph extends ComplianceItem{

  private ArrayList<Paragraph> paragraphs;
  private ArrayList<Rule> rules;
  private ArrayList<Insert> inserts;
  private ArrayList<InlineItem> inlineItems;


  public Paragraph(ComplianceItem _parent) {
    super(_parent);
    paragraphs=new ArrayList<Paragraph>();
    rules=new ArrayList<Rule>();
    inserts=new ArrayList<Insert>();
    inlineItems=new ArrayList<InlineItem>();
  }
  
  public void removeSubItem(ComplianceItem s) {
    super.removeSubItem(s);
    paragraphs.remove(s);
    inserts.remove(s);
  }

  public int getNoInlineItems() {
    return inlineItems.size();
  }

  public InlineItem getInlineItem(int i) {
    return inlineItems.get(i);
  }

  public String getBodyText() {
    StringBuffer str = new StringBuffer();
    for (int i=0; i < inlineItems.size();i++) {
      str.append(inlineItems.get(i).generateText());
    }
    return str.toString();
  }

  public void setInlineItems(List<InlineItem> _items) {
    inlineItems.addAll(_items);
  }

  public ArrayList<Paragraph> getAllSubParagraphs() {
    ArrayList<Paragraph> paras=new ArrayList<Paragraph>();
    paras.add(this);
    for (int i=0; i < paragraphs.size();i++) {
      paras.addAll(paragraphs.get(i).getAllSubParagraphs());
    }
    return paras;
  }

  public Insert getInsert(int i) {
    return inserts.get(i);
  }

  public int getNoInserts() {
    return inserts.size();
  }

  public void addInsert(Insert s) {
    inserts.add(s);
    addSubItem(s);
  }

  public Paragraph getParagraph(int i) {
    if (i >=getNoParagraphs()) return null;
    return paragraphs.get(i);
  }

  public int getNoParagraphs() {
    return paragraphs.size();
  }

  public void addParagraph(Paragraph s) {
    paragraphs.add(s);
    addSubItem(s);
  }

  public Rule getRule(int i) {
    return rules.get(i);
  }

  public int getNoRules() {
    return rules.size();
  }

  public void addRule(Rule s) {
    rules.add(s);
  }
  
  public String toString() {
      StringBuffer str=new StringBuffer();
      str.append("(").append(this.getClass().getSimpleName()).append("){");
      str.append(metadataToString());
      str.append("noparas:").append(paragraphs.size()).append(",");
      str.append("}");
      return str.toString();
  }
  
  public void mergeIn(Paragraph newItem) {
    super.mergeIn(newItem);
    rules=new ArrayList<Rule>();
    for (int i=0; i < newItem.getNoRules();i++) {
      rules.add(newItem.getRule(i));
    }
    inserts=new ArrayList<Insert>();
    for (int i=0; i < newItem.getNoInserts();i++) {
      inserts.add(newItem.getInsert(i));
    }
    paragraphs=new ArrayList<Paragraph>();
    for (int i=0; i < newItem.getNoParagraphs();i++) {
      paragraphs.add(newItem.getParagraph(i));
    }
  }

}
