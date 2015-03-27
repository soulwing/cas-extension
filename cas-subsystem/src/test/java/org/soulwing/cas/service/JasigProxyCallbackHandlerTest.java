/*
 * File created on Feb 21, 2015 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link JasigProxyCallbackHandler}.
 *
 * @author Carl Harris
 */
public class JasigProxyCallbackHandlerTest {

  private static final String IOU_VALUE = 
      JasigProxyCallbackHandler.PGT_IOU_PARAM + "=" + "someIOU";

  private static final String TICKET_VALUE = 
      JasigProxyCallbackHandler.PGT_PARAM + "=" + "someTicket";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private ProxyGrantingTicketStorage storage;
  
  private JasigProxyCallbackHandler handler =
      new JasigProxyCallbackHandler();
  
  @Test
  public void testWithoutIou() throws Exception {
    assertThat(handler.onProxyCallback(TICKET_VALUE), 
        hasProperty("status", is(200)));
  }

  @Test
  public void testWithoutGrantingTicket() throws Exception {
    assertThat(handler.onProxyCallback(IOU_VALUE), 
        hasProperty("status", is(200)));
  }

  @Test
  public void testSuccess() throws Exception {
    assertThat(handler.onProxyCallback(IOU_VALUE + "&" + TICKET_VALUE), 
        hasProperty("status", is(200)));
  }


}
