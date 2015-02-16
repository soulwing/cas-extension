/*
 * File created on Feb 15, 2015 
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

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.registry.Resource;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.msc.service.AbstractService;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.soulwing.cas.service.AuthenticationProtocol;
import org.soulwing.cas.service.Configuration;

/**
 * A service that holds a {@link Configuration}.
 *
 * @author Carl Harris
 */
public class ProfileService extends AbstractService<Profile> {

  private final Profile profile;
  
  /**
   * Constructs a new instance.
   * @param profile
   */
  public ProfileService(Profile profile) {
    this.profile = profile;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Profile getValue() throws IllegalStateException {
    return profile;
  }

  public static class ServiceUtil {
    
    public static ServiceName profileServiceName(PathAddress profileAddress) {
      return profileServiceName(profileName(profileAddress));
    }
    
    public static ServiceName profileServiceName(String profileName) {
      return ServiceName.of(Names.SUBSYSTEM_NAME, Names.PROFILE, profileName);
    }

    public static ServiceController<?> installService(
        OperationContext context, ModelNode model, PathAddress profileAddress) 
        throws OperationFailedException {
      return installService(context, model, profileName(profileAddress));
    }

    private static String profileName(PathAddress profileAddress) {
      PathElement pathElement = profileAddress.getLastElement();
      String key = pathElement.getKey();
      if (!key.equals(Names.PROFILE)) {
        throw new IllegalArgumentException("not a profile address");
      }
      return pathElement.getValue();
    }
    
    public static ServiceController<?> installService(
        OperationContext context, ModelNode model, String profileName) 
        throws OperationFailedException {
      
      ProfileService profileService = new ProfileService(
          createProfile(context, model));
      
      ServiceController<?> controller = context.getServiceTarget()
          .addService(profileServiceName(profileName), profileService)
          .install();

      LOGGER.debug("installed " + controller.getName());
      return controller;
    }

    public static void removeService(OperationContext context, 
        PathAddress profileAddress) throws OperationFailedException {
      removeService(context, profileName(profileAddress));
    }

    public static void removeService(OperationContext context, 
        String profileName) throws OperationFailedException {
      ServiceName serviceName = profileServiceName(profileName);      
      context.removeService(serviceName);
      LOGGER.debug("removed " + serviceName);
    }
    
    private static Profile createProfile(OperationContext context,
        ModelNode model) throws OperationFailedException {
     
      Profile config = new Profile();
      config.setProtocol(AuthenticationProtocol.toObject(
          ProfileDefinition.PROTOCOL.resolveModelAttribute(context, model)
              .asString()));
      config.setServerUrl(ProfileDefinition.SERVER_URL
          .resolveModelAttribute(context, model).asString());
      config.setServiceUrl(ProfileDefinition.SERVICE_URL
          .resolveModelAttribute(context, model).asString());
      config.setProxyCallbackUrl(ProfileDefinition.PROXY_CALLBACK_URL
          .resolveModelAttribute(context, model).asString());
      config.setAcceptAnyProxy(ProfileDefinition.ACCEPT_ANY_PROXY
          .resolveModelAttribute(context, model).asBoolean());
      config.setAllowEmptyProxyChain(ProfileDefinition.ALLOW_EMPTY_PROXY_CHAIN
          .resolveModelAttribute(context, model).asBoolean());
      config.setRenew(ProfileDefinition.RENEW
          .resolveModelAttribute(context, model).asBoolean());
      config.setClockSkewTolerance(ProfileDefinition.CLOCK_SKEW_TOLERANCE
          .resolveModelAttribute(context, model).asLong());
      config.setPostAuthRedirect(ProfileDefinition.POST_AUTH_REDIRECT
          .resolveModelAttribute(context, model).asBoolean());
      
      if (model.has(Names.PROXY_CHAIN)) {
        ModelNode chains = model.get(Names.PROXY_CHAIN);
        for (String name : chains.keys()) {
          List<String> proxies = new ArrayList<>();
          for (ModelNode proxy : chains.get(name).get(Names.PROXIES).asList()) {
            proxies.add(proxy.asString());
          }
          config.putAllowedProxyChain(name, proxies);
        }
      }

      LOGGER.debug(config);
      return config;
    }
    
  }

}
