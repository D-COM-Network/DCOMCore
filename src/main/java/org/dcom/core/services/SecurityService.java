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

/**
*The interface defining how the rest of DCOM codebase communicates with security services.
*/
public interface SecurityService {
  
  public DCOMBearerToken getBearerToken();
  public DCOMBearerToken getBearerToken(String username,String password);
  public void logout();
  
}