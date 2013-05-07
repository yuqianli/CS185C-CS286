/*
    Copyright 2008 San Jose State University
    
    This file is part of the Blackberry Cinequest client.

    The Blackberry Cinequest client is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Blackberry Cinequest client is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Blackberry Cinequest client.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sjsu.cinequest.comm.xmlparser;

import java.util.Vector;

import junit.framework.TestCase;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.VenueLocation;
import edu.sjsu.cinequest.javase.JavaSEPlatform;

public class VenuesParserTest extends TestCase
{
    protected void setUp() throws Exception
    {
        Platform.setInstance(new JavaSEPlatform());
    }

    public void testVenueCal() throws Exception
    {
        Vector result = VenuesParser.parse("http://mobile.cinequest.org/mobileCQ.php?type=venues", null);
        int cal = -1;
        for (int i = 0; cal == -1 && i < result.size(); i++)
        {
            VenueLocation venue = (VenueLocation) result.elementAt(i);
            if (venue.getVenueAbbreviation().equals("CAL")) cal = i;            
        }
        assertTrue(-1 != cal);
    }
}
