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

import java.io.File;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.io.IOException;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import java.util.Date;
import com.auth0.jwt.exceptions.JWTCreationException;

/**
*Representation of a services certificate provided by the Service Lookup.
*/
public class ServiceCertificate {
  
  private RSAPublicKey publicKey;
  private RSAPrivateKey privateKey;
  
  public ServiceCertificate(String certData)  {
    if (certData==null) return;
    certData=certData.trim();
    publicKey=PEMUtils.readPublicKeyFromString(certData);
    privateKey=null;
  }
  
  public ServiceCertificate(File fileName,String password) throws IOException {
      if (password!=null) {
        privateKey=PEMUtils.readPrivateKeyFromFile(fileName,password);
        publicKey=PEMUtils.readPublicKeyFromFile(fileName,password);
      } else {
        publicKey=PEMUtils.readPublicKeyFromFile(fileName);
        privateKey=null;
      }
  }
  
  public DCOMBearerToken generateBearerToken() {
      try {
        Algorithm RSAalgorithm = Algorithm.RSA256(null, privateKey);
        String token = JWT.create().withIssuedAt(new Date()).sign(RSAalgorithm);
        return new DCOMBearerToken(token);
      } catch (JWTCreationException e) {
        e.printStackTrace();
      }
      return null;
  }
  
  public ServiceCertificate(File fileName) throws IOException {
      this(fileName,null);
  }
  
  public boolean checkTokenValidity(DCOMBearerToken token) {
    return token.isValid(this);
    
  }
  
  public RSAPublicKey getPublicKey() {
      return publicKey;
  }
  
}
