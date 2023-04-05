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

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
*The programmatic implementation and helper serialisation/desieralisation methods of the  representation of a result of compliance check
*/
public class ComplianceCheckDataItem {
	
	private String propertyId;
	private String id;
	private String data;
	private String jobId;

	public ComplianceCheckDataItem(String _propertyId,String _id,String _data,String _jobId) {
			propertyId=_propertyId;
			id=_id;
			data=_data;
			jobId=_jobId;
	}
	
	ComplianceCheckDataItem(Map<String,Object> inputSet) {
		data="";
		if (inputSet.get("data")!=null)data=inputSet.get("data").toString();
		jobId="";
		if (inputSet.get("jobId")!=null) jobId=inputSet.get("jobId").toString();
		propertyId=inputSet.get("propertyId").toString();
		id=inputSet.get("id").toString();
		
	}
	
	public String getId() {
		return id;
	}
	
	public String getData() {
		return data;
	}
	
	public String getPropertyId() {
		return propertyId;
	}
	
	public boolean isJob() {
		return !jobId.equals("");
	}
	
	public String getJobId() {
		return jobId;
	}
	
	public String toJSON() {
		StringBuffer str=new StringBuffer();
		str.append("{");
		str.append("\"propertyId\":\"").append(propertyId).append("\",");
		str.append("\"id\":\"").append(id).append("\",");
		str.append("\"data\":\"").append(data).append("\",");
		str.append("\"jobId\":\"").append(jobId).append("\"");
		str.append("}");
		return str.toString();
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<ComplianceCheckDataItem>");
		str.append("<PropertyId>").append(propertyId).append("</PropertyId>");
		str.append("<Id>").append(id).append("</Id>");
		str.append("<Data>").append(data).append("</Data>");
		str.append("<JobId>").append(data).append("</JobId>");
		str.append("</ComplianceCheckDataItem>");
		return str.toString();
	}
	
	public static List<ComplianceCheckDataItem> fromJSONCollection(String json) {
		Map<String,Object> data=SerDerHelper.parseJSON(json);
		List<ComplianceCheckDataItem> items=new ArrayList<ComplianceCheckDataItem>();
		List<Object> dataItems=(List<Object>)data.get("dataItems");
		for (Object o: dataItems) items.add(new ComplianceCheckDataItem((Map<String,Object>)o));
		return items;
	}
	
	public static List<ComplianceCheckDataItem> fromXMLCollection(String json) {
		Map<String,Object> data=SerDerHelper.parseXML(json);
		List<ComplianceCheckDataItem> items=new ArrayList<ComplianceCheckDataItem>();
		List<Object> dataItems=(List<Object>)data.get("dataItems");
		for (Object o: dataItems) items.add(new ComplianceCheckDataItem((Map<String,Object>)o));
		return items;
	}
	
}