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

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.staxmapper.XMLExtendedStreamReader;

/**
 * A parser for the CAS subsystem management configuration.
 *
 * @author Carl Harris
 */
public class SubsystemParser implements ResourceParser {

  private final ResourceReader delegate;
  
  private final Deque<List<ModelNode>> opStack = new LinkedList<>();
  
  private final Deque<ResourceReader> stack = new LinkedList<>();
  
  private final Map<AttachmentKey<?>, Object> attachments = new HashMap<>();
  
  private List<ModelNode> ops;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public SubsystemParser(ResourceReader delegate) {
    this.delegate = delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void readElement(XMLExtendedStreamReader reader,
      List<ModelNode> ops) throws XMLStreamException {

    this.ops = ops;
    push(delegate);

    final ModelNode op = new ModelNode();
    op.get(ModelDescriptionConstants.OP).set(
        ModelDescriptionConstants.ADD);
    
    addOperation(op, ModelDescriptionConstants.SUBSYSTEM, 
        Names.SUBSYSTEM_NAME);
    
    while (reader.hasNext()) {
      switch (reader.next()) {
        case START_ELEMENT:   
          peek().startElement(reader, reader.getNamespaceURI(), 
              reader.getLocalName());
          break;
          
        case END_ELEMENT:
          peek().endElement(reader, reader.getNamespaceURI(), 
              reader.getLocalName());
          break;
          
        case CHARACTERS:
          peek().characters(reader, reader.getText());
          break;
          
        default:
          assert true;  // just ignore it
      }
    }
  }

  private ResourceReader peek() {
    if (stack.isEmpty()) {
      throw new RuntimeException("stack underflow");
    }
    return stack.peek();
  }
  
  @Override
  public void push(ResourceReader reader) {
    reader.init(this);
    stack.push(reader);

    List<ModelNode> ops = new ArrayList<>();
    if (!opStack.isEmpty()) {
      ops.addAll(opStack.peek());
    }
    opStack.push(ops);

  }

  @Override
  public void pop() {
    stack.pop();
    opStack.pop();
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void addOperation(ModelNode op, String type, String name) {
    PathAddress addr = PathAddress.pathAddress();
    List<ModelNode> ops = opStack.peek();
    if (!ops.isEmpty()) {
      ModelNode parent = ops.get(ops.size() - 1);    
      for (ModelNode node : 
        parent.get(ModelDescriptionConstants.OP_ADDR).asList()) {
        Property property = node.asProperty();
        addr = addr.append(property.getName(), property.getValue().asString());
      }
    }
    
    addr = addr.append(type, name);
    op.get(ModelDescriptionConstants.OP_ADDR).set(addr.toModelNode());
    this.ops.add(op);
    opStack.peek().add(op);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ModelNode lastOperation() {
    return ops.get(ops.size() - 1);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T getAttachment(AttachmentKey<T> key) {
    return key.cast(attachments.get(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T putAttachment(AttachmentKey<T> key, T value) {
    return key.cast(attachments.put(key, value));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T removeAttachment(AttachmentKey<T> key) {
    return key.cast(attachments.remove(key));
  }
 
}
