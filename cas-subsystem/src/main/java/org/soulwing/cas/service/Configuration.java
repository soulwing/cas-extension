/*
 * File created on Dec 24, 2014 
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

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * An (immutable) configuration for an {@link AuthenticationService}.
 *
 * @author Carl Harris
 */
public interface Configuration {

  SSLContext getSslContext();
  
  HostnameVerifier getHostnameVerifier();
  
  AuthenticationProtocol getProtocol();  

  String getServerUrl();
  
  String getServiceUrl();
  
  String getProxyCallbackUrl();
  
  boolean isAcceptAnyProxy();
  
  boolean isAllowEmptyProxyChain();
  
  List<String[]> getAllowedProxyChains();
  
  boolean isRenew();
  
  long getClockSkewTolerance();
  
  boolean isPostAuthRedirect();
  
}
