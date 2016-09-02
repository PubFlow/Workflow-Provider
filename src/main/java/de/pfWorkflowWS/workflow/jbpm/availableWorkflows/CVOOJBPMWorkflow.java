package de.pfWorkflowWS.workflow.jbpm.availableWorkflows;

import de.pfWorkflowWS.workflow.WorkflowEntity;

/**
 * Represents the CVOO {@link JBPMWorkflow}.
 * 
 * @author Marc Adolf
 *
 */
public class CVOOJBPMWorkflow extends JBPMWorkflow {

	private static CVOOJBPMWorkflow instance = null;
	private static String fileName = "EPRINTS.bpmn";

	private CVOOJBPMWorkflow(){
		super(fileName, "OCN");

	}

	synchronized public static CVOOJBPMWorkflow getInstance(){
		if (instance == null) {
			instance = new CVOOJBPMWorkflow();
		}
		return instance;
	}

	@Override
	public void handleResult(WorkflowEntity entity) {
		// do nothing, answers are created in the Workflow itself
	}

}
