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

/**
* The programmatic implementation and helper serialisation/desieralisation methods of the index of the result of a check on a given clause

*/
public class ComplianceCheckResultItem {
	
	private Map<String,Object> dataSet;
	
	public static ComplianceCheckResultItem fromJSON(String json) {
		return new ComplianceCheckResultItem(SerDerHelper.parseJSON(json));
	}
	
	public static ComplianceCheckResultItem fromXML(String json) {
		return new ComplianceCheckResultItem(SerDerHelper.parseXML(json));
	}
	public static List<ComplianceCheckResultItem> fromJSONCollection(String json) {
	 Map<String,Object> data=SerDerHelper.parseJSON(json);
	 List<ComplianceCheckResultItem> results=new ArrayList<ComplianceCheckResultItem>();
	 ArrayList<Object> resultsTemp=(ArrayList<Object>)data.get("results");
	 System.out.println(json);
	 for (Object o: resultsTemp) results.add(new ComplianceCheckResultItem((HashMap<String,Object>)o));
	 return results;
	}
	
	public static List<ComplianceCheckResultItem> fromXMLCollection(String xml) {
		 Map<String,Object> data=SerDerHelper.parseXML(xml);
		 List<ComplianceCheckResultItem> results=new ArrayList<ComplianceCheckResultItem>();
		 ArrayList<Object> resultsTemp=(ArrayList<Object>)data.get("results");
		 for (Object o: resultsTemp) results.add(new ComplianceCheckResultItem((HashMap<String,Object>)o));
		 return results;	 
	}
	
	ComplianceCheckResultItem(	Map<String,Object> inputData) {
			dataSet=inputData;
	}
	
	ComplianceCheckResultItem(Element input) {
			readXMLItem("Reference","reference",input);
			readXMLItem("Time","time",input);
			readXMLItem("Result","result",input);
			readXMLItem("Attributation","attributation",input);
			
			dataSet=new HashMap<String,Object>();
			
			NodeList supportFileDataList =  input.getElementsByTagName("SupportingFileDatas");
			Element supportFileDataElement=(Element)supportFileDataList.item(0);
			supportFileDataList =  supportFileDataElement.getElementsByTagName("SupportingFileData");
			List<String> supportingFileData=new ArrayList<String>();
			for (int i=0; i < supportFileDataList.getLength();i++) {
				supportingFileData.add( ((Element)supportFileDataList.item(i)).getTextContent());
			}
			dataSet.put("supportingFileData",supportingFileData);
			
			NodeList supportingFileContentTypeList =  input.getElementsByTagName("SupportingFileContentTypes");
			Element supportingFileContentTypeElement=(Element)supportingFileContentTypeList.item(0);
			supportingFileContentTypeList =  supportingFileContentTypeElement.getElementsByTagName("SupportingFileContentType");
			List<String> supportingFileContentType=new ArrayList<String>();
			for (int i=0; i < supportingFileContentTypeList.getLength();i++) {
				supportingFileContentType.add( ((Element)supportingFileContentTypeList.item(i)).getTextContent());
			}
			dataSet.put("supportingFileContentType",supportingFileContentType);
			
			
			NodeList reasonsList =  input.getElementsByTagName("Reasons");
			Element reasonsElement=(Element)reasonsList.item(0);
			NodeList reasonList =  reasonsElement.getElementsByTagName("Reason");
			List<String> reasons=new ArrayList<String>();
			for (int i=0; i < reasonList.getLength();i++) {
				reasons.add( ((Element)reasonList.item(i)).getTextContent());
			}
			dataSet.put("reasons",reasons);
	}
	
	private void readXMLItem (String itemName,String varName,Element input){
		NodeList elements =  input.getElementsByTagName(itemName);
		Element item=(Element)elements.item(0);
		dataSet.put(varName,item.getTextContent());
	}
	
	public ComplianceCheckResultItem(String docRef,LocalDateTime time, List<String> reasons, String attributation, String result, List<String> supportingFileData, List<String> supportingFileContentType) {
		dataSet=new HashMap<String,Object>();
		dataSet.put("reference",docRef);
		dataSet.put("time",time.toString());
		dataSet.put("result",result);
		dataSet.put("attributation",attributation);
		dataSet.put("reasons",reasons);
		dataSet.put("supportingFileData",supportingFileData);
		dataSet.put("supportingFileContentType",supportingFileContentType);
	}
	
	public String getReference() {
			return (String)dataSet.get("reference");
	}
	
	public LocalDateTime getTime() {
			return LocalDateTime.parse((String)dataSet.get("time"));
	}
	
	public List<String> getReasons() {
		List reasons=(List)dataSet.get("reasons");
		return (List<String>) reasons;
	}
	
	public String getAttributation() {
		return (String)dataSet.get("attributation");
	}
	
	public String getResult() {
		return (String)dataSet.get("result");
	}
	
	
	public List<String> getSupportingFileData() {
			return (List<String>)dataSet.get("supportingFileData");
	}
	
	public List<String> getSupportingFileContentType() {
			return (List<String>)dataSet.get("supportingFileContentType");
	}
	
	public String toJSON() {
		return new Genson().serialize(dataSet);
	}
	
	Map<String,Object> toMap() {
		return dataSet;
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<ComplianceCheckResultItem>");
		str.append("<Time>").append(getTime().toString()).append("</Time>");
		str.append("<Result>").append(dataSet.get("result").toString()).append("</Result>");
		str.append("<Reference>").append(dataSet.get("reference").toString()).append("</Reference>");
		str.append("<SupportingFileData>").append(dataSet.get("supportingFileData").toString()).append("</SupportingFileData>");
		str.append("<SupportingFileContentType>").append(dataSet.get("supportingFileContentType").toString()).append("</SupportingFileContentType>");
		str.append("<Attributation>").append(dataSet.get("attributation").toString()).append("</Attributation>");
		str.append("<Reasons>");
		for (String r: getReasons()) str.append("<Reason>").append(r).append("</Reason>");
		str.append("</Reasons>");
		str.append("</ComplianceCheckResultItem>");
		return str.toString();
	}
	
}