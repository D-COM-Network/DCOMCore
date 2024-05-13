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

import java.util.ArrayList;
import java.util.List;

/**
*This class represents a single rase tag
*
*/
public class RASEBox extends InlineItem {
	
		public static int REQUIREMENT_SECTION =1;
		public static int APPLICATION_SECTION =2;
		public static int SELECTION_SECTION =3;
		public static int EXCEPTION_SECTION =4;
		public static int BLANK_SECTION =5;
	
		private int type;
		private ArrayList<InlineItem> subItems;


		public RASEBox(String _type,String _id) {
				super(_id);
				if (_type.equalsIgnoreCase("RequirementSection")) type=REQUIREMENT_SECTION;
				else if (_type.equalsIgnoreCase("SelectionSection")) 	 type=SELECTION_SECTION;
				else if (_type.equalsIgnoreCase("ApplicationSection")) type=APPLICATION_SECTION;
				else if (_type.equalsIgnoreCase("ExceptionSection")) type=EXCEPTION_SECTION;
				else type = BLANK_SECTION;
				subItems = new ArrayList<InlineItem>();
		}

		public int getType() {
		 return type;
		}
		
		public int getNoSubItems() {
			return subItems.size();
		}
		
		public InlineItem getSubItem(int i) {
			return subItems.get(i);
		}
		
		public void addSubItem(InlineItem item) {
			subItems.add(item);
		}
		
		public void addAllSubItems(List<InlineItem> items) {
			subItems.addAll(items);
		}
		
		public void removeSubItem(InlineItem item) {
			subItems.remove(item);
		}
		
		public List<InlineItem> getAllSubItems() {
			return new ArrayList<InlineItem>(subItems);
		}
		
		public String toString() {
			return getTypeString()+"("+getId()+")["+getDocumentReference()+"]";
		}
		
		public String toStringShort() {
			return getTypeString()+"("+getId()+")";
		}

		public String getTypeString() {
			String t="";
			if (type==REQUIREMENT_SECTION) t="RequirementSection";
			else if (type==SELECTION_SECTION) t="SelectionSection";
			else if (type==APPLICATION_SECTION) t="ApplicationSection";
			else if (type==EXCEPTION_SECTION) t="ExceptionSection";
			return t;
		}

		
		public String generateText(boolean supressBlankRASEElements) {
			StringBuffer str= new StringBuffer();
			str.append("<span id=\""+getId()+"\"");
			if (!getTypeString().equals("")) str.append(" data-raseType=\""+getTypeString()+"\"");
			str.append(">");
			for (int i=0; i < subItems.size();i++) {
				str.append(subItems.get(i).generateText(supressBlankRASEElements));
			}
			str.append("</span>");
			return str.toString();
		}
}