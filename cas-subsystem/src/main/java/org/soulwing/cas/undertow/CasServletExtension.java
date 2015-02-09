/*
 * File created on June 11, 2014 
 *
 * Copyright 2007-2014 Carl Harris, Jr.
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
package org.soulwing.cas.undertow;

import static org.soulwing.cas.undertow.UndertowLogger.LOGGER;
import io.undertow.servlet.ServletExtension;
import io.undertow.servlet.api.DeploymentInfo;

import javax.servlet.ServletContext;

import org.soulwing.cas.service.AuthenticationService;

public class CasServletExtension implements ServletExtension {

  private final AuthenticationService authenticationService;
  
  /**
   * Constructs a new instance.
   * @param authenticationService
   * @param authorizationService
   */
  public CasServletExtension(AuthenticationService authenticationService) { 
    this.authenticationService = authenticationService;
  }

  @Override
  public void handleDeployment(DeploymentInfo deploymentInfo, 
      ServletContext servletContext) {
    
    CasAuthenticationMechanism authnMechanism = 
        new CasAuthenticationMechanism(authenticationService);
    
    deploymentInfo.clearLoginMethods();
    deploymentInfo.addFirstAuthenticationMechanism(
        CasAuthenticationMechanism.MECHANISM_NAME, authnMechanism);
    
    LOGGER.info("enabled CAS authentication profile '" 
        + authenticationService.getName() 
        + "' for deployment " + deploymentInfo.getDeploymentName());
  }

}
