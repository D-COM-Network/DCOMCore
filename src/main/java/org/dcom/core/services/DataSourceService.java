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
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
/**
* The interface defining how the rest of the DCOM Codebase interacts with a compliance data source.
*/
public class DataSourceService extends DCOMService {
  
  private ServiceCertificate myCert;
  private String guid;
  private String name;
  
  private static final Logger LOGGER = LoggerFactory.getLogger( DataSourceService.class );
  private ExecutorService executor = Executors.newFixedThreadPool(4);
  
  DataSourceService() {
    
  }
  
  public DataSourceService(String url,ServiceCertificate cert,String _guid) {
    super();
    setURL(url);
    myCert = cert;
    guid= _guid;
  }
  
  public void setDefaultCertificate(ServiceCertificate cert) {
    myCert = cert;
  }
  
  public String getServiceName(){
      return getClient().getJSON(getURL()).get("name").toString();
  }
  
  public  String getServiceDescription(){
      return getClient().getJSON(getURL()).get("description").toString();
  }
  public  String getServiceOperator(){
    return getClient().getJSON(getURL()).get("operator").toString();
  }
  
  public String getProjectName(){
    return getProjectName(guid,myCert.generateBearerToken());
  }
  
  public Future<List<String>> getIDSet(){
    return getIDSet(guid,myCert.generateBearerToken());
  }
  
  public void submitFeedback(ComplianceCheckFeedbackItem feedback){
    submitFeedback(guid,feedback,myCert.generateBearerToken());
  }
  
  public Future<List<ComplianceCheckAnswer>> getAnswer(String id, String propertyName, String comparator, String desiredAnswer, String unit, String complianceDocumentReference){
    return getAnswer(guid,id,propertyName,comparator,desiredAnswer,unit,complianceDocumentReference,myCert.generateBearerToken());
  }
  
  public Future<List<String>> getData(String id, String propertyName, String unit, String complianceDocumentReference){
    return getData(guid,id,propertyName,unit,complianceDocumentReference,myCert.generateBearerToken());
  }

  public Future<List<ComplianceCheckAnswer>> getAnswerFromJobId(String id,String jobId){
    return getAnswerFromJobId(guid,id,jobId,myCert.generateBearerToken());
  }
  
  public Future<List<String>> getDataFromJobId(String jobId){
    return getDataFromJobId(guid,jobId,myCert.generateBearerToken());
  }
  
  public String getProjectName(String compliancecCheckGuid,DCOMBearerToken token){
    return getClient().getJSON(getURL()+compliancecCheckGuid,token).get("projectName").toString();
  }
  
  public Future<List<String>> getIDSet(String compliancecCheckGuid,DCOMBearerToken token){
    return executor.submit(() -> {
      return ((ArrayList<String>)getClient().getJSON(getURL()+compliancecCheckGuid,token).get("ids"));
    });
  }
  
  public void submitFeedback(String compliancecCheckGuid,ComplianceCheckFeedbackItem feedback,DCOMBearerToken token){
    getClient().postString(getURL()+compliancecCheckGuid,"JSON",feedback.toJSON(),token);
  }
  
  public Future<List<ComplianceCheckAnswer>> getAnswer(String compliancecCheckGuid,String id, String propertyName, String comparator, String desiredAnswer, String unit, String complianceDocumentReference,DCOMBearerToken token){
    return executor.submit(() -> {
      ArrayList<Object> retData=(ArrayList<Object>)getClient().getJSON(getURL()+compliancecCheckGuid+"/answer?id="+id+"&propertyName="+URLEncoder.encode(propertyName, StandardCharsets.UTF_8)+"&comparator="+comparator+"&desiredAnswer="+ URLEncoder.encode(desiredAnswer, StandardCharsets.UTF_8)+"&unit="+unit+"&complianceDocumentReference="+complianceDocumentReference,token).get("answers");
      List<ComplianceCheckAnswer> answers=new ArrayList();
      for (int i=0; i < retData.size();i++) answers.add(new ComplianceCheckAnswer((HashMap<String,Object>)retData.get(i)));
      return answers; 
    });   
  }
  
  public Future<List<String>> getData(String compliancecCheckGuid,String id, String propertyName, String unit, String complianceDocumentReference,DCOMBearerToken token){
    return executor.submit(() -> {
       List<String> retData=new ArrayList<String>();
       ArrayList<Object> ret=((ArrayList<Object>)getClient().getJSON(getURL()+compliancecCheckGuid+"/data?id="+id+"&propertyName="+propertyName+"&unit="+unit+"&complianceDocumentReference="+complianceDocumentReference,token).get("data"));
       for (Object o:ret) {
         HashMap<String,Object> o1 = (HashMap<String,Object>)o;
         retData.add(o1.get("data").toString());
       }
        
        return retData;
      });
      
  }
  

  public Future<List<ComplianceCheckAnswer>> getAnswerFromJobId(String compliancecCheckGuid,String id,String jobId,DCOMBearerToken token){
      return executor.submit(() -> {
        ArrayList<Object> retData=(ArrayList<Object>)getClient().getJSON(getURL()+compliancecCheckGuid+"/retrieveAnswerJob/"+jobId+"?id="+id,token).get("answers");
        List<ComplianceCheckAnswer> answers=new ArrayList<ComplianceCheckAnswer>();
        for (int i=0; i < retData.size();i++) answers.add(new ComplianceCheckAnswer((HashMap<String,Object>)retData.get(i)));
        return answers;
      });
      
  }
  
  public Future<List<String>> getDataFromJobId(String compliancecCheckGuid,String jobId,DCOMBearerToken token){
    return executor.submit(() -> {
          return ((ArrayList<String>)getClient().getJSON(getURL()+compliancecCheckGuid+"/retrieveDataJob/"+jobId,token).get("data"));
      });
  }
}