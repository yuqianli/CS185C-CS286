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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.CallbackException;

/**
 * A callback for progress reporting.
 * @author Cay Horstmann
 */
public class ProgressMonitorCallback implements Callback
{
    private MainScreen progressScreen = new CinequestScreen();
    private LabelField label = new LabelField();
    private int count = -1; 
    private ProgressIndicator progressIndicator = new ProgressIndicator();
    private Timer timer = new Timer();
    private static final int DELAY = 200; // Delay between progress increments (milliseconds)
    
    class ProgressIndicator extends LabelField
    {
        public ProgressIndicator()
        {
            super("", Field.USE_ALL_WIDTH);
        }
        
        /**
         * Overridden paint method for showing the title graphic.
         */
        public void paint(Graphics graphics)
        {
            super.paint(graphics);
            int oldColor = graphics.getColor();
            int width = getHeight();
            int wrap = getWidth() / width;
            int x = 0;
            int y = width / 10;
            width -= y;
            
            for (int i = 0; i < count % wrap; i++)
            {
            
                if(i % 2 == 0)                    
                    graphics.setColor(0x00000000);
                else
                    graphics.setColor(0x00FF0000);
                graphics.fillRect(x, y, width, width);
                x += width + y;
            }
            graphics.setColor(oldColor);
        }
        
        public void invalidate()
        {            
            super.invalidate();
        }
        
    }

    public void starting()
    {
    	if (count >= 0) return;    	
    	
    	timer.schedule(new TimerTask() {
    		public void run() {
    	        count++;
    			progressIndicator.invalidate();    			
    		}
    	}, new Date(), DELAY);
    	
        progressScreen.add(label);
        
        progressScreen.add(progressIndicator);
        
  		label.setText("Fetching data...");
        Ui.getUiEngine().pushScreen(progressScreen);
    	count = 0;
    }

    private void popScreen()
    {
    	if (count >= 0)
    	{
	        try
	        {
	            Ui.getUiEngine().popScreen(progressScreen);
	        }
	        catch (IllegalArgumentException ex)
	        {
	            // This happens if the user already popped off the progress screen
	        }
	        timer.cancel();
    	}    	
    }
    
    public void invoke(Object result)
    {
    	popScreen();
    }

    public void failure(final Throwable t)
    {
		if (t instanceof CallbackException) {
			int level = ((CallbackException) t).getLevel();
			if (level == CallbackException.IGNORE)
			{
				popScreen();
				return;
			}
		}
		
    	if (count < 0) starting();    	    	
    	count = 0;
    	progressIndicator.invalidate();
        label.setText("Application Error");
        progressScreen.add(new SeparatorField());
        LabelField report = new LabelField(t.getMessage());
        progressScreen.add(report);
    }
}
