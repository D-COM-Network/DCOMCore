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

/**
*The programmatic representation and helper functionality for managing a Compliance Document.
*/
public class ComplianceDocument extends ComplianceItem {

  private ArrayList<Section> sections;

  public ComplianceDocument() {
    super(null);
    sections=new ArrayList<Section>();
  }

  public List<Section> getSections() { 
    return sections;
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

  public String toString() {
      StringBuffer str=new StringBuffer();
      str.append("(").append(this.getClass().getSimpleName()).append("){");
      str.append(metadataToString());
      str.append("nosections:").append(sections.size());
      str.append("}");
      return str.toString();
  }
  
  public String getVersion() {
    return getMetaDataString("ckterms:version");
  }
  
  public void setVersion(String v) {
    if (hasMetaData("ckterms:version")) removeMetaData("ckterms:version");
    setMetaData("ckterms:version",v);
    
  }
  
  public void mergeIn(ComplianceDocument newItem) {
    super.mergeIn(newItem);
    sections=new ArrayList<Section>();
    for (int i=0; i < newItem.getNoSections();i++) {
      sections.add(newItem.getSection(i));
    }
  }
}