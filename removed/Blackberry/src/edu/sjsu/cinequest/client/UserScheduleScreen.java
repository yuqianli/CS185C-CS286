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
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.User;
import edu.sjsu.cinequest.comm.cinequestitem.UserSchedule;

/**
 * This class describes the Schedules screen. The SchedulesScreen is reached
 * when a user clicks on a date from the ScheduleScreen. It shows all Schedule
 * items for that date. It uses the QueryManager to issue a query for all
 * schedules for a specified date.
 * 
 * @author Cay Horstmann
 * 
 */
public class UserScheduleScreen extends CinequestScreen {
	private Object[] sched; // mixture of date titles and Schedule items
	private UserScheduleListField slf;
	private HorizontalFieldManager fm;	
	private Field loggedInAs;
	private Field separator;
	private Field logout;
	
	private static final int LOGGED_OUT = 1;
	private static final int LOGGED_IN = 2;
	
	private int currentCommandSet = LOGGED_OUT;

	public UserScheduleScreen() {
		addMenuItem(new MenuItem("Remove", 1, 100) {
			public void run() {
				slf.setFocus();
				removeCurrent();
			}
		});

		addMenuItem(MenuItem.separator(10));

		add(new LabelField("Cinequest Interactive Schedule"));
				
		fm = new HorizontalFieldManager();
		
		Runnable sync = new Runnable() {
			public void run() {
				final User user = Main.getUser();
				Callback resultCallback = new Callback() {
					public void starting() {}
					public void invoke(Object result) { setScheduleItems(); }
					public void failure(Throwable t) {}
				};
				user.syncSchedule(LoginDialog1.getLoginAction(),
						SyncDialog.getSyncAction(),
						new ProgressMonitorCallback(), 
						resultCallback,
						Main.getQueryManager());				
			}};		
		
		fm.add(new ClickableField("Sync", sync));	
		add(fm);
		separator = new LabelField(" | ");
		logout = new ClickableField("Log out", new Runnable() {
			public void run() {
				User user = Main.getUser();
				if (user.getSchedule().isSaved() 
					|| Dialog.ask(Dialog.D_YES_NO, "Discard schedule?") == Dialog.YES) {
						user.logout();
						setScheduleItems();					
				}
			}
		});
		
		slf = new UserScheduleListField(DateFormat.TIME_SHORT);
		add(slf);
		Main.getUser().getSchedule().setDirty(true);
		setScheduleItems();
	}

	private void setCommandSet(int set) {
		if (currentCommandSet == set)
			return;
		currentCommandSet = set;
		if (set == LOGGED_OUT) { 
			fm.delete(separator); 
			fm.delete(logout); 
			fm.delete(loggedInAs); 
		}		
		else { 
			loggedInAs = new LabelField(" from " + Main.getUser().getEmail(), DrawStyle.ELLIPSIS);
			fm.add(separator);
			fm.add(logout);
			fm.add(loggedInAs);
		}
		updateDisplay();
	}

	protected void onExposed() // so it reflects schedule changes when popping
								// off schedules screen
	{
		setScheduleItems();
		super.onExposed();
	}

	public void setScheduleItems()
	// called after remove, onExposed
	{
		UserSchedule schedule = Main.getUser().getSchedule();
		if (schedule.isDirty()) {
			Vector entries = new Vector();
			String[] festivalDates = DateUtils.getFestivalDates();
			for (int i = 0; i < festivalDates.length; i++) {
				String date = festivalDates[i];
				entries.addElement(date);
				Vector items = schedule.getItemsOn(date);
				for (int j = 0; j < items.size(); j++)
					entries.addElement(items.elementAt(j));
			}
			sched = new Object[entries.size()];
			for (int i = 0; i < sched.length; i++)
				sched[i] = entries.elementAt(i);
			slf.set(sched);
			schedule.setDirty(false);
			invalidate();
		}
		if (Main.getUser().isLoggedIn())
			setCommandSet(LOGGED_IN);
		else
			setCommandSet(LOGGED_OUT);
	}

	/**
	 * Removes the currently selected item from the user schedule
	 */
	public void removeCurrent() {
		int index = slf.getSelectedIndex();
		if (index >= 0) {
			Object s = slf.get(slf, index);
			if (s instanceof Schedule) {
				Main.getUser().getSchedule().remove((Schedule) s);
				setScheduleItems();
			}
		}
	}
}