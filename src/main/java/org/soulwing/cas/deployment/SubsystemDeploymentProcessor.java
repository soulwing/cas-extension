package org.soulwing.cas.deployment;

import static org.soulwing.cas.extension.SubsystemExtension.logger;

import java.io.IOException;

import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.vfs.VirtualFile;

/**
 * An example deployment unit processor that does nothing. To add more
 * deployment processors copy this class, and add to the
 * {@link AbstractDeploymentChainStep}
 * {@link org.soulwing.cas.extension.SubsystemAdd#performBoottime(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode, org.jboss.as.controller.ServiceVerificationHandler, java.util.List)}
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SubsystemDeploymentProcessor implements DeploymentUnitProcessor {

  /**
   * See {@link Phase} for a description of the different phases
   */
  public static final Phase PHASE = Phase.POST_MODULE;

  /**
   * The relative order of this processor within the {@link #PHASE}. The current
   * number is large enough for it to happen after all the standard deployment
   * unit processors that come with JBoss AS.
   */
  public static final int PRIORITY = 0x8000;

  @Override
  public void deploy(DeploymentPhaseContext phaseContext)
      throws DeploymentUnitProcessingException {
    DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

    logger.info("deploy " + deploymentUnit.getName());
    ResourceRoot root = deploymentUnit.getAttachment(Attachments.DEPLOYMENT_ROOT);
    VirtualFile descriptor = root.getRoot().getChild("WEB-INF/cas.xml");
    AppConfiguration config = null;
    if (descriptor.exists()) {
      config = parseDescriptor(descriptor);
    }

    if (config != null) {
      logger.info("CAS authentication resource: " + config.getAuthenticationId());
      logger.info("CAS authorization resource: " + config.getAuthorizationId());
    }
  }

  private AppConfiguration parseDescriptor(VirtualFile descriptor) 
      throws DeploymentUnitProcessingException {
    DescriptorParser parser = new XMLStreamDescriptorParser();
    try {
      return parser.parse(descriptor.openStream());
    }
    catch (IOException | DescriptorParseException ex) {
      throw new DeploymentUnitProcessingException(ex);
    }
  }

  @Override
  public void undeploy(DeploymentUnit context) {
  }

}
