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
*The programmatic representation and helper functionality for managing A Table in a Compliance Document. A subtype of Insert.
*/
public class Table extends Insert{

  private TableHeader header;
  private TableBody body;
  private TableFooter footer;

  public Table(ComplianceItem _parent) {
    super(_parent);
  }

  public TableBody getBody() {
      return body;
  }

  public TableHeader getHeader() {
      return header;
  }

  public TableFooter getFooter() {
      return footer;
  }

  public int getNoRows() {
    int noRows=getBody().getNoRows();
    if (getHeader()!=null) noRows+=getHeader().getNoRows(); 
    if (getFooter()!=null) noRows+=getFooter().getNoRows();
    return noRows;
  }
  
  public int getNoColumns() {
    return getBody().getRow(0).getNoCells();
  }
  
  public Row getRow(int row) {
    if (getHeader()!=null) {
      if (row < getHeader().getNoRows()) return getHeader().getRow(row);
      else row-=getHeader().getNoRows();
    }
    if (row < getBody().getNoRows()) return getBody().getRow(row);
    else row-=getBody().getNoRows();
    if (getFooter()!=null) {
      if (row < getFooter().getNoRows()) return getFooter().getRow(row);
      else row-=getFooter().getNoRows();
    }
    return null;
  }
  
  public Cell getCell(int row, int column) {
    Row r = getRow(row);
    if (r==null) return null;
    else return r.getCell(column);
  }

  public void setFooter(TableFooter _footer) {
      footer=_footer;
      addSubItem(_footer);
  }

  public void setBody(TableBody _body) {
      body=_body;
      addSubItem(_body);
  }

  public void setHeader(TableHeader _header) {
      header=_header;
      addSubItem(_header);
  }
  
  public void mergeIn(Table newItem) {
    super.mergeIn(newItem);
    header=newItem.getHeader();
    footer=newItem.getFooter();
    body=newItem.getBody();
  }
}
