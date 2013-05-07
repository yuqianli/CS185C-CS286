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

import java.util.Vector;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/**
 * This class describes the Schedules screen. The SchedulesScreen is reached
 * when a user clicks on a date from the ScheduleScreen. It shows all Schedule
 * items for that date. It uses the QueryManager to issue a query for all
 * schedules for a specified date.
 * @author Ian Macauley
 * 
 */
public class SchedulesScreen extends CinequestScreen
{
    private Schedule[] sched;
    private SchedulesListField slf;

    /**
     * Construct a SchedulesScreen with a list of Schedule items from the
     * specified date.
     * @param date the date for these schedule items
     * @param sched the schedule items to display
     */
    public SchedulesScreen(String date, Schedule[] sched)
    {
        this.sched = sched;
        LabelField top = new LabelField("Schedule for "
                + new DateUtils().format(date, DateFormat.DATE_FULL));
        add(top);
        add(new SeparatorField());
        slf = new SchedulesListField(DateFormat.TIME_SHORT);
        slf.set(sched);
        add(slf);
        
        addMenuItem(new MenuItem("Add", 1, 70)
        {
            public void run()
            {
               addCurrent();
            }
        });
        addMenuItem(new MenuItem("Remove", 2, 100)
        {
            public void run()
            {
               removeCurrent();
            }
        });
        
        addMenuItem(MenuItem.separator(10));
                
        addVenuesToMenu();
        addMenuItem(new MenuItem("Show All", 20, 100)
        {
            public void run()
            {
                showAll();
            }
        });
        addMenuItem(MenuItem.separator(20));
    }

    /**
     * This method searches through the current Schedule items, and adds entries
     * to the menu for filtering the Schedule items by venue.
     */
    public void addVenuesToMenu()
    {
        Vector vNames = new Vector();
        for (int i = 0; i < sched.length; i++)
        {
            Schedule s = sched[i];
            String v = s.getVenue();
            if (!vNames.contains(v))
                vNames.addElement(v);
        }
        for (int i = 0; i < vNames.size(); i++)
        {
            final String v = (String) vNames.elementAt(i);
            addMenuItem(new MenuItem(v + " Only", 10 + i, 100) 
            {
                public void run()
                {
                    narrowList(v);
                }
            });
        }
    }

    /**
     * This method changes the Schedules being displayed to only display
     * Schedule items that are at the specified venue.
     * @param v the name of the venue to filter by
     */
    public void narrowList(String v)
    {
        Vector newSched = new Vector();
        for (int i = 0; i < sched.length; i++)
        {
            Schedule s = sched[i];
            if (s.getVenue().equals(v))
                newSched.addElement(s);
        }
        Schedule[] s = new Schedule[newSched.size()];
        for (int i = 0; i < s.length; i++)
        {
            s[i] = (Schedule) newSched.elementAt(i);
        }
        slf.set(s);
    }
    
    /**
     * Adds the currently selected item to the user schedule
     */
    public void addCurrent()
    {
       int index = slf.getSelectedIndex();
       if (index >= 0)
       {
           Schedule s = (Schedule) slf.get(slf, index);
           Main.getUser().getSchedule().add(s);
           invalidate();
       }
    }

    /**
     * Removes the currently selected item from the user schedule
     */
    public void removeCurrent()
    {
       int index = slf.getSelectedIndex();
       if (index >= 0)
       {
           Schedule s = (Schedule) slf.get(slf, index);
           Main.getUser().getSchedule().remove(s);
           invalidate();
       }
    }
    
    /**
     * Removes all filters and shows all Schedule items.
     */
    public void showAll()
    {
        slf.set(sched);
    }
}
