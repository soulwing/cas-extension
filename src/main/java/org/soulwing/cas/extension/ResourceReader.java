/*
 * File created on Dec 18, 2014 
 *
 * Copyright (c) 2015 Carl Harris, Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.cas.extension;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * A parser for the configuration of a management resource.
 *
 * @author Carl Harris
 */
public interface ResourceReader {

  /**
   * Initializes this reader.
   * @param parser the calling configuration parser
   */
  void init(ResourceParser parser);
  
  /**
   * Tests whether this reader supports a given element.
   * @param namespaceUri namespace URI of the element
   * @param localName local name of the element
   * @return {@code true} if this reader supports the given element
   */
  boolean supports(String namespaceUri, String localName);
    
  /**
   * Notifies the recipient of the start of an element.
   * @param reader the XML stream being parsed
   * @param namespaceUri namespace URI of the element
   * @param localName local name of the element
   * @throws XMLStreamException
   */
  void startElement(XMLStreamReader reader, String namespaceUri, String localName) 
      throws XMLStreamException;
  
  /**
   * Notifies the recipient of the end of an element.
   * @param reader the XML stream being parsed
   * @param namespaceUri namespace URI of the element
   * @param localName local name of the element
   * @throws XMLStreamException
   */
  void endElement(XMLStreamReader reader, String namespaceUri, String localName) 
      throws XMLStreamException;
}
