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

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectListField;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;

/**
 * An ObjectListField that can format schedules
 * @author Cay Horstmann
 * @author Ian Macauley
 */
public class SchedulesListField extends ObjectListField
{
    private int format;
    private int col1;
    private int col2;
    private DateUtils dateFormatter = new DateUtils();

    /**
     * Constructs a field for displaying a list of schedules with a given
     * DateFormat
     * @param format the DateFormat (such as TIME_SHORT)
     */
    public SchedulesListField(int format)
    {
        this.format = format;
    }
    
    public boolean trackwheelClick(int status, int time)
    {
        int index = getSelectedIndex();
        if (index >= 0)
        {
            Schedule s = (Schedule) get(this, index);
            Callback callback = new ControlListCallback();
            // TODO: Obsolete--always call getProgramItem
            if (s.isMobileItem())
                Main.getQueryManager().getMobileItem(s.getItemId(), callback);
            else
                Main.getQueryManager().getProgramItem(s.getItemId(), callback);
        }
        return true;
    }    

    /**
     * Overridden method to draw each row of the SchedulesListField.
     */
    public void drawListRow(ListField l, Graphics g, int index, int y, int width)
    {
        if (col1 == 0)
        {
            // the space between columns will be 1/25 the size of the screen
            // int spacer = Graphics.getScreenWidth() / 25;
            Font font = g.getFont();
            g.setFont(font.derive(Font.BOLD));

            col1 = g.drawText(dateFormatter.format("2009-03-03 00:00", format) + "W", 0,
                    y)
                    /* + spacer */;
            col2 = g.drawText("WWWW", 0, y) /* + spacer */;
            XYRect rect = g.getClippingRect();
            g.setFont(font);
            g.clear(0, y, width, rect.height);
        }
        Schedule s = (Schedule) get(this, index);
        String showTime = dateFormatter.format(s.getStartTime(), format);
        
        Font font = g.getFont();
        int color = g.getColor();
        g.setFont(getFont(font, s));
        g.setColor(getColor(color, s));
        g.drawText(showTime, 0, y, LEFT, col1);
        g.drawText(s.getVenue(), col1, y, 0, col2);
        g.drawText(s.getTitle(), col1 + col2, y, ELLIPSIS, width - col1 - col2);
        g.setFont(font);
        g.setColor(color);
    }
    
    public Font getFont(Font font, Schedule s)
    {
       Font newFont = font;
       if (s.isSpecialItem())
       {
          newFont = font.derive(Font.ITALIC);
       }
       if (Main.getUser().getSchedule().isScheduled(s))
       {
          newFont = font.derive(Font.BOLD);
       }
       return newFont;
    }
    
    public int getColor(int color, Schedule s)
    {
       int newColor = color;
       if (Main.getUser().getSchedule().conflictsWith(s) && ! Main.getUser().getSchedule().isScheduled(s))
       {           
          newColor = Color.DARKGRAY;
       }
       return newColor;
    }
}
