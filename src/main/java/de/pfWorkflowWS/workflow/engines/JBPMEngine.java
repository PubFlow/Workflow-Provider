/**
 * Copyright (C) 2016 Marc Adolf, Arnd Plumhoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.pfWorkflowWS.workflow.engines;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
//import org.drools.builder.KnowledgeBuilder;
//import org.drools.builder.KnowledgeBuilderFactory;
//import org.drools.builder.ResourceType;
//import org.drools.io.ResourceFactory;
//import org.drools.runtime.StatefulKnowledgeSession;
//import org.drools.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pfWorkflowWS.exceptions.WFOperationNotSupported;
import de.pfWorkflowWS.restConnection.restMessages.EventMessage;
import de.pfWorkflowWS.workflow.common.ParameterType;
import de.pfWorkflowWS.workflow.common.WFParameter;
import de.pfWorkflowWS.workflow.common.WFType;
import de.pfWorkflowWS.workflow.entity.JBPMPubflow;
import de.pfWorkflowWS.workflow.entity.PubFlow;

/**
 * 
 * @author Peer Brauer
 *
 */
public class JBPMEngine extends WorkflowEngine {

	private JBPMPubflow myWF;
	List<WFParameter> parameter;
	private StatefulKnowledgeSession ksession;
	static Logger myLogger;
	// private ProcessInstance processInstance = null;
	private KnowledgeBase kbase = null;

	/**
	 * @return the myWF
	 */
	public synchronized PubFlow getMyWF() {
		return myWF;
	}

	/**
	 * @param myWF
	 *            the myWF to set
	 */
	public synchronized void setMyWF(PubFlow myWF) {
		if (!(myWF instanceof JBPMPubflow)) {
			myLogger.error("Wrong workflow type!");
			return;
		}
		this.myWF = (JBPMPubflow) myWF;
	}

	/**
	 * @return the parameter
	 */
	public synchronized List<WFParameter> getParameter() {
		return parameter;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public synchronized void setParameter(List<WFParameter> parameter) {
		this.parameter = parameter;
	}

	static {
		myLogger = LoggerFactory.getLogger(JBPMEngine.class);
	}

	public JBPMEngine() {

	}

	public JBPMEngine(JBPMPubflow wf) {

		myWF = wf;
	}

	@Override
	public void deployWF(PubFlow wf){
		myWF = (JBPMPubflow) wf;
	}

	@Override
	public void undeployWF(long wfID) {
		throw new WFOperationNotSupported();
	}

	@Override
	public void stopWF(long wfID) {
		throw new WFOperationNotSupported();
	}

	/**
	 * Loads a process (processType BPMN2.0!) from the given location in a new
	 * knowledgeBase and returns the knowledgebase
	 * 
	 * @param processFile
	 *            (String) : the absolute filename
	 * @return (KnowledgeBase) : the KnowledgeBase
	 * @throws Exception
	 */
	private void createKnowledgeBase(JBPMPubflow wf){
		myLogger.info("Trying to add WF to knowledgebase");
		KnowledgeBuilder kbuilder = null;
		try {
			kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newByteArrayResource(wf.getWfDef()), ResourceType.BPMN2);
			myLogger.info("Knowledgebase created");
		}

		catch (Exception e) {
			myLogger.error("Couldn't create knowledgebase");
		}
		kbase = kbuilder.newKnowledgeBase();
		ksession = kbase.newStatefulKnowledgeSession();

	}

	/**
	 * Starts a given process in its knowledge base env and returns the process
	 * instance
	 * 
	 * @param kbase
	 *            (KnowledgeBase) : the knowledge base the process was added to
	 * @param processID
	 *            (String) : the id of the process (The one defined in the
	 *            process file - NOT the PubFlow ID)
	 * @return (ProcessInstance) : the instance of the running workflow
	 * @throws Exception
	 */
	private void runWF(){
		myLogger.info("Trying to start workflow: " + myWF.getWFID());
		List<WFParameter> params = parameter;
//		ProcessInstance instance = null;
		try {
			myLogger.info("Creating Knowledgebase ...");
			myLogger.info("Setting process parameter");
			for (WFParameter wfParam : params) {

				// set the parameter name to lower case, remove all spaces and
				// the workflow appendix
				String key = wfParam.getKey().replace(" ", "").toLowerCase();

				if (key.contains("_")) {
					key = key.substring(0, (key.indexOf("_")));
				}

				ParameterType payloadClazz = wfParam.getPayloadClazz();

					switch (payloadClazz) {
					case INTEGER:
						int valueI = ((Integer) wfParam.getValue()).intValue();
						myLogger.info("Setting parameter >>" + key + "<< to >>" + valueI + "<<");
						ksession.setGlobal(key, valueI);
						break;
					case STRING:
						String valueS = (String) wfParam.getValue();
						myLogger.info("Setting parameter >>" + key + "<< to >>" + valueS + "<<");
						ksession.setGlobal(key, valueS);
						break;
					case DOUBLE:
						double valueD = ((Double) wfParam.getValue()).doubleValue();
						myLogger.info("Setting parameter >>" + key + "<< to >>" + valueD + "<<");
						ksession.setGlobal(key, valueD);
						break;
					case LONG:
						long valueL = ((Long) wfParam.getValue()).longValue();
						myLogger.info("Setting parameter >>" + key + "<< to >>" + valueL + "<<");
						ksession.setGlobal(key, valueL);
						break;
					default:
						break;
					}
			}

			myLogger.info("Starting process : " + myWF.getWFID());

//			instance = ksession.startProcess(myWF.getWFID());
			ksession.startProcess(myWF.getWFID());

			myLogger.info("Workflow executed sucessfuly");
		} catch (Exception ex) {
			myLogger.error("Couldn't start workflow");
			throw new CompletionException(ex);
		}
		// processInstance = instance;
	}

	@Override
	public List<WFType> getCompatibleWFTypes() {
		List<WFType> result = new ArrayList<WFType>();
		result.add(WFType.BPMN2);
		return result;
	}

	@Override
	public void run(){
			myLogger.info("Starting ...");
			createKnowledgeBase((JBPMPubflow) myWF);
			runWF();
			myLogger.info("Success!");
	}

	@Override
	public void setParams(List<WFParameter> params){
		parameter = params;

	}

	@Override
	public synchronized void handleEvent(EventMessage msg) {
		ksession.signalEvent(msg.getEventType(), msg.getData());
	}

}
