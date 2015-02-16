/*
 * File created on Feb 12, 2015 
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
package org.soulwing.cas.ssl;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.jasig.cas.client.ssl.HttpURLConnectionFactory;

/**
 * An {@link HttpURLConnectionFactory} for HTTPS that locates the appropriate 
 * {@link SSLContext} using a {@link SSLContextLocator}.
 *
 * @author Carl Harris
 */
public class HttpsURLConnectionFactory 
    implements HttpURLConnectionFactory, Serializable {
  
  private static final long serialVersionUID = 1192037344009446034L;

  private final SSLContext sslContext;
  
  private final HostnameVerifier hostnameVerifier; 
  
  /**
   * Constructs a new instance.
   * @param sslContext
   * @param hostnameVerifier
   */
  public HttpsURLConnectionFactory(SSLContext sslContext,
      HostnameVerifier hostnameVerifier) {
    this.sslContext = sslContext;
    this.hostnameVerifier = hostnameVerifier;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpURLConnection buildHttpURLConnection(URLConnection url) {
    if (url instanceof HttpsURLConnection) {
      HttpsURLConnection connection = (HttpsURLConnection) url;
      
      if (hostnameVerifier != null) {
        connection.setHostnameVerifier(hostnameVerifier);
      }
      
      if (sslContext != null) {
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        connection.setSSLSocketFactory(socketFactory);
      }
      
    }
    
    return (HttpURLConnection) url;
  }

}
