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
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/**
 * This class describes the Schedule screen. This screen contains a list of hard
 * coded dates that correspond to each of the dates of the Cinequest Film
 * Festival. When a user clicks on a date, a corresponding query is issued for
 * the schedules for that day.
 * @author Ian Macauley
 * 
 */
class ScheduleScreen extends CinequestScreen
{
    /**
     * Default constructor
     */    
    public ScheduleScreen()
    {
        LabelField l = new LabelField("Cinequest Film Festival Schedule:");
        final String[] festivalDates = DateUtils.getFestivalDates();
        String[] dateNames = new String[festivalDates.length];
        DateUtils dateFormatter = new DateUtils();
        
        for (int i = 0; i < dateNames.length; i++)
        {
            dateNames[i] = dateFormatter.format(festivalDates[i],
                    DateFormat.DATE_FULL);
            // if(festivalDates[i].equals(today)) dateNames[i] = "* " + dateNames[i];
        }

        ObjectListField dates = new ObjectListField()
        {
            private String today = DateUtils.today();
            public boolean trackwheelClick(int status, int time)
            {
                final String date = festivalDates[getSelectedIndex()];
                                               
                Main.getQueryManager().getSchedulesDay(date, new ProgressMonitorCallback()
                {
                    public void invoke(Object obj)
                    {
                        super.invoke(obj);
                        Vector v = (Vector) obj;
                        Schedule[] sched = new Schedule[v.size()];
                        for (int i = 0; i < v.size(); i++)
                        {
                            Schedule s = (Schedule) v.elementAt(i);
                            sched[i] = s;
                        }
                        Ui.getUiEngine().pushScreen(new SchedulesScreen(date, sched));
                    }
                });
                return true;
            }
            
            public void drawListRow(ListField l, Graphics g, int index, int y, int width)
            {
                int color = g.getColor();
                if (festivalDates[index].equals(today))
                    g.setColor(0xFF0000); // Red
                else if (festivalDates[index].compareTo(today) < 0)
                    g.setColor(0x7F7F7F); // Gray
                super.drawListRow(l, g, index, y, width);
                g.setColor(color);
            }
        };
        dates.set(dateNames);
        add(l);
        add(new SeparatorField());
        add(dates);
    }
    
    


   




   /**
     * Overriden toString method merely returns the String "Cinequest Film Festival"
     * @return the String "Cinequest Film Festival"
     */
    public String toString()
    {
       return "Cinequest Film Festival";
    }
}
