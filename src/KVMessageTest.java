import static org.junit.Assert.*;

import org.junit.Test;

import sun.rmi.runtime.Log;



import edu.berkeley.cs162.KVException;
import edu.berkeley.cs162.KVMessage;


public class KVMessageTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	
	/*
	 * Test whether key and no value works properly
	 */
	@Test
	public void getTester1() throws KVException {
		
			KVMessage getTest1Msg = new KVMessage("getreq");
			getTest1Msg.setKey("kate");
			//no value set
			System.out.println(getTest1Msg.toXML());
			
		
	}
	/*
	 * Test a get request with no key 
	 */
	@Test
	public void getTester2() throws KVException{
		try{
			KVMessage getTest2Msg = new KVMessage("getreq");
			System.out.println(" getTest2Msg XML: " + getTest2Msg.toXML());
		}catch (KVException e){
			assertTrue(e.getMsg().getMsgType().equals("resp"));
			assertTrue(e.getMsg().getMessage().equals("Message format incorrect"));
			
		}
	}
	
	/*
	 * Test a standard Key and Value get
	 */
	@Test
	public void getTester3() throws KVException{
		KVMessage getTesterMsg3 = new KVMessage("getreq");
		getTesterMsg3.setKey("Katherine");
		getTesterMsg3.setValue("Gorman");
		System.out.println("Katherine Gorman get Test: " + getTesterMsg3.toXML());
	}
	
	/*
	 * Test a put, with key and value
	 */
	@Test
	public void putTester1() throws KVException{
		KVMessage putTest1 = new KVMessage("putreq");
		putTest1.setKey("Michael");
		putTest1.setValue("Jackson");
		System.out.println("Michael Jackson put test: " +putTest1.toXML());
	}
	

}
