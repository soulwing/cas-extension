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

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * A reader/writer for a transformer resource configuration. 
 *
 * @author Carl Harris
 */
class TransformerReaderWriter extends AbstractResourceReaderWriter {
  
  private static final OptionReaderWriter OPTION_RW = 
      new OptionReaderWriter();
  
  /**
   * Constructs a new instance.
   */
  public TransformerReaderWriter() {
    super(Names.TRANSFORMER, OPTION_RW);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleAttributes(XMLStreamReader reader)
      throws XMLStreamException {
    super.handleAttributes(reader);
    parser.lastOperation().get(Names.OPTIONS).setEmptyObject();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeChildResources(XMLExtendedStreamWriter writer,
      ModelNode node) throws XMLStreamException {
    ModelNode optionsModel = node.get(Names.OPTIONS);
    if (optionsModel.isDefined()) {
      ModelNode options = optionsModel.asObject();
      for (String key : options.keys()) {
        ModelNode model = new ModelNode();
        model.add(key);
        model.add(options.get(key));
        OPTION_RW.writeResource(writer, model);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SimpleAttributeDefinition[] attributes() {
    return new SimpleAttributeDefinition[] {
        TransformerDefinition.CODE,
        TransformerDefinition.MODULE
    };
  }

}
