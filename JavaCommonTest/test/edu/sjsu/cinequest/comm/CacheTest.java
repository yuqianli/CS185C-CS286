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

public class CacheTest extends TestCase
{
    public void testRemoval()
    {
        Cache cache = new Cache(3);
        cache.put("1", "A");
        cache.put("2", "B");
        cache.put("3", "C");
        cache.put("4", "D");
        assertTrue(cache.get("1") == null);
    }
    public void testMovementOfNewer()
    {
        Cache cache = new Cache(3);
        cache.put("1", "A");
        cache.put("2", "B");
        cache.put("1", "D");
        cache.put("3", "C");
        cache.put("4", "E");
        System.out.println(cache);
        assertTrue(cache.get("1") != null);
        assertTrue(cache.get("2") == null);
    }
    public void testThatAccessedElementBecomesNewest()
    {
        Cache cache = new Cache(3);
        cache.put("1", "A");
        cache.put("2", "B");
        cache.put("3", "C");
        // Access 1--should make it newest
        cache.get("1");

        // Make it oldest 
        cache.put("4", "D");
        cache.put("5", "E");
        
        // Check that 1 is still there
        assertTrue(cache.get("1") != null);
        
        // Unfortunately, that puts it back to the top, so make it oldest again
        cache.get("4");
        cache.get("5");
        
        // Now let's dislodge 1
        cache.put("6", "F");
        assertTrue(cache.get("1") == null);
    }
}
