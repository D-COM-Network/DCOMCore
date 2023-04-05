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
import org.dcom.core.compliancedocument.ComplianceDocument;
import org.dcom.core.compliancedocument.deserialisers.JSONComplianceDocumentDeserialiser;
import org.dcom.core.compliancedocument.serialisers.JSONComplianceDocumentSerialiser;
import java.util.HashMap;

/**
*The Compliance Document Service providing interfaces to invoke the various API methods provided by the rule engine.
*/
public class ComplianceDocumentService extends DCOMService {

  public ComplianceDocumentIndex getComplianceDocumentIndex() {
    return new ComplianceDocumentIndex(getClient().getJSON(getURL()+"/",null));
  }
  
  public ComplianceDocumentIndex getComplianceDocumentIndex(String jurisdiction,String type) {
    return new ComplianceDocumentIndex(getClient().getJSON(getURL()+"/"+jurisdiction+"/"+type,null));
  }
  
  public ComplianceDocumentIndex getComplianceDocumentIndex(String jurisdiction) {
    return new ComplianceDocumentIndex(getClient().getJSON(getURL()+"/"+jurisdiction,null));
  }
  
  public ComplianceDocument getComplianceDocument(String jurisdiction,String type,String shortName) {
    String content = getClient().getString(getURL()+"/"+jurisdiction+"/"+type+"/"+shortName,"JSON");
    if (content==null) return null;
    return JSONComplianceDocumentDeserialiser.parseComplianceDocument(content);
  }
  
  public ComplianceDocument getComplianceDocument(String jurisdiction,String type,String shortName,String version) {
    String content = getClient().getString(getURL()+"/"+jurisdiction+"/"+type+"/"+shortName+"/"+version,"JSON");
    if (content==null) return null;
    return JSONComplianceDocumentDeserialiser.parseComplianceDocument(content);
  }
  
  public ComplianceDocument getComplianceDocument(String jurisdiction,String type,String shortName,String version,String path) {
    String content = getClient().getString(getURL()+"/"+jurisdiction+"/"+type+"/"+shortName+"/"+version+"/"+path,"JSON");
    if (content==null) return null;
    return JSONComplianceDocumentDeserialiser.parseComplianceDocument(content);
  }
  
  public void uploadComplianceDocument(String jurisdiction,String type,String shortName,ComplianceDocument document,DCOMBearerToken token) {
      String content=JSONComplianceDocumentSerialiser.serialise(document);
      getClient().putString(getURL()+"/"+jurisdiction+"/"+type+"/"+shortName,"JSON",content,token);
  }
  
  public void uploadComplianceDocument(String jurisdiction,String type,String shortName,String version,ComplianceDocument document,DCOMBearerToken token) {
      String content=JSONComplianceDocumentSerialiser.serialise(document);
      getClient().putString(getURL()+"/"+jurisdiction+"/"+type+"/"+shortName+"/"+version,"JSON",content,token);
  }
  
  public void uploadComplianceDocument(String jurisdiction,String type,String shortName,String version,String path,ComplianceDocument document,DCOMBearerToken token) {
    String content=JSONComplianceDocumentSerialiser.serialise(document);
    System.out.println(getURL()+"/"+jurisdiction+"/"+type+"/"+shortName+"/"+version+"/"+path);
    System.out.println("------");
    System.out.println(content);
    getClient().putString(getURL()+"/"+jurisdiction+"/"+type+"/"+shortName+"/"+version+"/"+path,"JSON",content,token);
  }
  
  
}
