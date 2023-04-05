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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
*The programmatic implementation and helper serialisation/desieralisation methods of the feedback provided by the rule engine on a given element of a compliance check (i.e. a clause)
*/
public class ComplianceCheckFeedbackItem {
	
	private String complianceDocumentReference;
	private String answer;
	private List<String> reasons;
	
	public ComplianceCheckFeedbackItem(String _complianceDocumentReference,String _answer,List<String> _reasons) {
		answer=_answer;
		complianceDocumentReference=_complianceDocumentReference;
		reasons=_reasons;
	}
	
	public ComplianceCheckFeedbackItem(String _complianceDocumentReference,String _answer) {
		answer=_answer;
		complianceDocumentReference=_complianceDocumentReference;
		reasons=new ArrayList<String>();
	}
	
	ComplianceCheckFeedbackItem(HashMap<String,Object>inputSet) {
		this(inputSet.get("complianceDocumentReference").toString(),inputSet.get("answer").toString());
		reasons=(ArrayList<String>)inputSet.get("reasons");
	}
	
	public String toJSON() {
		StringBuffer str=new StringBuffer();
		str.append("{");
		str.append("\"complianceDocumentReference\":\"").append(complianceDocumentReference).append("\",");
		str.append("\"answer\":\"").append(answer).append("\",");
		str.append("\"reasons\":[");
		boolean first=true;
			for (String reason:reasons) {
				if (first) first=false; else str.append(",");
				str.append("\"").append(reason).append("\"");
			}
		str.append("]");
		str.append("}");
		return str.toString();
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<FeedbackItem>");
		str.append("<ComplianceDocumentReference>").append(complianceDocumentReference).append("</ComplianceDocumentReference>");
		str.append("<Answer>").append(answer).append("</Answer>");
		str.append("<Reasons>");
		for (String reason:reasons) str.append("<Reason>").append(reason).append("</Reason>");
		str.append("</Reasons>");
		str.append("</FeedbackItem>");
		return str.toString();
	}
	
	public String getComplianceDocumentReference() {
		return complianceDocumentReference;
	}
	
	public String getAnswer() {
		return answer;
	}
	
}