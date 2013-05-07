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
import net.rim.device.api.ui.UiApplication;
import edu.sjsu.cinequest.comm.ImageManager;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.QueryManager;
import edu.sjsu.cinequest.comm.cinequestitem.User;
import edu.sjsu.cinequest.comm.cinequestitem.VenueLocation;
import edu.sjsu.cinequest.rim.RIMPlatform;

/**
 * Entry point for the application. Contains the main method that enters the
 * event dispatcher. It pushes the EntryScreen by default.
 * @author Ian Macauley
 * 
 */
public class Main extends UiApplication
{   
    /**
     * Main method.
     */
    public static void main(String[] args)
    {
        Platform.setInstance(new RIMPlatform());
        imageManager = new ImageManager();
        queryManager = new QueryManager();
        user = new User();
        // TODO: Remove this to turn on test mode
        // DateUtils.setMode(DateUtils.FESTIVAL_TEST_MODE);
                
        Main app = new Main();
        app.enterEventDispatcher();
    }
    
    /**
     * Default constructor
     */
    public Main()
    {
    	//push a normal entry screen
    	pushScreen(new EntryScreen());
    }

    /**
     * Pushes a screen describing a venue
     * @param id the venue abbreviation
     */
    public static void showVenue(final String id)
    {
        if (venueLocations == null)
        {
            Main.getQueryManager().getVenues(new ProgressMonitorCallback()
            {
                public void invoke(Object result)
                {
                    super.invoke(result);
                    venueLocations = (Vector) result;
                    showVenue(id);
                }
            });
        }
        else
        {
            for (int i = 0; i < venueLocations.size(); i++)
            {
                VenueLocation venue = (VenueLocation) venueLocations
                        .elementAt(i);
                if (venue.getVenueAbbreviation().equals(id))
                {
                    Ui.getUiEngine().pushScreen(new ControlList(venue));
                    return;
                }
            }
        }
    }  
    
    private static Vector venueLocations;
    
    private static ImageManager imageManager;
    private static QueryManager queryManager;
    private static User user;
    
    public static ImageManager getImageManager()
    {
        return imageManager;
    }
    
    public static QueryManager getQueryManager()
    {
        return queryManager;
    }
    
    public static User getUser()
    {
       return user;
    }    
}
