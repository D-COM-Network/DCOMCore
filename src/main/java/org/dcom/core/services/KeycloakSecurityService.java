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
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.installed.KeycloakInstalled;
import org.keycloak.representations.AccessToken;
import java.util.concurrent.TimeUnit;
import org.dcom.core.security.DCOMBearerToken;
import org.keycloak.representations.adapters.config.AdapterConfig;
import java.util.HashMap;
import com.owlike.genson.Genson;
import java.util.Map;

/**
*The keycloak specific implementation of SecurityService.java
*/
public class KeycloakSecurityService extends DCOMService implements SecurityService {
  
   private String type;
   private KeycloakInstalled keycloak;
   private AdapterConfig config;
   
   KeycloakSecurityService(String _url) {
      super.setURL(_url);
      config=new AdapterConfig();
      config.setRealm(_url.substring(_url.lastIndexOf("/")+1));
      config.setAuthServerUrl(_url.substring(0,_url.lastIndexOf("/")));
      config.setSslRequired("external");
      config.setResource("DCOMAPI");
      config.setPublicClient(true);
      config.setUseResourceRoleMappings(true);
      keycloak= new KeycloakInstalled(KeycloakDeploymentBuilder.build(config));
  }
  
  public DCOMBearerToken getBearerToken(String username,String password) {
    String bodyData="client_id=DCOMAPI&username="+username+"&password="+password+"&grant_type=password";
    String url=config.getAuthServerUrl()+"/realms/"+config.getRealm()+"/protocol/openid-connect/token";
    String retData=getClient().postString(url,"FORM",bodyData);
    Map<String,Object> data=new Genson().deserialize(retData, Map.class);
    if (!data.containsKey("access_token")) return null;
    return new DCOMBearerToken(data.get("access_token").toString());
  }
  
  public DCOMBearerToken getBearerToken() {
    try {
        keycloak.loginDesktop();
        String token = keycloak.getTokenString();
        return new DCOMBearerToken(token);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
  }
  
  public void logout() {
      try {
        keycloak.logout();
      } catch (Exception e) {
        e.printStackTrace();
      }
  }
  
}