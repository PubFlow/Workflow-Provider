package de.pfWorkflowWS.workflow.jbpm;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JBPMWorkflow {

	private Logger myLogger;
	private byte[] bpmn;

	public JBPMWorkflow(byte[] bpmn) {
		myLogger = LoggerFactory.getLogger(this.getClass());
		this.bpmn = bpmn;
		createKnowledgeBase();
	}

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

	public KnowledgeBase getWorkflowKnowledgeBase() {
		return workflowKnowledgeBase;
	}

	private KnowledgeBase workflowKnowledgeBase;

}
