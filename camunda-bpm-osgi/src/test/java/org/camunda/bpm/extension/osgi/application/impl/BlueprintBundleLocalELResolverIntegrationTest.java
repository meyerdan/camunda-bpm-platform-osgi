/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.extension.osgi.application.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.extension.osgi.OSGiTestCase;
import org.camunda.bpm.extension.osgi.TestBean;
import org.camunda.bpm.extension.osgi.application.MyProcessApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * Test to see if the {@link BlueprintBundleLocalELResolver} can resolve an
 * EL-expression by finding a bean from the context.xml.
 * 
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BlueprintBundleLocalELResolverIntegrationTest extends OSGiTestCase {

  @Inject
  protected BundleContext bundleContext;

  @Inject
  protected BlueprintContainer blueprintContainer;

  @Configuration
  @Override
  public Option[] createConfiguration() {
    Option[] blueprintEnv = options(mavenBundle().groupId("org.assertj").artifactId("assertj-core").version("1.5.0"),
        mavenBundle().groupId("org.apache.aries.blueprint").artifactId("org.apache.aries.blueprint.core").version("1.0.0"),
        mavenBundle().groupId("org.apache.aries.proxy").artifactId("org.apache.aries.proxy").version("1.0.0"), mavenBundle().groupId("org.apache.aries")
            .artifactId("org.apache.aries.util").version("1.0.0"));
    Option testBundle = provision(createTestBundle());
    return OptionUtils.combine(OptionUtils.combine(super.createConfiguration(), blueprintEnv), testBundle);
  }

  private InputStream createTestBundle() {
    try {
      return TinyBundles.bundle().add("OSGI-INF/blueprint/context.xml", new FileInputStream(new File("src/test/resources/testprocessapplicationcontext.xml")))
          .set(Constants.BUNDLE_SYMBOLICNAME, "org.camunda.bpm.osgi.example")
          .add("META-INF/processes.xml", new FileInputStream(new File("src/test/resources/testprocesses.xml"))).add(TestBean.class)
          .add(MyProcessApplication.class).set(Constants.DYNAMICIMPORT_PACKAGE, "*").set(Constants.EXPORT_PACKAGE, "*").build();
    } catch (FileNotFoundException fnfe) {
      fail(fnfe.toString());
      return null;
    }
  }

  @Test
  public void shouldBeAbleToResolveBean() throws InterruptedException {
    // give the process application some time to start
    Thread.sleep(10000L);
    ServiceReference ref = bundleContext.getServiceReference(ProcessEngine.class.getName());
    ProcessEngine engine = (ProcessEngine) bundleContext.getService(ref);
    ProcessInstance processInstance = engine.getRuntimeService().startProcessInstanceByKey("foo");
    assertThat(processInstance.isEnded(), is(true));
  }

}