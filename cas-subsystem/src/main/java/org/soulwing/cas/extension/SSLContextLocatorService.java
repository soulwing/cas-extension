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
package org.soulwing.cas.extension;

import static org.soulwing.cas.extension.ExtensionLogger.LOGGER;

import javax.net.ssl.SSLContext;

import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.AbstractService;
import org.jboss.msc.value.InjectedValue;
import org.soulwing.cas.service.SSLContextLocator;

/**
 * An {@link SSLContextLocator} that obtains an {@link SSLContext} from
 * a specified security realm.
 *
 * @author Carl Harris
 */
public class SSLContextLocatorService 
    extends AbstractService<SSLContextLocator>
    implements SSLContextLocator {

  private final InjectedValue<SSLContext> context = new InjectedValue<>();
  
  public Injector<SSLContext> getContextInjector() {
    return context;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public SSLContext getSSLContext() {
    SSLContext context = this.context.getOptionalValue();
    if (context == null) {
      LOGGER.warn("SSL context not available");
    }
    return context;
  }

}
