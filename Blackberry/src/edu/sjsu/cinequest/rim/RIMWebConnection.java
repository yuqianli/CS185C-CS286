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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.blackberry.api.browser.URLEncodedPostData;

import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.WebConnection;

/**
 * The RIM-specific wrapper for a web connection
 * @author Cay Horstmann
 */
public class RIMWebConnection extends WebConnection
{
    private HttpConnection connection;       
    
    public RIMWebConnection(String url) throws IOException
    {
        //connection = (HttpConnection) Connector.open(url, Connector.READ);
       
       //doing this way otherwise it causes error w/ Blackberry OS 4.5
       //even if connection is never written to.
    	synchronized(this)
    	{    		
    		Platform.getInstance().log("Opening connection to " + url);
    		connection = (HttpConnection) Connector.open(url);    		
    	}
    }
    
    public void setPostParameters(Hashtable postData) throws IOException {
		URLEncodedPostData encodedPostData = new URLEncodedPostData(null,
				null);
		Enumeration keys = postData.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			String value = postData.get(key).toString();
			encodedPostData.append(key, value);
		}
        connection.setRequestMethod(HttpConnection.POST);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		OutputStream out = connection.openOutputStream();
		byte[] request = encodedPostData.getBytes();
		out.write(new String(request).getBytes());    	
    }

    public InputStream getInputStream() throws IOException
    {        
       int rc = connection.getResponseCode();
       if (rc != HttpConnection.HTTP_OK) {
           throw new IOException("HTTP response code: " + rc);
       }
       return connection.openInputStream();        
    }
    
    public byte[] getBytes() throws IOException 
    {        
        InputStream inputStream = getInputStream();
        byte[] responseData = new byte[10000];
        int length = 0;
        try
        {
            int count;
            while (-1 != (count = inputStream.read(responseData, length,
                    responseData.length - length)))
            {
                length += count;
                if (length == responseData.length)
                {
                    byte[] newData = new byte[2 * responseData.length];
                    System.arraycopy(responseData, 0, newData, 0, length);
                    responseData = newData;
                }
            }
        }
        finally
        {
            close();
        }
        byte[] response = new byte[length]; 
        System.arraycopy(responseData, 0, response, 0, length);
        return response;    
    }
    
    public String getHeaderField(String name) throws IOException
    {
        return connection.getHeaderField(name);
    }
    
    public void close() throws IOException
    {
        if (connection != null) connection.close();
        connection = null;
        Platform.getInstance().log("Closing connection");
    }        
}
