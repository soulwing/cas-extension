/*
 * File created on Feb 17, 2015
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
package org.soulwing.cas.deployment;

import static org.soulwing.cas.deployment.DeploymentLogger.LOGGER;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.soulwing.cas.extension.Names;
import org.soulwing.cas.extension.Profile;
import org.soulwing.cas.extension.ProfileService;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.undertow.CasAuthenticationService;
import org.soulwing.cas.undertow.CasServletExtension;
import org.wildfly.extension.undertow.deployment.UndertowAttachments;

/**
 * A {@link DeploymentUnitProcessor} that adds the CAS servlet extension to
 * a web application deployment.
 */
public class ServletExtensionDeploymentProcessor implements DeploymentUnitProcessor {

  private static final Phase PHASE = Phase.FIRST_MODULE_USE;
  private static final int PRIORITY = 0x8000;

  private static final ServletExtensionDeploymentProcessor INSTANCE =
      new ServletExtensionDeploymentProcessor();
  
  private ServletExtensionDeploymentProcessor() {
  }
  
  /**
   * Adds a step handler for a deployment chain step that adds an instance
   * of this processor to the deployment processor target.
   * @param context context to which the step handler will be added
   */
  public static void addStepHandler(OperationContext context) {
    context.addStep(new AbstractDeploymentChainStep() {
      public void execute(DeploymentProcessorTarget processorTarget) {
        processorTarget.addDeploymentProcessor(
            Names.SUBSYSTEM_NAME, PHASE, PRIORITY, INSTANCE);
      }
    }, OperationContext.Stage.RUNTIME);
  }

  @Override
  public void deploy(DeploymentPhaseContext phaseContext)
      throws DeploymentUnitProcessingException {
    
    DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
    AppConfiguration config = deploymentUnit.getAttachment(
        DeploymentAttachments.CAS_DESCRIPTOR);
    if (config == null) return;
    
    ServiceName authServiceName = ServiceName.of(
        phaseContext.getPhaseServiceName().getParent(), 
        Names.SUBSYSTEM_NAME, Names.AUTHENTICATION_SERVICE);
    
    installAuthenticationService(phaseContext, config, authServiceName);
    
    CasServletExtension extension = new CasServletExtension();
    installServletExtension(phaseContext, extension, authServiceName);
            
    deploymentUnit.addToAttachmentList(        
        UndertowAttachments.UNDERTOW_SERVLET_EXTENSIONS, extension);
    
    LOGGER.info("attached CAS servlet extension to deployment " 
            + deploymentUnit.getName()
            + "; profile=" + config.getProfileId());
  }
  
  private ServiceController<?> installAuthenticationService(
      DeploymentPhaseContext phaseContext, AppConfiguration config,
      ServiceName authServiceName) 
          throws DeploymentUnitProcessingException {
    
    ServiceName profileServiceName =
        ProfileService.ServiceUtil.profileServiceName(config.getProfileId());
        
    CasAuthenticationService service = new CasAuthenticationService();

    ServiceController<?> controller = phaseContext.getServiceTarget()
        .addService(authServiceName, service)
        .addDependency(phaseContext.getPhaseServiceName())
        .addDependency(profileServiceName, Profile.class, 
            service.getProfileInjector())
        .install();
    
    return controller;
  }
  
  private ServiceController<?> installServletExtension(
      DeploymentPhaseContext phaseContext, CasServletExtension extension,
      ServiceName authServiceName) {

    ServiceName extensionServiceName = ServiceName.of(
        phaseContext.getPhaseServiceName().getParent(), 
        Names.SUBSYSTEM_NAME, Names.SERVLET_EXTENSION);
    
    ServiceController<?> controller = phaseContext.getServiceTarget()
        .addService(extensionServiceName, extension)
        .addDependency(phaseContext.getPhaseServiceName())
        .addDependency(authServiceName, AuthenticationService.class, 
            extension.getAuthenticationServiceInjector())
        .install();
    
    return controller;
  }
  
  @Override
  public void undeploy(DeploymentUnit context) {
  }

}
