import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import de.pfWorkflowWS.exceptions.EngineNotInitializedException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.engines.JBPMEngine;
import de.pfWorkflowWS.workflow.engines.WorkflowEngine;
import de.pfWorkflowWS.workflow.jbpm.JBPMWFBroker;

public class WFBrokerTest {
	JBPMWFBroker broker;
	ReceiveMessage recvMessageBPMN2;
	ReceiveMessage recvMessageBPEL;
	ReceiveMessage recvMessageSomething;

	@Before
	public void setup() {
		broker = JBPMWFBroker.getInstance();
		recvMessageBPMN2 = new ReceiveMessage();
		recvMessageBPMN2.setCallbackAddress("http://se.informatik.uni-kiel.de");
		recvMessageBPMN2.setId(UUID.randomUUID().toString());

		recvMessageBPEL = new ReceiveMessage();
		recvMessageBPEL.setCallbackAddress("http://se.informatik.uni-kiel.de");
		recvMessageBPEL.setId(UUID.randomUUID().toString());

		recvMessageSomething = new ReceiveMessage();
		recvMessageSomething.setCallbackAddress("http://se.informatik.uni-kiel.de");
		recvMessageSomething.setId(UUID.randomUUID().toString());

	}

	// old tests

	// @Test
	// public void initWfEngineTestBPMn2() throws EngineNotInitializedException
	// {
	// WorkflowEngine engine = broker.initWfEngine(recvMessageBPMN2);
	// assertTrue(engine.getClass().equals(JBPMEngine.class));
	//
	// }
	//
	// // has to be adapted if BPEL is implemented in the future
	// @Test(expected = EngineNotInitializedException.class)
	// public void initWfEngineTestBPEL() throws EngineNotInitializedException {
	// @SuppressWarnings("unused")
	// WorkflowEngine engine = broker.initWfEngine(recvMessageBPEL);
	// // assertTrue(engine.getClass().equals(BPELEngine.class));
	//
	// }
	//
	// @Test(expected = EngineNotInitializedException.class)
	// public void initWFEngineTestUnsupportedType() throws
	// EngineNotInitializedException {
	// broker.initWfEngine(recvMessageSomething);
	//
	// }
	//
	// Hard to test without executing a real WF
	// @Test(expected=WFExecutionFailedException.class)
	// public void executeEngineExceptionTest() throws
	// EngineNotInitializedException, WFExecutionFailedException{
	// UUID id = recvMessageBPMN2.getId();
	// WorkflowEngine engine = broker.initWfEngine(recvMessageBPMN2);
	//
	// broker.executeWfEngine(engine, id);
	// System.out.println("Done");
	// }
}
