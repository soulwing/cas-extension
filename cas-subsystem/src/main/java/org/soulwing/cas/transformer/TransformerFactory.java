/*
 * File created on Feb 25, 2015 
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
package org.soulwing.cas.transformer;

import static org.soulwing.cas.transformer.TransformerLogger.LOGGER;

import java.util.Properties;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;
import org.soulwing.cas.api.AttributeTransformer;
import org.soulwing.cas.api.Transformer;

/**
 * A factory that produces {@link AttributeTransformer} objects.
 *
 * @author Carl Harris
 */
public class TransformerFactory {

  public static Transformer<?, ?> getTransformer(String code, 
      String moduleSpec, Properties properties) 
      throws TransformerLoadException {
    
    if (code == null) {
      throw new TransformerLoadException("transformer class name is required");
    }
    Class<?> transformerClass = null;
    ModuleIdentifier moduleId = moduleId(moduleSpec);
    try {
      transformerClass = loadTransformerClass(code, moduleId);
      Object transformer = transformerClass.newInstance();
      if (transformer instanceof AttributeTransformer) {
        ((AttributeTransformer<?, ?>) transformer).initialize(properties);
      }
      else if (!(transformer instanceof Transformer)) {
        throw new IllegalArgumentException("not a transformer object");
      }
      LOGGER.debug("loaded transformer " + transformerClass);
      return (Transformer<?, ?>) transformer;
    }
    catch (IllegalAccessException | InstantiationException ex) {
      throw new TransformerLoadException(
          "cannot instantiate transformer class " + transformerClass, ex);
    }
    catch (ClassNotFoundException ex) {
      throw new TransformerLoadException("cannot load transformer class "
          + ex.getMessage() + " from module " + moduleId, ex);
    }
    catch (ModuleLoadException ex) {
      throw new TransformerLoadException("cannot load module " + 
          moduleId, ex);
    }
  }
  
  static Class<?> loadTransformerClass(String className,
      ModuleIdentifier moduleId) throws ClassNotFoundException, 
      ModuleLoadException {
    try {
      return Module.loadClassFromCallerModuleLoader(moduleId, className);
    }
    catch (ClassNotFoundException ex) {
      if (!isSimpleJavaIdentifier(className)) {
        throw ex;
      }
      return Module.loadClassFromCallerModuleLoader(moduleId, 
          qualifiedTransformerName(className));
    }
  }

  static String qualifiedTransformerName(String className) {
    String pkg = TransformerFactory.class.getPackage().getName();
    String suffix = Transformer.class.getSimpleName();
    StringBuilder sb = new StringBuilder();
    sb.append(pkg);
    sb.append('.');
    sb.append(className);
    if (!className.endsWith(suffix)) {
      sb.append(suffix);
    }
    return sb.toString();
  }
  
  private static ModuleIdentifier moduleId(String moduleSpec) {
    if (moduleSpec == null) {
      return Module.getCallerModule().getIdentifier();
    }
    return ModuleIdentifier.fromString(moduleSpec);
  }
  
  static boolean isSimpleJavaIdentifier(final String className) {
    assert className.length() > 0: "className must be non-empty";
    if (!Character.isJavaIdentifierStart(className.charAt(0))) {
      return false;
    }
    for (int i = 1, max = className.length(); i < max; i++) {
      if (!Character.isJavaIdentifierPart(className.charAt(i))) {
        return false;
      }
    }
    return true;
  }

}
