package org.soulwing.cas.deployment;

import static org.soulwing.cas.deployment.DeploymentLogger.LOGGER;
import io.undertow.servlet.ServletExtension;

import java.io.IOException;

import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.msc.service.ServiceController;
import org.jboss.vfs.VirtualFile;
import org.soulwing.cas.extension.AuthenticationServiceControl;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.undertow.CasServletExtension;
import org.wildfly.extension.undertow.deployment.UndertowAttachments;

/**
 * An example deployment unit processor that does nothing. To add more
 * deployment processors copy this class, and add to the
 * {@link AbstractDeploymentChainStep}
 * {@link org.soulwing.cas.extension.SubsystemAdd#performBoottime(org.jboss.as.controller.OperationContext, org.jboss.dmr.ModelNode, org.jboss.dmr.ModelNode, org.jboss.as.controller.ServiceVerificationHandler, java.util.List)}
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SubsystemDeploymentProcessor implements DeploymentUnitProcessor {

  public static final String DEFAULT_PROFILE = "default";
  
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
    
    ResourceRoot root = deploymentUnit.getAttachment(Attachments.DEPLOYMENT_ROOT);
    VirtualFile descriptor = root.getRoot().getChild("WEB-INF/cas.xml");
    if (!descriptor.exists()) return;
    
    AppConfiguration config = parseDescriptor(descriptor);
    
    ServletExtension extension = new CasServletExtension(
        findAuthenticationService(phaseContext, config));
        
    deploymentUnit.addToAttachmentList(        
        UndertowAttachments.UNDERTOW_SERVLET_EXTENSIONS, extension);
    
    LOGGER.info(
        "attached CAS servlet extension to deployment " 
            + deploymentUnit.getName()
            + "; profile=" + config.getProfileId());
  }

  private AuthenticationService findAuthenticationService(
      DeploymentPhaseContext phaseContext, AppConfiguration config)
      throws DeploymentUnitProcessingException {
    ServiceController<?> controller = phaseContext.getServiceRegistry().getService(
        AuthenticationServiceControl.name(config.getProfileId()));
    if (controller == null) {
      throw new DeploymentUnitProcessingException(
          "cannot find a configuration profile named '"
          + config.getProfileId() + "'");
    }
    return (AuthenticationService) controller.getService().getValue();
  }

  private AppConfiguration parseDescriptor(VirtualFile descriptor) 
      throws DeploymentUnitProcessingException {
    DescriptorParser parser = new XMLStreamDescriptorParser();
    try {
      AppConfiguration config = parser.parse(descriptor.openStream());
      if (config.getProfileId() == null || config.getProfileId().isEmpty()) {
        config.setProfileId(DEFAULT_PROFILE);
      }
      return config;
    }
    catch (IOException | DescriptorParseException ex) {
      throw new DeploymentUnitProcessingException(ex);
    }
  }

  @Override
  public void undeploy(DeploymentUnit context) {
  }

}
