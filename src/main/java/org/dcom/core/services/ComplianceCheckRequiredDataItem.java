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

import java.util.HashMap;

/**
* The programmatic implementation and helper serialisation/desieralisation methods of an item in a list of  data items required by the rule engine.
*/
public class ComplianceCheckRequiredDataItem {
	
	private String propertyName;
	private String unit;
	private String id;
	private String docReference;
	
	public ComplianceCheckRequiredDataItem() {
		
	}
	
	public ComplianceCheckRequiredDataItem(String _id,String _propertyName,String _unit,String _docReference) {
			propertyName=_propertyName;
			unit=_unit;
			id=_id;
			docReference=_docReference;
	}
	
	ComplianceCheckRequiredDataItem(HashMap<String,Object> inputSet) {
		this(inputSet.get("id").toString(),inputSet.get("propertyName").toString(),inputSet.get("unit").toString(),inputSet.get("complianceDocumentReference").toString());
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public String getId() {
		return id;
	}
	
	public String getDocumentReference() {
		return docReference;
	}
	
	
	public String toJSON() {
		StringBuffer str=new StringBuffer();
		str.append("{");
		str.append("\"id\":\"").append(id).append("\",");
		str.append("\"unit\":\"").append(unit).append("\",");
		str.append("\"propertyName\":\"").append(propertyName).append("\",");
		str.append("\"complianceDocumentReference\":\"").append(docReference).append("\"");
		str.append("}");
		return str.toString();
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<RequiredDataItem>");
		str.append("<Id>").append(id).append("</Id>");
		str.append("<Unit>").append(unit).append("</Unit>");
		str.append("<PropertyName>").append(propertyName).append("</PropertyName>");
		str.append("<ComplianceDocumentReference>").append(propertyName).append("</ComplianceDocumentReference>");
		str.append("</RequiredDataItem>");
		return str.toString();
	}
}