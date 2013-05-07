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

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/**
 * This class describes the ForumsScreen, which is in charge of displaying
 * relevant information about Cinequest Forums and Events as Schedule items.
 * @author Ian Macauley
 * 
 */
public class ForumsScreen extends CinequestScreen
{
    /**
     * Construct a ForumsScreen with a list of Schedule items from the specified
     * date.
     * @param sched the schedule items to display
     */
    public ForumsScreen(String title, Schedule[] sched)
    {
        LabelField top = new LabelField(title);
        add(top);
        add(new SeparatorField());
        SchedulesListField slf = new SchedulesListField(DateFormat.DATE_SHORT);
        slf.set(sched);
        add(slf);
    }

    public String toString()
    {
        return "Forums";
    }
}
