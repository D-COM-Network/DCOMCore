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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import com.owlike.genson.Genson;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.util.Map;
import java.util.ArrayList;

/**
*A private helper class providing helper functionality for XML/JSON serialisation and deserialization.
*/
class SerDerHelper {
	
		static Map<String,Object> parseJSON(String json) {
			return new Genson().deserialize(json, Map.class);
		}
		
		static Map<String,Object> parseXML(String xml) {
			HashMap<String,Object> data=new HashMap<String,Object>();
			try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(new StringReader(xml)));
					Element root = (Element) document.getDocumentElement();
					NodeList nodes=root.getElementsByTagName("*");
					for (int i=0; i < nodes.getLength();i++) {
						 	Element e=(Element)nodes.item(i);
							NodeList subNodes=e.getElementsByTagName("*");
							if (subNodes.getLength()>0) {
								ArrayList<HashMap<String,Object>> list =new ArrayList<HashMap<String,Object>>();
								data.put(e.getTagName(),list);
								for (int z=0; z< subNodes.getLength();z++) {
									HashMap<String,Object> subData=new HashMap<String,Object>();
									NodeList subSubNodes=((Element)subNodes.item(z)).getElementsByTagName("*");
									for (int k=0; k< subNodes.getLength();k++) {
											Element innerE=(Element)subNodes.item(k);
											subData.put(innerE.getTagName(),innerE.getTextContent());
									}
									list.add(subData);
								}
							} else data.put(e.getTagName(),e.getTextContent());
					}
				
			} catch (Exception e) {
				e.printStackTrace();
				return data;
			}
			return data;
		}
	
	
}