package edu.sjsu.cinequest.client;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;

public class ClickableField extends LabelField
{
   private Runnable runnable; 
   
   public ClickableField(String label, Runnable runnable) { 
      super(label, Field.FOCUSABLE);
      setFont(getFont().derive(
            Font.UNDERLINED | Font.DOTTED_UNDERLINED));
      this.runnable = runnable;
   }

   public boolean trackwheelClick(int status, int time)
   {
      runnable.run();
      return true;
   }      
}
