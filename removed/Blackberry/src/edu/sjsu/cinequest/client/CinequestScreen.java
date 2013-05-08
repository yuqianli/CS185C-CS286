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

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.MainScreen;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.cinequestitem.MobileItem;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.Section;
import edu.sjsu.cinequest.comm.cinequestitem.User;

/**
 * This abstract class describes a CinequestScreen. It's up to the implementor
 * to implement what the screen actually looks like. Every CinequestScreen has
 * menu items Schedule, Films, Genres, News, Events, Forum, DVDs, Home, and
 * Back. If the screen is an EntryScreen, Back will be replaced by Quit, and
 * there will be no Home item. When a new screen is selected from the menu, all
 * other screens will be popped and the new screen is pushed, so going back from
 * that point will take you back to the Home screen. Otherwise, selecting back
 * will take you to the immediately preceding screen, like the back button in a
 * browser.
 * @author Ian Macauley
 * 
 */
public class CinequestScreen extends MainScreen
{
    /**
     * Default constructor.
     */
    public CinequestScreen()
    {
    	
        final Bitmap img = Bitmap.getBitmapResource("cinequestlogo.png");
        final Bitmap img2 = Bitmap.getBitmapResource("cinequestlogo2.png");
        // We are making the title focusable so that on a 7xxx device the user
        // can get a menu by clicking on it
        BitmapField bf = new BitmapField(img, BitmapField.LEFT
                | Field.FOCUSABLE)
        {
             // Overridden paint method for showing the title graphic.
             
            public void paint(Graphics graphics)
            {
                super.paint(graphics);
                int start = img.getWidth();
                int width2 = img2.getWidth();
                int height2 = img2.getHeight();
                int screenWidth = Graphics.getScreenWidth();
                while (start < screenWidth)
                {
                    graphics.drawBitmap(start, 0, width2, height2, img2, 0, 0);
                    start += width2;
                }
            }
        };
        setTitle(bf);
        
        // Add menu items
     
        if (!DateUtils.isOffSeason()) addMenuItem(new MenuItem("Your schedule", 21, 100)
        {
            public void run()
            {
                goHome();
                Ui.getUiEngine().pushScreen(new UserScheduleScreen());
            }
        });
        if (!DateUtils.isOffSeason()) addMenuItem(new MenuItem("Sync schedule", 22, 100)
        {
            public void run()
            {
            	new Runnable() {
            		public void run() {
            			final User user = Main.getUser();
            			Callback resultCallback = new Callback() {
            				public void starting() {}
            				public void invoke(Object result) {
            					if (CinequestScreen.this instanceof UserScheduleScreen)
            						((UserScheduleScreen) CinequestScreen.this).setScheduleItems();
            				}
            				public void failure(Throwable t) {}
            			};
            			user.syncSchedule(LoginDialog1.getLoginAction(),
            					SyncDialog.getSyncAction(),
            					new ProgressMonitorCallback(), 
            					resultCallback,
            					Main.getQueryManager());				
            		}};            	
            }
        });        
        if (!DateUtils.isOffSeason()) addMenuItem(new MenuItem("Festival", 23, 80)
        {
            public void run()
            {
                goHome();
                Ui.getUiEngine().pushScreen(new ScheduleScreen());
            }
        });
        if (!DateUtils.isOffSeason()) addMenuItem(new MenuItem("Events", 24, 100)
        {
            public void run()
            {
                goHome();
                Main.getQueryManager().getEventSchedules(
                        "special_events", new ProgressMonitorCallback()
                        {
                            public void invoke(Object obj)
                            {
                                super.invoke(obj);
                                Vector result = (Vector) obj;
                                Schedule[] schedules = new Schedule[result
                                        .size()];
                                result.copyInto(schedules);
                                Ui.getUiEngine().pushScreen(
                                        new ForumsScreen("Special Events:",
                                                schedules));
                            }
                        });
            }
        });
        if (!DateUtils.isOffSeason()) addMenuItem(new MenuItem("Forums", 25, 100)
        {
            public void run()
            {
                goHome();
                Main.getQueryManager().getEventSchedules("forums",
                        new ProgressMonitorCallback()
                        {
                            public void invoke(Object obj)
                            {
                                super.invoke(obj);
                                Vector result = (Vector) obj;
                                Schedule[] schedules = new Schedule[result
                                        .size()];
                                result.copyInto(schedules);
                                Ui.getUiEngine().pushScreen(
                                        new ForumsScreen("Filmmaking and Technology Forums:",
                                                schedules));
                            }
                        });
            }
        });
        if (!DateUtils.isOffSeason()) addMenuItem(new MenuItem("Films", 26, 100)
        {
            public void run()
            {
                goHome();
                Main.getQueryManager().getAllFilms(
                        new ProgressMonitorCallback()
                        {
                            public void invoke(Object obj)
                            {
                                super.invoke(obj);
                                Ui.getUiEngine().pushScreen(
                                        new FilmListScreen((Vector) obj, "Showing all films:"));
                            }
                        });
            }
        });
        if (!DateUtils.isOffSeason()) addMenuItem(new MenuItem("Genres", 27, 70)
        {
            public void run()
            {
                goHome();
                Main.getQueryManager().getGenres(new ProgressMonitorCallback()
                {
                    public void invoke(Object obj)
                    {
                        super.invoke(obj);
                        Vector result = (Vector) obj;
                        String[] genres = new String[result.size()];
                        result.copyInto(genres);
                        Ui.getUiEngine().pushScreen(new GenresScreen(genres));
                    }
                });
            }
        });
        addMenuItem(new MenuItem("DVDs", 27, 90)
        {
            public void run()
            {
                goHome();
                Ui.getUiEngine().pushScreen(new DVDEntryScreen());
            }
        });
        addMenuItem(new MenuItem("Credits", 28, 100)
        {
            public void run()
            {
                goHome();
                MobileItem credits = new MobileItem();
                credits.setTitle("Credits");
                credits
                        .setImageURL("http://www.sjsu.edu/publicaffairs/pics/sjsu_logo_color_thmb.jpg");
                credits
                        .setDescription("\nThis application was brought to you by the SJSU Computer Science Department\n"
                                + "\u2022 Sushma Bandekar\n"
                                + "\u2022 Aaditya Bhatia\n"
                                + "\u2022 Gavin Cooper\n"
                                + "\u2022 Arthur Corpuz\n"
                                + "\u2022 Travis Griffiths\n"
                                + "\u2022 Justin Holst\n"
                                + "\u2022 Cay Horstmann\n"
                                + "\u2022 Jindou Jiao\n"
                                + "\u2022 Bogdan Komlik\n"
                                + "\u2022 Ian Macauley\n"
                                + "\u2022 Kevin Ross\n"
                                + "\u2022 Tim Stanke\n"
                                + "\u2022 Prakash Shiwakoti\n"
                                + "Special thanks to\n"
                                + "\u2022 Lou Bash\n"
                                + "\u2022 Yung Le\n" + "\u2022 Matt Opsal\n");
                
                //display relevant version info
                MobileItem version = new MobileItem();
                version.setTitle("Cinequest Application Version");
                version.setDescription(ApplicationDescriptor
                      .currentApplicationDescriptor().getVersion()+"\n");
                
                MobileItem license = new MobileItem();
                license.setTitle("License");
                license.setLinkURL("http://www.gnu.org/licenses/gpl-3.0-standalone.html");
                MobileItem sourceLink = new MobileItem();
                sourceLink.setTitle("Source Code");
                sourceLink.setLinkURL("http://sjsu-cs.svn.cvsdude.com/cinequest-1_Fall08/cinequest/trunk/");
                sourceLink.setDescription("Log in with user name and password <b>sjsuguest</b>.");
                Section section = new Section();
                section.addItem(credits);
                section.addItem(version);
                section.addItem(sourceLink);
                section.addItem(license);
                                
                Ui.getUiEngine().pushScreen(new ControlList(section));
            }
        });
        if (!(this instanceof EntryScreen))
        {
            addMenuItem(MenuItem.separator(30));
           
            addMenuItem(new MenuItem("Back", 31, 100)
            {
                public void run()
                {
                    Ui.getUiEngine().popScreen(getScreen());
                }
            });
            addMenuItem(new MenuItem("Home", 32, 100)
            {
                public void run()
                {
                    goHome();
                }
            });
        }
    }

    /**
     * This method forces every screen but the EntryScreen to be popped off the
     * screen stack.
     */
    public void goHome()
    {
        while (!(Ui.getUiEngine().getActiveScreen() instanceof EntryScreen))
        {
            Ui.getUiEngine().popScreen(Ui.getUiEngine().getActiveScreen());
        }
    }

    /**
     * Don't overwrite our beautiful title.
     */
    public void setTitle(String arg0)
    {
        // Don't overwrite our beautiful title
    }
}
