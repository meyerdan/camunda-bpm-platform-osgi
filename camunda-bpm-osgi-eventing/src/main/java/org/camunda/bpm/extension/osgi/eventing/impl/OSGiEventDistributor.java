package org.camunda.bpm.extension.osgi.eventing.impl;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.core.model.CoreModelElement;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.extension.osgi.eventing.api.Topics;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author Ronny Bräunlich
 */
public class OSGiEventDistributor implements TaskListener, ExecutionListener, Serializable {

  /**
   * The task element this listener has been added to
   */
  private TaskDefinition taskDefinition;
  private EventAdmin eventAdmin;
  /**
   * The BPMN element this listener has been added to
   */
  private CoreModelElement element;

  public OSGiEventDistributor(EventAdmin eventAdmin, CoreModelElement element) {
    this.eventAdmin = eventAdmin;
    this.element = element;
  }

  public OSGiEventDistributor(EventAdmin eventAdmin, TaskDefinition taskDefinition) {
    this.eventAdmin = eventAdmin;
    this.taskDefinition = taskDefinition;
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception {
    Event event = createEvent(execution);
    eventAdmin.postEvent(event);
  }


  @Override
  public void notify(DelegateTask delegateTask) {
    Event event = createEvent(delegateTask);
    eventAdmin.postEvent(event);
  }

  private Event createEvent(DelegateTask delegateTask) {
    Dictionary<String, String> properties = new Hashtable<String, String>();
    BusinessProcessEventPropertiesFiller.fillDictionary(properties, delegateTask);
    return new Event(Topics.TASK_EVENT_TOPIC, properties);
  }

  private Event createEvent(DelegateExecution execution) {
    Dictionary<String, String> properties = new Hashtable<String, String>();
    BusinessProcessEventPropertiesFiller.fillDictionary(properties, execution);
    return new Event(Topics.EXECUTION_EVENT_TOPIC, properties);
  }
}
