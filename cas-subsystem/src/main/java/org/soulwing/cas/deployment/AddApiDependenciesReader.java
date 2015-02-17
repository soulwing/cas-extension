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
 * A {@link DescriptorReader} for the profile element of the 
 * deployment descriptor.
 *
 * @author Carl Harris
 */
class AddApiDependenciesReader extends AbstractDescriptorReader {

  public static final AddApiDependenciesReader INSTANCE = new AddApiDependenciesReader();
  
  private AddApiDependenciesReader() {
    super(Names.ADD_API_DEPENDENCIES);
  }

  
  /**
   * {@inheritDoc}
   */
  @Override
  public void endElement(XMLStreamReader reader, String namespaceUri,
      String localName, AppConfiguration config) throws XMLStreamException {
    config.setAddDependencies(true);
    super.endElement(reader, namespaceUri, localName, config);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void characters(XMLStreamReader reader, AppConfiguration config)
      throws XMLStreamException {
    if (!reader.getText().trim().isEmpty()) {
      throw new XMLStreamException(Names.ADD_API_DEPENDENCIES
          + "does not allow nested content", 
          reader.getLocation());
    }
  }
  
}
