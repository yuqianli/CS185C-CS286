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

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;

/**
 * This class describes the Genres screen that shows the user a list of genres.
 * A user can click on the genres and a query is made for all films that are of
 * the specified genre. The genres are retrieved from the database.
 * @author Ian Macauley
 * 
 */
public class GenresScreen extends CinequestScreen
{
   /**
    * Constructs a GenresScreen using a specified list of genres.
    * @param genres A String[] of genres
    */
    public GenresScreen(String[] genres)
    {
        LabelField l = new LabelField("Search for films by genre:");
        add(l);
        ObjectListField olf = new ObjectListField()
        {
            public boolean trackwheelClick(int status, int time)
            {
                final String genre = (String) get(this, getSelectedIndex());
                Main.getQueryManager().getFilmsByGenre(genre, new ProgressMonitorCallback()
                {
                    public void invoke(Object obj)
                    {
                        super.invoke(obj);
                        Ui.getUiEngine().pushScreen(
                                new FilmListScreen((Vector) obj, "Showing " + genre + " Films:"));
                    }
                });
                return true;
            }
        };
        olf.set(genres);
        add(new SeparatorField());
        add(olf);
    }
}
