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
import org.dcom.core.security.ServiceCertificate;
import org.dcom.core.security.DCOMBearerToken;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;

/**
*The Rule Engine Service providing interfaces to invoke the various API methods provided by the rule engine.
*/
public class RuleEngineService extends DCOMService {

  
  public String startComplianceCheck(ComplianceCheckSettings settings) {
    String returnDataStr = getClient().putString(getURL(),"JSON",settings.toJSON());
    Map<String,Object> returnData=SerDerHelper.parseJSON(returnDataStr);
    return returnData.get("complianceId").toString();
  }
  
  public List<ComplianceCheckEntityInformation> getEntityInformation(String checkId,DCOMBearerToken token) {
    Map<String,Object> returnData=getClient().getJSON(getURL()+"/"+checkId,token);
    List<Object> listReturn=(List<Object>)returnData.get("idSet");
    List<ComplianceCheckEntityInformation> entityList=new ArrayList<ComplianceCheckEntityInformation>();
    for (Object o: listReturn) {
      entityList.add(new ComplianceCheckEntityInformation((Map<String,Object>)o));
    }
    return entityList;
  }
  
  public List<String> getConditions(String checkId,DCOMBearerToken token) {
    Map<String,Object> returnData=getClient().getJSON(getURL()+"/"+checkId,token);
    return (List<String>)returnData.get("conditions");
  }
  
  public String getApprovalStatus(String checkId,DCOMBearerToken token) {
    Map<String,Object> returnData=getClient().getJSON(getURL()+"/"+checkId,token);
    return (String)returnData.get("approval");
  }

  public void submitIDSet(String checkId,List<String> ids,DCOMBearerToken token) {
    HashMap<String,Object> data=new HashMap<String,Object>();
    data.put("ids",ids);
    getClient().postJSON(getURL()+"/"+checkId,data,token);
  }
  
  public void submitAnswers(String checkId,ComplianceCheckAnswer data,DCOMBearerToken token) {
    getClient().postString(getURL()+"/"+checkId+"/answers","JSON",data.toJSON(),token);
  }
  
  public void submitData(String checkId,ComplianceCheckDataItem data,DCOMBearerToken token) {
    getClient().postString(getURL()+"/"+checkId+"/data","JSON",data.toJSON(),token);
  }
  
  public List<ComplianceCheckResultItem> getResults(String checkId,LocalDateTime start, LocalDateTime end, String search, DCOMBearerToken token) {
    String qString="";
    boolean first=true;
    if (start!=null) {
      if (first) {
        qString+="?";
        first=false;
      }else qString+="&";
      qString+="start="+start.toString();
    } 
    if (end!=null) {
      if (first) {
        qString+="?";
        first=false;
      }else qString+="&";
      qString+="end="+end.toString();
    } 
    if (search!=null) {
      if (first) {
        qString+="?";
        first=false;
      }else qString+="&";
      qString+="search="+search;
    }
  
    HashMap<String,Object> data = getClient().getJSON(getURL()+"/"+checkId+"/results"+qString,token);
    ArrayList<Object> dataSet=(ArrayList<Object>)data.get("results");
    ArrayList<ComplianceCheckResultItem> items=new ArrayList<ComplianceCheckResultItem>();
    for (Object o: dataSet) items.add(new ComplianceCheckResultItem((HashMap<String,Object>)o));
    return items;
  }
  
  public void submitResult(String checkId,ComplianceCheckResultSubmission data,DCOMBearerToken token) {
    getClient().postString(getURL()+"/"+checkId+"/results","JSON",data.toJSON(),token);
  }
  
  public void submitApproval(String checkId,boolean approval,List<String> conditions,DCOMBearerToken token) {
    HashMap<String,Object> data=new HashMap<String,Object>();
    data.put("approval",""+approval);
    data.put("conditions",conditions);
    getClient().postJSON(getURL()+"/"+checkId+"/approval",data,token);
  }
  
  public ComplianceCheckFeedbackItem getFeedback(String checkId,String entityId,DCOMBearerToken token) {
    HashMap<String,Object> data=getClient().getJSON(getURL()+"/"+checkId+"/"+entityId,token);
    return new ComplianceCheckFeedbackItem(data);
  } 
  
}
