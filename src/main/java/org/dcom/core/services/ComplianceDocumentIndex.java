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
import java.util.List;
import java.util.ArrayList;

/**
* The programmatic implementation and helper serialisation/desieralisation methods of the index of compliance documents stored on a compliance document service
*/
public class ComplianceDocumentIndex {

		private ArrayList<Object> data;

		ComplianceDocumentIndex(HashMap<String,Object> _in) {
			if (_in.containsKey("ServerIdentity")) {
				_in=(HashMap<String,Object>) _in.get("ServerIdentity");
			}
			data=(ArrayList<Object>) _in.get("documentList");
			
		}
		
		public List<String> getDocumentShortNames() {
				ArrayList<String> output=new ArrayList<String>();
				for (Object d: data) {
					HashMap<String,Object> d1=(HashMap<String,Object>) d;
					output.add(d1.get("shortName").toString());
				}
				return output;
		}
		
		
		private HashMap<String,Object> find(String sN) {
			for (Object d: data) {
				HashMap<String,Object> d1=(HashMap<String,Object>) d;
				String shrt=d1.get("shortName").toString();
				if (shrt.equals(sN)) return d1;
			}
			return null;
		}
		
		public String getDocumentFullName(String shortName) {
				HashMap<String,Object> d1=find(shortName);
				return d1.get("fullName").toString();
		}
		
		public String getDocumentType(String shortName) {
			HashMap<String,Object> d1=find(shortName);
			return d1.get("documentType").toString();
		}
		
		public String getDocumentJurisdiction(String shortName) {
			HashMap<String,Object> d1=find(shortName);
			return d1.get("jurisdiction").toString();
		}
		
		public List<String> getDocumentVersionList(String shortName) {
			HashMap<String,Object> d1=find(shortName);
			ArrayList<Object> versions=(ArrayList<Object>) d1.get("versions");
			ArrayList<String> versionList=new ArrayList<String>();
			for (Object o: versions) {
				HashMap<String,Object> v1=(HashMap<String,Object>) o;
				versionList.add(v1.get("versionName").toString());
			}
			return versionList;
		}
		
		public String getDocumentLatestVersion(String shortName) {
			HashMap<String,Object> d1=find(shortName);
			return d1.get("latestVersion").toString();
		}

}