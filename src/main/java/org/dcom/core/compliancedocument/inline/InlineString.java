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
*This class represents an abstract rase item
*
*/
public class InlineString extends InlineItem {
	
		private String body;
		
		public InlineString(String _id, String _body) {
			super(_id);
			body=_body;
		}

		public void append(String s) {
			body=body+s;
		}
		
		public String generateText(boolean supressBlankRASEElements) {
			return body+" ";
		}
		
		public String toString() {
			return body;
		}
}