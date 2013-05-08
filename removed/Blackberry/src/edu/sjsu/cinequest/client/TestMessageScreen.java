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

package edu.sjsu.cinequest.client;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;

/**
 * This class describes a generic test screen, only used for testing purposes.
 * @author Ian Macauley
 * 
 */
public class TestMessageScreen extends CinequestScreen
{
    /**
     * This constructor puts a specified String in the ErrorScreen
     * @param label the error string to display
     */
    public TestMessageScreen(String label)
    {
        add(new LabelField("Test Message"));
        add(new SeparatorField());
        add(new LabelField(label));
    }
}
