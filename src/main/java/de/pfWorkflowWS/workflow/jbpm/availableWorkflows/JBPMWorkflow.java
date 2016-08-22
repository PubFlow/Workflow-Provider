package de.pfWorkflowWS.workflow.jbpm.availableWorkflows;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pfWorkflowWS.workflow.WorkflowEntity;
import de.pfWorkflowWS.workflow.common.WFParameter;

public abstract class JBPMWorkflow {

	Logger myLogger;
	private byte[] bpmn;
	private KnowledgeBase workflowKnowledgeBase;
	private String workflowName;

	public JBPMWorkflow(byte[] bpmn, String workflowName) {
		myLogger = LoggerFactory.getLogger(this.getClass());
		this.bpmn = bpmn;
		this.workflowName = workflowName;
		createKnowledgeBase();
	}

	/**
	 * Handles occurrences of errors during the execution according to each
	 * Workflow definition. May restart the Workflow or just create an
	 * appropriate answer.
	 * 
	 * @param entity
	 *            in which the error occurred.
	 */
	abstract public void handleError(WorkflowEntity entity);
	
	abstract public void handleResult(WorkflowEntity entity);

	/**
	 * Loads a process (processType BPMN2.0!) from the given location in a new
	 * knowledgeBase
	 */
	private void createKnowledgeBase() {
		myLogger.info("Trying to add WF to knowledgebase");
		KnowledgeBuilder kbuilder = null;
		try {
			kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newByteArrayResource(bpmn), ResourceType.BPMN2);
		}

		catch (Exception e) {
			myLogger.error("Couldn't create knowledgebase");
		}
		workflowKnowledgeBase = kbuilder.newKnowledgeBase();
		myLogger.info("Knowledgebase created");

	}

	public ProcessInstance startNewWorkflowSession(WorkflowEntity workflowEntity) {
		myLogger.info("Trying to start workflow " + workflowName);
		List<WFParameter> wfParameters = workflowEntity.getInitMsg().getWorkflowParameters();
		ProcessInstance instance = null;

		myLogger.info("Creating Workflows session ...");
		StatefulKnowledgeSession ksession = workflowKnowledgeBase.newStatefulKnowledgeSession();
		myLogger.info("Setting process parameter");
		for (WFParameter wfParameter : wfParameters) {

			// set the parameter name to lower case, remove all spaces and
			// the workflow appendix
			String key = wfParameter.getKey().replace(" ", "").toLowerCase();

			if (key.contains("_")) {
				key = key.substring(0, (key.indexOf("_")));
			}

			// old payload class -> reduced to only string
			String valueS = (String) wfParameter.getValue();
			myLogger.info("Setting parameter >>" + key + "<< to >>" + valueS + "<<");
			ksession.setGlobal(key, valueS);

		}

		myLogger.info("Starting process : " + workflowName);

		instance = ksession.startProcess(workflowName);

		// myLogger.info("Workflow executed sucessfully");

		return instance;
	}

}
