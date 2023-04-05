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
package org.dcom.core.services;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import com.owlike.genson.Genson;
import java.io.File;
import java.io.ByteArrayOutputStream;
import com.owlike.genson.stream.ObjectWriter;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
*The templater specific implementation of the DictionaryService Interface.
*/
public class FileDictionaryService extends DCOMService implements DictionaryService {
  
  private HashMap<String,ArrayList<Object>> dataItems;
  private HashMap<String,HashMap<String,Object>> objectHeaders;
  
  
   public FileDictionaryService() {
    super();
    dataItems=new HashMap<String,ArrayList<Object>>();
    objectHeaders = new HashMap<String,HashMap<String,Object>>();
  }
  
  public FileDictionaryService(File file) throws java.io.IOException {
    this();
    String dictionaryContent = Files.readString(file.toPath(), StandardCharsets.US_ASCII);
    HashMap<String,Object> data =  new Genson().deserialize(dictionaryContent,HashMap.class);
    for (String k: data.keySet()) {
      if (k.equals("notAnObject")) {
          dataItems.put(k,(ArrayList<Object>)data.get(k));
      } else {
          ArrayList<Object> items=new ArrayList<Object>();
          HashSet<String> seenProperties2 = new HashSet<String>();
          for (Object o: (ArrayList<Object>)data.get(k)) {
            HashMap<String,Object> objectData = (HashMap<String,Object>)o;
            if (objectData.containsKey("IfcType")) {
              //it is an object header
              objectHeaders.put(k,objectData);
            } else {
              DictionaryItem dicItem = new DictionaryItem(objectData);
              if (seenProperties2.contains(dicItem.getPropertyName().toLowerCase().trim())) {
                System.out.println("[Error] Duplicated inside Object!:"+dicItem.getPropertyName());
                continue;
              } 
              seenProperties2.add(dicItem.getPropertyName().toLowerCase().trim());
              items.add(dicItem);
            }
            dataItems.put(k,items);
          }
      }
    }	
  }
  
  public void saveDictionary(File file) throws java.io.IOException {
    //we now need to sort alphabetically
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ObjectWriter writer = new Genson().createWriter(outputStream);
    writer.beginObject();
    List<String> objects = new ArrayList<String>(dataItems.keySet());
    Collections.sort(objects);
    for (String o: objects) {
      if (o.equals("notAnObject")) continue;
      writer.writeName(o).beginArray();
      if (objectHeaders.containsKey(o)) {
        writer.beginObject();
        writer.writeString("Uniclass",(String)objectHeaders.get(o).get("Uniclass"));
        writer.writeString("IfcSubType",(String)objectHeaders.get(o).get("IfcSubType"));
        writer.writeString("IfcType",(String)objectHeaders.get(o).get("IfcType"));
        writer.endObject();
      }
      ArrayList<DictionaryItem> objectData = (ArrayList)dataItems.get(o);
      Collections.sort(objectData,new ObjectComparator());
      for(DictionaryItem obj: objectData) obj.writeToJSON(writer);
      writer.endArray();
    }
    writer.endObject();
    writer.flush();
    Files.write( file.toPath(),outputStream.toByteArray());
  }
  
  public Set<String> getObjects(){
      Set<String> keys= new HashSet<String>(dataItems.keySet());
      keys.remove("notAnObject");
      return keys;
  }
  
  public void addObject(String name) {
    name=name.trim();
    if (!dataItems.containsKey(name)) dataItems.put(name,new ArrayList<Object>());
  }
  
  public void addProperty(String object,DictionaryItem item) {
    //check if already exists
    ArrayList<Object> items = dataItems.get(object);
    for (Object cItemOb: items) {
      if (!(cItemOb instanceof DictionaryItem)) continue;
      DictionaryItem cItem=(DictionaryItem)cItemOb;
      if (cItem.getPropertyName().equals(item.getPropertyName())) {
          //merge them
          List<String> docRefs = cItem.getComplianceDocumentReferences();
          cItem.addComplianceDocumentReference(item.getComplianceDocumentReferences());
          if (!cItem.getDataType().equals(item.getDataType())) cItem.setDataType("string");
          return;
      }
    }
    dataItems.get(object).add(item);
  }
  
  public Set<DictionaryItem> getProperties(String object){
      Set<DictionaryItem> returnData = new HashSet<DictionaryItem>();
      for (String dI : dataItems.keySet()) {
        if (dI.equalsIgnoreCase(object)) {
            for (Object obj: dataItems.get(dI)) returnData.add((DictionaryItem)obj);
        }
      }
      return returnData;
  }
  
  public synchronized void removeProperty(String object,String property){
      ArrayList<Object> properties = dataItems.get(object);
      for (Object o: properties) {
            if (((DictionaryItem)o).getPropertyName().equalsIgnoreCase(property)) {
              properties.remove(o);
              break;
            }
      }
}

  public boolean isNotObject(String name) {
      if (!dataItems.containsKey("notAnObject")) return false;
      return dataItems.get("notAnObject").contains(name);
  }
  
  public void addNotAnObject(String name){
    if (!dataItems.containsKey("notAnObject")) dataItems.put("notAnObject",new ArrayList<Object>());
    dataItems.get("notAnObject").add(name);
  }
  
  public void clearEmptyObjects() {
    ArrayList<String> toDelete = new ArrayList<String>();
    for (String obj: dataItems.keySet()) {
        if (obj.equals("notAnObject")) continue;
        if (dataItems.get(obj).size()==0) toDelete.add(obj);
    }
    for (String del: toDelete) dataItems.remove(del);
  }
  
  public String getIfcType(String object) {
    if (objectHeaders.containsKey(object)) {
      if (objectHeaders.get(object).get("IfcType")==null) return null;
      return objectHeaders.get(object).get("IfcType").toString();
    }
    return null;
  }
  
  public String getIfcSubType(String object) {
    if (objectHeaders.containsKey(object)) {
      if (objectHeaders.get(object).get("IfcSubType")==null) return null;
      return objectHeaders.get(object).get("IfcSubType").toString();
    }
    return null;
  }
  
  public String getClassification(String object) {
    if (objectHeaders.containsKey(object)) {
      if (objectHeaders.get(object).get("Uniclass")==null) return null;
      return objectHeaders.get(object).get("Uniclass").toString();
    }
    return null;
  }
  
  public void setIfcType(String object,String value) {
    if (objectHeaders.containsKey(object)) {
       objectHeaders.get(object).put("IfcType",value);
    } else {
      HashMap<String,Object> data = new HashMap<String,Object>();
      data.put("IfcType",value);
      objectHeaders.put(object,data);
    }
  }
  
  public void setIfcSubType(String object,String value) {
    if (objectHeaders.containsKey(object)) {
       objectHeaders.get(object).put("IfcSubType",value);
    } else {
      HashMap<String,Object> data = new HashMap<String,Object>();
      data.put("IfcType",value);
      objectHeaders.put(object,data);
    }
  }
  
  public void setClassification(String object,String value) {
    if (objectHeaders.containsKey(object)) {
       objectHeaders.get(object).put("Uniclass",value);
    } else {
      HashMap<String,Object> data = new HashMap<String,Object>();
      data.put("Uniclass",value);
      objectHeaders.put(object,data);
    }
  }

}

class ObjectComparator implements Comparator<DictionaryItem> {

    @Override
    public int compare(DictionaryItem firstObject, DictionaryItem secondObject) {
       return firstObject.getPropertyName().compareTo(secondObject.getPropertyName());
    }
}