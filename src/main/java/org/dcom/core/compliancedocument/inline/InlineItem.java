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
*This class represents an abstract inline text item
*
*/
public abstract class InlineItem {
	
		private String id;
		private String documentReference;
		
		public InlineItem(String _id) {
			if (_id.contains(".")) {
            	String[] idSplit = _id.split("\\.");
                _id = idSplit[idSplit.length-1];
             }
			id=_id;
		}
		
		public String getId() {
			return id;
		}
		
		
		public void setDocumentReference(String _docRef) {
			documentReference=_docRef;
		}
		
		public String getDocumentReference() {
			return documentReference;
		}

		public abstract String generateText(boolean supressBlankRASEElements);
		
}