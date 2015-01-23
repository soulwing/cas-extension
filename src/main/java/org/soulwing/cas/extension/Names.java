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

/**
 * Configuration names.
 *
 * @author Carl Harris
 */
public interface Names {

  static final String SUBSYSTEM_NAME = "cas";
  
  static final int VERSION_MAJOR = 1;

  static final int VERSION_MINOR = 0;

  String NAMESPACE = String.format("urn:soulwing.org:%s:%d.%d",
      SUBSYSTEM_NAME, VERSION_MAJOR, VERSION_MINOR);  

  String NAME = "name";
  String URL = "url";
  
  String AUTHENTICATION = "authentication";
  String PROTOCOL = "protocol";
  String SERVICE_URL = "service-url";
  String SERVER_URL = "server-url";
  String PROXY_CALLBACK_URL = "proxy-callback-url";
  String ACCEPT_ANY_PROXY = "accept-any-proxy";
  String ALLOW_EMPTY_PROXY_CHAIN = "allow-empty-proxy-chain";
  String RENEW = "renew";
  String PROXY_CHAIN = "allowed-proxy-chain";  
  String PROXIES = "proxies";
  String PROXY = "proxy";
  
  String AUTHORIZATION = "authorization";
  String LDAP = "ldap";
  String PROPERTIES = "properties";
  String USER_SEARCH = "user-search";
  String GROUP_SEARCH = "group-search";
  String BASE = "base";
  String FILTER = "filter";
  String SCOPE = "scope";
  String ROLE_ATTRIBUTE = "role-attribute";
  String ROLE_ATTRIBUTES = "role-attributes";
  String USER_MEMBER_TYPE = "user-member-type";
  String USER_MEMBER_ATTRIBUTE = "user-member-attribute";
  String GROUP_MEMBER_TYPE = "group-member-type";
  String GROUP_MEMBER_ATTRIBUTE = "group-member-attribute";
  String PATH = "path";
  String RELATIVE_TO = "relative-to";

  String KEYSTORE = "keystore";

}
