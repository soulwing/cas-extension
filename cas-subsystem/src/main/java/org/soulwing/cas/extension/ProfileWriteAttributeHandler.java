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

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.RestartParentWriteAttributeHandler;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;

/**
 * An {@link RestartParentWriteAttributeHandler} for profile attributes.
 *
 * @author Carl Harris
 */
class ProfileWriteAttributeHandler 
    extends RestartParentWriteAttributeHandler {

  public static ProfileWriteAttributeHandler INSTANCE =
      new ProfileWriteAttributeHandler();
  
  /**
   * Constructs a new instance.
   */
  private ProfileWriteAttributeHandler() {
    super(Names.PROFILE, ProfileDefinition.attributes());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ServiceName getParentServiceName(PathAddress parentAddress) {
    return ProfileService.ServiceUtil.profileServiceName(parentAddress);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void recreateParentService(OperationContext context,
      PathAddress parentAddress, ModelNode parentModel,
      ServiceVerificationHandler verificationHandler)
      throws OperationFailedException {
    ProfileService.ServiceUtil.installService(context, parentModel, 
        parentAddress);
  }
  
}
