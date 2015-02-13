/*
 * File created on Feb 13, 2015 
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link HttpsURLConnectionFactory}.
 *
 * @author Carl Harris
 */
public class HttpsURLConnectionFactoryTest {

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery() {
    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };
  
  @Mock
  private SSLContextLocator sslContextLocator;
  
  @Mock
  private HostnameVerifierLocator hostnameVerifierLocator;
  
  @Mock
  private HttpsURLConnection connection;
  
  
  @Mock
  private HostnameVerifier hostnameVerifier;
  
  private SSLContext sslContext;
  
  private HttpsURLConnectionFactory factory;
  
  @Before
  public void setUp() throws Exception {
    factory = new HttpsURLConnectionFactory(sslContextLocator, 
        hostnameVerifierLocator);
    sslContext = SSLContext.getDefault();
  }

  @Test
  public void testConfigureWithSSLContextAndHostnameVerifier() 
      throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(hostnameVerifierLocator).getHostnameVerifier();
        will(returnValue(hostnameVerifier));
        oneOf(sslContextLocator).getSSLContext();
        will(returnValue(sslContext));
        oneOf(connection).setHostnameVerifier(hostnameVerifier);
        oneOf(connection).setSSLSocketFactory(with(any(SSLSocketFactory.class)));
      }
    });
    
    factory.buildHttpURLConnection(connection);
  }

  @Test
  public void testConfigureWithoutSSLContext() 
      throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(hostnameVerifierLocator).getHostnameVerifier();
        will(returnValue(hostnameVerifier));
        oneOf(sslContextLocator).getSSLContext();
        will(returnValue(null));
        oneOf(connection).setHostnameVerifier(hostnameVerifier);
      }
    });
    
    factory.buildHttpURLConnection(connection);
  }

  @Test
  public void testConfigureWithoutHostnameVerifier() 
      throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(hostnameVerifierLocator).getHostnameVerifier();
        will(returnValue(null));
        oneOf(sslContextLocator).getSSLContext();
        will(returnValue(sslContext));
        oneOf(connection).setSSLSocketFactory(with(any(SSLSocketFactory.class)));
      }
    });
    
    factory.buildHttpURLConnection(connection);
  }

}
