import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import de.pfWorkflowWS.exceptions.DuplicateIDException;
import de.pfWorkflowWS.exceptions.WFException;
import de.pfWorkflowWS.exceptions.WFExecutionFailedException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.common.WFType;
import de.pfWorkflowWS.workflow.management.WorkflowEntity.ExecutionState;
import de.pfWorkflowWS.workflow.management.WorkflowManager;

/**
 * 
 * @author Marc Adolf
 *
 */
public class WorkflowManagerTest {
	ReceiveMessage recvMessage;
	WorkflowManager wfMan;

	@Before 
	public void setup() throws URISyntaxException{
		wfMan = WorkflowManager.getInstance();
		recvMessage = new ReceiveMessage();
		recvMessage.setCallbackAdress(new URI("se.informatik.uni-kiel.de"));
		recvMessage.setId(UUID.randomUUID());
		recvMessage.setWf(new byte[10]);
		recvMessage.setType(WFType.BPMN2.toString());

	}
	
	@Test
	public void addWorkflowSuccess() throws DuplicateIDException{
		wfMan.addWorkflowEntity(recvMessage);
		assertTrue(wfMan.lookupWorkflowEntryId(recvMessage.getId()).getInitMsg().equals(recvMessage));
	}
	
	@Test(expected=DuplicateIDException.class)
	public void addWorkflowDuplicate() throws DuplicateIDException{
		wfMan.addWorkflowEntity(recvMessage);
		wfMan.addWorkflowEntity(recvMessage);

	}

	@Test
	(expected=WFExecutionFailedException.class)
	public void startWorkflowExceptionAndErrorState() throws WFException{
		wfMan.addWorkflowEntity(recvMessage);
		wfMan.startWorkflow(recvMessage.getId());
		assertTrue(wfMan.lookupWorkflowEntryId(recvMessage.getId()).getState().equals(ExecutionState.error));
	}
}