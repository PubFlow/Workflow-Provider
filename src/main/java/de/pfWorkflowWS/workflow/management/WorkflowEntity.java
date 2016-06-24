package de.pfWorkflowWS.workflow.management;

import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;

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

	/**
	 * Represents the current state of one workflow entity.
	 * @author Marc Adolf
	 *
	 */
	public enum ExecutionState {
		received, started, waiting_for_event, error, finished;
	}

}
