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

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.soulwing.cas.ssl.HostnameVerifierType;

/**
 * A reader/writer for the server host verifier resource configuration. 
 *
 * @author Carl Harris
 */
class HostnameVerifierReaderWriter extends AbstractResourceReaderWriter {
  
  private static final HostReaderWriter HOST_RW = 
      new HostReaderWriter();
  
  /**
   * Constructs a new instance.
   */
  public HostnameVerifierReaderWriter() {
    super(Names.HOSTNAME_VERIFIER, HOST_RW);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getResourceKey() {
    return Names.TYPE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleAttributes(XMLStreamReader reader)
      throws XMLStreamException {
    super.handleAttributes(reader);
    ModelNode op = parser.lastOperation();
    validateType(op);
    op.get(Names.HOSTS).setEmptyList();
  }

  private void validateType(ModelNode op) throws XMLStreamException {
    PathAddress address = PathAddress.pathAddress(
        op.get(ModelDescriptionConstants.OP_ADDR));
    String type = address.getLastElement().getValue();
    try {
      HostnameVerifierType.toObject(type);
    }
    catch (IllegalArgumentException ex) {
      throw new XMLStreamException("unrecognized server host verifier type '"
          + type + "'");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeChildResources(XMLExtendedStreamWriter writer,
      ModelNode node) throws XMLStreamException {
    ModelNode hostsNode = node.get(Names.HOSTS);
    if (hostsNode.isDefined()) {
      for (ModelNode proxy : hostsNode.asList()) {
        HOST_RW.writeResource(writer, proxy);
      }
    }
  }

}
