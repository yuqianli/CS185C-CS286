package edu.sjsu.cinequest.comm;

import java.io.IOException;
import java.util.Hashtable;

import org.xml.sax.SAXException;

import edu.sjsu.cinequest.comm.cinequestitem.UserSchedule;
import edu.sjsu.cinequest.comm.xmlparser.UserScheduleParser;
import edu.sjsu.cinequest.javase.JavaSEPlatform;
import junit.framework.TestCase;

public class PostTest extends TestCase
{
   protected void setUp() throws Exception
   {
      Platform.setInstance(new JavaSEPlatform());
   }
   
   public void testPost() throws SAXException, IOException
   {
      Hashtable postData = new Hashtable();
      postData.put("type", "SLGET");
      String email = "Cay.Horstmann@sjsu.edu";
      postData.put("username", email);
      String password = "secret";
      postData.put("password", password);
      String url = "http://mobile.cinequest.org/mobileCQ.php";
      TestCallback callback = new TestCallback();
      UserSchedule result = UserScheduleParser.parseSchedule(url, postData, callback);
      assertFalse(result == null);
   }
}
