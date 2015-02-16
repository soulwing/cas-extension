/*
 * File created on Feb 16, 2015 
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

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.domain.management.SecurityRealm;
import org.jboss.as.domain.management.security.SSLContextService;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.AbstractService;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.value.InjectedValue;

/**
 * A service that provides an SSLContext injected from a security realm.
 * <p>
 *
 * @author Carl Harris
 */
public class WrapperSSLContextService extends AbstractService<SSLContext> {

  private final String realmName;

  private final InjectedValue<SSLContext> sslContext = new InjectedValue<>();
  
  /**
   * Constructs a new instance.
   * @param realmName
   */
  public WrapperSSLContextService(String realmName) {
    this.realmName = realmName;
  }
  
  public Injector<SSLContext> getSslContextInjector() {
    return sslContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSLContext getValue() throws IllegalStateException {
    if (realmName == null) return null;
    return sslContext.getValue();
  }

  public static class ServiceUtil {
    
    /**
     * Creates a service name for the SSL context service based on the
     * address of the CAS profile resource.
     * @param profileAddress address of the profile resource
     * @return service name for the profile's SSL context
     */
    public static ServiceName createServiceName(PathAddress profileAddress) {
      return createServiceName(
          ProfileService.ServiceUtil.profileServiceName(profileAddress));
    }
    
    /**
     * Creates a service name for the SSL context service based on the 
     * service name of the parent profile service.
     * @param parent parent profile service
     * @return service name for the profile's SSL context
     */
    public static ServiceName createServiceName(ServiceName parent) {
      return ServiceName.of(parent, Names.SSL_CONTEXT);      
    }
    
    /**
     * Installs the SSL context service.
     * @param context operation context
     * @param profileAddress address of the parent profile resource
     * @param realmName security realm from which the context will be injected
     *   (or {@code null}) to indicate use of the default SSL context
     * @return
     */
    public static ServiceController<?> installService(OperationContext context,
        PathAddress profileAddress, String realmName) {
      
      ServiceName serviceName = createServiceName(profileAddress);
      WrapperSSLContextService service = new WrapperSSLContextService(realmName);
      ServiceBuilder<?> builder = context.getServiceTarget()
            .addService(serviceName, service);
      
      if (realmName != null) {
        SSLContextService.ServiceUtil.addDependency(builder, 
            service.getSslContextInjector(), 
            SecurityRealm.ServiceUtil.createServiceName(realmName),
            false);
      }

      ServiceController<?> controller = builder.install();

      LOGGER.debug("installed " + serviceName +
          (realmName != null ? "for realm " + realmName : " using default context"));

      return controller;
    }

    /**
     * Removes the SSL context service.
     * @param context operation context
     * @param profileAddress address of the parent profile resource
     */
    public static void removeService(OperationContext context, 
        PathAddress profileAddress) {
      ServiceName serviceName = createServiceName(profileAddress);
      context.removeService(serviceName);
      LOGGER.debug("removed " + serviceName);
    }
    
  }

}
