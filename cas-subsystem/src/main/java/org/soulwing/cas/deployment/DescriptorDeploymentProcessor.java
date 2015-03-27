/*
 * File created on Feb 17, 2015 
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
package org.soulwing.cas.deployment;

import static org.soulwing.cas.deployment.DeploymentLogger.LOGGER;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.vfs.VirtualFile;
import org.soulwing.cas.extension.Names;

/**
 * A {@link DeploymentUnitProcessor} that parses the CAS deployment descriptor.
 *
 * @author Carl Harris
 */
public class DescriptorDeploymentProcessor implements DeploymentUnitProcessor {

  private static final Phase PHASE = Phase.PARSE;
  private static final int PRIORITY = 0x8000;

  private static final DescriptorDeploymentProcessor INSTANCE =
      new DescriptorDeploymentProcessor();
  
  private DescriptorDeploymentProcessor() {    
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void deploy(DeploymentPhaseContext phaseContext)
      throws DeploymentUnitProcessingException {

    DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
    
    ResourceRoot root = deploymentUnit.getAttachment(Attachments.DEPLOYMENT_ROOT);
    VirtualFile descriptor = root.getRoot().getChild("WEB-INF/cas.xml");
    if (!descriptor.exists()) return;
    
    AppConfiguration config = parseDescriptor(descriptor);
    deploymentUnit.putAttachment(DeploymentAttachments.CAS_DESCRIPTOR, 
        config);
    
    LOGGER.info("deployment unit " + deploymentUnit + " is CAS enabled");
    LOGGER.debug("deployment configuration " + config);
  }

  private AppConfiguration parseDescriptor(VirtualFile descriptor) 
      throws DeploymentUnitProcessingException {
    try {
      if (isEmptyFile(descriptor)) return new AppConfiguration();
      DescriptorParser parser = new XMLStreamDescriptorParser();
      AppConfiguration config = parser.parse(descriptor.openStream());
      return config;
    }
    catch (IOException | DescriptorParseException ex) {
      throw new DeploymentUnitProcessingException(ex);
    }
  }

  private boolean isEmptyFile(VirtualFile descriptor) throws IOException {
    if (descriptor.getSize() == 0) return true;
    InputStream inputStream = descriptor.openStream();
    try {
      return InputStreamUtil.isEmptyStream(inputStream);
    }
    finally {
      try {
        inputStream.close();
      }
      catch (IOException ex) {
        ex.printStackTrace(System.err);
      }
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void undeploy(DeploymentUnit context) {
  }

}
