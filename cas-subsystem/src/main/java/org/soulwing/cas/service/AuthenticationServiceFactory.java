/*
 * File created on Jan 23, 2015 
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
package org.soulwing.cas.service;

/**
 * A factory that produces {@link AuthenticationService} objects.
 *
 * @author Carl Harris
 */
public class AuthenticationServiceFactory {

  /**
   * Creates a new instance of {@link AuthenticationService}.
   * @param name name to assign to this service
   * @return
   */
  public static AuthenticationService newInstance(String name) {
    return new JasigAuthenticationService(name);
  }
  
}
