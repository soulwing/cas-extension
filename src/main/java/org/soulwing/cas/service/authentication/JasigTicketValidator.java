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
package org.soulwing.cas.service.authentication;

import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

/**
 * A {@link AuthenticationTicketValidator} that delegates to 
 * {@link TicketValidator}.
 *
 * @author Carl Harris
 */
class JasigTicketValidator implements AuthenticationTicketValidator {

  private final TicketValidator delegate;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public JasigTicketValidator(TicketValidator delegate) {
    this.delegate = delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IdentityAssertion validate(String ticket, String service)
      throws AuthenticationException {
    try {
      return new JasigIdentityAssertion(delegate.validate(ticket, service));
    }
    catch (TicketValidationException ex) {
      throw new AuthenticationException(ex.getMessage(), ex);
    }
  }

}
