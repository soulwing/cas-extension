/*
 * File created on Dec 19, 2014 
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

import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * A writer for the CAS subsystem management configuration.
 *
 * @author Carl Harris
 */
public class SubsystemWriter 
    implements XMLElementWriter<SubsystemMarshallingContext> {

  private ResourceWriter delegate;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public SubsystemWriter(ResourceWriter delegate) {
    this.delegate = delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeContent(XMLExtendedStreamWriter writer,
      SubsystemMarshallingContext context) throws XMLStreamException {
    context.startSubsystemElement(Names.NAMESPACE, false);
    delegate.writeChildResources(writer, context.getModelNode());
    writer.writeEndElement();
  }

}
