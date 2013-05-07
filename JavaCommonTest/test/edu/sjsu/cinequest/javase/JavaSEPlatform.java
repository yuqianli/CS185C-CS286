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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.MessageDigest;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.WebConnection;

public class JavaSEPlatform extends Platform
{
    public WebConnection createWebConnection(String url) throws IOException
    {
        return new JavaSEWebConnection(url);
    }

    public Object convert(byte[] imageBytes)
    {
        return new ImageIcon(imageBytes).getImage();
    }

    public Object getLocalImage(Object imageName)
    {
        return new ImageIcon((String) imageName).getImage();
    }

    public void parse(String url, DefaultHandler handler, Callback callback)
            throws SAXException, IOException
    {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp;
        try
        {
            sp = spf.newSAXParser();
        } catch (ParserConfigurationException e)
        {
            throw new SAXException(e.toString());
        }
        InputStream in = new URL(url).openStream();
        sp.parse(in, handler);
    }
    
    public String parse(final String url, Hashtable postData, DefaultHandler handler, Callback callback)
       throws SAXException, IOException
    {
       SAXParserFactory spf = SAXParserFactory.newInstance();
       SAXParser sp;
       try
       {
           sp = spf.newSAXParser();
       } catch (ParserConfigurationException e)
       {
           throw new SAXException(e.toString());
       }
       WebConnection connection = createWebConnection(url);
       connection.setPostParameters(postData);
       byte[] response = connection.getBytes();
       sp.parse(new ByteArrayInputStream(response), handler);
       return new String(response);
    }
    

    public void starting(Callback callback)
    {
        callback.starting();
    }

    public void invoke(Callback callback, Object arg)
    {
        if (callback == null)
            return;
        callback.invoke(arg);
    }

    public void failure(Callback callback, Throwable arg)
    {
        if (callback == null)
            return;
        callback.failure(arg);
    }

    public Object loadPersistentObject(long key)
    {
        File file = new File("" + key);
        ObjectInputStream in;
        try
        {
            in = new ObjectInputStream(new FileInputStream(file));
            Object ret;
            ret = in.readObject();
            in.close();
            return ret;
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void storePersistentObject(long key, Object object)
    {
        File file = new File("" + key);
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(file));
            out.writeObject(object);
            out.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public MessageDigest getMessageDigestInstance(String name)
    {
        if (name.equals("SHA-1"))
            try
            {
                return new MessageDigest()
                {
                    private java.security.MessageDigest delegate = java.security.MessageDigest
                            .getInstance("SHA-1");

                    public void update(byte[] input)
                    {
                        delegate.update(input);
                    }

                    public byte[] digest()
                    {
                        return delegate.digest();
                    }
                };
            } catch (NoSuchAlgorithmException ex)
            {
                ex.printStackTrace();
                return null;
            }

        return null;
    }
    
   public Vector sort(Vector vec, final Comparator comp)
   {
      Vector svec = new Vector(vec);
      Collections.sort(svec, new java.util.Comparator() {
    	  public int compare(Object obj1, Object obj2) {
    		  return comp.compare(obj1, obj2);
    	  }
      });
      return svec;
   }
    
   public void log(String message)
   {
       Logger.getLogger("global").info(message);
   }
   
   public void log(Throwable t)
   {
       Logger.getLogger("global").info(t.getMessage());
   }
   
   public void close()
   {
   }
}
