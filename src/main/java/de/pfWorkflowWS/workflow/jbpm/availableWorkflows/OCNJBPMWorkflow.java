package de.pfWorkflowWS.workflow.jbpm.availableWorkflows;

import org.drools.KnowledgeBase;
import org.springframework.web.client.RestTemplate;

import de.pfWorkflowWS.restConnection.restMessages.ResponseMessage;
import de.pfWorkflowWS.restConnection.restMessages.WorkflowReceiveMessage;
import de.pfWorkflowWS.workflow.WorkflowEntity;

/**
 * Represents the OCN Workflow and creates a {@link KnowledgeBase} for it. Is
 * designed as a singleton to avoid multiple costly creations of the
 * {@link KnowledgeBase}.
 * 
 * @author Marc Adolf
 *
 */
public class OCNJBPMWorkflow extends JBPMWorkflow {

	private final static OCNJBPMWorkflow instance = new OCNJBPMWorkflow();

	// TODO
	private OCNJBPMWorkflow() {
		// TODO read bpmn code als byte array from, where ever
		super(null, "OCN");

	}

	public static OCNJBPMWorkflow getInstance() {
		return instance;
	}

	@Override
	public void handleError(WorkflowEntity entity) {

		ResponseMessage answer = new ResponseMessage();
		RestTemplate restTemplate = new RestTemplate();
		WorkflowReceiveMessage msg = entity.getInitMsg();
		String msgId = msg.getId();

		answer.setId(msgId);

		answer.setErrorMessage(entity.getTriggeredException().getMessage());
		answer.setResult(entity.getState().toString());
		restTemplate.postForObject(msg.getCallbackAddress(), answer, String.class);
	}

	@Override
	public void handleResult(WorkflowEntity entity) {
		// do nothing, answers are created in the Workflow itself
	}

}
