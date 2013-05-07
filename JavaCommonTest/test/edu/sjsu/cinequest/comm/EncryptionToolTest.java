package edu.sjsu.cinequest.comm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import edu.sjsu.cinequest.javase.JavaSEPlatform;

import junit.framework.TestCase;

public class EncryptionToolTest extends TestCase
{
    protected void setUp() throws Exception
    {
        Platform.setInstance(new JavaSEPlatform());
    }

  public void testSHA1() throws NoSuchAlgorithmException, UnsupportedEncodingException
  {
	 ArrayList plainTexts = new ArrayList();
	 ArrayList sha1Result = new ArrayList();
	 plainTexts.add("puttheminthesafe20091007155500");
	 plainTexts.add("sacredcowsrunconsumed20091007155500");
	 plainTexts.add("deadmanscurve20091007155500");
	 plainTexts.add("stjamesandsaintlucysaywhat20091007155500");
	 sha1Result.add("e5809364e7cb14a30fbc46d191a262e68af21633");
	 sha1Result.add("0a555ef8a246ede6ee165391a587a3ed6f31d234");
	 sha1Result.add("cb324d8f994d51c5d5530f1aa894bf5d2705dc12");
	 sha1Result.add("a16b3e8113870cde26a58cea5bce33a243d2a4ef");
	 for(int i = 0; i < plainTexts.size(); i++)
	 {
            assertTrue(EncryptionTool.SHA1((String) plainTexts.get(i)).equals(sha1Result.get(i)));
	 } 
  }
}
