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

import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * A writer for the configuration of a management resource.
 *
 * @author Carl Harris
 */
public interface ResourceWriter {

  /**
   * Writes the configuration for the resource represented by the given 
   * management node.
   * <p>
   * This method calls {@link #writeChildResources(XMLExtendedStreamWriter, ModelNode)}
   * for each named child resource of the given management node.
   * @param writer XML writer to receive the configuration
   * @param node management node that represents the resource to write
   * @throws XMLStreamException
   */
  void writeResource(XMLExtendedStreamWriter writer, 
      ModelNode node) throws XMLStreamException;
  
  /**
   * Writes the children of the resource represented by the given 
   * management node.
   * <p>
   * @param writer XML writer to receive the configuration
   * @param node management node that represents the resource whose childen
   *   are to be written
   * @throws XMLStreamException
   */
  void writeChildResources(XMLExtendedStreamWriter writer, 
      ModelNode node) throws XMLStreamException;

}
