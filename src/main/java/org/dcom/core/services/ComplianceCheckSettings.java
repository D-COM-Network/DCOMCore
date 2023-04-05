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
*The programmatic implementation and helper serialisation/desieralisation methods of the settings required to initiate a compliance check.
*/
public class ComplianceCheckSettings {
  
    private String operationMode;
    private String securityType;
    private String securityURI;
    private String lifecyclestage;
    private ArrayList<String> documentReference;
    private String modelServerType;
    private String modelServerURL;
    private String uprn;
    private String regulatorIndentification;
    private ArrayList<ComplianceCheckIndividual> individuals;
    private String address;
    private String description;
    private String presentUse;
    private String proposedUse;
    private boolean fireSafetyOrder;
    private boolean consentToExtension;
    private boolean consentToConditions;
    private boolean electricalSafetyConfirmation;
    
    
    public static ComplianceCheckSettings fromJSON(String json) {
      return fromMap(SerDerHelper.parseJSON(json));
    }
    
    public static ComplianceCheckSettings fromXML(String xml) {
      return fromMap(SerDerHelper.parseXML(xml));
    }
    
    public String toJSON() {
      StringBuffer str=new StringBuffer();
      str.append("{");
      str.append("\"operationMode\":\"").append(operationMode).append("\",");
      str.append("\"lifecyclestage\":\"").append(lifecyclestage).append("\",");
      str.append("\"projectSecurityServiceType\":\"").append(securityType).append("\",");
      str.append("\"projectSecurityServiceURI\":\"").append(securityURI).append("\",");
      str.append("\"complianceDocumentReference\":[");
      boolean first=true;
      for (String doc: documentReference) {
          if (first) first=false; else str.append(",");
          str.append("\"").append(doc).append("\"");
      }
      str.append("],");
      str.append("\"modelServerType\":\"").append(modelServerType).append("\",");
      str.append("\"modelServerURL\":\"").append(modelServerURL).append("\",");    
      str.append("\"UPRN\":\"").append(uprn).append("\",");
      str.append("\"regulatorIdentification\":\"").append(regulatorIndentification).append("\",");
      str.append("\"individuals\":[");
      for (ComplianceCheckIndividual individual:individuals) {
          if (first) first=false; else str.append(",");
          str.append(individual.toJSON());
      }
      str.append("],");
      str.append("\"address\":\"").append(address).append("\",");
      str.append("\"description\":\"").append(description).append("\",");
      str.append("\"presentUse\":\"").append(presentUse).append("\",");
      str.append("\"proposedUse\":\"").append(proposedUse).append("\",");
      str.append("\"fireSafetyOrder\":\"").append(fireSafetyOrder).append("\",");
      str.append("\"consentToExtension\":\"").append(consentToExtension).append("\",");    
      str.append("\"consentToConditions\":\"").append(consentToConditions).append("\",");
      str.append("\"electricalSafetyConfirmation\":\"").append(electricalSafetyConfirmation).append("\"");
  
      str.append("}");
      return str.toString();
    }
    
    static ComplianceCheckSettings fromMap(  Map<String,Object> inputSet) {
      ComplianceCheckSettings settings=new ComplianceCheckSettings(inputSet.get("operationMode").toString(),inputSet.get("lifecyclestage").toString(),inputSet.get("projectSecurityServiceType").toString(),inputSet.get("projectSecurityServiceURI").toString(),(ArrayList<String>)inputSet.get("complianceDocumentReference"), inputSet.get("modelServerType").toString(),inputSet.get("modelServerURL").toString(),inputSet.get("UPRN").toString(),inputSet.get("regulatorIdentification").toString(),
      inputSet.get("address").toString(),inputSet.get("description").toString(),inputSet.get("presentUse").toString(),inputSet.get("proposedUse").toString(),Boolean.parseBoolean(inputSet.get("fireSafetyOrder").toString()),Boolean.parseBoolean(inputSet.get("consentToExtension").toString()),Boolean.parseBoolean(inputSet.get("consentToConditions").toString()),Boolean.parseBoolean(inputSet.get("electricalSafetyConfirmation").toString()));
    

      List<Object> individualsInput=(List<Object>)inputSet.get("individuals");
      for (Object o: individualsInput) settings.addIndividual(ComplianceCheckIndividual.fromMap((	HashMap<String,Object>)o));
  
      return settings;
    }

    public ComplianceCheckSettings(@JsonProperty("operationMode") String _operationMode, @JsonProperty("lifecyclestage") String _lifecyclestage, @JsonProperty("securityType") String _securityType, @JsonProperty("securityURI") String _securityURI,@JsonProperty("documentReference") ArrayList<String> _documentReference, @JsonProperty("modelServerType") String _modelServerType, @JsonProperty("modelServerURL") String _modelServerURL, @JsonProperty("uprn") String _uprn, @JsonProperty("regulatorIndentification") String _regulatorIndentification, @JsonProperty("address") String _address, @JsonProperty("description") String _description, @JsonProperty("presentUse") String _presentUse, @JsonProperty("proposedUse") String _proposedUse, @JsonProperty("fireSafetyOrder") boolean _fireSafetyOrder,@JsonProperty("consentToExtension") boolean _consentToExtension,@JsonProperty("consentToConditions") boolean _consentToConditions,@JsonProperty("electricalSafetyConfirmation") boolean _electricalSafetyConfirmation) {
      operationMode=_operationMode;
      lifecyclestage=_lifecyclestage;
      securityType=_securityType;
      securityURI=_securityURI;
      documentReference=_documentReference;
      modelServerType=_modelServerType;
      modelServerURL=_modelServerURL;
      regulatorIndentification=_regulatorIndentification;
      address=_address;
      description=_description;
      presentUse=_presentUse;
      proposedUse=_proposedUse;
      fireSafetyOrder=_fireSafetyOrder;
      consentToExtension=_consentToExtension;
      consentToConditions=_consentToConditions;
      electricalSafetyConfirmation=_electricalSafetyConfirmation;
      uprn=_uprn;
      individuals=new ArrayList<ComplianceCheckIndividual>();
    }
    
    public void addIndividual(ComplianceCheckIndividual individual) {
      individuals.add(individual);
    }
    
    public ComplianceCheckIndividual getIndividual(int i) {
      return individuals.get(i);
    }
    
    public int getNoIndividuals() {
      return individuals.size();
    }
    
    public String getSecurityType() {
      return securityType;
    }
    
    public String getSecurityURI() {
      return securityURI;
    }
    
    public String getOperationModel() {
      return operationMode;
    }
    
    public String getLifecyclestage() {
      return lifecyclestage;
    }
    
    public ArrayList<String> getDocumentReference() {
      return documentReference;
    }
    
    public String getModelServerType() {
      return modelServerType;
    }
    
    public String getModelServerURL() {
      return modelServerURL;
    }
    
    public String getRegulatorIndentification() {
      return regulatorIndentification;
    }
    
    public String getUPRN() {
      return uprn;
    }
    
    public String getAddress() {
      return address;
    }
    
    public String getDescription() {
      return description;
    }
    
    public String getPresentUse() {
      return presentUse;
    }
    
    public String getProposedUse() {
      return proposedUse;
    }
    
    public boolean getFireSafetyOrder() {
      return fireSafetyOrder;
    }
    
    public boolean getConsentToExtension() {
      return consentToExtension;
    }
    
    public boolean getConsentToConditions() {
      return consentToConditions;
    }
    
    public boolean getElectricalSafetyConfirmation(){
      return electricalSafetyConfirmation;
    }
  
}
