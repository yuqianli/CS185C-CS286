package edu.sjsu.cinequest.client;

import java.util.Vector;

import net.rim.device.api.ui.Graphics; /*
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

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;
import edu.sjsu.cinequest.comm.cinequestitem.Filmlet;

/**
 * This class describes a CinequestScreen that takes a genre and a list of
 * Filmlets of that genre. It is effectively a list of a subset of the entire
 * film list.
 * @author Ian Macauley
 * 
 */
public class FilmListScreen extends CinequestScreen
{
    private ObjectListField flf = new FilmListField();
    private Filmlet[] list;
    
    /**
     * Constructor for a FilmListScreen.
     * @param filmlets a Vector of Filmlets to display
     * @param title the desired title for the screen
     */
    public FilmListScreen(Vector filmlets, String title)
    {
        Filmlet[] items = new Filmlet[filmlets.size()];
        for (int i = 0; i < filmlets.size(); i++)
        {
            items[i] = (Filmlet) filmlets.elementAt(i);
        }
        list = items;
        LabelField top = new LabelField(title);
        add(top);
        add(new SeparatorField());
        flf.set(items);
        add(flf);
    }
    
    /**
     * Sets the list of Filmlets to be displayed
     * @param newList the list of Filmlets to display
     */
    public void setList(Filmlet[] newList)
    {
        flf.set(newList);
    }
    
    /**
     * Returns the list of Filmlets the screen is holding.
     * @return the list of Filmlets.
     */
    public Filmlet[] getList()
    {
        return list;
    }
    
    /**
     * This class is an extension of ObjectListField, except each row drawn
     * contains the title of the Filmlet items returned from the query.
     * @author Ian Macauley
     * 
     */
    class FilmListField extends ObjectListField
    {
        /**
         * Overriden method to draw each row of the FilmsListField.
         */
        public void drawListRow(ListField l, Graphics g, int index, int y,
                int width)
        {
            Filmlet f = (Filmlet) get(this, index);
            String title = f.getTitle();
            g.drawText(title, 0, y, ELLIPSIS, width);
        }

        public boolean trackwheelClick(int status, int time)
        {
            int index = getSelectedIndex();
            if (index >= 0)
            {
                makeDetailQuery((Filmlet) get(this, index));
            }
            return true;
        }
    }      
    
    public void makeDetailQuery(Filmlet f)
    {
        Main.getQueryManager().getFilm(f.getId(),
                new ControlListCallback());        
    }
}
