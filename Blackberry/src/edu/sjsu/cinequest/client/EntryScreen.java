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

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectListField;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.Platform;

/**
 * This class describes the EntryScreen, which is the application entry for the
 * whole application.
 * @author Ian Macauley
 *
 */
public class EntryScreen extends CinequestScreen
{   
    private ObjectListField topMenu;     
    /**
     * Default constructor
     */
    public EntryScreen()
    {

       Main.getQueryManager().getFestivalDates(new Callback() {
          public void starting() 
          {
          }          
          public void failure(Throwable t)
          {
        	  Platform.getInstance().log(t.getMessage());
          }
          public void invoke(Object result)
          {
             DateUtils.setFestivalDates((String[]) result);            
          }
       });
       
        topMenu = new ObjectListField()
        {
            public boolean trackwheelClick(int status, int time)
            {
               String choice = (String) get(this, getSelectedIndex());
               
               if(choice.equals("Cinequest Film Festival"))
               {
                  Ui.getUiEngine().pushScreen(new ScheduleScreen());
               }
               else if(choice.equals("Interactive Schedule"))
               {
                  /*
                  Main.getQueryManager().getTestQuery(
                        new ProgressMonitorCallback()
                        {
                            public void invoke(Object obj)
                            {
                                super.invoke(obj);
                                Ui.getUiEngine().pushScreen(
                                        new TestMessageScreen((String) obj));
                            }
                        });
                   */
                  Ui.getUiEngine().pushScreen(new UserScheduleScreen());
               }
               else if(choice.equals("DVDs and Downloads"))
               {
                   Ui.getUiEngine().pushScreen(new DVDEntryScreen());
               }
               else if(choice.endsWith("News"))
               {
                   Main.getQueryManager().getSpecialScreen(DateUtils.isOffSeason() ? "offseason" : "home", new ControlListCallback());
               }
               else if(choice.endsWith("Information"))
               {
                   Main.getQueryManager().getSpecialScreen(DateUtils.isOffSeason() ? "offseasoninfo" : "info", new ControlListCallback());
               }               
               else if(choice.equals("Test offseason mode")) 
               {
                   DateUtils.setMode(DateUtils.OFFSEASON_TEST_MODE);
                   setTopMenu();
                   invalidate();
               }               
               else if(choice.equals("Test festival mode")) 
               {
                   DateUtils.setMode(DateUtils.FESTIVAL_TEST_MODE);
                   setTopMenu();
                   invalidate();
               }               
               return true;
            }
        };        
        
        setTopMenu();
        add(topMenu);
        add(new LabelField("", Field.NON_FOCUSABLE));
        
        // NOTE: This image is not cached persistently
        String url = Main.getQueryManager().getMainImageURL();
        final BitmapField image = new BitmapField(null, BitmapField.LEFT | Field.FOCUSABLE);
        Bitmap bitmap = (Bitmap) Main.getImageManager()
                .getImage(url, new Callback()
                {
                    public void starting() 
                    {
                    }
                    public void invoke(Object result)
                    {
                        image.setBitmap((Bitmap) result);
                        invalidate();
                    }

                    public void failure(Throwable t)
                    {
                  	  	Platform.getInstance().log(t.getMessage());                    	
                    }
                }, "creative.png", false /* persistent */);
        image.setBitmap(bitmap);
        add(image);
    }
    
    public void setTopMenu()
    {
        String[] choices;
        
        if (DateUtils.isOffSeason()) choices = new String[] { "Hot News", "DVDs and Downloads", "Cinequest Film Festival Information" };
        else choices = new String[] { "Cinequest Film Festival", "Interactive Schedule", "DVDs and Downloads", "Top News", "General Information" }; 
        
        if (DateUtils.getMode() != DateUtils.NORMAL_MODE)
        {
            String[] newChoices = new String[choices.length + 1];
            System.arraycopy(choices, 0, newChoices, 0, choices.length);
            newChoices[choices.length] = DateUtils.isOffSeason() ? "Test festival mode" : "Test offseason mode";
            choices = newChoices;
        }
        
        topMenu.set(choices);        
    }
    
   /**
     * Overrides default onClose method with a dialog notifying the user the
     * program is exiting.
     */
    public boolean onClose()
    {
        Main.getUser().persistSchedule();
        Main.getImageManager().close();
        Platform.getInstance().close();
        System.exit(0);
        return true;
    }
}
