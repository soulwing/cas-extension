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

import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

/**
 * A parser for the CAS subsystem management configuration.
 *
 * @author Carl Harris
 */
public class SubsystemParser extends AbstractResourceParser 
    implements XMLElementReader<List<ModelNode>> {

  /**
   * Constructs a new instance.
   * @param delegate
   */
  public SubsystemParser(ResourceReader delegate) {
    super(delegate);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void readElement(XMLExtendedStreamReader reader,
      List<ModelNode> ops) throws XMLStreamException {
    super.readElement(reader, ops);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void init() {
    final ModelNode op = new ModelNode();
    op.get(ModelDescriptionConstants.OP).set(ModelDescriptionConstants.ADD);    
    addOperation(op, ModelDescriptionConstants.SUBSYSTEM, 
        Names.SUBSYSTEM_NAME);
  }
 
  /**
   * {@inheritDoc}
   */
  @Override
  public void unexpectedElement(XMLStreamReader reader) 
      throws XMLStreamException {
    throw ParseUtils.unexpectedElement((XMLExtendedStreamReader) reader);    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unexpectedEndElement(XMLStreamReader reader)
      throws XMLStreamException {
    throw ParseUtils.unexpectedEndElement((XMLExtendedStreamReader) reader);    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unexpectedAttribute(XMLStreamReader reader, 
      int index) throws XMLStreamException {
    ParseUtils.unexpectedAttribute((XMLExtendedStreamReader) reader, index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void missingAttributes(XMLStreamReader reader, 
      Set<String> names) throws XMLStreamException {
    throw ParseUtils.missingRequired((XMLExtendedStreamReader) reader, names);    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void duplicateAttribute(XMLStreamReader reader,
      String name) throws XMLStreamException {
    throw ParseUtils.duplicateAttribute((XMLExtendedStreamReader) reader, name);
  }

}
