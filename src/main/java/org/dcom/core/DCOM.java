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

package org.dcom.core;

import org.dcom.core.services.ServiceLookup;

/**
*The DCOM Home class. Provides various utility methods – but also the factory method to acquire a ServiceLookup object so that the rest of the services can be accessed.
*/
public class DCOM {
  
  private DCOM() {
    
  }

  public static ServiceLookup getServiceLookup(String url) {
      return new ServiceLookup(url);
      
  }

  public static ServiceLookup getServiceLookup() {
    if (existsEnvironmentVariable("DCOM_SERVICE_LOCATOR")) {
      return getServiceLookup(getEnvironmentVariable("DCOM_SERVICE_LOCATOR"));
    }
    return getServiceLookup("https://lookup.dcom.org.uk");
  }
  
  public static boolean existsEnvironmentVariable(String name) {
      String sysvar=System.getenv(name);
      if (sysvar==null) return false;
      return true;
  }
  
  public static String getEnvironmentVariable(String name) {
    return System.getenv(name);  
  }
  
  public static boolean checkDCOMCertificatePath() {
      return existsEnvironmentVariable("DCOMCertificatePath");
  }
  
  public static boolean checkDCOMCertificatePassword() {
    return existsEnvironmentVariable("DCOMCertificatePassword");
  }
  
  public static String getDCOMCertificatePath() {
    return getEnvironmentVariable("DCOMCertificatePath");
  }
  
  public static String getDCOMCertificatePassword() {
      return getEnvironmentVariable("DCOMCertificatePassword");
  }

}
