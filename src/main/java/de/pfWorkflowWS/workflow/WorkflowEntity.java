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

import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.engines.WorkflowEngine;

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
	private ReceiveMessage initMsg;
	private WorkflowEngine engine;
	private Throwable uncaughtException;

	public WorkflowEntity(ReceiveMessage initMsg) {
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

	public ReceiveMessage getInitMsg() {
		return initMsg;
	}

	public WorkflowEngine getEngine() {
		return engine;
	}

	public void setEngine(WorkflowEngine engine) {
		this.engine = engine;
	}

	synchronized public Throwable getUncaughtException() {
		return uncaughtException;
	}

	synchronized public void setUncaughtException(Throwable uncaughtException) {
		this.uncaughtException = uncaughtException;
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
