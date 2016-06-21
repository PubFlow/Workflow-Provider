package de.pfWorkflowWS.workflow;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pfWorkflowWS.exceptions.WFException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.common.WFParameterList;
import de.pfWorkflowWS.workflow.common.WFType;
import de.pfWorkflowWS.workflow.engines.JBPMEngine;
import de.pfWorkflowWS.workflow.engines.WorkflowEngine;
import de.pfWorkflowWS.workflow.entity.JBPMPubflow;
import de.pfWorkflowWS.workflow.entity.PubFlow;



public class WFBroker {
	
	private static volatile WFBroker instance;

	private Logger myLogger;

private WFBroker(){
		myLogger = LoggerFactory.getLogger(this.getClass());	
		myLogger.info("Starting WFBroker");

	}


	public static synchronized WFBroker getInstance(){
		if(instance == null){
			instance = new WFBroker();
		}
		return instance;
	}

	public void receiveWFCall(ReceiveMessage wC){
		myLogger.info("Received WF-Msg");

		
		myLogger.info("Loading WF with ID (" + wC.getId() + ") from WFRepo");
		String type = wC.getType();

		PubFlow myWF = null;
	    Class<? extends WorkflowEngine> clazz = WorkflowEngine.class;
		
		if(type.equals(WFType.BPMN2.name())){
			myLogger.info("BPMN2.0 Workflow detected");
			myWF = new JBPMPubflow();
			myWF.setWFID(wC.getId().toString());
			myWF.setWfDef(wC.getWf());
			clazz = JBPMEngine.class;

			//myLogger.info("Name : "+wC.getWorkflowName());
			//TODO fill var

		}else if (type.equals(WFType.BPEL.name())) {
			myLogger.info("BPEL Workflow detected");
			//TODO
			return;
		}else{
			myLogger.error("Workflow NOT deployed >> Type could not be resolved");
			return;
		}


		WorkflowEngine engine = null;


        try {
            myLogger.info("Creating new "+clazz.getCanonicalName());
            engine = clazz.newInstance();
            myLogger.info("Instance created! ");

        }
        catch (ReflectiveOperationException e){
            e.printStackTrace();
        }

        try {
			myLogger.info("Deploying WF");
			engine.deployWF(myWF);
			WFParameterList params = wC.getWorkflowParameters();

			if (params!=null){
				myLogger.info("Parameter found ...");
				engine.setParams(params);

			}else{
				myLogger.info("No Parameter found!");

			}
			myLogger.info("Starting WF ...");
			Thread wfEngineThread = new Thread(engine);
			wfEngineThread.start();
			myLogger.info("... engine up and running");

		} catch (WFException e) {
			e.printStackTrace();
		}
	}
}
