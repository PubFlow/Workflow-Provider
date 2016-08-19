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
package de.pfWorkflowWS.workflow.jbpm;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import org.drools.KnowledgeBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pfWorkflowWS.exceptions.EngineNotInitializedException;
import de.pfWorkflowWS.exceptions.WFExecutionFailedException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.WorkflowEntity;
import de.pfWorkflowWS.workflow.common.WFParameter;
import de.pfWorkflowWS.workflow.common.WFType;
import de.pfWorkflowWS.workflow.engines.JBPMEngine;
import de.pfWorkflowWS.workflow.engines.WorkflowEngine;
import de.pfWorkflowWS.workflow.entity.JBPMPubflow;
import de.pfWorkflowWS.workflow.entity.PubFlow;

/**
 * Responsible for initializing and executing the workflows. Differentiates the
 * workflow types, described by {@link WFType}, through the parameters in the
 * initiating message {@link ReceiveMessage message}
 * 
 * @author Marc Adolf, Peer Brauer
 *
 */
public class JBPMWFBroker {

	private static JBPMWFBroker instance;

	private Logger myLogger;

	private JBPMWFBroker() {
		myLogger = LoggerFactory.getLogger(this.getClass());
		myLogger.info("Starting WFBroker");

	}

	public static synchronized JBPMWFBroker getInstance() {
		if (instance == null) {
			instance = new JBPMWFBroker();
		}
		return instance;
	}

	/**
	 * Initializes the workflow engine with the given parameters in the
	 * {@link ReceiveMessage}.
	 * 
	 * @param workflowCall
	 *            the {@link ReceiveMessage} that provides the needed
	 *            information.
	 * @return the {@link WorkflowEngine} that can execute the workflow.
	 * @throws EngineNotInitializedException
	 */
	public WorkflowEngine initWfEngine(ReceiveMessage workflowCall, KnowledgeBase currentWorkflowKnowledgeBase)
			throws EngineNotInitializedException {
		myLogger.info("Received jBPM (BPMN 2.0) WF-Msg");

		myLogger.info("Loading WF with ID (" +workflowCall.getId() + ") from WFRepo");

		PubFlow myWF = new JBPMPubflow();
		
		myWF.setWFID(workflowCall.getId());

		WorkflowEngine engine = new JBPMEngine();

		myLogger.info("Deploying WF " + workflowCall.getId());
		engine.deployWF(myWF);
		List<WFParameter> params = workflowCall.getWorkflowParameters();

		if (params != null) {
			myLogger.info(workflowCall.getId() + " Parameter found ...");
			engine.setParams(params);

		} else {
			myLogger.info(workflowCall.getId() + " No Parameter found!");

		}

		return engine;

	}

	/**
	 * Starts the given workflow engine and waits for its completion.
	 * 
	 * @param engine
	 * @param id
	 *            the id of the workflow, to provide information for the logging
	 * @throws WFExecutionFailedException
	 */
	public void executeWfEngine(WorkflowEntity entity) throws WFExecutionFailedException {
		Thread.UncaughtExceptionHandler exHandler = new WorkflowExceptionHandler(entity);
		String id = entity.getInitMsg().getId();
		WorkflowEngine engine = entity.getEngine();
		Throwable possibleException;

		try {
			myLogger.info("Starting WF " + id + " ...");
			Thread wfEngineThread = new Thread(engine);
			// get exceptions from workflow engine
			wfEngineThread.setUncaughtExceptionHandler(exHandler);
			wfEngineThread.start();
			myLogger.info("... engine " + id + " up and running");
			wfEngineThread.join();
			String errorString = "";
			possibleException = entity.getUncaughtException();
			if (possibleException != null) {
				errorString = "with errors (" + entity.getUncaughtException().getMessage() + ")";
			}
			myLogger.info("...engine " + id + " finished " + errorString);

		} catch (InterruptedException e) {
			throw new WFExecutionFailedException("Interupted while waiting for completion");
		}
		if (possibleException != null) {
			throw new WFExecutionFailedException(possibleException.getMessage());
		}
	}

	private class WorkflowExceptionHandler implements UncaughtExceptionHandler {
		WorkflowEntity wfEntity;

		WorkflowExceptionHandler(WorkflowEntity wfEntity) {
			this.wfEntity = wfEntity;
		}

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			wfEntity.setUncaughtException(e);
		}

	}
}
