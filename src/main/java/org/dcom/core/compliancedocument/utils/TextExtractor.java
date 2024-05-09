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
package org.dcom.core.compliancedocument.utils;


import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.dcom.core.compliancedocument.inline.InlineItem;
import org.dcom.core.compliancedocument.inline.RASEBox;
import org.dcom.core.compliancedocument.inline.RASETag;
import org.dcom.core.compliancedocument.inline.InlineString;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.apache.commons.text.StringEscapeUtils;
import org.dcom.core.compliancedocument.*;
import java.util.List;

/**
* This class contains the extractor to extract inline items from paragraphs
*
*/
public class TextExtractor {

		
		public static List<Element> extractInserts(NodeList children) {
			List<Element> inserts = new ArrayList<Element>();
			for (int i=0; i < children.getLength();i++) {
				Node n = children.item(i);
				if (n.getNodeType()==Node.ELEMENT_NODE) {
					inserts.addAll(extractInserts(n.getChildNodes()));
					if (((Element)n).getTagName().equals("figure") || ((Element)n).getTagName().equals("table")) inserts.add((Element)n);
				}
			}	
			return inserts;
		}

		public static List<InlineItem> extractStructure(NodeList children) {
				List<InlineItem> items = new ArrayList<InlineItem>();
				for (int i=0; i < children.getLength();i++) items.addAll(crawlStructure(children.item(i)));
				
				//join any duplicate text items
				List<InlineItem> newItems = new ArrayList<InlineItem>();
				for (int i=0; i < items.size();i++) {
					InlineItem item = items.get(i);
					if (item instanceof InlineString && newItems.size() > 0 && newItems.get(newItems.size()-1) instanceof InlineString) {
							((InlineString)newItems.get(newItems.size()-1)).append(item.generateText(false));
					} else if (item instanceof InlineString && item.generateText(false).strip().equals("") ) continue;
					else  newItems.add(item);
				}
				return newItems;
		}

		public static List<InlineItem> extractStructure(String body) {
				List<InlineItem> items = new ArrayList<InlineItem>();
				String bodyText = "<body>"+body+"</body>";
				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(new StringReader(bodyText)));

					NodeList children = document.getElementsByTagName("body").item(0).getChildNodes();
					for (int i=0; i < children.getLength();i++) items.addAll(crawlStructure(children.item(i)));
				} catch (Exception e) {
					e.printStackTrace();
				}

				//join any duplicate text items
				List<InlineItem> newItems = new ArrayList<InlineItem>();
				for (int i=0; i < items.size();i++) {
					InlineItem item = items.get(i);
					if (item instanceof InlineString && newItems.size() > 0 && newItems.get(newItems.size()-1) instanceof InlineString) {
							((InlineString)newItems.get(newItems.size()-1)).append(item.generateText(false));
					} else if (item instanceof InlineString && item.generateText(false).strip().equals("") ) continue;
					else  newItems.add(item);
				}
				return newItems;
		}

		private static List<InlineItem> crawlStructure(Node n) {
			List<InlineItem> items = new ArrayList<InlineItem>();
			if ( n.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)n;
				if ((element.getTagName().equalsIgnoreCase("span") || element.getTagName().equalsIgnoreCase("div")) && ( element.hasAttribute("data-raseType") || element.hasAttribute("data-rasetype"))) {
						String type = element.hasAttribute("data-raseType") ? element.getAttribute("data-raseType") : element.getAttribute("data-rasetype");
						if (type!=null) {
							if (type.equals("RequirementSection") || type.equals("SelectionSection") || type.equals("ApplicationSection") || type.equals("ExceptionSection")) {
								RASEBox box = new RASEBox(type,element.getAttribute("id"));
								items.add(box);
								NodeList children = n.getChildNodes();
								for (int i=0; i < children.getLength();i++) box.addAllSubItems(crawlStructure(children.item(i)));
								return items;
							} else if (element.hasAttribute("data-raseType") || element.hasAttribute("data-rasetype")) {
								RASETag tag = produceTag(element);
								if (tag != null) items.add(tag);
								return items;
							}
						}
				} else {
					NodeList children = n.getChildNodes();
					for (int i=0; i < children.getLength();i++) {
						RASEBox box = new RASEBox("",element.getAttribute("id"));	
						box.addAllSubItems(crawlStructure(children.item(i)));
						items.add(box);
					}
					return items;
				}
			}

			DOMImplementationLS lsImpl = (DOMImplementationLS)n.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
			LSSerializer lsSerializer = lsImpl.createLSSerializer();
			lsSerializer.getDomConfig().setParameter("xml-declaration", false);
			items.add(new InlineString("",lsSerializer.writeToString(n).trim()));	
			return items;
		}

		private static RASETag produceTag(Element element) {
			//check there are no sub elements here
			NodeList children = element.getChildNodes();
			String type = element.hasAttribute("data-raseType") ? element.getAttribute("data-raseType") : element.getAttribute("data-rasetype");
			String property = element.hasAttribute("data-raseProperty") ? element.getAttribute("data-raseProperty") : element.getAttribute("data-raseproperty");
			if (type.equals("")) {
				System.err.println("Found Empty Rase Tag!");
				return null;
			}
			if (type!=null) {
				RASETag tag = new RASETag(type,property,element.hasAttribute("data-raseComparator") ? element.getAttribute("data-raseComparator") : element.getAttribute("data-rasecomparator"),element.hasAttribute("data-raseTarget") ? element.getAttribute("data-raseTarget") : element.getAttribute("data-rasetarget"),element.hasAttribute("data-raseUnit") ? element.getAttribute("data-raseUnit") : element.getAttribute("data-raseunit"),element.getAttribute("id"),innerXml(element).trim(),element.getAttribute("data-references"));
				return tag;
			}
			return null;
		}

		private static String innerXml(Node node) {
			DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
			LSSerializer lsSerializer = lsImpl.createLSSerializer();
			lsSerializer.getDomConfig().setParameter("xml-declaration", false);
			NodeList childNodes = node.getChildNodes();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < childNodes.getLength(); i++) {
			   Node innerNode = childNodes.item(i);
			  if (innerNode!=null) {
			    if (innerNode.hasChildNodes()) {
			      sb.append(lsSerializer.writeToString(innerNode));
			    } else {
			      sb.append(innerNode.getNodeValue());
			    }
			  }
			}   
			return sb.toString().trim().replaceAll(" +", " ").replaceAll("[\\n\\t]", "").replace("&lt;","<").replace("&gt;",">");
		}
		
}