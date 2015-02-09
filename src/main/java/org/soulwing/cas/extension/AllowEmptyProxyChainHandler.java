/*
 * File created on Dec 15, 2014 
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

import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.soulwing.cas.service.MutableConfiguration;

/** 
 * A write attribute handler for the allow empty proxy chain attribute.
 *
 * @author Carl Harris
 */
class AllowEmptyProxyChainHandler 
    extends AbstractProfileAttributeHandler<Void> {

  static final AllowEmptyProxyChainHandler INSTANCE =
      new AllowEmptyProxyChainHandler();
  
  private AllowEmptyProxyChainHandler() {
    super(ProfileDefinition.ALLOW_EMPTY_PROXY_CHAIN);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected Void applyUpdateToConfiguration(String attributeName,
      ModelNode value, MutableConfiguration config)
      throws OperationFailedException {
    config.setAllowEmptyProxyChain(value.resolve().asBoolean());
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void revertUpdateToConfiguration(String attributeName,
      ModelNode value, MutableConfiguration config, Void handback)
      throws OperationFailedException {
    config.setAllowEmptyProxyChain(value.resolve().asBoolean());
  }

}
