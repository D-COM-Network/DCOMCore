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

package org.dcom.core.compliancedocument.utils;


/**
*A class that provides a friendly interface to the previous two classes for the rest of the ecosystem to use
*/
public class GuidHelper {
	
	private GuidHelper() {
		
	}
	
	public static String generateGuid() {
		return GuidCompressor.getNewIfcGloballyUniqueId();
	}
}
