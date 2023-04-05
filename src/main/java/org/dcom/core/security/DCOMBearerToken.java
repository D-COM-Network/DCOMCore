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
package org.dcom.core.security;


import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWTVerifier;


/**
*The representation of a bearer token used to provide authentication between services and services and users and services.
*/
public class DCOMBearerToken{
  
  private String token;

  public DCOMBearerToken(String inputToken) {
        if (inputToken==null) inputToken="";
        token=inputToken.replace("Bearer","").trim();
  }
  
  public String getToken() {
    return token;
  }
  
  public boolean isValid(ServiceCertificate certificate) {
      JWTVerifier verifier = JWT.require(Algorithm.RSA256(certificate.getPublicKey(), null)).acceptIssuedAt(5*60).build();
      try {
        DecodedJWT jwt = verifier.verify(token);
        return true;
      } catch (Exception e) {
        return false;
      }
  }
  
  public String getIdentifier() {
    try {
      DecodedJWT jwt = JWT.decode(token);
      return jwt.getClaim("preferred_username").asString();
    } catch (Exception e) {
      return null;
    }
  }
  
}
