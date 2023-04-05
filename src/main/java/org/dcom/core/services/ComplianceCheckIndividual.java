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

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.owlike.genson.annotation.JsonProperty;

/**
*The programmatic implementation and helper serialisation/desieralisation methods of the settings required to represent an individual involved in the application for a compliance check.
*/
public class ComplianceCheckIndividual {
  
    private String title;
    private String name;
    private String companyName;
    private String email;
    private String address;
    private String postcode;
    private String telephone;
    private String fax;
    private String mobile;
    private String position;

    
    public static ComplianceCheckIndividual fromJSON(String json) {
      return fromMap(SerDerHelper.parseJSON(json));
    }
    
    public static ComplianceCheckIndividual fromXML(String xml) {
      return fromMap(SerDerHelper.parseXML(xml));
    }
    
    public String toJSON() {
      StringBuffer str=new StringBuffer();
      str.append("{");
      str.append("\"title\":\"").append(title).append("\",");
      str.append("\"name\":\"").append(name).append("\",");
      str.append("\"companyName\":\"").append(companyName).append("\",");
      str.append("\"email\":\"").append(email).append("\",");
      str.append("\"address\":\"").append(address).append("\",");
      str.append("\"postcode\":\"").append(postcode).append("\",");
      str.append("\"telephone\":\"").append(telephone).append("\",");    
      str.append("\"fax\":\"").append(fax).append("\",");
      str.append("\"mobile\":\"").append(mobile).append("\",");
      str.append("\"position\":\"").append(position).append("\"");
      str.append("}");
      return str.toString();
    }
    
    static ComplianceCheckIndividual fromMap(  Map<String,Object> inputSet) {
      String title="";
      String name="";
      String companyName="";
      String email="";
      String address="";
      String postcode="";
      String telephone="";
      String fax="";
      String mobile="";
      String position="";
      if (inputSet.get("title")!=null) title=inputSet.get("title").toString();
      if (inputSet.get("name")!=null) name=inputSet.get("name").toString();
      if (inputSet.get("companyName")!=null) companyName=inputSet.get("companyName").toString();
      if (inputSet.get("email")!=null) email=inputSet.get("email").toString();
      if (inputSet.get("address")!=null) address=inputSet.get("address").toString();
      if (inputSet.get("postcode")!=null) postcode=inputSet.get("postcode").toString();
      if (inputSet.get("telephone")!=null) telephone=inputSet.get("telephone").toString();
      if (inputSet.get("fax")!=null) fax=inputSet.get("fax").toString();
      if (inputSet.get("mobile")!=null) mobile=inputSet.get("mobile").toString();
      if (inputSet.get("position")!=null) position=inputSet.get("position").toString();
      
      ComplianceCheckIndividual indv=new ComplianceCheckIndividual(title,name,companyName,email,address,postcode, telephone, fax, mobile, position);
      return indv;
    }

    public ComplianceCheckIndividual(@JsonProperty("title") String _title,@JsonProperty("name") String _name, @JsonProperty("companyName") String _companyName,@JsonProperty("email") String _email, @JsonProperty("address") String _address,@JsonProperty("postcode") String _postcode, @JsonProperty("telephone") String _telephone, @JsonProperty("_ax") String _fax,@JsonProperty("mobile") String _mobile,@JsonProperty("position") String _position) {
      title=_title;
      name=_name;
      companyName=_companyName;
      email=_email;
      address=_address;
      postcode=_postcode;
      telephone=_telephone;
      fax=_fax;
      mobile=_mobile;
      position=_position;  
    }
      
    
    
    public String getTitle() {
      return title;
    }
    
    public String getName() {
      return name;
    }
    
    public String getCompanyName() {
      return companyName;
    }
    
    public String getEmail() {
      return email;
    }
    
    public String getAddress() {
      return address;
    }
    
    public String getPostcode() {
      return postcode;
    }
    
    public String getTelephone() {
      return telephone;
    }
    
    public String getFax() {
      return fax;
    }
    
    public String getMobile() {
      return mobile;
    }
    
    public String getPosition() {
      return position;
    }
  
}
