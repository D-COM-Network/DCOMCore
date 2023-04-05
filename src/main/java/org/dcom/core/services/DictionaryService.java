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
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

/**
* The interface defining how the rest of the DCOM codebase interacts with a dictionary service.
*/
public interface DictionaryService {
  
  public Set<String> getObjects();
  public Set<DictionaryItem> getProperties(String object);
  
  default boolean containsObject(String oTest) {
    for (String o: getObjects()) {
      if (o.equalsIgnoreCase(oTest.trim())) return true;
      if (sanitiseName(o).equalsIgnoreCase(oTest.trim())) return true;
    }
    return false;
  }
  
  default Set<DictionaryItem> getProperties(Set<String> objects) {
    Set<DictionaryItem> items = new HashSet<DictionaryItem>();
    for (String o: objects) items.addAll(getProperties(o));
    return items;
  }

  default DictionaryItem getProperty(String object,String property) {
    Set<DictionaryItem> properties = getProperties(object);
    for (DictionaryItem dItem: properties) {
      if (dItem.getPropertyName().equalsIgnoreCase(property.trim())) return dItem;
      if (sanitiseName(dItem.getPropertyName()).equalsIgnoreCase(property.trim())) return dItem;
    }
    return null;
  }
  
  default DictionaryItem getProperty(Set<String> object,String property) {
    Set<DictionaryItem> properties = getProperties(object);
    for (DictionaryItem dItem: properties) {
      if (dItem.getPropertyName().equalsIgnoreCase(property.trim())) return dItem;
      if (sanitiseName(dItem.getPropertyName()).equalsIgnoreCase(property.trim())) return dItem;
    }
    return null;
  }
  
  default List<String> getObjectFromProperty(String property) {
    List<String> results = new ArrayList<String>();
    for (String o: getObjects()) {
      if (getProperty(o,property) != null ) results.add(o.toLowerCase());
    }
    return results;
  }
  
  
  public String getIfcType(String object);
  public String getIfcSubType(String object);
  public String getClassification(String object);
  
  
  private static String sanitiseName(String input) {
      String[] data = input.split(" ");
      if (data.length > 1 ) {
        StringBuilder builder = new StringBuilder();
        for (String d: data) {
          String cap = d.trim().substring(0, 1).toUpperCase() + d.substring(1);
          builder.append(cap);
        }
        input =  builder.toString();
      } 
      input = input.replaceAll("[^a-zA-Z0-9 -/_]", "");
      return input;
  }
  
}