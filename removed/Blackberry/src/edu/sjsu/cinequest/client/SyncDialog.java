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

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import edu.sjsu.cinequest.comm.Action;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.cinequestitem.User;

/**
 * This class describes the sync dialog.
 * 
 * @author Cay Horstmann
 * 
 */
public class SyncDialog extends CinequestScreen {
	private Runnable action(final Callback cb, final int i) {
		return new Runnable() {
			public void run() {
				Ui.getUiEngine().popScreen(SyncDialog.this);
				cb.invoke(new Integer(i));
			}
		};
	}

	public SyncDialog(Callback cb) {
		// TODO: Explanatory test
		VerticalFieldManager fm = new VerticalFieldManager();
		fm.add(new LabelField("Conflict between phone and server schedule"));
		fm.add(new ClickableField("Merge phone and server", action(cb, User.SYNC_MERGE)));
		fm.add(new ClickableField("Phone overwrites server", action(cb, User.SYNC_SAVE)));
		fm.add(new ClickableField("Server overwrites phone ", action(cb, User.SYNC_REVERT)));	
		fm.add(new ClickableField("Cancel sync", action(cb, User.SYNC_CANCEL))); 
		add(fm);
	}
	
	public boolean onClose() {
		return false;
	}

	public static Action getSyncAction() {
		return new Action() {
			private SyncDialog dialog;
			public void start(Object in, final Callback cb) {
				dialog = new SyncDialog(cb);			
				Ui.getUiEngine().pushScreen(dialog);
			}
		};
	}
}