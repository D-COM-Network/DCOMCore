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

/**
*The programmatic representation and helper functionality for managing A Section in a Compliance Document. A section is made up of, sections and paragraphs.
*/
public class Section extends ComplianceItem {

  private ArrayList<Section> sections;
  private ArrayList<Paragraph> paragraphs;


  public Section(ComplianceItem _parent) {
    super(_parent);
    sections=new ArrayList<Section>();
    paragraphs=new ArrayList<Paragraph>();
  }


  public Section getSection(int i) {
    if (i >=getNoSections()) return null;
    return sections.get(i);
  }

  public int getNoSections() {
    return sections.size();
  }

  public void addSection(Section s) {
    sections.add(s);
    addSubItem(s);
  }
  
  
  public void removeSubItem(ComplianceItem s) {
    super.removeSubItem(s);
    sections.remove(s);
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

  public String toString() {
      StringBuffer str=new StringBuffer();
      str.append("(").append(this.getClass().getSimpleName()).append("){");
      str.append(metadataToString());
      str.append("nosections:").append(sections.size());
      str.append(",noparas:").append(paragraphs.size());
      str.append("}");
      return str.toString();
  }
  
  public void mergeIn(Section newItem) {
    super.mergeIn(newItem);
    sections=new ArrayList<Section>();
    for (int i=0; i < newItem.getNoSections();i++) {
      sections.add(newItem.getSection(i));
    }
    paragraphs=new ArrayList<Paragraph>();
    for (int i=0; i < newItem.getNoParagraphs();i++) {
      paragraphs.add(newItem.getParagraph(i));
    }
  }


}
