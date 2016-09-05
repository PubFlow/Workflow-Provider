package de.pfWorkflowWS.workflow.jbpm.availableWorkflows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import de.pfWorkflowWS.restConnection.restMessages.ResponseMessage;
import de.pfWorkflowWS.restConnection.restMessages.WorkflowReceiveMessage;
import de.pfWorkflowWS.workflow.WorkflowEntity;
import de.pfWorkflowWS.workflow.common.WFParameter;

/**
 * Represents a general JBPM Workflow and creates a {@link KnowledgeBase} for
 * it. Extending classes may be designed as a singleton to avoid multiple costly
 * creations of the {@link KnowledgeBase}.
 * 
 * @author Marc Adolf
 *
 */
public abstract class JBPMWorkflow {

	Logger myLogger;
	private byte[] bpmn;
	private KnowledgeBase workflowKnowledgeBase;
	private String workflowName;
	private boolean isInit = false;
	private String fileName;

	public JBPMWorkflow(String fileName, String workflowName) {
		myLogger = LoggerFactory.getLogger(this.getClass());
		this.workflowName = workflowName;
		this.fileName = fileName;
	}

	abstract public void handleResult(WorkflowEntity entity);

	/**
	 * Loads the Workflow from a predefined file and creates a {@link KnowledgeBase}
	 * @throws IOException
	 */
	public void init() throws IOException {
		if (!isInit) {
			this.bpmn = readFile(new File(fileName));
			createKnowledgeBase();
			isInit = true;
		}
	}

	/**
	 * Loads a process (processType BPMN2.0!) from the given location in a new
	 * knowledgeBase
	 */
	private void createKnowledgeBase() {
		myLogger.info("Trying to add WF to knowledgebase");
		KnowledgeBuilder kbuilder = null;
		System.out.println("name:" +fileName);
		System.out.println("file:"+ bpmn);
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

	/**
	 * Creates a new {@link StatefulKnowledgeSession} and executes it
	 * 
	 * @param workflowEntity
	 * @throws IOException,
	 *             if the BPMN file could not be loaded
	 */
	public void startNewWorkflowSession(WorkflowEntity workflowEntity) throws IOException {
		myLogger.info("Trying to start workflow " + workflowName);
		List<WFParameter> wfParameters = workflowEntity.getInitMsg().getWorkflowParameters();

		// in case it wasn't done before
		init();

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

			// legacy 'payload' class was reduced to only use String
			String valueS = (String) wfParameter.getValue();
			myLogger.info("Setting parameter >>" + key + "<< to >>" + valueS + "<<");
			ksession.setGlobal(key, valueS);

		}

		myLogger.info("Starting process : " + workflowName);

		ksession.startProcess(workflowName);

		myLogger.info("Workflow executed (sucessfully)");

	}

	private byte[] readFile(File f) throws IOException {
		try {
			FileInputStream fis = new FileInputStream(f);
			byte[] fileContent = new byte[(int) f.length()];
			fis.read(fileContent);
			fis.close();
			return fileContent;

		} catch (Exception e) {
			throw new IOException("Unable to read file " + f.getName());
		}
	}

	/**
	 * Handles occurrences of errors during the execution according to each
	 * Workflow definition. May restart the Workflow or just create an
	 * appropriate answer.
	 * This way may not be be consistent to the messaging inside the BPMN Workflows
	 *  
	 * @param entity
	 *            in which the error occurred.
	 */
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

}
