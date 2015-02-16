package org.soulwing.cas.deployment;

import static org.soulwing.cas.deployment.DeploymentLogger.LOGGER;

import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.SSLContext;

import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.server.deployment.module.ResourceRoot;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.vfs.VirtualFile;
import org.soulwing.cas.extension.Names;
import org.soulwing.cas.extension.Profile;
import org.soulwing.cas.extension.ProfileService;
import org.soulwing.cas.extension.WrapperSSLContextService;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.undertow.CasAuthenticationService;
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
    
    ServiceName authServiceName = ServiceName.of(
        phaseContext.getPhaseServiceName().getParent(), 
        Names.SUBSYSTEM_NAME, Names.AUTHENTICATION_SERVICE);
    
    installAuthenticationService(phaseContext, config, authServiceName);
    
    CasServletExtension extension = new CasServletExtension();
    installServletExtension(phaseContext, extension, authServiceName);
            
    deploymentUnit.addToAttachmentList(        
        UndertowAttachments.UNDERTOW_SERVLET_EXTENSIONS, extension);
    
    LOGGER.info(
        "attached CAS servlet extension to deployment " 
            + deploymentUnit.getName()
            + "; profile=" + config.getProfileId());
  }

  private ServiceController<?> installAuthenticationService(
      DeploymentPhaseContext phaseContext, AppConfiguration config,
      ServiceName authServiceName) 
          throws DeploymentUnitProcessingException {
    
    ServiceName profileServiceName =
        ProfileService.ServiceUtil.profileServiceName(config.getProfileId());
    
    ServiceName sslContextServiceName =
        WrapperSSLContextService.ServiceUtil.createServiceName(profileServiceName);
    
    CasAuthenticationService service = new CasAuthenticationService();

    ServiceController<?> controller = phaseContext.getServiceTarget()
        .addService(authServiceName, service)
        .addDependency(phaseContext.getPhaseServiceName())
        .addDependency(profileServiceName, Profile.class, 
            service.getProfileInjector())
        .addDependency(sslContextServiceName, SSLContext.class, 
            service.getSslContextInjector())
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
  
  @Override
  public void undeploy(DeploymentUnit context) {
  }

}
