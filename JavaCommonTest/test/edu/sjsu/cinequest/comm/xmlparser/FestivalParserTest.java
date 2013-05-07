package edu.sjsu.cinequest.comm.xmlparser;

import junit.framework.TestCase;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.Festival;
import edu.sjsu.cinequest.javase.JavaSEPlatform;

public class FestivalParserTest extends TestCase {

	protected void setUp() throws Exception
    {
        Platform.setInstance(new JavaSEPlatform());
    }
    
	public void testFestival1() throws Exception
    {        
		Festival festival = FestivalParser.parseFestival("http://mobile.cinequest.org/mobileCQ.php?type=festival&lastChanged=0", null);
		assertTrue(festival.getProgramItems().size() > 0);		
    }
}
