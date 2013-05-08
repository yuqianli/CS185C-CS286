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
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.ListField;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.UserSchedule;

/**
 * An ObjectListField that can format schedules and dates
 * 
 * @author Cay Horstmann
 * @author Ian Macauley
 */
public class UserScheduleListField extends SchedulesListField
{
   private DateUtils dateUtils = new DateUtils();
   
   /**
    * Constructs a field for displaying a list of schedules with a given
    * DateFormat
    * 
    * @param format
    *           the DateFormat (such as TIME_SHORT)
    */
   public UserScheduleListField(int format)
   {
      super(format);
   }

   public boolean trackwheelClick(int status, int time)
   {
      int index = getSelectedIndex();
      if (index >= 0)
      {
         if (get(this, index) instanceof Schedule)
            super.trackwheelClick(status, time);
         else 
         {
            final String date = (String) get(this, index);
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
                    UiEngine engine = Ui.getUiEngine();
                    engine.pushScreen(new SchedulesScreen(date, sched));
                }
            });            
         }
      }
      return true;
   }

   /**
    * Overridden method to draw each row of the UserScheduleListField.
    */
   public void drawListRow(ListField l, Graphics g, int index, int y, int width)
   {
      if (index >= 0)
      {
         if (get(this, index) instanceof Schedule)
         {
            super.drawListRow(l, g, index, y, width);
            return;
         }

         Font font = g.getFont();
         g.setFont(font.derive(Font.BOLD));

         String date = (String) get(this, index);
         g.drawText(dateUtils.format(date, DateFormat.DATE_FULL), 0, y, LEFT, width);
         g.setFont(font);
      }
   }
   
   public Font getFont(Font font, Schedule s)
   {
      return font.derive(Font.PLAIN); // TODO: Why necessary?
   }
   
   public int getColor(int color, Schedule s)
   {
      int type = Main.getUser().getSchedule().getType(s);
      int newColor = color;
      if (type == UserSchedule.MOVED) newColor = Color.RED;
      else if (type == UserSchedule.REMOVED) newColor = Color.LIGHTGREY;            
      return newColor;
   }   
}
