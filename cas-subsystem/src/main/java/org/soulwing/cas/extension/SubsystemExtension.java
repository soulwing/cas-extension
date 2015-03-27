/*
 * File created on Dec 15, 2014 
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

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ManagementResourceRegistration;


/**
 * A Wildfly extension that provides a CAS authentication mechanism to the
 * Undertow subsystem.
 *
 * @author Carl Harris
 */
public class SubsystemExtension implements Extension {

  private final SubsystemReaderWriter delegate = new SubsystemReaderWriter();
  private final SubsystemParser parser = new SubsystemParser(delegate);  
  private final SubsystemWriter writer = new SubsystemWriter(delegate);

  @Override
  public void initializeParsers(ExtensionParsingContext context) {
    context.setSubsystemXmlMapping(Names.SUBSYSTEM_NAME,
        Names.NAMESPACE, parser);
  }

  @Override
  public void initialize(ExtensionContext context) {
    final SubsystemRegistration subsystem =
        context.registerSubsystem(Names.SUBSYSTEM_NAME, 
            Names.VERSION_MAJOR, 
            Names.VERSION_MINOR);
    
    final ManagementResourceRegistration registration = 
        subsystem.registerSubsystemModel(SubsystemDefinition.INSTANCE);
    
    registration.registerSubModel(ProfileDefinition.INSTANCE);
    subsystem.registerXMLElementWriter(writer);
  }

}
