/*
 * File created on Feb 18, 2015 
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.cas.api.IdentityAssertion;
import org.soulwing.cas.api.Transformer;

/**
 * Unit tests for {@link JasigAuthenticator}.
 *
 * @author Carl Harris
 */
public class JasigAuthenticatorTest {

  private static final String APPLICATION_URL = "https://application";

  private static final String TICKET = "someTicket";

  private static final String REQUEST_PATH = "/app/path";

  private static final AuthenticationProtocol PROTOCOL = AuthenticationProtocol.CAS2_0;

  private static final String SERVER_URL = "https://server";

  private static final String SERVICE_URL = "https://service";

  private static final Map<String, Transformer<Object, Object>> TRANSFORMERS =
      new HashMap<>();
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Configuration config;
  
  @Mock
  private TicketValidator validator;
  
  @Mock
  private Assertion assertion;
  
  @Mock
  private AttributePrincipal principal;
  
  private JasigAuthenticator authenticator;
  
  @Before
  public void setUp() throws Exception {
    authenticator = new JasigAuthenticator(config, validator);
  }

  @Test(expected = NoTicketException.class)
  public void testValidateTicketWhenNoTicket() throws Exception {
    context.checking(commonConfigExpectations());
    authenticator.validateTicket(REQUEST_PATH, "");
  }
  
  @Test(expected = AuthenticationException.class)
  public void testValidateTicketWhenInvalidTicket() throws Exception {
    context.checking(commonConfigExpectations());
    context.checking(new Expectations() {
      {
        oneOf(validator).validate(TICKET, SERVICE_URL + REQUEST_PATH);
        will(throwException(new TicketValidationException("some message")));
      }
    });
    authenticator.validateTicket(REQUEST_PATH, 
        PROTOCOL.getTicketParameterName() + "=" + TICKET);
  }

  @Test
  public void testValidateTicketWhenValidTicket() throws Exception {
    context.checking(commonConfigExpectations());
    context.checking(new Expectations() {
      {
        allowing(assertion).getPrincipal();
        will(returnValue(principal));
        allowing(assertion).getAttributes();
        will(returnValue(Collections.emptyMap()));
        allowing(principal).getAttributes();
        will(returnValue(Collections.emptyMap()));
        oneOf(validator).validate(TICKET, SERVICE_URL + REQUEST_PATH);
        will(returnValue(assertion));
      }
    });
    
    IdentityAssertion identityAssertion = authenticator.validateTicket(
        REQUEST_PATH, PROTOCOL.getTicketParameterName() + "=" + TICKET);
    
    assertThat(identityAssertion.getDelegate(),
        is(sameInstance((Object) assertion)));
    
    assertThat(identityAssertion.getPrincipal().getDelegate(),
        is(sameInstance((Object) principal)));
  }

  @Test
  public void testPostAuthUrl() throws Exception {
    context.checking(commonConfigExpectations());
    assertThat(authenticator.postAuthUrl(APPLICATION_URL,
            "name=value&" + PROTOCOL.getServiceParameterName() + "=someServiceURL&"
                + PROTOCOL.getTicketParameterName() + "=" + TICKET),
        is(equalTo(APPLICATION_URL + "?name=value")));
  }
  
  @Test
  public void testServiceUrlWithEmptyQuery() throws Exception {
    context.checking(commonConfigExpectations());
    assertThat(authenticator.serviceUrl(REQUEST_PATH, ""),
        is(equalTo(SERVICE_URL + REQUEST_PATH)));
  }

  @Test
  public void testServiceUrlWithNonEmptyQuery() throws Exception {
    context.checking(commonConfigExpectations());
    assertThat(authenticator.serviceUrl(REQUEST_PATH, "name=value"),
        is(equalTo(SERVICE_URL + REQUEST_PATH + "?name=value")));
  }

  @Test
  public void testServiceUrlWithQueryContainingProtocolParam() throws Exception {
    context.checking(commonConfigExpectations());
    assertThat(authenticator.serviceUrl(REQUEST_PATH, 
        PROTOCOL.getTicketParameterName() + "=" + TICKET),
        is(equalTo(SERVICE_URL + REQUEST_PATH)));
  }

  @Test
  public void testServiceUrlWithQueryContainingQueryWithProtocolSuffix() 
      throws Exception {
    context.checking(commonConfigExpectations());
    assertThat(authenticator.serviceUrl(REQUEST_PATH, "name=value&" +
        PROTOCOL.getTicketParameterName() + "=" + TICKET),
        is(equalTo(SERVICE_URL + REQUEST_PATH + "?name=value")));
  }

  @Test
  public void testServiceUrlWithQueryContainingQueryWithProtocolPrefix() 
      throws Exception {
    context.checking(commonConfigExpectations());
    assertThat(authenticator.serviceUrl(REQUEST_PATH,
        PROTOCOL.getTicketParameterName() + "=" + TICKET
        + "&name=value"),
        is(equalTo(SERVICE_URL + REQUEST_PATH + "?name=value")));
  }

  private Expectations commonConfigExpectations() throws Exception {
    return new Expectations() {
      {
        allowing(config).getProtocol();
        will(returnValue(PROTOCOL));
        allowing(config).getServiceUrl();
        will(returnValue(SERVICE_URL));
        allowing(config).getAttributeTransformers();
        will(returnValue(TRANSFORMERS));
      }
    };
  }

  @Test
  public void testLogoutUrlWithNoPath() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(config).getServerUrl();
        will(returnValue(SERVER_URL));
        allowing(config).getServiceUrl();
        will(returnValue(SERVICE_URL));
      }
    });

    assertThat(authenticator.logoutUrl(null),
        is(equalTo(SERVER_URL + "/logout")));
  }

  @Test
  public void testLogoutUrlWithAbsolutePath() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(config).getServerUrl();
        will(returnValue(SERVER_URL));
        allowing(config).getServiceUrl();
        will(returnValue(SERVICE_URL));
      }
    });

    assertThat(authenticator.logoutUrl("/application/path"),
        is(equalTo(SERVER_URL + "/logout?url="
          + CommonUtils.urlEncode(SERVICE_URL + "/application/path"))));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLogoutUrlWithRelativePath() throws Exception {
    context.checking(new Expectations() {
      {
        allowing(config).getServerUrl();
        will(returnValue(SERVER_URL));
        allowing(config).getServiceUrl();
        will(returnValue(SERVICE_URL));
      }
    });

    authenticator.logoutUrl("relative/path");
  }

}
