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
*The programmatic representation and helper functionality for managing table Rows in a Compliance Document. A row is made up of Cells.
*/
public class Row extends ComplianceItem{

  ArrayList<Cell> cells;

  public Row(ComplianceItem _parent) {
    super(_parent);
    cells=new ArrayList<Cell>();
  }

  public Cell getCell(int i) {
    return cells.get(i);
  }

  public int getNoCells() {
    return cells.size();
  }

  public void addCell(Cell c) {
    cells.add(c);
    addSubItem(c);
  }

   public List<Cell> getCells() { 
    return cells;
  }

  public String toString() {
      StringBuffer str=new StringBuffer();
      str.append("(").append(this.getClass().getSimpleName()).append("){");
      str.append(metadataToString());
      str.append("nocells:").append(cells.size());
      str.append("}");
      return str.toString();
  }
  
  public void mergeIn(Row newItem) {
    super.mergeIn(newItem);
    cells=new ArrayList<Cell>();
    for (int i=0; i < newItem.getNoCells();i++) {
      cells.add(newItem.getCell(i));
    }
  }

}
