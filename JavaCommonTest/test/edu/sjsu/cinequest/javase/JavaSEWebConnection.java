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

package edu.sjsu.cinequest.javase;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.sjsu.cinequest.comm.WebConnection;

public class JavaSEWebConnection extends WebConnection
{
    private HttpURLConnection connection;
    
    public JavaSEWebConnection(String url) throws IOException
    {
        connection = (HttpURLConnection) new URL(url).openConnection();
    }
    
    public byte[] getBytes() throws IOException {
        InputStream inputStream = connection.getInputStream();
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
        connection.disconnect();
        connection = null;
    }

	@Override
	public void setPostParameters(Hashtable postData) throws IOException {
       connection.setDoOutput(true);
       PrintWriter out = new PrintWriter(connection.getOutputStream());
       boolean first = true;
       Enumeration keys = postData.keys();
       while (keys.hasMoreElements()) 
       {
          if (first) first = false;
          else out.print('&');
          String key = keys.nextElement().toString();
          String value = postData.get(key).toString();
          out.print(key);
          out.print('=');
          out.print(URLEncoder.encode(value, "UTF-8"));               
       }         
       out.close();
	}

}
