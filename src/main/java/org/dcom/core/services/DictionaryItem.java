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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import com.owlike.genson.stream.ObjectWriter;

/**
* The an item from the DCOM dictionary
*/
public class DictionaryItem {
  
  DictionaryItem(String _propertySetName,String _propertyName, String _propertyDescription,String _dataType,String _application,List<String> _complianceDocumentReferences,String _ifcDataItem,List<String> _unit,List<String> _possibleValues) {
    propertyDescription=_propertyDescription;
    propertySetName=_propertySetName;
    propertyName=_propertyName;
    dataType=_dataType;
    application=_application;
    complianceDocumentReferences=new ArrayList<String>(_complianceDocumentReferences);
    ifcDataItem=_ifcDataItem;
    unit=_unit;
    possibleValues = _possibleValues;
  }
  
  DictionaryItem(HashMap<String,Object> data) {
    propertyDescription=(String) data.get("propertyDescription");
    propertySetName=(String) data.get("propertySetName");
    propertyName=(String) data.get("propertyName");
    dataType=(String) data.get("dataType");
    application=(String) data.get("application");
    complianceDocumentReferences=(ArrayList<String>)data.get("complianceDocumentReferences");
    ifcDataItem=(String) data.get("ifcDataItem");
    unit=(ArrayList<String>)data.get("unit");
    possibleValues = (ArrayList<String>)data.get("possibleValues");
  }
  
  public DictionaryItem(String _propertyName) {
    propertyName=_propertyName;
    complianceDocumentReferences=new ArrayList<String>();
    dataType="";
    unit=new ArrayList<String>();
    possibleValues = new ArrayList<String>();
  }
  
  public void addComplianceDocumentReference(String reference) {
      if (!complianceDocumentReferences.contains(reference)) complianceDocumentReferences.add(reference);
  }
  
  public void addComplianceDocumentReference(List<String> references) {
      for (String r: references) addComplianceDocumentReference(r);
  }
  
  public void addUnit(String _unit) {
    if (unit==null) unit = new ArrayList<String>();
    if (!unit.contains(_unit)) unit.add(_unit);
  }
  
  public boolean containsUnit(String _unit) {
    if (unit==null) unit = new ArrayList<String>();
    return unit.contains(_unit);
  }
  
  public boolean containsPossibleValue(String _possVal) {
    if (possibleValues==null) possibleValues = new ArrayList<String>();
    return possibleValues.contains(_possVal);
  }
  
  public boolean containsComplianceDocumentReference(String _reference) {
    if (complianceDocumentReferences==null) complianceDocumentReferences = new ArrayList<String>();
    return complianceDocumentReferences.contains(_reference);
  }
  
  public void addPossibleValue(String _possVal) {
    if (possibleValues==null) possibleValues = new ArrayList<String>();
    if (!possibleValues.contains(_possVal)) possibleValues.add(_possVal);
  }
  
  public void setDataType(String _dataType) {
    dataType=_dataType;
  }
  
  private String propertyDescription;
  private String propertySetName;
  private String propertyName;
  private String dataType;
  private String application;
  private List complianceDocumentReferences;
  private String ifcDataItem;
  private List unit;
  private List possibleValues;
  
  
  public String getPropertyDescription() {
    return propertyDescription;
  }
  
  public String getPropertySetName() {
    return propertySetName;
  }
  
  public String getPropertyName() {
    return propertyName;
  }
  
  public String getDataType() {
    return dataType;
  }
  
  public String getApplication() {
    return application;
  }
  
  public List<String> getComplianceDocumentReferences() {
    if (complianceDocumentReferences ==null ) return new ArrayList<String>();
    return complianceDocumentReferences;
  }
  
  public List<String> getUnit() {
    if (unit ==null ) return new ArrayList<String>();
    return unit;
  }

  public List<String> getPossibleValues() {
    if (possibleValues ==null ) return new ArrayList<String>();
    return possibleValues;
  }
  
  public String getIfcDataItem() {
    return ifcDataItem;
  }
  
  public void clearComplianceDocmentReferences() {
    complianceDocumentReferences.clear();
  }
  
  public void clearPossibleValues() {
    if (possibleValues!=null) possibleValues.clear();
  }
  
  public void setPropertySetName(String val) {
    propertySetName=val;
  }
  
  public void setIfcDataItem(String val) {
    ifcDataItem=val;
  }
  
  void writeToJSON(ObjectWriter writer) {
    writer.beginObject();
    writer.writeString("propertyDescription",propertyDescription);
    writer.writeString("propertyName",propertyName);
    writer.writeString("propertySetName",propertySetName);
    writer.writeString("dataType",dataType);
    writer.writeString("application",application);
    writer.writeString("ifcDataItem",ifcDataItem);
    writer.writeName("complianceDocumentReferences");
    writer.beginArray();
    for (Object s: complianceDocumentReferences) writer.writeString((String)s);
    writer.endArray();
    writer.writeName("possibleValues");
    writer.beginArray();
    for (Object s: possibleValues) writer.writeString((String)s);
    writer.endArray();
    writer.writeName("unit");
    writer.beginArray();
    for (Object s: unit) writer.writeString((String)s);
    writer.endArray();
    writer.endObject();
  }
}