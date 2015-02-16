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
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.domain.management.SecurityRealm;
import org.jboss.as.domain.management.security.SSLContextService;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.AbstractService;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.soulwing.cas.service.AuthenticationProtocol;
import org.soulwing.cas.service.Configuration;
import org.soulwing.cas.ssl.HostnameVerifierFactory;
import org.soulwing.cas.ssl.HostnameVerifierType;

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

  public Injector<SSLContext> getSslContextInjector() {
    return profile.getSslContextInjector();
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

      Resource resource = context.readResourceFromRoot(profileAddress, true);
      
      Profile profile = createProfile(context, model);
      addAllowedProxyChains(resource, profile);
      addHostnameVerifier(resource, profile);
      
      ProfileService profileService = new ProfileService(profile);
      
      ServiceBuilder<Profile> builder = context.getServiceTarget()
          .addService(profileServiceName(profileAddress), profileService);

      if (profile.getSecurityRealm() != null) {
        SSLContextService.ServiceUtil.addDependency(builder, 
            profileService.getSslContextInjector(), 
            SecurityRealm.ServiceUtil.createServiceName(
                profile.getSecurityRealm()), false);
      }
      
      ServiceController<?> controller = builder.install();

      LOGGER.debug("installed " + controller.getName() + " with profile "
          + profile);

      return controller;

    }

    private static String profileName(PathAddress profileAddress) {
      PathElement pathElement = profileAddress.getLastElement();
      String key = pathElement.getKey();
      if (!key.equals(Names.PROFILE)) {
        throw new IllegalArgumentException("not a profile address");
      }
      return pathElement.getValue();
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
      
      ModelNode securityRealm = ProfileDefinition.SECURITY_REALM
          .resolveModelAttribute(context, model);
      if (securityRealm.isDefined()) {
        config.setSecurityRealm(securityRealm.asString());
      }
            
      return config;
    }
    
    private static void addAllowedProxyChains(Resource profileResource,
        Profile config) {
      for (String name : profileResource.getChildrenNames(Names.PROXY_CHAIN)) {
        ModelNode chain = profileResource.getChild(
            PathElement.pathElement(Names.PROXY_CHAIN, name)).getModel(); 
        List<String> proxies = new ArrayList<>();
        if (chain.has(Names.PROXIES)) {
          for (ModelNode proxy : chain.get(Names.PROXIES).asList()) {
            proxies.add(proxy.asString());
          }
        }
        config.putAllowedProxyChain(name, proxies);        
      }
    }
    
    private static void addHostnameVerifier(Resource profileResource,
        Profile config) throws OperationFailedException {
      Set<String> names = profileResource.getChildrenNames(
          Names.HOSTNAME_VERIFIER);
      if (names.size() > 1) {
        throw new OperationFailedException("too many server host verifiers");
      }
      
      if (names.isEmpty()) return;
      
      String name = names.iterator().next();
      
      ModelNode model = profileResource.getChild(
          PathElement.pathElement(Names.HOSTNAME_VERIFIER, name)).getModel();
      
      config.setHostnameVerifier(
          HostnameVerifierFactory.newInstance(
              HostnameVerifierType.toObject(name), createHosts(model)));
     
    }

    private static String[] createHosts(ModelNode model) {
      List<String> hosts = new ArrayList<>();
      if (model.has(Names.HOSTS)) {
        ModelNode hostsModel = model.get(Names.HOSTS);
        if (hostsModel.isDefined()) {
          for (ModelNode host : hostsModel.asList()) {
            hosts.add(host.asString());
          }
        }
      }
      return hosts.toArray(new String[hosts.size()]);
    }
    
  }

}
