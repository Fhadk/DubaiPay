package com.DubaiPay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class SSL {
	/**
	 * Path to the clients keystore
	 */
	private static final String CLIENT_KEYSTORE_PATH = "C:\\D\\Upwork\\Epay\\ePay5Client.jks";

	/**
	 * Password for the clients keystore
	 */
	private static final String CLIENT_KEYSTORE_PASSWORD = "Change1$$";

	/**
	 * The servers certificate's alias within the clients keystore.
	 */
//	private static final String SERVER_CERTIFICATE_ALIAS = "C:\\D\\Upwork\\Epay\\epay5.simulator.pfx";   // need to look into that

	private static final String SERVER_CERTIFICATE_ALIAS = "epay5.qa.dubai.ae";   // need to look into that
	
	/**
	 * URL to our SOAP UI service
	 */
	private static final String SOAP_URI = "https://epayment.qa.dubai.ae/ePayHub/WSDL/PaymentAPIService.wsdl";

	private static final String URN = "urn:examples:helloservice";   // need to look into that

	/**
	 * Loads the KeyStore using the default KeyStore type (JKS) with the given file
	 * path and password.
	 * 
	 * @param filePath
	 *            Path to the KeyStore.
	 * @param password
	 *            The KeyStores password.
	 * @return Returns the KeyStore.
	 * @throws NoSuchAlgorithmException
	 * @see Keystore#load(InputStream, char[])
	 * @throws CertificateException
	 * @see Keystore#load(InputStream, char[])
	 * @throws IOException
	 * @see FileInputStream()
	 * @throws KeyStoreException
	 * @see Keystore#getInstance()
	 */
	private KeyStore loadKeystore(String filePath, char[] password)
			throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {

		FileInputStream is = new FileInputStream(new File(filePath));

		final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

		keystore.load(is, password);

		return keystore;
	}

	public void run() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException,
			KeyManagementException, UnrecoverableKeyException, SOAPException {

		/*
		 * Load the keystore
		 */
		char[] password = CLIENT_KEYSTORE_PASSWORD.toCharArray();
		KeyStore keystore = loadKeystore(CLIENT_KEYSTORE_PATH, password);

		/*
		 * Get the servers trusted certificate.
		 */
		final Certificate trusted = keystore.getCertificate(SERVER_CERTIFICATE_ALIAS);

		/*
		 * Create a trust manager that validates the servers certificate
		 */
		TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {

				if (certs == null || certs.length == 0) {
					throw new IllegalArgumentException("null or zero-length certificate chain");
				}

				if (authType == null || authType.length() == 0) {
					throw new IllegalArgumentException("null or zero-length authentication type");
				}

				// check if certificate sent is your CA's

				if (!certs[0].equals(trusted)) {

					// check if its been signed by the CA

					try {
						certs[0].verify(trusted.getPublicKey());
					} catch (Exception e) {
						throw new CertificateException(e);
					}
				}

				certs[0].checkValidity();
			}
		} };

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

		kmf.init(keystore, password);

		// set the trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(kmf.getKeyManagers(), trustManager, new java.security.SecureRandom());

		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// create an all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		// setup an example soap message

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		SOAPEnvelope envelope = soapPart.getEnvelope();

		// envelope.addNamespaceDeclaration("example", SOAP_URI);
		// envelope.addNamespaceDeclaration("urn", URN);

		// SOAP Envelope

		
		
		
		/**
		 * <pre>
		 * 		<soapenv:Envelope 
		 * 				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		 * 				xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
		 * 				xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
		 * 				xmlns:urn="urn:examples:helloservice">
		 * 		   <soapenv:Header/>
		 * 		   <soapenv:Body>
		 * 		      <urn:sayHello soapenv:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
		 * 		         <firstName xsi:type="xsd:string">Example First Name</firstName>
		 * 		      </urn:sayHello>
		 * 		   </soapenv:Body>
		 * 		</soapenv:Envelope>
		 * </pre>
		 */
		
		
		
		
		
		envelope.setAttribute("xmlns:com", "http://dsg.dubai.gov.ae/schema/common");
		envelope.setAttribute("xmlns:epay", "http://dsg.dubai.gov.ae/schema/epay");
		envelope.setAttribute("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");

		// Setting Header
		SOAPHeader soapHeader = envelope.getHeader();
		SOAPElement security = soapHeader.addChildElement("Security", "wsse",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

		// security.setAttribute("soapenv:mustUnderstand","1");

		security.setAttribute("xmlns:wsu",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

		SOAPElement timeStamp = security.addChildElement("Timestamp", "wsu");
		timeStamp.setAttribute("wsu:Id", "TS-34");

		SOAPElement created = timeStamp.addChildElement("Created", "wsu");
		created.addTextNode(LocalDateTime.now().toString());

		SOAPElement expried = timeStamp.addChildElement("Expires", "wsu");
		expried.addTextNode(LocalDateTime.now().plusHours(2).toString());

		// Header End Here

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();

		SOAPElement generateTransactionToken = soapBody.addChildElement("generateTransactionToken", "epay");
		SOAPElement transactionInfo = generateTransactionToken.addChildElement("transactionInfo", "epay");

		SOAPElement spCode = transactionInfo.addChildElement("spCode", "epay");
		spCode.addTextNode("ZFND");

		SOAPElement servCode = transactionInfo.addChildElement("servCode", "epay");
		servCode.addTextNode("zakat");

		SOAPElement sptrn = transactionInfo.addChildElement("sptrn", "epay");
		int randomNum = ThreadLocalRandom.current().nextInt(99999, 999999999 + 1);
		sptrn.addTextNode(String.valueOf(randomNum));

		SOAPElement amount = transactionInfo.addChildElement("amount", "epay");
		amount.setAttribute("currency", "AED");
		amount.addTextNode("10.00");

		SOAPElement timestamp = transactionInfo.addChildElement("timestamp", "epay");
		timestamp.addTextNode(LocalDateTime.now(ZoneOffset.UTC).toString() + "+05:00"); // need to fix this

		SOAPElement description = transactionInfo.addChildElement("description", "epay");
		description.addTextNode("Test Transaction");

		SOAPElement type = transactionInfo.addChildElement("type", "epay");
		type.addTextNode("sale");

		SOAPElement versionCode = transactionInfo.addChildElement("versionCode", "epay");
		versionCode.addTextNode("2.1");

		SOAPElement paymentChannel = transactionInfo.addChildElement("paymentChannel", "epay");
		paymentChannel.addTextNode("100");

		SOAPElement userInfo = generateTransactionToken.addChildElement("userInfo", "epay");

		SOAPElement isAuthenticated = userInfo.addChildElement("isAuthenticated", "epay");
		isAuthenticated.addTextNode("false");

		SOAPElement userId = userInfo.addChildElement("userId", "epay");
		userId.addTextNode("32396");

		SOAPElement userName = userInfo.addChildElement("userName", "epay");
		userName.addTextNode("tester001");

		SOAPElement fullNameEn = userInfo.addChildElement("fullNameEn", "epay");
		fullNameEn.addTextNode("ing-e ca VanNister");

		SOAPElement fullNameAr = userInfo.addChildElement("fullNameAr", "epay");
		fullNameAr.addTextNode("first mid last");

		SOAPElement mobileNo = userInfo.addChildElement("mobileNo", "epay");
		mobileNo.addTextNode("97150332968");

		SOAPElement email = userInfo.addChildElement("email", "epay");
		email.addTextNode("tstuser0@rta.com");

		SOAPElement emirateCode = userInfo.addChildElement("emirateCode", "epay");
		emirateCode.addTextNode("DXB");

		SOAPElement poBox = userInfo.addChildElement("poBox", "epay");
		poBox.addTextNode("PO");

		soapMessage.saveChanges();

		// SOAPBody soapBody = envelope.getBody();
		// SOAPElement soapBodyElem = soapBody.addChildElement("sayHello", "urn");
		// SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("firstName");
		// soapBodyElem1.addTextNode("Example First Name");
		//
		// MimeHeaders headers = soapMessage.getMimeHeaders();
		// headers.addHeader("SOAPAction", SOAP_URI + "VerifyEmail");
		// soapMessage.saveChanges();

		// send to the server
		URL url = new URL(SOAP_URI);
		URLConnection con = url.openConnection();
		con.setDoOutput(true);
		soapMessage.writeTo(con.getOutputStream());

		// read in response and print it to screen
		Reader reader = new InputStreamReader(con.getInputStream());

		while (true) {

			int ch = reader.read();
			if (ch == -1) {
				break;
			}

			System.out.print((char) ch);
		}

	}

	public static void main(String[] args) throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException, SOAPException {

		SSL test = new SSL();
		test.run();
	}

}
