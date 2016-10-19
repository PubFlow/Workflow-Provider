package de.pfWorkflowWS.workflow.jbpm.availableWorkflows;

import de.pfWorkflowWS.workflow.WorkflowEntity;

/**
 * Represents the OCN {@link JBPMWorkflow}.
 * 
 * @author Marc Adolf
 *
 */
public class OCNJBPMWorkflow extends JBPMWorkflow {

	private static OCNJBPMWorkflow instance = null;
	private static String fileName = "OCN.bpmn";

	private OCNJBPMWorkflow() {
		super(fileName, "de.pubflow.OCN");

	}

	synchronized public static OCNJBPMWorkflow getInstance() {
		if (instance == null) {
			instance = new OCNJBPMWorkflow();
		}
		return instance;
	}

	@Override
	public void handleResult(WorkflowEntity entity) {
		// do nothing, answers are created in the Workflow itself
	}

}
