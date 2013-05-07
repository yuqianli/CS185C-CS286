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

package edu.sjsu.cinequest.rim;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.xml.parsers.ParserConfigurationException;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.sjsu.cinequest.client.ErrorScreen;
import edu.sjsu.cinequest.comm.Cache;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.MessageDigest;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.WebConnection;

/**
 * This class implements various RIM-specific services
 * 
 * @author Cay Horstmann
 * 
 */
public class RIMPlatform extends Platform {
	private Cache xmlRawBytesCache;
	private static final int MAX_IMAGE_WIDTH = 500;
	private static final int MAX_IMAGE_HEIGHT = 300;
	private static final int MAX_CACHE_SIZE = 50;
	// echo -n "edu.sjsu.cinequest.rim.RIMPlatform" | md5sum | cut -c1-16
	private static final long PERSISTENCE_KEY = 0xcfbd786faca62011L;
	private static final long MAX_CACHE_AGE = 1000L * 60 * 60 * 6; // 6 hours

	public RIMPlatform() {
		xmlRawBytesCache = (Cache) loadPersistentObject(PERSISTENCE_KEY);
		if (xmlRawBytesCache == null) {
			xmlRawBytesCache = new Cache(MAX_CACHE_SIZE);
		}
	}

	public WebConnection createWebConnection(String url) throws IOException {
		return new RIMWebConnection(url);
	}

	public Object convert(byte[] byteImageData) {
		return EncodedImage.createEncodedImage(byteImageData, 0,
				byteImageData.length).getBitmap();
	}

	// imageId must be a string
	public Object getLocalImage(Object imageId) {
		Bitmap bitmap = Bitmap.getBitmapResource((String) imageId);
		if (bitmap.getWidth() <= MAX_IMAGE_WIDTH
				&& bitmap.getHeight() <= MAX_IMAGE_HEIGHT)
			return bitmap;
		else
			return null;
	}

	public void parse(final String url, DefaultHandler handler,
			Callback callback) throws SAXException, IOException {
		SAXParser parser = null;
		InputSource inputSource = null;
		WebConnection connection = null;
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new SAXException(e.toString());
		}
		if (getFromCache(url, parser, handler, MAX_CACHE_AGE)) return;  
		starting(callback);
		try {
			try {
				connection = createWebConnection(url);
				byte[] xmlSource = (byte[]) connection.getBytes();
				// Store the xml source
				xmlRawBytesCache.put(url, xmlSource);
				inputSource = new InputSource(new InputStreamReader(
						new ByteArrayInputStream(xmlSource), "ISO-8859-1"));
				parser.parse(inputSource, handler);
			}
			// Reading fails. 
			catch (IOException e) {
				Platform.getInstance().log(e.getMessage());
				// Try to get XML from cache, no matter how old
				if (getFromCache(url, parser, handler, 0)) return;				
				throw e; // XML not found on cache.				
			}
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	private boolean getFromCache(String url, SAXParser sp,
			DefaultHandler handler, long maxage) throws SAXException, IOException {
		byte[] bytes = (byte[]) xmlRawBytesCache.get(url, maxage);
		// XML exists in cache and isn't too old
		if (bytes != null) {
			InputSource in = new InputSource(new InputStreamReader(
					new ByteArrayInputStream(bytes), "ISO-8859-1"));
			sp.parse(in, handler);
			Platform.getInstance().log(
					"RIMPlatform.getFromCache: Returned cached response for "
							+ url);
			return true;
		} else
			return false;
	}

	
	
	public String parse(final String url, Hashtable postData,
			DefaultHandler handler, Callback callback) throws SAXException,
			IOException {
		starting(callback);
		SAXParser parser = null;
		String doc = null;
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new SAXException(e.toString());
		}
		WebConnection connection = createWebConnection(url);
		try {
			connection.setPostParameters(postData);
			byte[] response = connection.getBytes();
			doc = new String(response);
			InputSource inputSource = new InputSource(new ByteArrayInputStream(
					response));
			parser.parse(inputSource, handler);
		} finally {
			connection.close();
		}
		return doc;
	}

	public void starting(final Callback callback) {
		if (callback == null)
			return;
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				callback.starting();
			}
		});
	}

	public void invoke(final Callback callback, final Object arg) {
		if (callback == null)
			return;
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				try {
					callback.invoke(arg);
				} catch (Throwable t) {
					Ui.getUiEngine()
							.pushScreen(new ErrorScreen(t.getMessage()));
				}
			}
		});
	}

	public void failure(final Callback callback, final Throwable arg) {
		if (callback == null)
			return;
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				callback.failure(arg);
			}
		});
	}

	public Object loadPersistentObject(long key) {
		try {
			PersistentObject pers = PersistentStore.getPersistentObject(key);
			if (pers == null)
				return null;	
			return pers.getContents();
		} catch (Throwable t) {
			log(t.getMessage());
			return null;
		}
	}

	public void storePersistentObject(long key, Object object) {
		PersistentObject pers = PersistentStore.getPersistentObject(key);
		pers.setContents(object);
		pers.commit();
	}

	public MessageDigest getMessageDigestInstance(String name) {
		if (name.equals("SHA-1"))
			return new MessageDigest() {
				private SHA1Digest delegate = new SHA1Digest();

				public void update(byte[] input) {
					delegate.update(input);
				}

				public byte[] digest() {
					return delegate.getDigest();
				}
			};

		return null;
	}

	public Object crypt(Object obj, boolean decrypt) {
		if (decrypt) {
			try {
				return PersistentContent.decodeString(obj);
			} catch (IllegalStateException e) {
				// unable to decode data; the device must be locked.
				return null;
			}
		} else {
			if (obj instanceof String)
				return PersistentContent.encode((String) obj);
			else if (obj instanceof byte[])
				return PersistentContent.encode((byte[]) obj);
			else
				return null;
		}
	}

	public void log(String message) {
		// key produced by: echo -n "edu.sjsu.cinequest.client.Main" | md5sum |
		// cut
		// -c1-16
		long APP_ID = 0xe2a3a144c78e37aaL;
		if (!loggerRegistered) {
			EventLogger
					.register(APP_ID, "Cinequest", EventLogger.VIEWER_STRING);
			loggerRegistered = true;
		}
		EventLogger.logEvent(APP_ID, message.getBytes());
	}

	public void log(Throwable ex) {
		log(ex.toString());
	}	
	
	public Vector sort(Vector vec, final Platform.Comparator comp) {
		SimpleSortingVector svec = new SimpleSortingVector();
		svec.setSortComparator(new net.rim.device.api.util.Comparator() {
			public int compare(Object obj1, Object obj2) {
				return comp.compare(obj1, obj2);
			}
		});
		for (int i = 0; i < vec.size(); i++)
			svec.add(vec.elementAt(i));
		svec.reSort();
		return svec.getVector();
	}

	public void close() {
		storePersistentObject(PERSISTENCE_KEY, xmlRawBytesCache);
	}

	private static SAXParserFactory factory = SAXParserFactory.newInstance();
	private boolean loggerRegistered;
}
