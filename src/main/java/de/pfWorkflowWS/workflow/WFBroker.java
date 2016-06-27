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
package de.pfWorkflowWS.workflow;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pfWorkflowWS.exceptions.EngineNotInitializedException;
import de.pfWorkflowWS.exceptions.WFExecutionFailedException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.common.WFParameterList;
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
public class WFBroker {

	private static volatile WFBroker instance;

	private Logger myLogger;

	private WFBroker() {
		myLogger = LoggerFactory.getLogger(this.getClass());
		myLogger.info("Starting WFBroker");

	}

	public static synchronized WFBroker getInstance() {
		if (instance == null) {
			instance = new WFBroker();
		}
		return instance;
	}

	/**
	 * Initializes the workflow engine with the given parameters in the
	 * {@link ReceiveMessage}.
	 * 
	 * @param wC
	 *            the {@link ReceiveMessage} that provides the needed
	 *            information.
	 * @return the {@link WorkflowEngine} that can execute the workflow.
	 * @throws EngineNotInitializedException
	 */
	public WorkflowEngine initWfEngine(ReceiveMessage wC) throws EngineNotInitializedException {
		myLogger.info("Received WF-Msg");

		myLogger.info("Loading WF with ID (" + wC.getId() + ") from WFRepo");
		String type = wC.getType();

		PubFlow myWF = null;
		Class<? extends WorkflowEngine> clazz = WorkflowEngine.class;

		if (type.equals(WFType.BPMN2.name())) {
			myLogger.info("BPMN2.0 Workflow detected");
			myWF = new JBPMPubflow();
			myWF.setWFID(wC.getId().toString());
			myWF.setWfDef(wC.getWf());
			clazz = JBPMEngine.class;

			// myLogger.info("Name : "+wC.getWorkflowName());

		} else if (type.equals(WFType.BPEL.name())) {
			myLogger.info("BPEL Workflow detected (" + wC.getId() + ")");
			// TODO
			throw new EngineNotInitializedException("BPEL not yet supported");
		} else {
			myLogger.error(wC.getId() + " Workflow NOT deployed >> Type could not be resolved");
			throw new EngineNotInitializedException("Workflow type not supported");
		}

		WorkflowEngine engine = null;

		try {
			myLogger.info("Creating new " + clazz.getCanonicalName() + " for " + wC.getId());
			engine = clazz.newInstance();
			myLogger.info("Instance created for " + wC.getId() + " !");

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}

		myLogger.info("Deploying WF " + wC.getId());
		engine.deployWF(myWF);
		WFParameterList params = wC.getWorkflowParameters();

		if (params != null) {
			myLogger.info(wC.getId() + " Parameter found ...");
			engine.setParams(params);

		} else {
			myLogger.info(wC.getId() + " No Parameter found!");

		}

		return engine;

	}

	/**
	 * Starts the given workflow engine and waits for its completion.
	 * @param engine
	 * @param id the id of the workflow, to provide information for the logging
	 * @throws WFExecutionFailedException
	 */
	public void executeWfEngine(WorkflowEngine engine, UUID id) throws WFExecutionFailedException {
		try {
			myLogger.info("Starting WF " + id + " ...");
			Thread wfEngineThread = new Thread(engine);
			wfEngineThread.start();
			myLogger.info("... engine " + id + " up and running");
			wfEngineThread.join();
			myLogger.info("...engine " + id + " finished ");

		} catch (InterruptedException e) {
			throw new WFExecutionFailedException("Interupted while waiting for completion");
		}
	}
}
