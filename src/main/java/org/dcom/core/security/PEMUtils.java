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


import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.security.Security;

import java.util.Base64;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
*A private helper class providing a friendly interface for the other two classes in this folder to utilise the bouncy castle API.
*/
class PEMUtils {

  private static byte[] parsePEMFile(File pemFile) throws IOException {
          if (!pemFile.isFile() || !pemFile.exists()) {
              throw new FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
          }
          PemReader reader = new PemReader(new FileReader(pemFile));
          PemObject pemObject = reader.readPemObject();
          if (pemObject==null) {
              throw new FileNotFoundException(String.format("The file '%s' cannot be parsed.", pemFile.getAbsolutePath()));
          }
          byte[] content = pemObject.getContent();
          reader.close();
          return content;
      }

      private static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
          PublicKey publicKey = null;
          try {
              KeyFactory kf = KeyFactory.getInstance(algorithm);
              EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
              publicKey = kf.generatePublic(keySpec);
          } catch (NoSuchAlgorithmException e) {
              System.out.println("Could not reconstruct the public key, the given algorithm could not be found.");
          } catch (InvalidKeySpecException e) {
              System.out.println("Could not reconstruct the public key");
          }

          return publicKey;
      }

      public static RSAPublicKey readPublicKeyFromString(String input) {
        input=input.replace("-----BEGIN PUBLIC KEY-----", "").replaceAll(System.lineSeparator(), "").replaceAll("\n","").replaceAll("\r","").replace("-----END PUBLIC KEY-----", "");
        try {
          RSAPublicKey gotKey=(RSAPublicKey) getPublicKey(Base64.getDecoder().decode(input.getBytes()), "RSA");
          return gotKey;
        } catch (Exception e) {
          System.out.println("Invalid Key:"+input);
        }
        return null;
        
      }

      public static RSAPublicKey readPublicKeyFromFile(File filepath) throws IOException {
        byte[] bytes = PEMUtils.parsePEMFile(filepath);
        return (RSAPublicKey)PEMUtils.getPublicKey(bytes, "RSA");
      }

      private static PrivateKey getPrivateKey(byte[] keyBytes, String algorithm) {
          PrivateKey privateKey = null;
          try {
              KeyFactory kf = KeyFactory.getInstance(algorithm);
              EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
              privateKey = kf.generatePrivate(keySpec);
          } catch (NoSuchAlgorithmException e) {
              System.out.println("Could not reconstruct the private key, the given algorithm could not be found.");
          } catch (InvalidKeySpecException e) {
              System.out.println("Could not reconstruct the private key");
          }

          return privateKey;
      }

      public static RSAPrivateKey readPrivateKeyFromFile(File filepath,String password) throws IOException {
          Security.addProvider(new BouncyCastleProvider());
          Object object = new PEMParser(new FileReader(filepath)).readObject();
          PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
          PEMKeyPair decryptedKeyPair = ((PEMEncryptedKeyPair) object).decryptKeyPair(decProv);
          PrivateKeyInfo keyInfo = decryptedKeyPair.getPrivateKeyInfo();
          return (RSAPrivateKey)getPrivateKey(keyInfo.getEncoded(),"RSA");
      }
      
      public static RSAPublicKey readPublicKeyFromFile(File filepath,String password) throws IOException {
          Security.addProvider(new BouncyCastleProvider());
          Object object = new PEMParser(new FileReader(filepath)).readObject();
          PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
          PEMKeyPair decryptedKeyPair = ((PEMEncryptedKeyPair) object).decryptKeyPair(decProv);
          SubjectPublicKeyInfo keyInfo = decryptedKeyPair.getPublicKeyInfo();
          return (RSAPublicKey)getPublicKey(keyInfo.getEncoded(),"RSA");
      }


}
