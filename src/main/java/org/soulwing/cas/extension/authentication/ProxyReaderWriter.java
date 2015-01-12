/*
 * File created on Dec 21, 2014 
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
package org.soulwing.cas.extension.authentication;

import java.util.Collections;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.soulwing.cas.extension.AbstractResourceReaderWriter;
import org.soulwing.cas.extension.Names;

/**
 * A reader/writer for the proxy element.
 *
 * @author Carl Harris
 */
class ProxyReaderWriter extends AbstractResourceReaderWriter {

  public ProxyReaderWriter() {
    super(Names.PROXY);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleAttributes(XMLStreamReader reader)   
      throws XMLStreamException {
    String url = null;
    for (int i = 0, max = reader.getAttributeCount(); i < max; i++) {
      String localName = reader.getAttributeLocalName(i);
      if (Names.URL.equals(localName)) {
        if (url == null) {
          url = reader.getAttributeValue(i);
        }
        else {
          parser.duplicateAttribute(reader, localName);
        }
      }
      else {
        parser.unexpectedAttribute(reader, i);
      }
    }
    
    if (url == null) {
      parser.missingAttributes(reader, Collections.singleton(Names.URL));
    }
    
    ModelNode value = ProxyChainDefinition.PROXIES.parse(url, reader);
    parser.lastOperation().get(Names.PROXIES).add(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeResource(XMLExtendedStreamWriter writer, ModelNode node)
      throws XMLStreamException {
    writer.writeStartElement(Names.PROXY);
    writer.writeAttribute(Names.URL, node.asString());
    writer.writeEndElement();
  }

}
