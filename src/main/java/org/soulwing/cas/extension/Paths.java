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

import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;

/**
 * Resource path constants.
 *
 * @author Carl Harris
 */
public interface Paths {

  PathElement SUBSYSTEM  = 
      PathElement.pathElement(ModelDescriptionConstants.SUBSYSTEM, 
          Names.SUBSYSTEM_NAME);
  
  PathElement AUTHENTICATION = PathElement.pathElement(Names.AUTHENTICATION);
  PathElement PROXY_CHAIN = PathElement.pathElement(Names.PROXY_CHAIN);
  PathElement AUTHORIZATION = PathElement.pathElement(Names.AUTHORIZATION);
  PathElement SAML = PathElement.pathElement(Names.SAML);
  PathElement LDAP = PathElement.pathElement(Names.LDAP);
  PathElement PROPERTIES = PathElement.pathElement(Names.PROPERTIES);
  PathElement USER_SEARCH = PathElement.pathElement(Names.USER_SEARCH);
  PathElement GROUP_SEARCH = PathElement.pathElement(Names.GROUP_SEARCH);

}
