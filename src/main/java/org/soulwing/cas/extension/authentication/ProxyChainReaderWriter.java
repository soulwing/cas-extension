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
package org.soulwing.cas.extension.authentication;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.soulwing.cas.extension.AbstractResourceReaderWriter;
import org.soulwing.cas.extension.Names;

/**
 * A reader/writer for a proxy chain resource configuration. 
 *
 * @author Carl Harris
 */
class ProxyChainReaderWriter extends AbstractResourceReaderWriter {
  
  private static final ProxyReaderWriter PROXY_RW = 
      new ProxyReaderWriter();
  
  /**
   * Constructs a new instance.
   */
  public ProxyChainReaderWriter() {
    super(Names.PROXY_CHAIN, PROXY_RW);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleAttributes(XMLStreamReader reader)
      throws XMLStreamException {
    super.handleAttributes(reader);
    parser.lastOperation().get(Names.PROXIES).setEmptyList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeChildResources(XMLExtendedStreamWriter writer,
      ModelNode node) throws XMLStreamException {
    for (ModelNode proxy : node.get(Names.PROXIES).asList()) {
      PROXY_RW.writeResource(writer, proxy);
    }
  }

}
