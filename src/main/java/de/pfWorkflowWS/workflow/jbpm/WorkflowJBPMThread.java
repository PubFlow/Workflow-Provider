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

import org.drools.KnowledgeBase;
import org.springframework.web.client.RestTemplate;

import de.pfWorkflowWS.exceptions.EngineNotInitializedException;
import de.pfWorkflowWS.exceptions.WFException;
import de.pfWorkflowWS.exceptions.WFExecutionFailedException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.restConnection.restMessages.ResponseMessage;
import de.pfWorkflowWS.workflow.WorkflowEntity;
import de.pfWorkflowWS.workflow.WorkflowEntity.ExecutionState;
import de.pfWorkflowWS.workflow.engines.WorkflowEngine;

/**
 * Worker thread to execute a single workflow.
 * 
 * @author Marc Adolf
 *
 */
public class WorkflowJBPMThread extends Thread {
	private ReceiveMessage msg;
	private WorkflowEntity currentWorkflowInstance;
	private JBPMWorkflow workflow;
	
	public WorkflowJBPMThread(ReceiveMessage msg, JBPMWorkflow workflow) {
		this.msg = msg;
		this.workflow = workflow;
		currentWorkflowInstance = new WorkflowEntity(msg);
	}

	@Override
	public void run() {
		ResponseMessage answer = new ResponseMessage();
		RestTemplate restTemplate = new RestTemplate();
		String msgId = msg.getId();

		try {
			answer.setId(msgId);
			this.startWorkflow(msgId);
		} catch (WFException e) {
			answer.setErrorMessage(e.getMessage());
		} // Better more verbose/clear result
		answer.setResult(currentWorkflowInstance.getState().toString());
		restTemplate.postForObject(msg.getCallbackAddress(), answer, String.class);
	}

	/**
	 * Starts the workflow and waits for its completion. Changes the state of the
	 * {@link WorkflowEntity}.
	 * 
	 * @param msgId
	 * @throws WFExecutionFailedException
	 * @throws EngineNotInitializedException
	 */
	public void startWorkflow(String msgId) throws WFException {

		KnowledgeBase currentWorkflowKnowledgeBase = workflow.getWorkflowKnowledgeBase() ;
		
		//TODO can the wf broker be eliminated?
		JBPMWFBroker wfBroker = JBPMWFBroker.getInstance();
		currentWorkflowInstance.setState(ExecutionState.initialized);
		WorkflowEngine engine = wfBroker.initWfEngine(currentWorkflowInstance.getInitMsg(),currentWorkflowKnowledgeBase);
		currentWorkflowInstance.setEngine(engine);
		currentWorkflowInstance.setState(ExecutionState.started);
		try {
			wfBroker.executeWfEngine(currentWorkflowInstance);
		} catch (WFExecutionFailedException e) {
			currentWorkflowInstance.setState(ExecutionState.error);
			throw e;
		}
		currentWorkflowInstance.setState(ExecutionState.finished);

	}
}