/**
 * XML Parsing library for the key-value store
 * 
 * @author Mosharaf Chowdhury (http://www.mosharaf.com)
 * @author Prashanth Mohan (http://www.cs.berkeley.edu/~prmohan)
 * 
 * Copyright (c) 2012, University of California at Berkeley
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of University of California, Berkeley nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *    
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.berkeley.cs162;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


/**
 * This is the object that is used to generate messages the XML based messages 
 * for communication between clients and servers. 
 */
public class KVMessage {
	private String msgType = null;
	private String key = null;
	private String value = null;
	private String status = null;
	private String message = null;
	
	public final String getKey() {
		return key;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	public final String getValue() {
		return value;
	}

	public final void setValue(String value) {
		this.value = value;
	}

	public final String getStatus() {
		return status;
	}

	public final void setStatus(String status) {
		this.status = status;
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public String getMsgType() {
		return msgType;
	}

	/* Solution from http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html */
	private class NoCloseInputStream extends FilterInputStream {
	    public NoCloseInputStream(InputStream in) {
	        super(in);
	    }
	    
	    public void close() {} // ignore close
	}
	
	/***
	 * 
	 * @param msgType
	 * @throws KVException of type "resp" with message "Message format incorrect" if msgType is unknown
	 */
	public KVMessage(String msgType) throws KVException {
	    // TODO: implement me
	}
	
	public KVMessage(String msgType, String message) throws KVException {
        // TODO: implement me
	}
	
	 /***
     * Parse KVMessage from incoming network connection
     * @param sock
     * @throws KVException if there is an error in parsing the message. The exception should be of type "resp and message should be :
     * a. "XML Error: Received unparseable message" - if the received message is not valid XML.
     * b. "Network Error: Could not receive data" - if there is a network error causing an incomplete parsing of the message.
     * c. "Message format incorrect" - if there message does not conform to the required specifications. Examples include incorrect message type. 
     */
	public KVMessage(InputStream input) throws KVException {
	   
		Document newDoc= null; 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	     DocumentBuilder db;
	    //CREATE A NEW DOCUMENT BUILDER
	     try {
	    	 db = dbf.newDocumentBuilder();
	     }catch(ParserConfigurationException e){
	    	 KVMessage errorMsg = new KVMessage("parser config error, line 123");
	    	 throw new KVException (errorMsg);
	     }
	     
	     //TRY TO PARSE THE INPUT STREAM
	     try{
	    	 newDoc = db.parse(new NoCloseInputStream(input));
	     }catch(IOException e){
	    	 KVMessage ioError = new KVMessage( "error in parsing, line 136:" + e.getMessage());
	    	 
	     } catch (SAXException e) {
			// TODO Auto-generated catch block
			KVMessage saxError = new KVMessage ("error in parsing: line 142 " + e.getMessage());
			
		}
	     
	    //NORMALIZE THE UNDERLYING DOM TREE
	    // used this website as a resource: http://sanjaal.com/java/tag/getdocumentelementnormalize/
	     newDoc.getDocumentElement().normalize();
	     
	     
	     //MAKE A NODE LIST
	     NodeList listOfMessageTypes = newDoc.getElementsByTagName("KVMessage");
	     //MAKE A NODE
	     Node nodeType = listOfMessageTypes.item(0);
	     Element elementType = (Element) nodeType;
	    
	     
	     //expecting status to be "True" or "False"
	     status= findTagsOfElement (elementType, "Status");
	     //GET THE MSGTYPE FROM THE ELEMENT
	     msgType = elementType.getAttribute("type");
	     key = findTagsOfElement(elementType, "Key");
	     value = findTagsOfElement(elementType, "Value");
	     message = findTagsOfElement(elementType, "Message");
	     //TODO ERROR CHECKING?
	}
	
	/*
	 * Returns the tag Value of an element
	 * 
	 */
	public String findTagsOfElement (Element thisElm, String tag){
		NodeList thisList = thisElm.getElementsByTagName(tag);
		if (thisList.getLength() == 0){
			return null;
			
		}
		else{
			//get the children of the first node
			NodeList node1ChildList = thisElm.getElementsByTagName(tag).item(0).getChildNodes();
			Node firstChild = (Node) node1ChildList.item(0);
			return firstChild.getNodeValue();
		}
	}
	
	/**
	 * Generate the XML representation for this message.
	 * @return the XML String
	 * @throws KVException if not enough data is available to generate a valid KV XML message
	 */
	public String toXML() throws KVException {
		 String xmlString;
		Text text;
	      // TODO: implement me
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document newDoc = db.newDocument();
		//try to make a new Doc builder which is the XML base
		try{
			db = dbf.newDocumentBuilder();
		}catch (ParserConfigurationException e){
			KVMessage dbMessage = new KVMessage("error in making the XML" + e.getMessage());
			throw new KVException(dbMessage);
		}
		
//now to make the XML Tree! 
		//MAKE THE ROOT ELEMENT the KVMessage
		Element rootElement = newDoc.createElement("KVMessage");
		//ADD THE ROOT TO THE XML
		newDoc.appendChild(rootElement);
		//SET THE TYPE
		rootElement.setAttribute("type", msgType);
		
		//HERE COMES THE FUN
		//DO THE MESSAGE BUSINESS
		
		if (message != null){
			//CREATE CHILD ELEMENT
			Element messageChild = newDoc.createElement("Message");
			//ADD IT TO THE ROOT
			rootElement.appendChild(messageChild);
			//CREATE TEXT NODE TO STORE THE MESSAGE VALUE
			text = newDoc.createTextNode(message);
			//ADD IT TO THE MESSAGE
			messageChild.appendChild(text);
		}
		
		if (status.equals("True")){
			//CREATE CHILD ELEMENT
			Element statusChild = newDoc.createElement("Status");
			//ADD IT TO THE ROOT
			rootElement.appendChild(statusChild);
			//CREAT TEXT
			text = newDoc.createTextNode(status);
			//ADD TEXT TO THE STATUS
			statusChild.appendChild(text);
			
		}
		
		//
		// DO THE KEY BUSINESS
		
		if (key !=null){
			//CREATE CHILD ELEMENT
			Element keyChild = newDoc.createElement("Key");
			//ADD IT TO THE ROOT
			rootElement.appendChild(keyChild);
			//CREATE TEXT NODE TO HOLD THE KEY'S VAL
			text = newDoc.createTextNode(key);
			//ADD THE TEXT TO THE CHILD
			keyChild.appendChild(text);
		}
		
		if (value !=null){
			//CREATE CHILD
			Element valueChild = newDoc.createElement("Value");
			//ADD IT TO THE ROOT
			rootElement.appendChild(valueChild);
			//CREATE TEXT
			text = newDoc.createTextNode(value);
			//ADD IT TO THE ELEMENT
			valueChild.appendChild(text);
		}
//XML Tree done
		// now need to transform this to output it
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer arnold = null;
		
		//try to make a new transformer
		try{
			arnold = tf.newTransformer();
		}catch (TransformerConfigurationException e){
			KVMessage transformerError = new KVMessage("make new transformer error: "+ e.getMessage());
			throw new KVException(transformerError);
		}
		

// now take the string from the xml
		StringWriter stringwriter = new StringWriter();
		StreamResult dst = new StreamResult(stringwriter);
		
		DOMSource src = new DOMSource(newDoc);
		//try to tranfsorm xml src to dst
		try{
			arnold.transform(src, dst);
		}catch (TransformerException e){
			KVMessage transError = new KVMessage ("error converting xml src to dst" + e.getMessage());
			throw new KVException(transError);
		}
		//get the string from the string writer
		return stringwriter.toString();
		
	}//ends method
	
	public void sendMessage(Socket sock) throws KVException {
	      // TODO: implement me
		String xml = this.toXML();
		Writer output;
		try{
			output = new OutputStreamWriter(sock.getOutputStream());
			output.write(xml);
			output.flush();
		}catch(IOException e){
			//KVMessage
			KVMessage ioError = new KVMessage("error in sendMessage "+ e.getMessage());
			throw new KVException (ioError);
		}
		
		try {
			sock.shutdownOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			KVMessage socketError = new KVMessage("socket closure error "+ e.getMessage());
			throw new KVException(socketError);
			
		}
	}
}
