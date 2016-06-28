import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import de.pfWorkflowWS.exceptions.EngineNotInitializedException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.WFBroker;
import de.pfWorkflowWS.workflow.common.WFType;
import de.pfWorkflowWS.workflow.engines.JBPMEngine;
import de.pfWorkflowWS.workflow.engines.WorkflowEngine;

public class WFBrokerTest {
	WFBroker broker;
	ReceiveMessage recvMessageBPMN2;
	ReceiveMessage recvMessageBPEL;
	ReceiveMessage recvMessageSomething;

	@Before
	public void setup() throws URISyntaxException {
		broker = WFBroker.getInstance();
		recvMessageBPMN2 = new ReceiveMessage();
		recvMessageBPMN2.setCallbackAdress(new URI("se.informatik.uni-kiel.de"));
		recvMessageBPMN2.setId(UUID.randomUUID());
		recvMessageBPMN2.setWf(new byte[10]);
		recvMessageBPMN2.setType(WFType.BPMN2.toString());

		recvMessageBPEL = new ReceiveMessage();
		recvMessageBPEL.setCallbackAdress(new URI("se.informatik.uni-kiel.de"));
		recvMessageBPEL.setId(UUID.randomUUID());
		recvMessageBPEL.setWf(new byte[10]);
		recvMessageBPEL.setType(WFType.BPEL.toString());

		recvMessageSomething = new ReceiveMessage();
		recvMessageSomething.setCallbackAdress(new URI("se.informatik.uni-kiel.de"));
		recvMessageSomething.setId(UUID.randomUUID());
		recvMessageSomething.setWf(new byte[10]);
		recvMessageSomething.setType("some unsopported engine");

	}

	@Test
	public void initWfEngineTestBPMn2() throws EngineNotInitializedException {
		WorkflowEngine engine = broker.initWfEngine(recvMessageBPMN2);
		assertTrue(engine.getClass().equals(JBPMEngine.class));

	}

	// has to be adapted if BPEL is implemented in the future
	@Test(expected = EngineNotInitializedException.class)
	public void initWfEngineTestBPEL() throws EngineNotInitializedException {
		WorkflowEngine engine = broker.initWfEngine(recvMessageBPEL);
		// assertTrue(engine.getClass().equals(BPELEngine.class));

	}

	@Test(expected = EngineNotInitializedException.class)
	public void initWFEngineTestUnsupportedType() throws EngineNotInitializedException {
		broker.initWfEngine(recvMessageSomething);

	}
	
	//Hard to test without executing a real WF  
//	@Test(expected=WFExecutionFailedException.class)
//	public void executeEngineExceptionTest() throws EngineNotInitializedException, WFExecutionFailedException{
//		UUID id = recvMessageBPMN2.getId();
//		WorkflowEngine engine = broker.initWfEngine(recvMessageBPMN2);
//		
//		broker.executeWfEngine(engine, id);
//		System.out.println("Done");
//	}
}
