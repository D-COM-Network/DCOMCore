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

import java.util.Set;
import org.dcom.core.security.ServiceCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.dcom.core.security.DCOMBearerToken;

/**
*The ServiceLookup service provides methods to retrieve objects representing all other types of DCOM services as well as their certificates.
*/
public class ServiceLookup {

    public static String COMPLIANCEDOCUMENTSERVICE="ComplianceDocumentService";
    public static String RULEENGINESERVICE="RuleEngineService";
    public static String DICTIONARYSERVICE="DictionaryService";
    public static String RESULTSERVICE="ResultService";
    public static String DATASOURCE="DataSource";

    private static final Logger LOGGER = LoggerFactory.getLogger( ServiceLookup.class );

    private HttpClient client;
    private String baseURL;

    public ServiceLookup(String url) {
        client=new HttpClient();
        baseURL=url;
    }

    public void registerMyself(String appType,String name,String hostname,int port,DCOMBearerToken token) {
        String template=registerTemplate(appType,name,hostname,port);
        //System.out.println(template);
        //System.out.println(baseURL+"/eureka/apps/"+appType);
        String response = client.postString(baseURL+"/eureka/apps/"+appType,"JSON",template,token);
        LOGGER.info("Waiting for Service Lookup");
        while ( response == null ) {
          LOGGER.info(".");
          try {
            Thread.sleep(1000);
          } catch (Exception e) {
            e.printStackTrace();
          }
          response = client.postString(baseURL+"/eureka/apps/"+appType,"JSON",template,token);
        } 
        
        LOGGER.info("Registered myself with Service Lookup");
        ServiceLookupThread lookupCycle=new ServiceLookupThread(baseURL,appType,hostname,client,token);
        lookupCycle.start();
    }

    private String registerTemplate(String appType,String name, String hostname,int port) {
      return "{ \"instance\": { \"vipAddress\": \""+name+"\",\"hostName\": \""+hostname+"\",\"secureVipAddress\": null,\"app\": \""+appType+"\",\"homePageUrl\": null,\"ipAddr\": \""+hostname+"\",\"dataCenterInfo\": {\"@class\":\"com.netflix.appinfo.MyDataCenterInfo\",\"name\": \"MyOwn\"},\"healthCheckUrl\": null,\"port\": {\"$\": "+port+",\"@enabled\": \"true\" },\"statusPageUrl\": null}}";
    }

    public Set<ComplianceDocumentService> getComplianceDocumentServices() {
      return getApplications(ComplianceDocumentService.class,COMPLIANCEDOCUMENTSERVICE);
    }

    public Set<DictionaryService> getDictionaries() {
      //return getApplications(DictionaryService.class,DICTIONARYSERVICE);
      return new HashSet<DictionaryService>();
    }

    public Set<ResultService> getResultServices() {
      return getApplications(ResultService.class,RESULTSERVICE);
    }

    public Set<RuleEngineService> getRuleEngines() {
      return getApplications(RuleEngineService.class,RULEENGINESERVICE);
    }


    public Set<DataSourceService> getDataSources() {
      return getApplications(DataSourceService.class,DATASOURCE);
    }


    public ServiceCertificate getServiceCertificate(String serviceHostName) {
        String certificate=client.getString(baseURL+"/certificates/"+serviceHostName+".pub","STRING");
        return new ServiceCertificate(certificate);
    }

    private <T extends DCOMService> Set<T> getApplications(Class<T> cl,String appType) {
        HashSet<T> results=new HashSet<T>();
        String response = client.getString(baseURL+"/eureka/apps/"+appType,"XML");
        LOGGER.info("Waiting for Service Lookup");
        while ( response == null ) {
          LOGGER.info(".");
          try {
            Thread.sleep(1000);
          } catch (Exception e) {
            e.printStackTrace();
          }
          response = client.getString(baseURL+"/eureka/apps/"+appType,"XML");
        } 
        
        LOGGER.info("Located Service Lookup");
      
        Document xmlResponse=client.getXML(baseURL+"/eureka/apps/"+appType);
        NodeList nodes=xmlResponse.getElementsByTagName("instance");
        for (int i=0; i < nodes.getLength();i++) {
            Element e=(Element)nodes.item(i);
            NodeList nl1=e.getElementsByTagName("hostName");
            String hostName=nl1.item(0).getTextContent();
            NodeList nl2=e.getElementsByTagName("port");
            String port=nl2.item(0).getTextContent();
            NodeList nl3=e.getElementsByTagName("vipAddress");
            String name=nl3.item(0).getTextContent();
            try {
              T newItem=cl.newInstance();
              String url=null;
              if (port.equals("80")) url="http://"+hostName;
              else if (port.equals("443")) url="https://"+hostName;
              else if (hostName.contains(":")) url="http://"+hostName;
              else url="http://"+hostName+":"+port;
              newItem.setURL(url);
              newItem.setName(name);
              newItem.setCertificate(getServiceCertificate(hostName));
              results.add(newItem);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
        }
        return results;
    }
}


class ServiceLookupThread extends Thread {

  private HttpClient client;
  private String url;
  private static final Logger LOGGER = LoggerFactory.getLogger( ServiceLookup.class );
  private DCOMBearerToken token;

  public ServiceLookupThread(String baseURL,String appType,String appId,HttpClient _client,DCOMBearerToken _token) {
      url=baseURL+"/eureka/apps/"+appType+"/"+appId;
      client=_client;
      token=_token;
  }

  public void run () {
    LOGGER.info("Starting Renewal Thread");
    try {
      while (true) {
          Thread.sleep(60000);
          client.putString(url,"JSON","",token);
      }
    } catch (Exception e) {
      LOGGER.error("Error Renewing Lease"+e.toString());
    }

  }


}
