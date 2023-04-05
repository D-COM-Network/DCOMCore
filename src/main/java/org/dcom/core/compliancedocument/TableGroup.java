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
*The programmatic representation and helper functionality for managing groups of rows in a Compliance Document.
*/
public class TableGroup extends ComplianceItem{


  private ArrayList<Row> rows;

  public TableGroup(ComplianceItem _parent) {
    super(_parent);
    rows=new ArrayList<Row>();
  }

  public Row getRow(int i) {
    return rows.get(i);
  }

  public int getNoRows() {
    return rows.size();
  }

  public void addRow(Row r) {
    rows.add(r);
    addSubItem(r);
  }

  public String toString() {
      StringBuffer str=new StringBuffer();
      str.append("(").append(this.getClass().getSimpleName()).append("){");
      str.append(metadataToString());
      str.append("norows:").append(rows.size());
      str.append("}");
      return str.toString();
  }
  
  public void mergeIn(TableGroup newItem) {
    super.mergeIn(newItem);
    rows=new ArrayList<Row>();
    for (int i=0; i < newItem.getNoRows();i++) {
      rows.add(newItem.getRow(i));
    }
  }

}
