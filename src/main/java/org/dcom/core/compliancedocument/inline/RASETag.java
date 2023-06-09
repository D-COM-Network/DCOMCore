/*
Copyright (C) 2022 Cardiff University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

*/
package org.dcom.core.compliancedocument.inline;


/**
*This class represents a single rase tag
*
*/
public class RASETag extends InlineItem {
	
		public static int REQUIREMENT =1;
		public static int APPLICATION =2;
		public static int SELECTION =3;
		public static int EXCEPTION =4;
	
		private int type;
		private String comparator;
		private String property;
		private String value;
		private String unit;
		private String documentReference;
		private String body;
	
		public RASETag(String _type,String _property,String _comparator, String _value, String _unit, String _id,String _body) {
				super(_id);
				if (_type.equalsIgnoreCase("requirement")) type=REQUIREMENT;
				else if (_type.equalsIgnoreCase("selection")) 	 type=SELECTION;
				else if (_type.equalsIgnoreCase("application")) type=APPLICATION;
				else if (_type.equalsIgnoreCase("exception")) type=EXCEPTION;
				
				property=_property;
				value=_value;
				comparator=_comparator;
				unit=_unit;
				body=_body;
		}  
		
		private String sanitise(String in) {
			in = in.replaceAll("[^a-zA-Z0-9 -/_]", "").trim();
			char[] charArray = in.toCharArray();
    		boolean foundSpace = true;
    		for(int i = 0; i < charArray.length; i++) {
				if(Character.isLetter(charArray[i])) {
					if(foundSpace) {
						charArray[i] = Character.toUpperCase(charArray[i]);
          				foundSpace = false;
        			}
      			} else foundSpace = true;
    		}
			// convert the char array to the string
    		return String.valueOf(charArray);
		}
	
		public int getType() {
		 return type;
		}
		
		public String getComparator() {
			if (comparator.equals("==")) return "==";
			if (comparator.equals("")) return "==";
			if (comparator==null) return "==";
			if (comparator.equals("=")) return "==";
			comparator=comparator.replace("+amp;","&");
			if (comparator.equals("&le;") || comparator.equals("&lt;=") || comparator.equals("<=")) return "<=";
			if (comparator.equals("&lt;") || comparator.equals("<") ) return "<";
			if (comparator.equals("&gt;") || comparator.equals(">")) return ">";
			if (comparator.equals("&ge;") || comparator.equals("&gt;=") || comparator.equals(">=")) return ">=";
			if (comparator.equals("&gt;&lt;")) return "==";
			if (!comparator.equals("includes") && !comparator.equals("excludes")) System.err.println("[Error]! Comparator["+getId()+"]:"+comparator);
			return comparator;
		} 
		
		public String getProperty() {
			return property;
		}
		
		public String getSanitisedProperty() {
			return sanitise(property);
		}
		
		public String getUnit() {
			if (unit==null) return "";
			else return unit;
		}
		
		public String getValue() {
				if (value==null || value.equals("")) return "true";
				else return value.toLowerCase();
		}
		
		public boolean isPropertyOnly() {
			if (value==null && comparator ==null) return true;
			if (value.equals("true") && comparator.equals("=")) return true;
			return false;
		}
		
		public String toString() {
			return getTypeString()+"("+getId()+"):"+property+":"+getComparator()+":"+getValue()+":"+getUnit()+"["+getDocumentReference()+"]";
		}
		
		public String toStringShort() {
			return getTypeString()+"("+getId()+"):"+property;
		}
		
		public String getTypeString() {
			String t="";
			if (type==REQUIREMENT) t="Requirement";
			else if (type==SELECTION) t="Selection";
			else if (type==APPLICATION) t="Application";
			else if (type==EXCEPTION) t="Exception";
			return t;
		}

		public String generateText() {
			return "<span data-raseType='"+getTypeString()+"' data-raseTarget='"+getValue()+"' data-raseUnit='"+getUnit()+"' id='"+getId()+"' data-raseComparator='"+getComparator()+"'>"+body+"</span>";
		}
}