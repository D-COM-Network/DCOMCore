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
* The programmatic implementation and helper serialisation/desieralisation methods of the  representation of a result an answer of a given question submitted to the rule engine
*/
public class ComplianceCheckAnswer {
	
	
	private String propertyId;
	private String id;
	private String answer;
	private String missValue;
	private String supportingFileData;
	private String supportingFileContentType;
	private String jobId;
	
	
	public ComplianceCheckAnswer(String _propertyId, String _id, String _answer, String _missValue,String _supportingFileData,String _supportingFileContentType,String _jobId){
		id=_id;
		propertyId=_propertyId;
		answer=_answer;
		missValue=_missValue;
		supportingFileData=_supportingFileData;
		supportingFileContentType=_supportingFileContentType;
		jobId=_jobId;
	}
	
	ComplianceCheckAnswer(Map<String,Object> inputSet) {
		this(inputSet.get("propertyId").toString(),inputSet.get("id").toString(),"",inputSet.get("missValue").toString(),"","","");
		if (inputSet.get("answer")!=null) answer=inputSet.get("answer").toString();
		if (inputSet.get("jobId")!=null) jobId=inputSet.get("jobId").toString();
		if (inputSet.get("supportingFileData")!=null) supportingFileData=inputSet.get("supportingFileData").toString();
		if (inputSet.get("supportingFileContentType")!=null) supportingFileContentType=inputSet.get("supportingFileContentType").toString();
	}
	
	public String toJSON() {
		StringBuffer str=new StringBuffer();
		str.append("{");
		str.append("\"id\":\"").append(id).append("\",");
		str.append("\"propertyId\":\"").append(propertyId).append("\",");
		str.append("\"answer\":\"").append(answer).append("\",");
		str.append("\"missValue\":\"").append(missValue).append("\",");
		str.append("\"supportingFileData\":\"").append(supportingFileData).append("\",");
		str.append("\"supportingFileContentType\":\"").append(supportingFileContentType).append("\",");
		str.append("\"jobId\":\"").append(jobId).append("\"");
		str.append("}");
		return str.toString();
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<ComplianceCheckAnswer>");
		str.append("<Id>").append(id).append("</Id>");
		str.append("<PropertyId>").append(propertyId).append("</PropertyId>");
		str.append("<Answer>").append(answer).append("</Answer>");
		str.append("<MissValue>").append(missValue).append("</MissValue>");
		str.append("<SupportingFileData>").append(supportingFileData).append("</SupportingFileData>");
		str.append("<SupportingFileContentType>").append(supportingFileContentType).append("</SupportingFileContentType>");
		str.append("<JobId>").append(jobId).append("</JobId>");
		str.append("</ComplianceCheckAnswer>");
		return str.toString();
	}
	
	public String getPropertyId() {
		return propertyId;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isJob() {
		return !jobId.equals("");
	}
	public String getMissValue() {
		return missValue;
	}
	
	public String getSupportingFileData() {
		return supportingFileData;
	}
	
	public String getSupportingFileContentType() {
		return supportingFileContentType;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public String getJobId() {
		return jobId;
	}
	
	public static List<ComplianceCheckAnswer> fromJSONCollection(String json) {
		Map<String,Object> data=SerDerHelper.parseJSON(json);
		List<ComplianceCheckAnswer> items=new ArrayList<ComplianceCheckAnswer>();
		List<Object> dataItems=(List<Object>)data.get("answers");
		for (Object o: dataItems) items.add(new ComplianceCheckAnswer((Map<String,Object>)o));
		return items;
	}
	
	public static List<ComplianceCheckAnswer> fromXMLCollection(String json) {
		Map<String,Object> data=SerDerHelper.parseXML(json);
		List<ComplianceCheckAnswer> items=new ArrayList<ComplianceCheckAnswer>();
		List<Object> dataItems=(List<Object>)data.get("answers");
		for (Object o: dataItems) items.add(new ComplianceCheckAnswer((Map<String,Object>)o));
		return items;
	}
	
}