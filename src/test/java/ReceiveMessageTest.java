import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import de.pfWorkflowWS.restConnection.restMessages.WorkflowReceiveMessage;

public class ReceiveMessageTest {
	@Test
	public void testIsValidSuccess() {
		WorkflowReceiveMessage recvMessage = new WorkflowReceiveMessage();
		recvMessage.setId(UUID.randomUUID().toString());
		recvMessage.setCallbackAddress("http://se.informatik.uni-kiel.de");
		assertTrue(recvMessage.isValid());
	}
	
	@Test
	public void testIsValidFail() {
		WorkflowReceiveMessage recvMessage = new WorkflowReceiveMessage();
		//missing id
		recvMessage.setCallbackAddress("http://se.informatik.uni-kiel.de");
		assertFalse(recvMessage.isValid());
	}
}
