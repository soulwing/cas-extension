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

import org.soulwing.cas.extension.Names;

/**
 * A {@link DescriptorReader} for the root element of the deployment 
 * descriptor.
 *
 * @author Carl Harris
 */
class RootReader implements DescriptorReader {

  public static final RootReader INSTANCE = new RootReader();

  private DescriptorParser parser;
  
  private RootReader() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(DescriptorParser parser) {
    this.parser = parser;    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startElement(XMLStreamReader reader, String namespaceUri,
      String localName, AppConfiguration config) throws XMLStreamException {
    if (Names.NAMESPACE.equals(namespaceUri)
        && Names.SUBSYSTEM_NAME.equals(localName)) {
      parser.push(InnerReader.INSTANCE);
      return;
    }
   
    parser.stop();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endElement(XMLStreamReader reader, String namespaceUri,
      String localName, AppConfiguration config) throws XMLStreamException {
    throw new XMLStreamException("unexpected end element", 
        reader.getLocation());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void characters(XMLStreamReader reader, AppConfiguration config)
      throws XMLStreamException {
    throw new XMLStreamException("unexpected text", reader.getLocation());
  }

  static class InnerReader extends AbstractDescriptorReader {
    static final InnerReader INSTANCE = new InnerReader();
    
    private InnerReader() {
      super(Names.SUBSYSTEM_NAME, AuthenticationReader.INSTANCE);
    }
  }
  
}
