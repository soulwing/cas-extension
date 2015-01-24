/*
 * File created on Dec 20, 2014 
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
package org.soulwing.cas.extension.authorization;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;
import org.soulwing.cas.extension.AbstractResourceReaderWriter;
import org.soulwing.cas.extension.Names;

/**
 * A reader for the LDAP user search resource configuration.
 *
 * @author Carl Harris
 */
class SamlReaderWriter extends AbstractResourceReaderWriter {

  private static final RoleAttributeReaderWriter ROLE_ATTRIBUTE_RW =
      new RoleAttributeReaderWriter(SamlDefinition.ROLE_ATTRIBUTE);
  
  /**
   * Constructs a new instance.
   */
  public SamlReaderWriter() {
    super(Names.SAML, ROLE_ATTRIBUTE_RW);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleAttributes(XMLStreamReader reader)
      throws XMLStreamException {
    super.handleAttributes(reader);
    parser.lastOperation().get(Names.ROLE_ATTRIBUTES).setEmptyList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeChildResources(XMLExtendedStreamWriter writer, ModelNode node)
      throws XMLStreamException {
    for (ModelNode roleAttribute : node.get(Names.ROLE_ATTRIBUTES).asList()) {
      ROLE_ATTRIBUTE_RW.writeResource(writer, roleAttribute);
    }
  }

}
