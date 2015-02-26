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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * An abstract base for {@link ResourceReader} implementations.
 *
 * @author Carl Harris
 */
public abstract class AbstractResourceReaderWriter 
    implements ResourceReader, ResourceWriter {

  private final List<AbstractResourceReaderWriter> children = new ArrayList<>();
  
  protected ResourceParser parser;
  private final String namespaceUri;
  private final String localName;
  
  /**
   * Constructs a new instance.
   * @param localName
   */
  public AbstractResourceReaderWriter(String localName) {
    this(Names.NAMESPACE, localName);
  }
  
  /**
   * Constructs a new instance.
   * @param localName
   */
  @SafeVarargs
  public AbstractResourceReaderWriter(String localName,
      AbstractResourceReaderWriter... children) {
    this(Names.NAMESPACE, localName, children);
  }

  /**
   * Constructs a new instance.
   * @param namespaceUri
   * @param localName
   */
  public AbstractResourceReaderWriter(String namespaceUri, String localName) {
    this.namespaceUri = namespaceUri;
    this.localName = localName;
  }

  /**
   * Constructs a new instance.
   * @param namespaceUri
   * @param localName
   */
  @SafeVarargs
  public AbstractResourceReaderWriter(String namespaceUri, String localName,
      AbstractResourceReaderWriter... children) {
    this.namespaceUri = namespaceUri;
    this.localName = localName;
    this.children.addAll(Arrays.asList(children));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(ResourceParser parser) {
    this.parser = parser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean supports(String namespaceUri, String localName) {
    return this.namespaceUri.equals(namespaceUri)
        && this.localName.equals(localName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void startElement(XMLStreamReader reader,
      String namespaceUri, String localName) 
          throws XMLStreamException {    
    for (AbstractResourceReaderWriter child : children) {
      if (child.supports(namespaceUri, localName)) {
        parser.push(child);
        child.handleAttributes(reader);
        return;
      }
    }
    
    parser.unexpectedElement(reader);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endElement(XMLStreamReader reader,
      String namespaceUri, String localName) 
      throws XMLStreamException {
    if (!supports(namespaceUri, localName)) {
      parser.unexpectedEndElement(reader);
    }
    parser.pop();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void characters(XMLStreamReader reader, String text)
      throws XMLStreamException {
    if (!text.trim().isEmpty()) {
      throw new XMLStreamException("unexpected text: " 
          + text, reader.getLocation());
    }
  }

  protected void handleAttributes(XMLStreamReader reader) 
      throws XMLStreamException {

    final String key = getResourceKey();
    final SimpleAttributeDefinition[] attributes = attributes();
    final Set<String> names = new HashSet<>();

    String resourceName = null;

    final ModelNode op = new ModelNode();
    op.get(ModelDescriptionConstants.OP).set(
        ModelDescriptionConstants.ADD);

    outer:    
    for (int i = 0, max = reader.getAttributeCount(); i < max; i++) {
      String localName = reader.getAttributeLocalName(i);
      String value = reader.getAttributeValue(i);
      if (key.equals(localName)) {
        resourceName = value;
        continue;
      }
      for (SimpleAttributeDefinition attribute : attributes) {
        if (attribute.getXmlName().equals(localName)) {
          names.add(localName);
          attribute.parseAndSetParameter(value, op, reader);
          continue outer;
        }
        
      }
      parser.unexpectedAttribute(reader, i);
    }
    
    final Set<String> missing = new HashSet<>();
    if (resourceName == null) {
      missing.add(key);
    }
    
    for (SimpleAttributeDefinition attribute : attributes) {
      if (!names.contains(attribute.getXmlName())
          && attribute.isRequired(op)) {
        missing.add(attribute.getXmlName());
      }
    }
    
    if (!missing.isEmpty()) {
      parser.missingAttributes(reader, missing);
    }
    
    parser.addOperation(op, getResourceType(), resourceName);

  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void writeResource(XMLExtendedStreamWriter writer, ModelNode node)
      throws XMLStreamException {
    
    final String resourceType = getResourceType();

    if (!node.has(resourceType)) return;
    
    ModelNode name = node.get(resourceType);
    for (Property property : name.asPropertyList()) {
      writer.writeStartElement(localName);
      writer.writeAttribute(getResourceKey(), property.getName());
      ModelNode resourceModel = property.getValue();
      for (SimpleAttributeDefinition attribute : attributes()) {
        attribute.marshallAsAttribute(resourceModel, writer);
      }
      writeChildResources(writer, resourceModel);
      writer.writeEndElement();
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void writeChildResources(XMLExtendedStreamWriter writer,
      ModelNode node) throws XMLStreamException {
    for (AbstractResourceReaderWriter child : children) {
      child.writeResource(writer, node);
    }
  }
  
  /**
   * Gets the key used to name this resource in the configuration model.
   * @return {@link Names#NAME}; subclasses may override to return a different
   *    key
   */
  protected String getResourceKey() {
    return Names.NAME;
  }

  /**
   * Gets the resource type name for this resource in the configuration model.
   * @return the local name used in the XML configuration model; subclasses
   *    may override to use a different resource type name
   */
  protected String getResourceType() {
    return localName;
  }

  /**
   * Gets the simple attributes enclosed in this resource.
   * @return empty list; subclasses should override to provide resource-specific
   *    attributes
   */
  protected SimpleAttributeDefinition[] attributes() {
    return new SimpleAttributeDefinition[0];
  }
  
}
