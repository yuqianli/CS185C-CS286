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

import edu.sjsu.cinequest.comm.Action;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.CallbackException;
import edu.sjsu.cinequest.comm.QueryManager;
import edu.sjsu.cinequest.comm.cinequestitem.User;
import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

/**
 * This class describes the login dialog.
 * 
 * @author Cay Horstmann
 * 
 */
public class LoginDialog1 extends CinequestScreen {
	private EmailAddressEditField emailField = new EmailAddressEditField(
			"Email: ", "");
	private PasswordEditField passwordField = new PasswordEditField(
			"Password: ", "");

	public LoginDialog1(String command, String email, String password,
			final Runnable okRunnable, final Runnable cancelRunnable) {
		emailField.setText(email);
		passwordField.setText(password);
		add(emailField);
		add(passwordField);
		HorizontalFieldManager hfm = new HorizontalFieldManager();
		hfm.add(new ClickableField(command, new Runnable() {
			public void run() {
				Ui.getUiEngine().popScreen(LoginDialog1.this);
				okRunnable.run();
			}
		}));
		hfm.add(new LabelField(" | "));
		hfm.add(new ClickableField("Cancel", new Runnable() {
			public void run() {
				Ui.getUiEngine().popScreen(LoginDialog1.this);
				cancelRunnable.run();
			}
		}));
		hfm.add(new LabelField(" | "));
		hfm.add(new ClickableField("Register", new Runnable() {
			public void run() {
				Ui.getUiEngine().popScreen(LoginDialog1.this);
				BrowserSession browserSession = Browser.getDefaultSession();
				browserSession.displayPage(QueryManager.registrationURL);
			}
		}));
		
		add(hfm);
	}

	public String getEmail() {
		return emailField.getText();
	}

	public String getPassword() {
		return passwordField.getText();
	}

	public boolean onClose() {
		return false;
	}	
	
	public static Action getLoginAction() {
		return new Action() {
			private LoginDialog1 dialog;
			public void start(Object in, final Callback cb) {
				User.Credentials creds = (User.Credentials) in;
				dialog = new LoginDialog1("Log in",
						creds.email, creds.password, new Runnable() {
							public void run() {
								cb.invoke(new User.Credentials(dialog.getEmail(),
										dialog.getPassword()));
							}
						}, new Runnable() {
							public void run() {
								cb.failure(new CallbackException("Login canceled", CallbackException.IGNORE));
							}
						});
				Ui.getUiEngine().pushScreen(dialog);
			}
		};
	}
}