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
package de.pfWorkflowWS.workflow.management;

import java.util.HashMap;
import java.util.UUID;

import de.pfWorkflowWS.exceptions.DuplicateIDException;
import de.pfWorkflowWS.exceptions.EngineNotInitializedException;
import de.pfWorkflowWS.exceptions.WFException;
import de.pfWorkflowWS.exceptions.WFExecutionFailedException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.WFBroker;
import de.pfWorkflowWS.workflow.engines.WorkflowEngine;
import de.pfWorkflowWS.workflow.management.WorkflowEntity.ExecutionState;

/**
 * Controls the logic for the processing of all registered workflows. Its
 * registered as a singleton to provide a central entry point.
 * 
 * 
 * @author Marc Adolf
 *
 */
public class WorkflowManager {

	private static WorkflowManager instance = null;
	private HashMap<String, WorkflowEntity> registeredWorkflows = new HashMap<String, WorkflowEntity>();

	private WorkflowManager() {
	}

	synchronized public static WorkflowManager getInstance() {
		if (instance == null) {
			instance = new WorkflowManager();
		}
		return instance;
	}

	// lookup +add + (remove) +(save)

	// manipulation of the registeredWorkflows should not be able outside of
	// this class
	/**
	 * Adds a new workflow to the list of currently managed tasks. The
	 * {@link WorkflowEntity} will be created and a {@link UUID} will be
	 * returned. Workflows with id's that already exist are rejected. Designed
	 * to control the access of this list in this control class.
	 * 
	 * @param newRecMsg
	 *            The {@link ReceiveMessage} that will be used as a base for a
	 *            new {@link WorkflowEntity}.
	 * @return the {@link UUID} which maps to the created
	 *         {@link WorkflowEntity}.
	 * @throws DuplicateIDException
	 */
	synchronized public void addWorkflowEntity(ReceiveMessage newRecMsg) throws DuplicateIDException {

		String msgId = newRecMsg.getId();

		if (lookupWorkflowEntryId(msgId) != null) {
			throw new DuplicateIDException("Workflow with ID already exists");
		}

		registeredWorkflows.put(msgId, new WorkflowEntity(newRecMsg));
	}

	/**
	 * Looks up a {@link UUID} to find a corresponding {@link WorkflowEntity}.
	 *
	 * Designed to control the access of this list in this control class.
	 * 
	 * @param id
	 *            the {@link UUID} of the searched entry
	 * @return the {@link WorkflowEntity} or if no entry was found null is
	 *         returned.
	 */
	synchronized public WorkflowEntity lookupWorkflowEntryId(String id) {
		return registeredWorkflows.get(id);
	}

	/**
	 * Removes the workflow from the list.
	 * 
	 * Designed to control the access of this list in this control class.
	 * 
	 * @param id
	 *            the {@link UUID} of the {@link WorkflowEntity} to be deleted.
	 */
	synchronized public void deleteWorkflowEntry(UUID id) {
		// TODO will this also stop/cancel the workflow? (adapt jdoc)
		registeredWorkflows.remove(id);
	}

	// TODO save the WorkflowEntry for persistence

	/**
	 * Starts the workflow and waits for its cmpletion. Changes the state of the
	 * {@link WorkflowEntity}.
	 * 
	 * @param msgId
	 * @throws WFExecutionFailedException
	 * @throws EngineNotInitializedException
	 */
	public void startWorkflow(String msgId) throws WFException {
		WorkflowEntity currentWorkflow = registeredWorkflows.get(msgId);

		// currently not possible, if changed later add exception handling
		if (currentWorkflow == null) {
			// do nothing
			return;
		}

		// states other than received were already started.
		if (currentWorkflow.getState() != ExecutionState.received) {
			// do nothing
			return;
		}

		WFBroker wfBroker = WFBroker.getInstance();
		currentWorkflow.setState(ExecutionState.initialized);
		WorkflowEngine engine = wfBroker.initWfEngine(currentWorkflow.getInitMsg());
		currentWorkflow.setEngine(engine);
		currentWorkflow.setState(ExecutionState.started);
		try {
			wfBroker.executeWfEngine(currentWorkflow);
		} catch (WFExecutionFailedException e) {
			currentWorkflow.setState(ExecutionState.error);
			throw e;
		}
		currentWorkflow.setState(ExecutionState.finished);

	}

}
