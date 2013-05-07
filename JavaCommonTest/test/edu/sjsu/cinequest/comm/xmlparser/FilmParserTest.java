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

import junit.framework.TestCase;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.cinequestitem.Film;
import edu.sjsu.cinequest.javase.JavaSEPlatform;

public class FilmParserTest extends TestCase
{
    protected void setUp() throws Exception
    {
        Platform.setInstance(new JavaSEPlatform());
    }
    
    public void testFilm1374() throws Exception
    {        
        Film result = FilmParser.parseFilm("http://oslo.cs.sjsu.edu:8080/cinequest/xml/films/1374.xml",
                null);
        
        assertEquals(result.getId(), 1374);
        assertEquals(result.getTitle(), "El Camino");
        assertEquals(result.getGenre(), "Drama");
        assertEquals(result.getDirector(), "Erik S. Weigel");
        assertEquals(result.getTagline(), "");
        assertEquals(result.getCinematographer(), "Till Neumann");
        assertEquals(result.getEditor(), "Bill Henry");
        assertEquals(result.getCast(), "Leo Fitzpatrick, Elisabeth Moss, Christopher Denham, Wes Studi");

        String o = result.getDescription();
        assertTrue(o.startsWith("<h2>World Premiere</h2>"));
        assertTrue(o.indexOf("frailty and redemption") > 0);
        assertTrue(o.endsWith("Pete Crane"));
    }
    
    public void testDVD() throws Exception
    {        
    	Film result = FilmParser.parseFilm("http://mobile.cinequest.org/mobileCQ.php?type=dvd&id=796",
                null);
        
        assertEquals(result.getId(), 796);
        assertEquals(result.getTitle(), "Flourish");
        assertEquals(result.getDirector(), "Kevin Palys");
        assertEquals(result.getTagline(), "");
        assertEquals(result.getWriter(), "Kevin Palys");        
        assertEquals(result.getFilmInfo(), "Color/95min/NTSC");        
        
        
        String o = result.getDescription();
        assertTrue(o.startsWith("<b>Starring"));
        assertTrue(o.indexOf("<i>House</i>") > 0);
        assertTrue(o.endsWith("situation."));
    }
}
