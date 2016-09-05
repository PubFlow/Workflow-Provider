/**
 * Copyright (C) 2016 Marc Adolf, Arnd Plumhoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.pfWorkflowWS.workflow.jbpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pfWorkflowWS.restConnection.restMessages.WorkflowReceiveMessage;
import de.pfWorkflowWS.workflow.WorkflowEntity;
import de.pfWorkflowWS.workflow.WorkflowEntity.ExecutionState;
import de.pfWorkflowWS.workflow.jbpm.availableWorkflows.JBPMWorkflow;

/**
 * Worker thread to execute a single Workflow call.
 * 
 * @author Marc Adolf
 *
 */
public class WorkflowJBPMThread extends Thread {
	private Logger myLogger = LoggerFactory.getLogger(getClass());
	private WorkflowEntity currentWorkflowInstance;
	private JBPMWorkflow workflow;

	public WorkflowJBPMThread(WorkflowReceiveMessage msg, JBPMWorkflow workflow) {
		this.workflow = workflow;
		currentWorkflowInstance = new WorkflowEntity(msg);
	}

	@Override
	public void run() {

		try {
			currentWorkflowInstance.setState(ExecutionState.started);
			// blocking execution (to the best of my knowledge)
			workflow.startNewWorkflowSession(currentWorkflowInstance);

			currentWorkflowInstance.setState(ExecutionState.finished);

		} catch (Exception e) {
			myLogger.info("Exception during workflow execution. ID: " + currentWorkflowInstance.getInitMsg().getId());
			myLogger.info(e.getMessage());
			currentWorkflowInstance.setState(ExecutionState.error);
			currentWorkflowInstance.setTriggeredException(e);

			workflow.handleError(currentWorkflowInstance);
			return;
		}
		
		myLogger.info("Workflow with ID '"+ currentWorkflowInstance.getInitMsg().getId()+"' successfully executed ");
		workflow.handleResult(currentWorkflowInstance);
	}

}