package de.pfWorkflowWS.workflow.jbpm.availableWorkflows;

import de.pfWorkflowWS.workflow.WorkflowEntity;

/**
 * Represents the EPrints {@link JBPMWorkflow}.
 * 
 * @author Marc Adolf
 *
 */
public class TestFlowJBPMWorkflow extends JBPMWorkflow {

	private static TestFlowJBPMWorkflow instance = null;
	private static String fileName = "TestFlow.bpmn";

	private TestFlowJBPMWorkflow(){
		super(fileName, "Test-WF");

	}

	synchronized public static TestFlowJBPMWorkflow getInstance(){
		if (instance == null) {
			instance = new TestFlowJBPMWorkflow();
		}
		return instance;
	}

	@Override
	public void handleResult(WorkflowEntity entity) {
		myLogger.info("Test successfull");
	}

}
