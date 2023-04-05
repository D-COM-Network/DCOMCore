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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
*The programmatic implementation and helper serialisation/desieralisation methods of a specific entity (i.e BIM object) within a compliance check.
*/
public class ComplianceCheckEntityInformation {
	
	private String id;
	private HashSet<String> type;
	protected String friendlyName;
	private List<String> keyVariables;
	protected List<ComplianceCheckRequiredDataItem> requiredData;
	
	protected ComplianceCheckEntityInformation(String _id) {
		id=_id;
		keyVariables=new ArrayList<String>();
		requiredData=new ArrayList<ComplianceCheckRequiredDataItem>();
		type=new HashSet<String>();
		friendlyName="TBC";
	}
	
	ComplianceCheckEntityInformation(Map<String,Object> inputData) {
			this(inputData.get("id").toString());
			type=(HashSet<String>) inputData.get("type");
			setFriendlyName(inputData.get("friendlyName").toString());
			keyVariables=(ArrayList<String>)inputData.get("keyVariables");
			ArrayList<Object> reqDat=(ArrayList<Object>)inputData.get("requiredData");
			for (Object o: reqDat){
				requiredData.add(new ComplianceCheckRequiredDataItem((HashMap<String,Object>)o));
			}
	}
	
	public void setType(String _type) {
		type.add(_type);
	}
	
	
	public String getTypeString() {
		StringJoiner joiner = new StringJoiner(",", "(", ")");
		for (String t: getType()) joiner.add(t);
		return joiner.toString();
	}
	
	public void setFriendlyName(String _name) {
		friendlyName=_name;
	}
	
	public int getNoRequireData() {
		return requiredData.size();
	}
	
	public int getNoKeyVariables() {
		return keyVariables.size();
	}
	
	public String getKeyVariable(int i) {
		return keyVariables.get(i);
	}
	
	public Set<String> getType() {
		return type;
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
	public ComplianceCheckRequiredDataItem getRequiredData(int i) {
		return requiredData.get(i);
	}

	public String getId() {
		return id;
	}
	
	public String toJSON() {
		StringBuffer str=new StringBuffer();
		str.append("{");
		str.append("\"id\":\"").append(id).append("\",");
		str.append("\"type\":[");
		boolean first=true;
		for (String t : type) {
			if (first) first=false;
			else str.append(",");
			str.append("\"").append(t).append("\"");
		}
		str.append("],");
		str.append("\"friendlyName\":\"").append(friendlyName).append("\",");
		str.append("\"requiredData\":[");
		first=true;
			for (ComplianceCheckRequiredDataItem rD:requiredData) {
				if (first) first=false; else str.append(",");
				str.append(rD.toJSON());
			}
		str.append("],");
		str.append("\"keyVariables\":[");
		first=true;
			for (String kV:keyVariables) {
				if (first) first=false; else str.append(",");
				str.append("\"").append(kV).append("\"");
			}
		str.append("]");
		str.append("}");
		return str.toString();
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<EntityInformation>");
		str.append("<Id>").append(id).append("</Id>");
		str.append("<Type>").append(type).append("</Type>");
		str.append("<FriendlyName>").append(friendlyName).append("</FriendlyName>");
		str.append("<RequiredData>");
		for (ComplianceCheckRequiredDataItem rD:requiredData) 	str.append(rD.toXML());
		str.append("</RequiredData>");
		str.append("<KeyVariables>");
		for (String kV:keyVariables) 	str.append("<KeyVariable>").append(kV).append("</KeyVariable>");
		str.append("</KeyVariables>");
		str.append("</EntityInformation>");
		return str.toString();
	}
	
	
}