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
import static org.hamcrest.Matchers.instanceOf;

import java.util.Collections;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link AuthenticationFactory}.
 *
 * @author Carl Harris
 */
public class AuthenticatorFactoryTest {

  private static final String ENCODING = "someEncoding";
  private static final String SERVER_URL = "someServerURL";
  private static final String PROXY_CALLBACK_URL = "someProxyCallbackURL";
  private static final boolean RENEW = true;
  private static final long CLOCK_SKEW_TOLERANCE = -1L;
  private static final String[] PROXY_CHAIN = { "someProxyURL" };
  
  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Configuration config;

  @Mock
  private HostnameVerifier hostnameVerifier;
  
  @Mock
  private ProxyCallbackHandler proxyCallbackHandler;
  
  @Mock
  private ProxyGrantingTicketStorage storage;
  
  private SSLContext sslContext;
  
  @Before
  public void setUp() throws Exception {
    sslContext = SSLContext.getDefault(); 
  }

  @Test
  public void testNewCas1Validator() throws Exception {
    context.checking(commonConfigExpectations());
    context.checking(new Expectations() {
      {
        allowing(config).getProtocol();
        will(returnValue(AuthenticationProtocol.CAS1_0));
      }
    });
    
    JasigAuthenticator authenticator = (JasigAuthenticator)
        AuthenticatorFactory.newInstance(config, PROXY_CALLBACK_URL, proxyCallbackHandler);
    
    assertThat(authenticator.getValidator(), 
        instanceOf(Cas10TicketValidator.class));
  }

  @Test
  public void testNewCas2ServiceValidator() throws Exception {
    context.checking(commonConfigExpectations());
    context.checking(proxyTicketStorageExpectations());
    context.checking(new Expectations() {
      {
        allowing(config).getProtocol();
        will(returnValue(AuthenticationProtocol.CAS2_0));
        allowing(config).isAcceptAnyProxy();
        will(returnValue(false));
        allowing(config).isAllowEmptyProxyChain();
        will(returnValue(false));
        allowing(config).getAllowedProxyChains();
        will(returnValue(Collections.emptyList()));        
      }
    });
    
    JasigAuthenticator authenticator = (JasigAuthenticator)
        AuthenticatorFactory.newInstance(config, PROXY_CALLBACK_URL, proxyCallbackHandler);
    
    assertThat(authenticator.getValidator(), 
        instanceOf(Cas20ServiceTicketValidator.class));
  }

  @Test
  public void testNewCas2ProxyValidatorForAnyProxy() throws Exception {
    context.checking(commonConfigExpectations());
    context.checking(proxyTicketStorageExpectations());
    context.checking(new Expectations() {
      {
        allowing(config).getProtocol();
        will(returnValue(AuthenticationProtocol.CAS2_0));
        allowing(config).isAcceptAnyProxy();
        will(returnValue(true));
        atLeast(1).of(config).isAllowEmptyProxyChain();
        will(returnValue(true));
        atLeast(1).of(config).getAllowedProxyChains();
        will(returnValue(Collections.emptyList()));        
      }
    });
    
    JasigAuthenticator authenticator = (JasigAuthenticator)
        AuthenticatorFactory.newInstance(config, PROXY_CALLBACK_URL, proxyCallbackHandler);
    
    assertThat(authenticator.getValidator(), 
        instanceOf(Cas20ProxyTicketValidator.class));
  }

  @Test
  public void testNewCas2ProxyValidatorForSpecifiedProxyChains() 
      throws Exception {
    context.checking(commonConfigExpectations());
    context.checking(proxyTicketStorageExpectations());
    context.checking(new Expectations() {
      {
        allowing(config).getProtocol();
        will(returnValue(AuthenticationProtocol.CAS2_0));
        allowing(config).isAcceptAnyProxy();
        will(returnValue(false));
        atLeast(1).of(config).isAllowEmptyProxyChain();
        will(returnValue(true));
        atLeast(1).of(config).getAllowedProxyChains();
        will(returnValue(Collections.singletonList(PROXY_CHAIN)));        
      }
    });
    
    JasigAuthenticator authenticator = (JasigAuthenticator)
        AuthenticatorFactory.newInstance(config, PROXY_CALLBACK_URL, proxyCallbackHandler);
    
    assertThat(authenticator.getValidator(), 
        instanceOf(Cas20ProxyTicketValidator.class));
  }

  @Test
  public void testNewSaml11Validator() throws Exception {
    context.checking(commonConfigExpectations());
    context.checking(new Expectations() {
      {
        allowing(config).getProtocol();
        will(returnValue(AuthenticationProtocol.SAML1_1));
        atLeast(1).of(config).getClockSkewTolerance();
        will(returnValue(CLOCK_SKEW_TOLERANCE));
      }
    });
    
    JasigAuthenticator authenticator = (JasigAuthenticator)
        AuthenticatorFactory.newInstance(config, PROXY_CALLBACK_URL, proxyCallbackHandler);
    
    assertThat(authenticator.getValidator(), 
        instanceOf(Saml11TicketValidator.class));
  }
  
  private Expectations commonConfigExpectations() throws Exception {
    return new Expectations() {
      {
        atLeast(1).of(config).getEncoding();
        will(returnValue(ENCODING));
        atLeast(1).of(config).getServerUrl();
        will(returnValue(SERVER_URL));
        atLeast(1).of(config).isRenew();
        will(returnValue(RENEW));
        atLeast(1).of(config).getSslContext();
        will(returnValue(sslContext));
        atLeast(1).of(config).getHostnameVerifier();
        will(returnValue(hostnameVerifier));
      }
    };
  }

  private Expectations proxyTicketStorageExpectations() throws Exception {
    return new Expectations() {
      {
        oneOf(proxyCallbackHandler).getStorage();
        will(returnValue(storage));
      }
    };
  }
}
