import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Test;

import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;

public class ReceiveMessageTest {
	@Test
	public void testIsValidSuccess() throws URISyntaxException {
		ReceiveMessage recvMessage = new ReceiveMessage();
		recvMessage.setId(UUID.randomUUID());
		recvMessage.setType("BPMN2");
		recvMessage.setWf(new byte[9]);
		recvMessage.setCallbackAdress(new URI("se.informatik.uni-kiel.de"));
		assertTrue(recvMessage.isValid());
	}
	
	@Test
	public void testIsValidFail() throws URISyntaxException {
		ReceiveMessage recvMessage = new ReceiveMessage();
		//missing id
		recvMessage.setType("BPMN2");
		recvMessage.setWf(new byte[9]);
		recvMessage.setCallbackAdress(new URI("se.informatik.uni-kiel.de"));
		assertFalse(recvMessage.isValid());
	}
}
