package de.pfWorkflowWS.authentication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth.common.signature.RSAKeySecret;
import org.springframework.security.oauth.common.signature.RSA_SHA1SignatureMethod;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.security.oauth.consumer.net.DefaultOAuthURLStreamHandlerFactory;

@Configuration
@PropertySource("classpath:oauth.properties")
public class ServiceJiraAuthentication {

	@Value("${pubflow.SERVER_URL}")
	private String SERVER_URL;

	@Value("${pubflow.SERVLET_URL}")
	private String SERVLET_URL;

	@Value("${pubflow.URL_OAUTH_AUTHZ}")
	private String URL_OAUTH_AUTHZ;

	@Value("${pubflow.URL_OAUTH_REQUEST}")
	private String URL_OAUTH_REQUEST;

	@Value("${pubflow.CONSUMER_KEY}")
	private String CONSUMER_KEY;

	private PrivateKey CONSUMER_SECRET;
	private PublicKey PUBLIC_KEY;
	private final String SIGNATURE_METHOD = RSA_SHA1SignatureMethod.SIGNATURE_NAME;
	private OAuthConsumerToken requestToken;
	private final OAuthConsumerToken accessToken = new OAuthConsumerToken();

	private ProtectedResourceDetails resource;

	public ServiceJiraAuthentication createConfiguration() {
		return new ServiceJiraAuthentication();
	}

	public void authenticate() {
		// Read the private and public key
		
			try {
				this.readKeyPair("");
				this.retrieveRequestToken();
				this.retrieveAccessToken();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	public void retrieveRequestToken()
			throws OAuthRequestFailedException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		// create default Consumer
		final CoreOAuthConsumerSupport localConsumerSupport = new CoreOAuthConsumerSupport();
		localConsumerSupport.setStreamHandlerFactory(new DefaultOAuthURLStreamHandlerFactory());

		// set the resource to connect
		this.setResource(createResource());

		// retrieve request token
		this.requestToken = localConsumerSupport.getUnauthorizedRequestToken(this.getResource(), null);
	}

	private ProtectedResourceDetails createResource() {
		BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
		resource.setId("pubflow");
		resource.setConsumerKey(this.CONSUMER_KEY);
		resource.setSharedSecret(new RSAKeySecret(this.getPrivateKey(), this.getPublicKey()));
		resource.setSignatureMethod(SIGNATURE_METHOD);
		resource.setUse10a(true);
		resource.setRequestTokenURL(SERVER_URL + SERVLET_URL + URL_OAUTH_REQUEST);
		resource.setUserAuthorizationURL(SERVER_URL + SERVLET_URL + URL_OAUTH_REQUEST);
		resource.setAcceptsAuthorizationHeader(true);

		return resource;
	}

	private void retrieveAccessToken() {
		this.accessToken.setValue("");
		this.accessToken.setSecret(this.getRequestToken().getSecret());
		this.accessToken.setAccessToken(true);
	}

	private void setPrivateKey(PrivateKey priavteKey) {
		this.CONSUMER_SECRET = priavteKey;
	}

	private PrivateKey getPrivateKey() {
		return this.CONSUMER_SECRET;
	}

	private void setPublicKey(PublicKey publicKey) {
		this.PUBLIC_KEY = publicKey;
	}

	private PublicKey getPublicKey() {
		return this.PUBLIC_KEY;
	}

	private void setResource(ProtectedResourceDetails resource) {
		this.resource = resource;
	}

	protected ProtectedResourceDetails getResource() {
		return this.resource;
	}

	private void readKeyPair(String path)
			throws NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, IOException {
		Security.addProvider(new BouncyCastleProvider());

		KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
		try {
			this.setPrivateKey(generatePrivateKey(factory, path + "id_rsa"));
			this.setPublicKey(generatePublicKey(factory, path + "id_rsa.pub"));
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	private PrivateKey generatePrivateKey(KeyFactory factory, String filename)
			throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemObject pemFile = readPem(filename);
		byte[] content = pemFile.getContent();
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
		return factory.generatePrivate(privKeySpec);
	}

	private PublicKey generatePublicKey(KeyFactory factory, String filename)
			throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemObject pemFile = readPem(filename);
		byte[] content = pemFile.getContent();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		return factory.generatePublic(pubKeySpec);
	}

	private PemObject readPem(String filename) throws IOException {
		PemReader pemReader = new PemReader(new InputStreamReader(new FileInputStream(filename)));
		try {
			return pemReader.readPemObject();
		} finally {
			pemReader.close();
		}
	}

	private OAuthConsumerToken getRequestToken() {
		return this.requestToken;
	}

	protected OAuthConsumerToken getAccessToken() {
		return this.accessToken;
	}

}
