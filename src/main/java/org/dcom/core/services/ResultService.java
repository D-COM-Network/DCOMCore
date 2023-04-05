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

import org.dcom.core.security.DCOMBearerToken;
import org.dcom.core.security.ServiceCertificate;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDateTime;

/**
*The Result Service class, contains method to enable retrieval and transmission of Results to the Result Service.
*/
public class ResultService extends DCOMService{
  
  public List<ComplianceCheckResultIndexItem> getBuildingChecks(String uprn,DCOMBearerToken token) {
    HashMap<String,Object> data = getClient().getJSON(getURL()+"/"+uprn,token);
    ArrayList<Object> dataSet=(ArrayList<Object>)data.get("results");
    ArrayList<ComplianceCheckResultIndexItem> items=new ArrayList<ComplianceCheckResultIndexItem>();
    for (Object o: dataSet) items.add(new ComplianceCheckResultIndexItem((HashMap<String,Object>)o));
    return items;
  }
  
  public void updateUPRN(String oldUPRN,String newUPRN,DCOMBearerToken token) {
    getClient().patchString(getURL()+"/"+oldUPRN,"JSON",newUPRN,token);
  }
  
  public List<ComplianceCheckResultItem> getAllChecks(String building,DCOMBearerToken token) {
    return getCheck(building,"all",null,null,null,null,token);
  }
  
  public List<ComplianceCheckResultItem> getAllChecks(String building,String checkUid,LocalDateTime start, LocalDateTime end, String search, String documentFilter, DCOMBearerToken token) {
    return getCheck(building,"all",start,end,search,documentFilter,token);
  }
  
  public List<ComplianceCheckResultItem> getCheck(String building,String checkUid,DCOMBearerToken token) {
    return getCheck(building,checkUid,null,null,null,null,token);
  }
  
  public List<ComplianceCheckResultItem> getCheck(String building,String checkUid,LocalDateTime start, LocalDateTime end, String search, String documentFilter, DCOMBearerToken token) {
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
    if (documentFilter!=null) {
      if (first) {
        qString+="?";
        first=false;
      }else qString+="&";
      qString+="documentFilrer="+documentFilter;
    }
    if (search!=null) {
      if (first) {
        qString+="?";
        first=false;
      }else qString+="&";
      qString+="search="+search;
    }
  
    HashMap<String,Object> data = getClient().getJSON(getURL()+"/"+building+"/"+checkUid+qString,token);
    ArrayList<Object> dataSet=(ArrayList<Object>)data.get("results");
    ArrayList<ComplianceCheckResultItem> items=new ArrayList<ComplianceCheckResultItem>();
    for (Object o: dataSet) items.add(new ComplianceCheckResultItem((HashMap<String,Object>)o));
    return items;
  }
  
  public void sendResults(String uprn, String uid,List<String> conditions,List<ComplianceCheckResultItem> results,DCOMBearerToken token) {
      HashMap<String,Object> data=new HashMap<String,Object>();
      data.put("conditions",conditions);
      ArrayList<Map<String,Object>> resultsList=new ArrayList<Map<String,Object>>();
      for (ComplianceCheckResultItem result:results) {
        resultsList.add(result.toMap());
      }
      data.put("results",resultsList);
      getClient().putJSON(getURL()+"/"+uprn+"/"+uid,data,token);
  }
  

}
