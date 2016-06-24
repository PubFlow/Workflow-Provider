package de.pfWorkflowWS.workflow.management;

import java.util.HashMap;
import java.util.UUID;

import de.pfWorkflowWS.exceptions.MessageNotValidException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.WFBroker;
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
	private HashMap<UUID, WorkflowEntity> registeredWorkflows = new HashMap<UUID, WorkflowEntity>();

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
	 * returned. Designed to control the access of this list in this control
	 * class.
	 * 
	 * @param newRecMsg
	 *            The {@link ReceiveMessage} that will be used as a base for a
	 *            new {@link WorkflowEntity}.
	 * @return the {@link UUID} which maps to the created
	 *         {@link WorkflowEntity}.
	 * @throws MessageNotValidException
	 */
	synchronized public void addWorkflowEntity(ReceiveMessage newRecMsg) throws MessageNotValidException {
		// TODO start wf and change Jdoc

		// TODO validate message
		// if(validateMsg(newRecMsg) or newRecMSG.validate()){
		// throw new MessageNotValidException();
		// }

		UUID msgId = newRecMsg.getId();

		// UUID is null -> no mapping possible on client side
		if (msgId == null) {
			throw new MessageNotValidException("id is null");
		}
		// UUDI already registered
		if (registeredWorkflows.containsKey(msgId)) {
			throw new MessageNotValidException("id already exsits in the system");
		}
		registeredWorkflows.put(msgId, new WorkflowEntity(newRecMsg));
		// TODO start wf
		startWorkflow(msgId);
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
	synchronized public WorkflowEntity lookupWorkflowEntryId(UUID id) {
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

	private synchronized void startWorkflow(UUID msgId){
		WorkflowEntity currentWorkflow = registeredWorkflows.get(msgId);
		
		//currently not possible, if changed later add exception handling
		if(currentWorkflow == null){
			//do nothing
			return;
		}
		
		//states other than received were already started.
		if(currentWorkflow.getState() != ExecutionState.received){
			//do nothing
			return;
		}
		
		currentWorkflow.setState(ExecutionState.started);
		WFBroker.getInstance().receiveWFCall(currentWorkflow.getInitMsg());
		
	}
	
}
