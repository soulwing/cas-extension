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

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A {@link DescriptorParser}
 *
 * @author Carl Harris
 */
class XMLStreamDescriptorParser implements DescriptorParser {

  private static final XMLInputFactory factory = XMLInputFactory.newFactory();
  
  private final Deque<DescriptorReader> stack = new LinkedList<>();
  
  private boolean stopped;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public AppConfiguration parse(InputStream inputStream)
      throws DescriptorParseException {
    try {
      return parse(factory.createXMLStreamReader(inputStream));
    }
    catch (XMLStreamException ex) {
      throw new DescriptorParseException(ex.getMessage(), ex);
    }
  }

  private AppConfiguration parse(XMLStreamReader reader) 
      throws XMLStreamException {
    
    AppConfiguration config = new AppConfiguration();
    
    push(RootReader.INSTANCE);
    while (!stopped && reader.hasNext()) {
      int type = reader.next();
      switch (type) {
        case START_ELEMENT:
          peek().startElement(reader, reader.getNamespaceURI(), 
              reader.getLocalName(), config);
          break;
        case END_ELEMENT:
          peek().endElement(reader, reader.getNamespaceURI(), 
              reader.getLocalName(), config);
          break;
        case CHARACTERS:
          peek().characters(reader, config);
          break;
        default:
          assert true; // just ignore it
      }
    }
    
    if (stopped) return null;
    return config;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void push(DescriptorReader reader) {
    reader.init(this);
    stack.push(reader);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void pop() {
    assertStackNotEmpty();
    stack.pop();
  }

  private DescriptorReader peek() {
    assertStackNotEmpty();
    return stack.peek();
  }

  private void assertStackNotEmpty() {
    if (stack.isEmpty()) {
      throw new RuntimeException("stack underflow");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    stopped = true;
  }

}
