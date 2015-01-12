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
 * A {@link DescriptorReader} for the authentication element of the 
 * deployment descriptor.
 *
 * @author Carl Harris
 */
class AuthorizationReader extends AbstractDescriptorReader {

  public static final AuthorizationReader INSTANCE = new AuthorizationReader();
  
  private AuthorizationReader() {
    super(Names.AUTHORIZATION);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void characters(XMLStreamReader reader, AppConfiguration config)
      throws XMLStreamException {
    String id = reader.getText().trim();
    if (id.isEmpty()) {
      throw new XMLStreamException("expected identifier", 
          reader.getLocation());
    }
    config.setAuthorizationId(id);
  }
  
}
