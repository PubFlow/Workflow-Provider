/**
 * 
 */
package de.pfWorkflowWS.workflow.jbpm.availableWorkflows;

import de.pfWorkflowWS.workflow.WorkflowEntity;

/**
 * Represents the EPrints {@link JBPMWorkflow}.
 * 
 * @author Marc Adolf
 *
 */
public class EPrintsJBPMWorkflow extends JBPMWorkflow {

	private static EPrintsJBPMWorkflow instance = null;
	private static String fileName = "EPRINTS.bpmn";

	private EPrintsJBPMWorkflow(){
		super(fileName, "de.pubflow.EPRINTS");

	}

	synchronized public static EPrintsJBPMWorkflow getInstance(){
		if (instance == null) {
			instance = new EPrintsJBPMWorkflow();
		}
		return instance;
	}

	@Override
	public void handleResult(WorkflowEntity entity) {
		// do nothing, answers are created in the Workflow itself
	}

}
