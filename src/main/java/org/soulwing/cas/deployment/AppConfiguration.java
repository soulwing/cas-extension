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

/**
 * An object that provides the configuration obtained from an application's
 * CAS descriptor.
 *
 * @author Carl Harris
 */
class AppConfiguration {

  private String authenticationId;
  
  private String authorizationId;

  /**
   * Gets the identifier of the CAS authentication resource.
   * @return authentication resource specified in the deployment descriptor
   */
  public String getAuthenticationId() {
    return authenticationId;
  }

  /**
   * Sets the identifier for the CAS authentication resource.
   * @param authenticationId the value to set
   */
  public void setAuthenticationId(String authenticationId) {
    this.authenticationId = authenticationId;
  }

  /**
   * Gets the identifier of the CAS authorization resource.
   * @return authorization resource specified in the deployment descriptor
   */
  public String getAuthorizationId() {
    return authorizationId;
  }

  /**
   * Sets the identifier for the CAS authentication resource.
   * @param authenticationId the value to set
   */
  public void setAuthorizationId(String authorizationId) {
    this.authorizationId = authorizationId;
  }  
  
}
