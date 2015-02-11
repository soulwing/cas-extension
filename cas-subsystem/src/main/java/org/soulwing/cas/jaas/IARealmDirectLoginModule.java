/*
 * File created on Feb 7, 2015 
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
package org.soulwing.cas.jaas;

import static org.soulwing.cas.jaas.JaasLogger.LOGGER;

import java.security.AccessController;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jboss.as.core.security.RealmRole;
import org.jboss.as.core.security.RealmUser;
import org.jboss.as.core.security.SubjectUserInfo;
import org.jboss.as.domain.management.AuthMechanism;
import org.jboss.as.domain.management.AuthorizingCallbackHandler;
import org.jboss.as.domain.management.SecurityRealm;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceController;
import org.soulwing.cas.api.IdentityAssertion;

/**
 * A JAAS {@code LoginModule} that validates a credential of type 
 * {@link IdentityAssertion} obtained from a CAS ticket validation.
 * <p>
 * Assertion attribute values can be used as role names by specifying the
 * {{roleAttributes}} module option.  The value is a list of attribute names
 * whose values will be used as role name.  The list may be delimited with
 * spaces and/or commas.  Each value of each named role attribute is used
 * as a role for the authentic user.
 *
 * @author Carl Harris
 */
public class IARealmDirectLoginModule extends IdentityAssertionLoginModule {

  public static final String REALM = "realm";
  
  public static final String ROLE_ATTRIBUTES = "role-attributes";
  
  public static final String DEFAULT_REALM = "ApplicationRealm";
  
  private String realmName;
  
  private SecurityRealm realm;
  
  private AuthorizingCallbackHandler authorizingCallbackHandler;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(Subject subject, CallbackHandler callbackHandler,
      Map<String, ?> sharedState, Map<String, ?> options) {
    super.initialize(subject, callbackHandler, sharedState, options);
    
    realmName = (String) options.get(REALM);
    if (realmName == null) {
      realmName = DEFAULT_REALM;
    }
    
    ServiceController<?> controller = serviceContainer().getService(
        SecurityRealm.ServiceUtil.createServiceName(realmName));
    if (controller != null) {
      realm = (SecurityRealm) controller.getValue();
    }    
    if (realm == null) {
      throw new IllegalArgumentException("realm '" + realmName + "' not found");
    }
    
    Set<AuthMechanism> mechs = realm.getSupportedAuthenticationMechanisms();
    if (mechs.isEmpty()) {
      throw new IllegalArgumentException("realm '" + realmName
          + "' does not support any authentication mechanisms");
    }
    
    AuthMechanism mech = AuthMechanism.PLAIN; 
    if (!mechs.contains(mech)) {
      mech = mechs.iterator().next();
    }

    authorizingCallbackHandler = realm.getAuthorizingCallbackHandler(mech);
    if (authorizingCallbackHandler == null) {
      throw new IllegalArgumentException("realm '" + realmName
          + "' does not provide authorization");
    }  

    LOGGER.info("attached to realm " + realmName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Set<Principal> getRoles() throws LoginException {
    Set<Principal> roles = super.getRoles();  
    try {
      RealmUser user = new RealmUser(assertion.getPrincipal().getName());
      SubjectUserInfo subjectUserInfo = authorizingCallbackHandler
          .createSubjectUserInfo(Collections.<Principal>singleton(user));
      Set<RealmRole> realmRoles = subjectUserInfo.getSubject()
          .getPrincipals(RealmRole.class);
      LOGGER.debug("user '" + user + '@' + realmName + "' has roles " 
          + realmRoles);
      for (RealmRole role : realmRoles) {
        roles.add(createRole(role.getName()));
      }
    }
    catch (Exception ex) {
      LOGGER.error("error getting realm roles: " + ex, ex);
      throw new LoginException("error getting realm roles: " + ex);
    }
    return roles;
  }
 
  private static ServiceContainer serviceContainer() {
    if (System.getSecurityManager() == null) {
      return CurrentServiceContainer.getServiceContainer();
    }
    return AccessController.doPrivileged(CurrentServiceContainer.GET_ACTION);
  }
  
}
