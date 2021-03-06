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
package org.soulwing.cas.extension;

import java.util.Collections;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * A reader/writer for the host element.
 *
 * @author Carl Harris
 */
class HostReaderWriter extends AbstractResourceReaderWriter {

  public HostReaderWriter() {
    super(Names.HOST);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleAttributes(XMLStreamReader reader)   
      throws XMLStreamException {
    String name = null;
    for (int i = 0, max = reader.getAttributeCount(); i < max; i++) {
      String localName = reader.getAttributeLocalName(i);
      if (Names.NAME.equals(localName)) {
        if (name == null) {
          name = reader.getAttributeValue(i);
        }
        else {
          parser.duplicateAttribute(reader, localName);
        }
      }
      else {
        parser.unexpectedAttribute(reader, i);
      }
    }
    
    if (name == null) {
      parser.missingAttributes(reader, Collections.singleton(Names.NAME));
    }
    
    ModelNode value = HostnameVerifierDefinition.HOSTS.parse(name, reader);
    parser.lastOperation().get(Names.HOSTS).add(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeResource(XMLExtendedStreamWriter writer, ModelNode node)
      throws XMLStreamException {
    writer.writeStartElement(Names.HOST);
    writer.writeAttribute(Names.NAME, node.asString());
    writer.writeEndElement();
  }

}
