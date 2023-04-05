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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Element;
import com.owlike.genson.Genson;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.Map;
import com.owlike.genson.annotation.JsonProperty;

/**
*The programmatic implementation and helper serialisation/desieralisation methods of the submission of a result from the project team of building control to the rule engine (i.e. where not automation is possible).
*/
public class ComplianceCheckResultSubmission {
	
	private Map<String,Object> dataSet;
	
	public static ComplianceCheckResultSubmission fromJSON(String json) {
		return new ComplianceCheckResultSubmission(SerDerHelper.parseJSON(json));
	}
	
	public static ComplianceCheckResultSubmission fromXML(String json) {
		return new ComplianceCheckResultSubmission(SerDerHelper.parseXML(json));
	}
	public static List<ComplianceCheckResultSubmission> fromJSONCollection(String json) {
	 Map<String,Object> data=SerDerHelper.parseJSON(json);
	 List<ComplianceCheckResultSubmission> results=new ArrayList<ComplianceCheckResultSubmission>();
	 ArrayList<Object> resultsTemp=(ArrayList<Object>)data.get("dataItems");
	 for (Object o: resultsTemp) results.add(new ComplianceCheckResultSubmission((HashMap<String,Object>)o));
	 return results;
	}
	
	public static List<ComplianceCheckResultSubmission> fromXMLCollection(String xml) {
		 Map<String,Object> data=SerDerHelper.parseXML(xml);
		 List<ComplianceCheckResultSubmission> results=new ArrayList<ComplianceCheckResultSubmission>();
		 ArrayList<Object> resultsTemp=(ArrayList<Object>)data.get("dataItems");
		 for (Object o: resultsTemp) results.add(new ComplianceCheckResultSubmission((HashMap<String,Object>)o));
		 return results;	 
	}
	
	ComplianceCheckResultSubmission(	Map<String,Object> inputData) {
			dataSet=inputData;
	}
	
	ComplianceCheckResultSubmission(Element input) {
			readXMLItem("ComplianceDocumentReference","complianceDocumentReference",input);
	
			readXMLItem("Result","result",input);

			readXMLItem("SupportingFileData","supportingFileData",input);
			readXMLItem("SupportingFileContentType","supportingFileContentType",input);
			NodeList reasonsList =  input.getElementsByTagName("Reasons");
			Element reasonsElement=(Element)reasonsList.item(0);
			NodeList reasonList =  reasonsElement.getElementsByTagName("Reason");
			List<String> reasons=new ArrayList<String>();
			for (int i=0; i < reasonList.getLength();i++) {
				reasons.add( ((Element)reasonList.item(i)).getTextContent());
			}
	}
	
	private void readXMLItem (String itemName,String varName,Element input){
		NodeList elements =  input.getElementsByTagName(itemName);
		Element item=(Element)elements.item(0);
		dataSet.put(varName,item.getTextContent());
	}
	
	public ComplianceCheckResultSubmission(@JsonProperty("complianceDocumentReference") String docRef,@JsonProperty("operationMode")  LocalDateTime time, @JsonProperty("reasons")  List<String> reasons, @JsonProperty("result")  String result, @JsonProperty("supportingFileData")  String supportingFileData,@JsonProperty("supportingFileContentType")  String supportingFileContentType) {
		dataSet=new HashMap<String,Object>();
		dataSet.put("complianceDocumentReference",docRef);
		dataSet.put("result",result);
		dataSet.put("reasons",reasons);
		dataSet.put("supportingFileData",supportingFileData);
		dataSet.put("supportingFileContentType",supportingFileContentType);
	}
	
	public String getComplianceDocumentReference() {
			return (String)dataSet.get("complianceDocumentReference");
	}
	
	
	public List<String> getReasons() {
		List reasons=(List)dataSet.get("reasons");
		return (List<String>) reasons;
	}

	public String getResult() {
		return (String)dataSet.get("result");
	}
	
	public String getSupportingFileData() {
			return (String)dataSet.get("supportingFileData");
	}
	
	public String getSupportingFileContentType() {
			return (String)dataSet.get("supportingFileContentType");
	}
	
	public String toJSON() {
		return new Genson().serialize(dataSet);
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<ComplianceCheckResultItem>");
		str.append("<Result>").append(dataSet.get("result").toString()).append("</Result>");
		str.append("<ComplianceDocumentReference>").append(dataSet.get("complianceDocumentReference").toString()).append("</ComplianceDocumentReference>");
		str.append("<SupportingFileData>").append(dataSet.get("supportingFileData").toString()).append("</SupportingFileData>");
		str.append("<SupportingFileContentType>").append(dataSet.get("supportingFileContentType").toString()).append("</SupportingFileContentType>");
		str.append("<Reasons>");
		for (String r: getReasons()) str.append("<Reason>").append(r).append("</Reason>");
		str.append("</Reasons>");
		str.append("</ComplianceCheckResultItem>");
		return str.toString();
	}
	
}