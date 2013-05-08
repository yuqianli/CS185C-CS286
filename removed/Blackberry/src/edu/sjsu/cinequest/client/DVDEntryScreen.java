package edu.sjsu.cinequest.client;

import java.util.Vector;

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ObjectListField;

public class DVDEntryScreen extends CinequestScreen
{
    private ObjectListField topMenu;     
    /**
     * Default constructor
     */
    public DVDEntryScreen()
    {        
        topMenu = new ObjectListField()
        {
            public boolean trackwheelClick(int status, int time)
            {
                int index = getSelectedIndex();
                if (index == 0)
                {
                    Main.getQueryManager().getSpecialScreen("release", new ControlListCallback());                    
                }
                else if (index == 1)
                {
                    Main.getQueryManager().getSpecialScreen("pick", new ControlListCallback());                    
                }
                else if (index == 2)
                {
                    Main.getQueryManager().getDVDs(new ProgressMonitorCallback()
                    {
                        public void invoke(Object obj)
                        {
                            super.invoke(obj);
                            Ui.getUiEngine().pushScreen(new DVDScreen((Vector) obj));
                        }
                    });                    
                }
               return true;
            }
        };        
  
        topMenu.set(new String[] { "New DVD Release", "Pick of the Week", "Browse all DVDs and Downloads"});
        add(topMenu);
    }
}
