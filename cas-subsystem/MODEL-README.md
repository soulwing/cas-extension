Adding Attributes to an Existing Resource
=========================================

Suppose you want to add an attribute for the Foo management resource.

1. Add the attribute to the complex type for Foo in the XML schema
   document (cas.xsd)
2. Create a description for the attribute in LocalDescriptions.properties
3. Define the name of the new attribute in the Names interface.
4. Create a static final attribute definition in the FooDefinition class.
5. Create a write handler for the new attribute.
6. Modify FooDefinition.registerAttributes so that it registers the new
   attribute and corresponding handler.
7. Modify FooAdd.populateModel so that it calls validateAndSet on the 
   new attribute definition.
8. Modify FooReaderWriter.attributes so that it includes the new attribute
   definition.

   
Adding a Subresource to an Existing Resource
============================================

Suppose you want to add a Bar resource as a child of the Foo resource.

1. In the XML schema document (cas.xsd), add a complex type definition for 
   the XML representation of the Bar resource and modify the type definition 
   for Foo resource so that it includes an element for Bar.
2. Create a description of the new resource and its attributes in 
   LocalDescription.properties
3. Add definitions for the name of the new subresource and its attributes to
   the Names interface.
4. Add a definition for the subresource path to the Paths interface.
5. Create a BarDefinition that extends SimpleResourceDefinition.  See
   AuthenticationDefinition for an example that defines both attributes and
   subresources.
6. Create a BarAdd that extends AbstractAddStepHandler.  See AuthenticationAdd
   for an example.  Be sure that the populateModel method calls validateAndSet
   on each of the attributes defined in BarDefinition.  
7. Create a BarRemove that extends AbstractRemoveStepHandler.  See 
   AuthenticationRemove for an example.
8. Create a BarReaderWriter that extends AbstractResourceReaderWriter.  See
   AuthenticationReaderWriter for an example.  Be sure that the attributes
   method returns an array containing each of the attributes defined in
   BarDefinition.
9. Modify the constructor of FooReaderWriter so that it passes an instance
   of BarReaderWriter to the super class constructor.
   