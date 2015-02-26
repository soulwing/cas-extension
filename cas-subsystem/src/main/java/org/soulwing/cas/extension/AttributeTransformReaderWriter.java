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

/**
 * A reader/writer for a proxy chain resource configuration. 
 *
 * @author Carl Harris
 */
class AttributeTransformReaderWriter extends AbstractResourceReaderWriter {
  
  private static final TransformerReaderWriter TRANSFORMER_RW = 
      new TransformerReaderWriter();
  
  /**
   * Constructs a new instance.
   */
  public AttributeTransformReaderWriter() {
    super(Names.ATTRIBUTE_TRANSFORM, TRANSFORMER_RW);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleAttributes(XMLStreamReader reader)
      throws XMLStreamException {
    super.handleAttributes(reader);
  }

}
