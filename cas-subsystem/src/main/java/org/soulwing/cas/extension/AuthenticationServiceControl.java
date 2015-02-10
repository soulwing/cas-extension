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
package org.soulwing.cas.extension;

import java.util.List;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.msc.service.AbstractService;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.soulwing.cas.service.AuthenticationService;

/**
 * A JBoss MSC service that manages an {@link AuthenticationService}.
 *
 * @author Carl Harris
 */
public class AuthenticationServiceControl 
    extends AbstractService<AuthenticationService> {

  private final AuthenticationService delegate;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public AuthenticationServiceControl(AuthenticationService delegate) {
    this.delegate = delegate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public AuthenticationService getValue() throws IllegalStateException {
    return delegate;
  }

  public static AuthenticationService locateService(OperationContext context, 
      ModelNode address) throws OperationFailedException{
    ServiceController<?> controller = context.getServiceRegistry(true)
        .getRequiredService(name(address));    
    return (AuthenticationService) 
        controller.getService().getValue();
  }
  
  public static ServiceName name(ModelNode address) {    
    return name(profileName(address));
  }

  public static String profileName(ModelNode address) {
    List<Property> names = address.asPropertyList();
    if (!ModelDescriptionConstants.SUBSYSTEM.equals(names.get(0).getName())) {
      throw new IllegalArgumentException("address not of subsystem type");
    }
    if (!Names.SUBSYSTEM_NAME.equals(names.get(0).getValue().asString())) {
      throw new IllegalArgumentException(address + " is not an address in the "
          + Names.SUBSYSTEM_NAME + " subsystem");
    }
    if (!Names.PROFILE.equals(names.get(1).getName())) {
      throw new IllegalArgumentException("address not of profile type");      
    }
    return names.get(1).getValue().asString();
  }

  public static ServiceName name(String profileName) {
    return ServiceName.of(Names.SUBSYSTEM_NAME, Names.PROFILE, profileName);
  }
  
}
