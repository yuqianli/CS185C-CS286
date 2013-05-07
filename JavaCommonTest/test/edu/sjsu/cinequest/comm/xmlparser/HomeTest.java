package edu.sjsu.cinequest.comm.xmlparser;

import java.util.Vector;

import junit.framework.TestCase;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.CinequestItem;
import edu.sjsu.cinequest.comm.cinequestitem.Section;
import edu.sjsu.cinequest.javase.JavaSEPlatform;

public class HomeTest extends TestCase {
	
    protected void setUp() throws Exception
    {
        Platform.setInstance(new JavaSEPlatform());
    }

    public void testHome() throws Exception
    {
        Vector result = SectionsParser
                .parse("http://mobile.cinequest.org/mobileCQ.php?type=xml&name=home", null);
        Section sec = (Section) result.elementAt(0);
        CinequestItem it = (CinequestItem) sec.getItems().get(0);
        assertEquals(it.getTitle(), "John Turturro - Maverick Spirit Event");
    }
}
