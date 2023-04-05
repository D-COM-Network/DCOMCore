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
import java.time.LocalDateTime;
import org.w3c.dom.Element;
import com.owlike.genson.Genson;
import org.w3c.dom.NodeList;
import java.util.Map;

/**
*The programmatic implementation and helper serialisation/desieralisation methods of the index of results stored on the result service
*/
public class ComplianceCheckResultIndexItem {
	
	
	private HashMap<String,Object> dataSet;
	
	
	ComplianceCheckResultIndexItem(Map<String,Object> inputData) {
			inputData=dataSet;
	}
	
	ComplianceCheckResultIndexItem(Element input) {
		dataSet=new HashMap<String,Object>();
		readXMLItem("ComplianceDocument","complianceDocument",input);
		readXMLItem("Time","time",input);
		readXMLItem("ComplianceCheckUID","complianceCheckUID",input);
		readXMLItem("Result","result",input);
	}
	
	private void readXMLItem (String itemName,String varName,Element input){
		NodeList elements =  input.getElementsByTagName(itemName);
		Element item=(Element)elements.item(0);
		dataSet.put(varName,item.getTextContent());
	}
	
	
	public ComplianceCheckResultIndexItem(String complianceDocument, String uid, String result, LocalDateTime time) {
			dataSet=new HashMap<String,Object>();
			dataSet.put("complianceDocument",complianceDocument);
			dataSet.put("complianceCheckUID",uid);
			dataSet.put("result",result);
			dataSet.put("time",time.toString());
	}
	
	public String getComplianceDocuemnt() {
			return (String)dataSet.get("complianceDocument");
	}
	
	public String getComplianceCheckUID() {
		return (String)dataSet.get("complianceCheckUID");
	}
	
	public String getResult() {
		return (String)dataSet.get("result");
	}
	
	public LocalDateTime getTime() {
		if (dataSet.get("time") instanceof LocalDateTime) return (LocalDateTime)dataSet.get("time");
		return LocalDateTime.parse(dataSet.get("time").toString());
	}
	
	public String toJSON() {
			return new Genson().serialize(dataSet);
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<ComplianceCheckResultIndexItem>");
		str.append("<Time>").append(getTime().toString()).append("</Time>");
		str.append("<Result>").append(dataSet.get("result").toString()).append("</Result>");
		str.append("<ComplianceDocument>").append(dataSet.get("complianceDocument").toString()).append("</ComplianceDocument>");
		str.append("<ComplianceCheckUID>").append(dataSet.get("complianceCheckUID").toString()).append("</ComplianceCheckUID>");
		str.append("</ComplianceCheckResultIndexItem>");
		return str.toString();
	}
	
}