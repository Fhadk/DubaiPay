package com.DubaiPay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.soap.*;

/**
 * @author Fhadk
 *
 */
public class App {

	// SAAJ - SOAP Client Testing
	public static void main(String args[]) {
		/*
		 * The example below requests from the Web Service at:
		 * http://www.webservicex.net/uszip.asmx?op=GetInfoByCity
		 * 
		 * 
		 * To call other WS, change the parameters below, which are: - the SOAP Endpoint
		 * URL (that is, where the service is responding from) - the SOAP Action
		 * 
		 * Also change the contents of the method createSoapEnvelope() in this class. It
		 * constructs the inner part of the SOAP envelope that is actually sent.
		 */
		String soapEndpointUrl = "https://epayment.qa.dubai.ae/ePayHub/WSDL/PaymentAPIService.wsdl";
		String soapAction = "http://dsg.dubai.gov.ae/ws/epay/generateTransactionToken";

//		System.out.println(LocalDateTime.now(ZoneOffset.UTC).toString());
		
//		LocalDateTime ldt = LocalDateTime.now();
//		System.out.println(ldt.toLocalTime());
//
//		ZonedDateTime ldtZoned = ldt.atZone(ZoneId.systemDefault());
//
//		ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("GMT"));
//
//		System.out.println(utcZoned.toLocalTime());
		
		
		callSoapWebService(soapEndpointUrl, soapAction);
		
		
//		new PaymentAPIServiceStub().generateTransactionToken();
	}

	private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// String myNamespace = "myNamespace";
		// String myNamespaceURI = "http://dsg.dubai.gov.ae/ws/epay";

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.setAttribute("xmlns:com", "http://dsg.dubai.gov.ae/schema/common");
		envelope.setAttribute("xmlns:epay", "http://dsg.dubai.gov.ae/schema/epay");
		envelope.setAttribute("xmlns:soapenv","http://schemas.xmlsoap.org/soap/envelope/");
		
		// Setting Header
		SOAPHeader soapHeader = envelope.getHeader();
		SOAPElement security = soapHeader.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

//		security.setAttribute("soapenv:mustUnderstand","1");
	
		 security.setAttribute("xmlns:wsu","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

		 SOAPElement timeStamp = security.addChildElement("Timestamp", "wsu");
		 timeStamp.setAttribute("wsu:Id", "TS-34");
		 
		 SOAPElement created = timeStamp.addChildElement("Created", "wsu");
		 created.addTextNode(LocalDateTime.now().toString());
		 
		 SOAPElement expried = timeStamp.addChildElement("Expires","wsu");
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
		timestamp.addTextNode(LocalDateTime.now(ZoneOffset.UTC).toString()+"+05:00");  // need to fix this
		
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

	}

	private static void callSoapWebService(String soapEndpointUrl, String soapAction) {
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);

			// Print the SOAP Response
			System.out.println("Response SOAP Message:");
			soapResponse.writeTo(System.out);
			System.out.println();

			soapConnection.close();
		} catch (Exception e) {
			System.err.println(
					"\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
			e.printStackTrace();
		}
	}

	private static SOAPMessage createSOAPRequest(String soapAction) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSoapEnvelope(soapMessage);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		/* Print the request message, just for debugging purposes */
		System.out.println("Request SOAP Message:");
		soapMessage.writeTo(System.out);
		System.out.println("\n");

		return soapMessage;
	}
}
