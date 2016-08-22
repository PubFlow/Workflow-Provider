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
package de.pfWorkflowWS.workflow;

import de.pfWorkflowWS.restConnection.restMessages.WorkflowReceiveMessage;

/**
 * Represents an execution of a workflow. It is used to coordinate states,
 * messages ,events, and ids with the workflow entity
 * 
 * 
 * @author Marc Adolf
 *
 */
public class WorkflowEntity {
	// BETTER maybe a enum can be useful in the future
	// many information are already saved in the initial message
	private WorkflowReceiveMessage initMsg;
	private Throwable triggeredException;

	public WorkflowEntity(WorkflowReceiveMessage initMsg) {
		this.initMsg = initMsg;
		state = ExecutionState.received;

	}

	private ExecutionState state;

	public ExecutionState getState() {
		return state;
	}

	public void setState(ExecutionState state) {
		this.state = state;
	}

	public WorkflowReceiveMessage getInitMsg() {
		return initMsg;
	}

	synchronized public Throwable getTriggeredException() {
		return triggeredException;
	}

	synchronized public void setTriggeredException(Throwable uncaughtException) {
		this.triggeredException = uncaughtException;
	}

	/**
	 * Represents the current state of one workflow entity.
	 * 
	 * @author Marc Adolf
	 *
	 */
	public enum ExecutionState {
		received, initialized, started, waiting_for_event, error, finished;
	}

}
