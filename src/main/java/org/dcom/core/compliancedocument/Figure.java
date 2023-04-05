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
package org.dcom.core.compliancedocument;

import java.util.Base64;

/**
*The programmatic representation and helper functionality for managing table Cells in a Compliance Document. A sub type of Insert
*/
public class Figure extends Insert{

  private String imageData;

  public Figure(ComplianceItem _parent) {
    super(_parent);

  }

  public byte[] getImageDataBytes() {
    return Base64.getDecoder().decode(imageData);
  }

  public String getImageDataString() {
    return imageData;
  }

  public void setImageData(byte[] data) {
    imageData=Base64.getEncoder().encodeToString(data);
  }

  public void setImageData(String data){
    imageData=data;
  }
  
  public void mergeIn(Figure newItem) {
    super.mergeIn(newItem);
    imageData=newItem.getImageDataString();
  }



}
