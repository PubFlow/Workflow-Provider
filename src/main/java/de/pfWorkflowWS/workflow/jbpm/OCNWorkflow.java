package de.pfWorkflowWS.workflow.jbpm;

public class OCNWorkflow extends JBPMWorkflow {

	private final static OCNWorkflow instance = new OCNWorkflow();
//TODO
	private OCNWorkflow() {
		//TODO read bpmn code als byte array from, where ever
		super(null);

	}

	public static OCNWorkflow getInstance() {
		return instance;
	}

}
