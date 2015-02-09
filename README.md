cas-subsystem
=============

A Wildfly extension module that supports web application authentication using the 
Central Authentication Service (CAS) protocol.  

This extension provides container-managed support for CAS, such that web applications 
can use the Java EE standard security mechanisms; for example, declarative roles and 
constraints in `web.xml` and the `@RolesAllowed` bean annotation. 

This extension consists of two major components.  The first is a Wildfly subsystem 
that monitors web application deployment events and attaches support for the CAS 
authentication protocol to those deployments that include a CAS deployment descriptor.  

The second major component of the extension is a standard JAAS `LoginModule` 
that participates in Wildfly's security subsystem.  The login module provides 
the mechanism for communicating the result of a CAS authentication exchange into 
Wildfly's built-in security stack.  In addition to providing support for using SAML 
attributes as user roles for authorization, the login module also allows the use of CAS 
in combination with Wildfly's built-in features such as LDAP-based authorization, role 
name mapping, etc.

This extension uses the JASIG Java CAS Client library to perform the CAS protocol 
operations, and exposes most of the features of the client through configuration 
attributes expressed as part of the Wildfly management model.


Installation
------------

The module and its dependencies must be installed in the `modules` directory of 
your Wildfly server.  Thanks to Wildfly's modular design, the installed modules 
remain isolated in your server's configuration, and will never be seen by applications 
that do not require CAS support.  Moreover, the various library components needed by 
this extension will not appear on your application's class loader, avoiding any 
potential for conflict.

(NEED INSTRUCTIONS FOR EXTRACTING THE MODULES FROM THE BUILD ARTIFACTS; this depends 
on resolution of a couple of TODO items -- opensaml module and use of Maven assemblies 
instead of Smartics module to build the module artifacts)


Configuration
-------------

The recommended approach to configuring the CAS extension is to use the Wildfly (JBoss) 
command line interface.  See the Wildfly documentation for instructions on how to run the 
command line interface.  The remainder of this document provides commands that are used 
at the CLI prompt.

The following CLI commands are used to create the subsystem resource and a Wildfly security domain for applications that use CAS authentication.  Note
that the last command given here is `reload`.  The container must be reloaded
to activate these components.  Subsequent configuration of the CAS subsystem
does not require the container to be reloaded.

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

CAS Configuration Profiles
--------------------------

The CAS subsystem allows you to create one or more configuration profiles.
Each profile fully describes the properties needed for the CAS subsystem to
exchange authentication requests with a given CAS server.  You might use 
multiple profiles to differentiate servers for *production* versus 
*development* or to distinguish different authentication realms in a hosted
environment.

By convention, a profile named *default* is used for application deployments that don't specify a particular configuration profile.  See [Application Deployment]
below to learn how an application can specify a CAS configuration profile.

The following CLI command creates the default configuration profile using
the CAS 2 protocol with a CAS server named cas.example.org.

```
/subsystem=cas/profile=default:add(server-url=https://cas.example.org, service-url=https://webapp.example.org)
```

Additional profiles can be created using the same basic syntax, substituting *profile=default* with *profile=some-other-name*.

The complete set of configurable profile attributes is described below.  You
can also view the description of these attributes using the `:read-resource-description ` CLI operation on a CAS configuration profile.

* *server-url* -- URL for the CAS server
* *service-url* -- URL for the application/service/container
* *protocol* -- authentication protocol (CAS-1.0, CAS-2.0, SAML-1.1); default
  is CAS-2.0
* *proxy-callback-url* -- URL for proxy granting ticket callbacks; default is
  no URL
* *accept-any-proxy* -- flag indicating whether any proxy is acceptable (true, false); default is false
* *allow-empty-proxy-chain* -- flag indicating whether an empty proxy chain
  should be allowed (true, false); default is false
* *renew* -- flag indicating whether users should be forced to 
  re-authenticate before being admitted to CAS-enabled applications using this
  profile (true, false); default is false
* *clock-skew-tolerance* -- indicates the amount of clock skew to tolerate 
  when evaluating the validity of a SAML protocol response (milliseconds);
  default is 1000 ms

In addition to these attributes, a profile supports zero or more named 
*allowed-proxy-chain* resources.  Each proxy chain specifies one or more allowed proxy URLs.  See the help in the CLI for more information.


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

The CAS deployment descriptor can be an empty file (or an empty root element), indicating that CAS authentication should be enabled using the profile named *default*.

In addition to the CAS deployment descriptor, the application must specify the
appropriate CAS-enabled security domain in the `/WEB-INF/jboss-web.xml` deployment
descriptor.

```
<?xml version="1.0" encoding="UTF-8"?>
<jboss-web 
  xmlns="http://www.jboss.com/xml/ns/javaee" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.jboss.com/xml/ns/javaee http://www.jboss.org/j2ee/schema/jboss-web_8_0.xsd"
  version="8.0">
  
  <security-domain>cas</security-domain>
  
</jboss-web>
```

Note that the *cas* security domain specified here was created in an earlier configuration step.  If you have created more than one security domain for CAS, specify the appropriate domain name here.




  

 
