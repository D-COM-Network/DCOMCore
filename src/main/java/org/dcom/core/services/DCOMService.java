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
import java.util.HashMap;

/**
*The abstract implementation of any DCOM service – provides helper functionality for all other service implementations.
*/
abstract class DCOMService {

  private String url,name;
  private ServiceCertificate cert;
  private HttpClient client;
  
  DCOMService() {
      client=new HttpClient();
  }
  
  void setURL(String _url) {
      url=_url;
  }
  
  void setName(String _name) {
    name=_name;
  }
  
  void setCertificate(ServiceCertificate _cert) {
    cert=_cert;
  }
  
  public HttpClient getClient() {
    return client;
  }
  
  public String getURL() {
    return url;
  }
  
  public String getName() {
    return name;
  }
  
  public String getSecurityServiceType() {
    HashMap<String,Object> dataReturn=client.getJSON(getURL()+"/",null);
    return (String)dataReturn.get("SecurityServiceType");
  }
  
  public String getSecurityServiceURI() {
    HashMap<String,Object> dataReturn=client.getJSON(getURL()+"/",null);
     return (String)dataReturn.get("SecurityServiceUri");
  }
  
  public ServiceCertificate getCertificate() {
    return cert;
  }
  
  public SecurityService getSecurityService() {
    HashMap<String,Object> dataReturn=client.getJSON(getURL()+"/",null);
    String url=(String)dataReturn.get("SecurityServiceUri");
    String type=(String)dataReturn.get("SecurityServiceType");
    if (type.equalsIgnoreCase("Keycloak")) return new KeycloakSecurityService(url);
    return null;
  }

}
