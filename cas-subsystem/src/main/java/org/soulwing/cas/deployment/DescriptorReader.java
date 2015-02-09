/*
 * File created on Dec 22, 2014 
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
package org.soulwing.cas.deployment;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A reader for a subtree of the CAS deployment descriptor.
 *
 * @author Carl Harris
 */
interface DescriptorReader {

  /**
   * Initializes this reader.
   * @param parser the active parser
   */
  void init(DescriptorParser parser);
  
  /**
   * Notifies the recipient of the start of a supported element.
   * @param reader XML stream being parsed
   * @param namespaceUri namespace of the element
   * @param localName local name of the element
   * @param config application configuration
   * @throws XMLStreamException
   */
  void startElement(XMLStreamReader reader, String namespaceUri, 
      String localName, AppConfiguration config)
      throws XMLStreamException;
 
  /**
   * Notifies the recipient of the end of an element.
   * @param reader XML stream being parsed
   * @param namespaceUri namespace of the element
   * @param localName local name of the element
   * @param config application configuration
   * @throws XMLStreamException
   */
  void endElement(XMLStreamReader reader, String namespaceUri,
      String localName, AppConfiguration config)
      throws XMLStreamException;
  
  /**
   * Notifies the recipient of character data in an element
   * @param reader XML stream being parsed
   * @param config application configuration
   * @throws XMLStreamException
   */
  void characters(XMLStreamReader reader,
      AppConfiguration config) throws XMLStreamException;

}
