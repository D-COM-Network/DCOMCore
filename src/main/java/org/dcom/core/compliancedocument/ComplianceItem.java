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
import java.util.HashMap;
import java.util.Set;
import org.dcom.core.compliancedocument.utils.GuidHelper;

/**
*An abstract supertype for all elements within a compliance document.
*/
public class ComplianceItem {

  private HashMap<String,Object> metaData;
  private ArrayList<ComplianceItem> subItems;
  private Integer myNumber;
  private int highestParaNum;
  private ComplianceItem parent;

  public ComplianceItem(ComplianceItem _parent) {
        metaData=new HashMap<String,Object>();
        subItems=new ArrayList<ComplianceItem>();
        myNumber=null;
        highestParaNum=0;
        parent=_parent;
  }
  
  public ComplianceItem getParent() {
    return parent;
  }

  public boolean isListMetadata(String item ) {
      if (metaData.get(item) instanceof ArrayList){
        return true;
      }
      return false;
  }

  public void setMetaData(String item, String data) {
    data=data.trim().replace("\n","").replace("\r","").replace("\t","");
    while (data.contains("  ")) {
      data=data.replace("  "," ");
    }
    if (metaData.containsKey(item)) {
        Object o=metaData.get(item);
        if (o instanceof ArrayList) {
            ((ArrayList<String>)o).add(data);
        } else {
          String old=(String)o;
          if (old.equalsIgnoreCase(data)) return;
          metaData.put(item,new ArrayList<String>());
          ((ArrayList<String>)metaData.get(item)).add(old);
          ((ArrayList<String>)metaData.get(item)).add(data);
        }
    } else metaData.put(item,data);
  }

  public Set<String> getMetaDataList() {
    return metaData.keySet();
  }
  
  public boolean hasMetaData(String item) {
    return metaData.containsKey(item);
  }
  
  public void removeMetaData(String item) {
    metaData.remove(item);
  }

  public String getMetaDataString(String item) {
    if (!hasMetaData(item)) return null;
    return ""+metaData.get(item);
  }

  public ArrayList<String> getMetaDataList(String item) {
    return (ArrayList<String>) metaData.get(item);
  }

  public ComplianceItem getSubItem(int i) {
    return subItems.get(i);
  }

  public int getNoSubItems() {
    return subItems.size();
  }

  public void addSubItem(ComplianceItem s) {
    subItems.add(s);
  }
  
  public void addSubItem(int i,ComplianceItem s) {
    subItems.add(i,s);
  }
  
  public void removeSubItem(ComplianceItem s) {
    subItems.remove(s);
  }
  
  public String getAccessURL() {
    return getMetaDataString("ckterms:accessLocation");
  }
  
  public String getIdentifier() {
    return getMetaDataString("dcterms:identifier");
  }
  
  public boolean hasSubItem(ComplianceItem s) {
    return subItems.contains(s);
  }


  String metadataToString() {
    StringBuffer str=new StringBuffer();
    for (String s: metaData.keySet()) {
      str.append(s).append(":").append(metaData.get(s)).append(",");
    }
    return str.toString();
  }

  public String toString() {
      StringBuffer str=new StringBuffer();
      str.append("(").append(this.getClass().getSimpleName()).append("){");
      str.append(metadataToString());
      str.append("}");
      return str.toString();
  }
  
  public boolean hasNumber() {
    return myNumber!=null;
  }
  
  public void setNumber(Integer i){
    myNumber=i;
  }
  
  public Integer getNumber() {
    return myNumber;
  }
  
  public int getHighestParaNumber() {
    return highestParaNum;
  }
  
  public void setHighestParaNumber(int paraNum) {
    highestParaNum=paraNum;
  }
  
  public void generateNewGuid() {
    metaData.put("dcterms:identifier",GuidHelper.generateGuid());
  }
  
  public void mergeIn(ComplianceItem newItem) {
    metaData=new HashMap<String,Object>();
    subItems=new ArrayList<ComplianceItem>();
    myNumber=newItem.getNumber();
    highestParaNum=newItem.getHighestParaNumber();
    for (int i=0; i < newItem.getNoSubItems();i++) {
      subItems.add(newItem.getSubItem(i));
    }
    metaData=newItem.metaData;
  }
  
}
