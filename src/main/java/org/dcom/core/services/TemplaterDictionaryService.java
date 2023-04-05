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
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;

/**
*The templater specific implementation of the DictionaryService Interface.
*/
public class TemplaterDictionaryService extends DCOMService implements DictionaryService {
  
  private HashMap<String,String> objects;
  private HashMap<String,Set<DictionaryItem>> dataItems;
  private HashMap<String,HashMap<String,String>> objectHeaders;
  
   public TemplaterDictionaryService() {
    super();
    setURL("https://templater-dcom.transformingconstruction.co.uk");
    dataItems=new HashMap<String,Set<DictionaryItem>>();
    objectHeaders= new HashMap<String,HashMap<String,String>>();
  }
  
  private void loadObjects() {
    HashMap<String,Object> jsonQuery=new HashMap<String,Object>();
    jsonQuery.put("Offset",0);
    jsonQuery.put("Limit",1000);
    jsonQuery.put("Order","asc");
    jsonQuery.put("Sort",null);
    jsonQuery.put("Search","DCOM");
    HashMap<String,Object> returnData=getClient().postJSON(getURL()+"/api/TemplateApi/getTemplatesPaged",jsonQuery);
    ArrayList<Object> rows=(ArrayList<Object>)returnData.get("rows");
    objects=new HashMap<String,String>();
    for (Object o: rows) {
      HashMap<String,Object> data=(HashMap<String,Object>)o;
      objects.put(data.get("TemplateLabel").toString(),data.get("IdTemplate").toString());
      objectHeaders.put(data.get("TemplateLabel").toString(),new HashMap<String,String>());
      objectHeaders.get(data.get("TemplateLabel").toString()).put("IfcType",data.get("IfcType").toString());  
    }
  }
  
  private void loadObject(String object) {
    HashMap<String,Object> jsonQuery=new HashMap<String,Object>();
    jsonQuery.put("IdTemplate",objects.get(object));
    ArrayList<Object> returnData=getClient().postListJSON(getURL()+"/api/AttributeApi/GetTemplateAttributes",jsonQuery);
    HashSet<DictionaryItem> properties=new HashSet<DictionaryItem>();
    for (Object o: returnData) {
        HashMap<String,Object> data=(HashMap<String,Object>)o;
        String propertySetName=data.get("PropertySetName").toString();
        String dataType=data.get("DataType").toString();
        String propertyName=data.get("Attribute").toString();
        String propertyDescription=data.get("AttributeShortDesc").toString();
        String[] combinedField = data.get("AttributeDescription").toString().split("\\[");
        String ifcDataItem="";
        if (combinedField.length>3) ifcDataItem=combinedField[3].replaceAll("]","").replaceAll ("IFCDataItem:","").trim();
        else System.out.println("Missing IFCDataItem");
        String application=combinedField[1].replaceAll("]","").replaceAll("Application Model:","").trim();
        String[] complianceDocumentReferences=combinedField[2].replaceAll("]","").replaceAll("Related Compliance Resource:","").trim().split(",");
        DictionaryItem dItem=new DictionaryItem(propertySetName,propertyName,propertyDescription,dataType,application,Arrays.asList(complianceDocumentReferences),ifcDataItem, new ArrayList<String>(),new ArrayList<String>());
        properties.add(dItem);
        
    }
    dataItems.put(object,properties);
  }
  
  public Set<String> getObjects(){
      if (objects==null) loadObjects();
      return objects.keySet();
  }
  
  public Set<DictionaryItem> getProperties(String object){
      if (!objects.containsKey(object)) return new HashSet<DictionaryItem>();
      if (!dataItems.containsKey(object)) loadObject(object);
      return dataItems.get(object);
  }
  
  public String getIfcType(String object) {
    if (objectHeaders.containsKey(object)) {
      return objectHeaders.get(object).get("IfcType").toString();
    }
    return null;
  }
  
  public String getClassification(String object) {
    if (objectHeaders.containsKey(object)) {
      return objectHeaders.get(object).get("Uniclass").toString();
    }
    return null;
  }
  
  public String getIfcSubType(String object) {
    if (objectHeaders.containsKey(object)) {
      return objectHeaders.get(object).get("IfcSubType").toString();
    }
    return null;
  }

}