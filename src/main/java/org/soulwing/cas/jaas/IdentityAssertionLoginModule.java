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

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collection;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.jboss.logging.Logger;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.callback.ObjectCallback;
import org.jboss.security.auth.spi.AbstractServerLoginModule;
import org.soulwing.cas.service.IdentityAssertion;

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
public class IdentityAssertionLoginModule extends AbstractServerLoginModule {

  private static final Logger logger = 
      Logger.getLogger(IdentityAssertionLoginModule.class);
  
  public static final String ROLE_ATTRIBUTES = "role-attributes";
  
  private String[] roleAttributes;
  private IdentityAssertion assertion; 
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize(Subject subject, CallbackHandler callbackHandler,
      Map<String, ?> sharedState, Map<String, ?> options) {
    super.initialize(subject, callbackHandler, sharedState, options);
    logger.info("initializing module");
    logger.trace("initializing module at TRACE level");
    this.roleAttributes = parseRoleAttributes(options.get(ROLE_ATTRIBUTES));
  }

  private static String[] parseRoleAttributes(Object attrs) {
    if (attrs == null) return new String[0];
    String attributes = attrs.toString();
    if (attributes.isEmpty()) return new String[0];
    return attributes.split("\\s*(,|\\s)\\s*");
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean login() throws LoginException {
    logger.trace("login invoked");
    ObjectCallback callback = new ObjectCallback("Credential");
    try {
      callbackHandler.handle(new Callback[] { callback });
      Object obj = callback.getCredential();
      logger.trace("got object from callback: " + obj);
      if (!(obj instanceof IdentityAssertionHolder)) {
        return false;
      }

      assertion = ((IdentityAssertionHolder) obj).getIdentityAssertion();
      loginOk = true;
      return true;
    }
    catch (UnsupportedCallbackException ex) {
      logger.trace("ObjectCallback not supported", ex);
      throw new LoginException("ObjectCallback not supported");
    }
    catch (IOException ex) {
      logger.trace("callback I/O error", ex);
      throw new LoginException("I/O error: " + ex.toString());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean commit() throws LoginException {
    logger.trace("commit invoked");
    return super.commit();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Principal getIdentity() {
    Principal principal = assertion.getPrincipal();
    logger.trace("assertion principal: " + principal);
    return principal;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Group[] getRoleSets() throws LoginException {
    if (roleAttributes.length == 0) return new Group[0];
    Group rolesGroup = new SimpleGroup("Roles");
    Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
    for (String roleAttribute : roleAttributes) {
      Object attrValue = attributes.get(roleAttribute);
      if (attrValue instanceof Collection) {
        for (Object value : (Collection<?>) attrValue) {
          rolesGroup.addMember(createRole(value.toString()));
        }
      }
      else if (attrValue != null) {
        rolesGroup.addMember(createRole(attrValue.toString()));
      }
      else {
        logger.trace("assertion does not contain attribute '" 
            + roleAttribute + "'");
      }
    }
    return new Group[] { rolesGroup };
  }

  private Principal createRole(String name) throws LoginException {
    try {
      Principal role = createIdentity(name);
      logger.trace("created role principal: " + name);
      return role;
    }
    catch (Exception ex) {
      logger.error("while creating role '" + name + "': " + ex, ex);
      throw new LoginException("cannot create role: " + name);
    }
  }
  
}
