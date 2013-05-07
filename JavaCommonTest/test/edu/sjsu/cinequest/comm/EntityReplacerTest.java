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

package edu.sjsu.cinequest.comm;

import junit.framework.TestCase;

public class EntityReplacerTest extends TestCase
{
    public void testQuote()
    {
        String original = "&quot;Hello&quot;";
        String transformed = replaceEntities(original);
        String expected = "\"Hello\"";
        assertEquals(expected, transformed);
    }
    
    public void testCurlyQuotes()
    {
        String original = "This is &ldquo;original&rdquo;";
        String transformed = replaceEntities(original);
        String expected = "This is \u201coriginal\u201d";
        assertEquals(expected, transformed);
    }
    
    public void testUnknown()
    {
        String original = "This is &unknown;";
        String transformed = replaceEntities(original);
        String expected = original;
        assertEquals(expected, transformed);
    }
    
    public void testSpurious()
    {
        String original = "This is &spurious";
        String transformed = replaceEntities(original);
        String expected = original;
        assertEquals(expected, transformed);
    }
    
    public static String replaceEntities(String source)
    {
        StringBuffer buffer = new StringBuffer(source);
        CharUtils.replaceEntities(buffer);
        return buffer.toString();
    }
}
