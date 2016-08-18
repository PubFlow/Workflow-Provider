import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;

public class ReceiveMessageTest {
	@Test
	public void testIsValidSuccess() {
		ReceiveMessage recvMessage = new ReceiveMessage();
		recvMessage.setId(UUID.randomUUID().toString());
		recvMessage.setType("BPMN2");
		recvMessage.setCallbackAddress("http://se.informatik.uni-kiel.de");
		assertTrue(recvMessage.isValid());
	}
	
	@Test
	public void testIsValidFail() {
		ReceiveMessage recvMessage = new ReceiveMessage();
		//missing id
		recvMessage.setType("BPMN2");
		recvMessage.setCallbackAddress("http://se.informatik.uni-kiel.de");
		assertFalse(recvMessage.isValid());
	}
}
