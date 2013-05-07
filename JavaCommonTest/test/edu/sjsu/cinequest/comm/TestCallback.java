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

package edu.sjsu.cinequest.comm;

public class TestCallback implements Callback
{
    private Object result;
    private volatile boolean ready;

    public synchronized Object getResult()
    {
        try
        {
            while (!ready)
                wait();
            return result;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void invoke(Object result)
    {
        if (ready) throw new IllegalStateException("Do not reuse callback object");
        this.result = result;
        ready = true;
        notify();
    }
    
    public void starting() {
        // Do nothing
    }
    
    public synchronized void failure(Throwable t)
    {
        t.printStackTrace();
        this.result = null;
        ready = true;
        notify();
    }
}
