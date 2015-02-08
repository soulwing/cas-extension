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

import org.jboss.as.controller.SimpleAttributeDefinition;

/**
 * A reader/writer for the configuration associate with an authentication 
 * resource.
 *
 * @author Carl Harris
 */
public class AuthenticationReaderWriter extends AbstractResourceReaderWriter {
  
  /**
   * Constructs a new instance.
   */
  public AuthenticationReaderWriter() {
    super(Names.AUTHENTICATION, new ProxyChainReaderWriter());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SimpleAttributeDefinition[] attributes() {
    return new SimpleAttributeDefinition[] { 
        AuthenticationDefinition.PROTOCOL,
        AuthenticationDefinition.SERVICE_URL,
        AuthenticationDefinition.SERVER_URL,
        AuthenticationDefinition.PROXY_CALLBACK_URL,
        AuthenticationDefinition.ACCEPT_ANY_PROXY,
        AuthenticationDefinition.ALLOW_EMPTY_PROXY_CHAIN,
        AuthenticationDefinition.RENEW,
        AuthenticationDefinition.CLOCK_SKEW_TOLERANCE
    };
  }
  
}
