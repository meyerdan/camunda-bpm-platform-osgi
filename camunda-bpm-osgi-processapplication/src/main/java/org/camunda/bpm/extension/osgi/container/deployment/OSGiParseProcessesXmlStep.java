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
package org.camunda.bpm.extension.osgi.container.deployment;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.camunda.bpm.application.AbstractProcessApplication;
import org.camunda.bpm.container.impl.deployment.ParseProcessesXmlStep;
import org.camunda.bpm.extension.osgi.application.OSGiProcessApplication;

/**
 * @author Daniel Meyer
 * @author Roman Smirnov
 * @author Ronny Bräunlich
 *
 */
public class OSGiParseProcessesXmlStep extends ParseProcessesXmlStep {

  @Override
  protected List<URL> getProcessesXmlUrls(String[] deploymentDescriptors, AbstractProcessApplication processApplication) {
    OSGiProcessApplication osgiProcessApplication = (OSGiProcessApplication) processApplication;
    List<URL> urls = new ArrayList<URL>();
    for (String descriptorName : deploymentDescriptors) {
      Enumeration<URL> foundDescriptors;
      try {
        foundDescriptors = osgiProcessApplication.getBundle().getResources(descriptorName);
        while (foundDescriptors.hasMoreElements()) {
          urls.add(foundDescriptors.nextElement());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return urls;
  }


}
