cas-extension
=============

A Wildfly extension module that supports web application authentication using 
the Central Authentication Service (CAS) protocol.  

This extension provides container-managed support for CAS, such that web 
applications can use the Java EE standard security mechanisms; for example, 
declarative roles and constraints in `web.xml` and the `@RolesAllowed` bean
annotation. 

This extension consists of two major components.  The first is a Wildfly 
subsystem that monitors web application deployment events and attaches support 
for the CAS authentication protocol to those deployments that include a CAS
deployment descriptor.  

The second major component of the extension is a standard JAAS `LoginModule` 
that participates in Wildfly's security subsystem.  The login module 
provides the mechanism for communicating the result of a CAS authentication 
exchange into Wildfly's built-in security stack.  In addition to providing 
support for using SAML attributes as user roles for authorization, the login 
module also allows the use of CAS in combination with Wildfly's built-in 
features such as LDAP-based authorization, role name mapping, etc.

This extension uses the JASIG Java CAS Client library to perform the CAS 
protocol operations, and exposes most of the features of the client through 
configuration attributes expressed as part of the Wildfly management model.


Installation
------------

The module and its dependencies must be installed in the `modules` directory 
of your Wildfly server.  Thanks to Wildfly's modular design, the installed 
modules remain isolated in your server's configuration, and will never be seen 
by applications that do not require CAS support.  Moreover, the various 
library components needed by this extension will not appear on your 
application's class loader, avoiding any potential for conflict.

In the top level of the build directory:
```
tar -C ${WILDFLY_HOME} -xpvf cas-modules/target/cas-modules-1.0.0-SNAPSHOT-modules.tar.gz
```

Configuration
-------------

The recommended approach to configuring the CAS extension is to use the 
Wildfly (JBoss) command line interface.  See the Wildfly documentation for 
instructions on how to run the command line interface.  The remainder of this 
document provides commands that are used at the CLI prompt.

The following CLI commands are used to create the subsystem resource and a 
Wildfly security domain for applications that use CAS authentication.  Note
that the last command given here is `reload`.  The container must be reloaded
to activate these components.  

```
batch
/extension=org.soulwing.cas:add(module=org.soulwing.cas)
/subsystem=cas:add
/subsystem=security/security-domain=cas:add
/subsystem=security/security-domain=cas/authentication=classic:add
/subsystem=security/security-domain=cas/authentication=classic/login-module=IdentityAssertion:add(module=org.soulwing.cas, code=org.soulwing.cas.jaas.IdentityAssertionLoginModule, flag=required)
run-batch
reload
```

Once the CAS subsystem has been added to your Wildfly configuration, it is not
necessary to reload (e.g. using the CLI) or restart Wildfly in order to change
the CAS configuration.  See [Avoiding the Need to Reload/Restart] for more
information.


CAS Configuration Profiles
--------------------------

The CAS subsystem allows you to create one or more configuration profiles.
Each profile fully describes the properties needed for the CAS subsystem to
exchange authentication requests with a given CAS server.  You might use 
multiple profiles to differentiate servers for *production* versus 
*development* or to distinguish different authentication realms in a hosted
environment.

By convention, a profile named *default* is used for application deployments 
that don't specify a particular configuration profile.  See [Application 
Deployment] below to learn how an application can specify a CAS configuration 
profile.

The following CLI command creates the default configuration profile using the 
CAS 2 protocol with a CAS server named cas.example.org.

```
/subsystem=cas/profile=default:add(server-url=https://cas.example.org, service-url=https://webapp.example.org)
```

Additional profiles can be created using the same basic syntax, substituting 
*profile=default* with *profile=some-other-name*.

### Profile Attributes

The complete set of configurable profile attributes is described below.  You
can also view the description of these attributes using the 
`:read-resource-description ` CLI operation on a CAS configuration profile.

* *server-url* -- URL for the CAS server
* *service-url* -- URL for the application/service/container
* *protocol* -- authentication protocol; CAS-1.0, CAS-2.0 (default), SAML-1.1
* *proxy-callback-url* -- URL for proxy granting ticket callbacks; default is
  no URL
* *accept-any-proxy* -- flag indicating whether any proxy is acceptable; 
  true or false (default)
* *allow-empty-proxy-chain* -- flag indicating whether an empty proxy chain
  should be allowed; true or false (default)
* *renew* -- flag indicating whether users should be forced to 
  re-authenticate before being admitted to CAS-enabled applications using this
  profile; true or false (default)
* *clock-skew-tolerance* -- indicates the amount of clock skew to tolerate 
  when evaluating the validity of a SAML protocol response; milliseconds,  
  default is 1000 ms
* *post-auth-redirect* -- flag indicating whether, after a successful 
  authentication exchange, a redirect should be sent for the original
  request URL with all of the protocol query parameters removed; 
  true (default) or false
* *security-realm* -- (Wildfly 9 only) specifies the name of a Security Realm
  to use to obtain SSL truststore and/or keystore configuration to use when
  communicating with the CAS server

### Profile Sub-Resources

A configuration profile supports two kinds of sub-resources:

* *allowed-proxy-chain* -- specifies a named chain of allowed proxy URLs to 
  be used when validating proxy authentication tickets; you can define as 
  many proxy chains as needed
* *hostname-verifier* -- (Wildfly 9 only) specifies a hostname verifier to use
  when communicating with the CAS server (often needed when the CAS server is
  using a self-signed certificate)

#### Configuring Allowed Proxy Chains

To add an allowed proxy chain to the *default* profile, use the following
CLI command.  The name of the chain added by this command is *example*.

```
/subsystem=cas/profile=default/allowed-proxy-chain=example:add(proxies=[http://www.example.org])
```

To remove the *example* proxy chain from the *default* profile, use the
follwing CLI command.

```
/subsystem=cas/profile=default/allowed-proxy-chain=example:remove
```
#### Configuring a Hostname Verifier

When creating an HTTPS connection to the CAS server, the underlying SSL 
support verifies that the name of the host matches a name presented on
the certificate.  When connecting to a CAS server whose certificate does
not match the host name, you can use a hostname verifier to allow the
connection.  

Three types of hostname verification are supported by the CAS subsystem:

* *allow-any* -- no verification is performed; any hostname is allowed
* *white-list* -- the server hostname is compared to the entries in a 
  configured list of hostnames to allow
* *pattern-match* -- the server hostname is matched to one of the entries
  in a configured list of regular expression patterns

To add any of the supported verifiers to the *default* profile, use *one* of
 the following CLI commands:

```
/subsystem=cas/profile=default/hostname-verifier=allow-any:add
/subsystem=cas/profile=default/hostname-verifier=white-list:add(hosts=[cas.example.org])
/subsystem=cas/profile=default/hostname-verifier=pattern-match:add(hosts=[".*\.example.org$", "^localhost$"])
```

To remove a configured hostname verifier, specify the verifier type in the
remove command.  For example, to remove the *pattern-match* verifier, use
this command:

```
/subsystem=cas/profile=default/hostname-verifier=pattern-match:remove
```

Using SAML
----------

Recent versions of the CAS server support authentication using SAML.  One of
the most significant advantages of using SAML is that the response payload
can include attributes that further describe the authenticated user.  These
attribute values are often used as roles authorizing the user to perform
certain kinds of operations in an application.

The JAAS login module provided with this extension includes support for using
SAML response attributes as role names.  In order to take advantage of this 
support you must do both of the following:

1. Use a configuration profile that specifies the SAML-1.1 protocol.
2. Configure the login module with the names of the SAML response attributes
   whose values are to be used as role names.
   
   
### Specifying the SAML-1.1 Protocol

You can modify the configuration of an existing profile using the 
`:write-attribute` CLI operation on the profile:

```
/subsystem=cas/profile=default:write-attribute(name=protocol, value=SAML-1.1)
```

Of course, you can also specify the protocol attribute when creating a 
configuration profile:

```
/subsystem=cas/profile=default-saml:add(protocol=SAML-1.1, server-url=...)
```

### Specifying Role Attributes

The *role-attributes* module option is used to specify a list of attribute 
names whose values will be be mapped to role names by the *IdentityAssertion* 
login module.  In this example, the multi-valued attributes 
*eduPersonAffiliation* and *groupMembership* are assumed to contain the names
of roles that should be granted to the authenticated user.

You can modify the configuration of the *IdentityAssertion* module in the
default CAS security domain using the `:write-attribute` CLI command:

```
/subsystem=security/security-domain=cas/authentication=classic/login-module=IdentityAssertion:write-attribute(name="module-options", value={ role-attributes="eduPersonAffiliation, groupMembership" })
```

Of course, you can also specify the *role-attributes* module option when
creating a security domain:

```
/subsystem=security/security-domain=cas/authentication=classic/login-module=IdentityAssertion:add(module=org.soulwing.cas, code=org.soulwing.cas.jaas.IdentityAssertionLoginModule, flag=required, module-options={ role-attributes="eduPersonAffiliation, groupMembership" })
```

Delegating Authorization to a Security Realm
--------------------------------------------

Security Realms are a Wildfly management concept that is intended to simplify
the configuration needed to manage users and roles.  A security realm can
perform password-based, certificate-based, or other authentication, and can 
support role-based authorization using simple properties files or custom
plugins.

You can configure the *security domain* used for CAS such that it delegates
authorization to a *security realm*.  This can be done in addition to (or as 
an alternative to) using SAML attributes as roles used for authorization.

Assuming that you have already configured the your `cas` security domain,
you can reconfigure the domain to support realm delegation as follows:

```
/subsystem=security/security-domain=cas/authentication=classic/login-module=IdentityAssertion:remove
/subsystem=security/security-domain=cas/authentication=classic/login-module=DelegatingIdentityAssertion:add(code=org.soulwing.cas.jaas.DelegatingIdentityAssertionLoginModule, module=org.soulwing.cas, flag=required)
```

By default, this configuration delegates to the security realm named
`ApplicationRealm`.  If you wish to use a different realm (perhaps creating
your own realm for this purpose) you can specify the `realm` module option.
The following command changes the delegate realm name to 
`CasAuthorizationRealm`.

```
/subsystem=security/security-domain=cas/authentication=classic/login-module=DelegatingIdentityAssertion:write-attribute(module-options={realm:CasAuthorizationRealm, role-attributes=...})
```

Note that you must also specify the `role-attributes` module option (as
previously described) if you wish to use SAML attributes as authorization
roles.

Of course, you can also specify the `module-options` attribute when adding
the `DelegatingIdentityAssertion`.

Application Deployment
----------------------

Web applications indicate their intent to use CAS authentication by means of a 
deployment descriptor named `/WEB-INF/cas.xml`.  The deployment descriptor
identifies the CAS configuration profile that will be used by the application.

```
<?xml version="1.0" encoding="UTF-8"?>
<cas xmlns="urn:soulwing.org:cas:1.0">
  <profile>cas-prod</profile>
</cas>
```

The CAS deployment descriptor can be an empty file (or an empty root element), 
indicating that CAS authentication should be enabled using the profile named 
*default*.

In addition to the CAS deployment descriptor, the application must specify 
the appropriate CAS-enabled security domain in the `/WEB-INF/jboss-web.xml` 
deployment descriptor.

```
<?xml version="1.0" encoding="UTF-8"?>
<jboss-web 
  xmlns="http://www.jboss.com/xml/ns/javaee" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.jboss.com/xml/ns/javaee 
  http://www.jboss.org/j2ee/schema/jboss-web_8_0.xsd"
  version="8.0">
  
  <security-domain>cas</security-domain>
  
</jboss-web>
```

Note that the *cas* security domain specified here was created in an earlier 
configuration step.  If you have created more than one security domain for 
CAS, specify the appropriate domain name here.

### Using the Application API

In some web applications, it may be desirable to have direct access to the
`UserPrincipal` created by the CAS subsystem.  For example, in applications 
that take responsibility for access control using a framework like Spring 
Security, or applications that use CAS proxy authentication to gain access 
to backend services.

Using the application API provided by the CAS subsystem, you can cast the
return value of `javax.servlet.http.HttpServletRequest.getUserPrincipal` to
the `UserPrincipal` object provided in the `cas-api` module.  This principal
includes methods to get the attributes associated with the user (e.g. from 
a SAML authentication response) as well as a method to obtain a proxy
authentication ticket.

```
public interface UserPrincipal extends Principal, Serializable {

  /**
   * Gets the map of attribute name-value pairs that further describe the
   * user.
   * @return attribute map
   */
  Map<String, Object> getAttributes();
  
  /**
   * Generates a proxy ticket for use in accessing another service.
   * @param service name (URL) of the service to access
   * @return ticket that may be used to authenticate access by this user
   *    to the given {@code service}
   * @throws IllegalStateException if no proxy granting ticket is available.
   */
  String generateProxyTicket(String service) throws IllegalStateException;
  
  /**
   * Gets the underlying delegate principal.
   * <p>
   * The returned object is an instance of a class defined by the CAS client
   * implementation; e.g. for the JASIG CAS Client the returned object is
   * an instance of {@link org.jasig.cas.client.Authentication.AttributePrincipal}
   * @return delegate principal
   */
  Object getDelegate();
  
}
```

Assuming that your application uses a Maven, you will need to add a 
compile-time dependency on the `cas-api` module, as shown below.  Note the
use of the `provided` scope, which makes the API classes available at 
compile time, but does not include the module in your application's. 
`/WEB-INF/lib`.

```
<dependency>
  <groupId>org.soulwing.cas</groupId>
  <artifactId>cas-api</artfiactId>
  <version>1.0.0-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
```

In order to have access to this API at runtime, your CAS deployment descriptor
must include the `<add-api-dependencies/>` element, as shown below.  When this
element is present, the CAS subsystem will add the necessary classes to your
application's runtime class path.

```
<?xml version="1.0" encoding="UTF-8"?>
<cas xmlns="urn:soulwing.org:cas:1.0">
  ...
  <add-api-dependencies/>
  ...
</cas>
```

Avoiding the Need to Restart/Reload
-----------------------------------

Configuration profiles can be modified through the CLI without the need to 
reload (e.g. via the CLI) or restart Wildfly.  You simply need to include 
the `allow-resource-service-restart=true` header with the command(s) that 
should be applied without the need for reload.  Configuration profile changes
that include this header will be *immediately* applied to deployments that 
are attached to the subject profile.

For example, to change the CAS server URL without the need to reload, use
this command:

```
/subsystem=cas/profile=default:write-attribute(name=server-url, value="https://cas2.example.org"){allow-resource-service-restart=true}
```

When changing more than one configuration characteristic, you may wish to use
the CLI's *batch* facility.  By executing commands in a batch, you ensure that
the updated configuration is not applied to any deployment attached to the
subject profile until all commands in the batch have completed:

```
batch
/subsystem=cas/profile=default:write-attribute(name=server-url, value="https://localhost:8443")
/subsystem=cas/profile=default:write-attribute(name=security-realm, value="LocalhostSslRealm")
/subsystem=cas/profile=default/hostname-verifier=white-list:add(hosts=[localhost])
run-batch --headers={allow-resource-service-restart=true}
```



  

 
