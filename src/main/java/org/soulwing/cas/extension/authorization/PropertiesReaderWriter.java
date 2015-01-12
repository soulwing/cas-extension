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

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.soulwing.cas.extension.AbstractResourceReaderWriter;
import org.soulwing.cas.extension.Names;

/**
 * A reader for the properties authorization resource configuration.
 *
 * @author Carl Harris
 */
class PropertiesReaderWriter extends AbstractResourceReaderWriter {

  /**
   * Constructs a new instance.
   */
  public PropertiesReaderWriter() {
    super(Names.PROPERTIES);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SimpleAttributeDefinition[] attributes() {
    return new SimpleAttributeDefinition[] { 
        PropertiesDefinition.PATH,
        PropertiesDefinition.RELATIVE_TO
    };
  }

}
