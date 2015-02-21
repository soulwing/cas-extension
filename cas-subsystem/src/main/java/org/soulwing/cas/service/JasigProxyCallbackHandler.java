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

import static org.soulwing.cas.service.ServiceLogger.LOGGER;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.util.CommonUtils;

/**
 * A {@link ProxyCallbackHandler} that delegates to a JASIG
 * {@link ProxyGrantingTicketStorage} instance.
 *
 * @author Carl Harris
 */
public class JasigProxyCallbackHandler implements ProxyCallbackHandler {

  static final String PGT_IOU_PARAM = "pgtIou";
  static final String PGT_PARAM = "pgtId";

  private final ProxyGrantingTicketStorage storage =
      new ProxyGrantingTicketStorageImpl();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public ProxyCallbackResponse onProxyCallback(String query) {
    String pgtIou = QueryUtil.findParameter(PGT_IOU_PARAM, query);
    String pgt = QueryUtil.findParameter(PGT_PARAM, query);
    boolean ok = !CommonUtils.isBlank(pgtIou) && !CommonUtils.isBlank(pgt);
    if (!ok) {
      LOGGER.debug("proxy callback missing required parameters: query='"
          + query + "'");
      return new ProxyCallbackResponse();
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("proxy callback for IOU " + pgtIou);
    }
    storage.save(pgtIou, pgt);
    return new ProxyCallbackResponse();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getStorage() {
    return storage;
  }

}
