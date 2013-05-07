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

import java.util.Calendar;


import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.util.DateTimeUtilities;

/**
 * RIM-specific utilities for date handling
 * @author Cay Horstmann
 */
public class DateUtils
{
	private int[] fields = new int[7];
    private Calendar cal = Calendar.getInstance();
    public static final int NORMAL_MODE = 0;
    public static final int FESTIVAL_TEST_MODE = 1;
    public static final int OFFSEASON_TEST_MODE = 2;
    public static final int UNINITIALIZED_MODE = -1;
    private static int mode = UNINITIALIZED_MODE;
    
    private static String[] festivalDates =
    { 
    "2011-03-01", "2011-03-02", "2011-03-03", "2011-03-04", 
    "2011-03-05", "2011-03-06", "2011-03-07", "2011-03-08",
    "2011-03-09", "2011-03-10", "2011-03-11", "2011-03-12",
    "2011-03-13"
    };

    
    /**
     * Get all dates for this festival in the format yyyy-MM-dd
     * @return an array of all dates
     */
    public static String[] getFestivalDates()
    {
    	if (mode == UNINITIALIZED_MODE) return new String[0];
        return festivalDates;
    }
    
    /**
     * Set the festival dates
     * @param fdates a String[] containing each festival date (like "2009-02-26") in order.
     */
    public static void setFestivalDates(String[] fdates)
    {
    	if (mode == UNINITIALIZED_MODE) mode = NORMAL_MODE;
    	if (mode == NORMAL_MODE) 
    		festivalDates = fdates;
    }
 
    
    /**
     * Checks whether we are in off-season
     * @return if today's date indicates that we should display the app in off-season mode
     */
    public static boolean isOffSeason()
    {
    	//return season.equalsIgnoreCase("off-season");
    	if (mode == UNINITIALIZED_MODE) return false;
        return today().compareTo(festivalDates[festivalDates.length - 1]) > 0;
    }
    
    /**
     * Sets the mode for date/time reporting
     * @param mode one of NORMAL_MODE, FESTIVAL_TEST_MODE, OFFSEASON_TEST_MODE
     */
    public static void setMode(int mode)
    {
        DateUtils.mode = mode;
    }
    
    /**
     * Gets the mode for date/time reporting
     * @return one of NORMAL_MODE, FESTIVAL_TEST_MODE, OFFSEASON_TEST_MODE
     */
    public static int getMode()
    {
        return mode;
    }
    
    /**
     * Parses a number inside a string, without error checking and without extracting a substring
     * @param str a string that contains a decimal number in str.substring(from, to)
     * @param from the starting index
     * @param to the past-the-end index
     * @return the integer that would be obtained by Integer.parseInt(str.substring(from, to))
     */
    private static int parseInt(String str, int from, int to)
    {
        int r = 0;
        for (int i = from; i < to; i++)
        {
            r = 10 * r + str.charAt(i) - '0';
        }
        return r;
    }

    /**
     * Formats a date string into a locale-specific version. (Note: This is not a static method for thread safety)
     * @param date a string in the format yyyy-MM-dd HH:mm or yyyy-MM-dd
     * @style one of the net.rim.device.api.i18n.DateFormat constants DATE_FULL, DATE_LONG, DATE_MEDIUM,
     * DATE_SHORT, DATE_DEFAULT, TIME_FULL, TIME_LONG, TIME_MEDIUM, TIME_SHORT,
     * TIME_DEFAULT, DATETIME_DEFAULT
     */
    public String format(String date, int style)
    {
        if (date == null) return "";
        fields[0] = parseInt(date, 0, 4);
        fields[1] = parseInt(date, 5, 7) - 1;
        fields[2] = parseInt(date, 8, 10);
        if (date.length() > 10)
        {
            fields[3] = parseInt(date, 11, 13);
            fields[4] = parseInt(date, 14, 16);
        }
        else
        {
            fields[3] = 0;
            fields[4] = 0;
        }
        DateTimeUtilities.setCalendarFields(cal, fields);
        return DateFormat.getInstance(style).format(cal);
    }
    
    /**
     * Returns today's date
     * @return today's date in yyyy-MM-dd format
     */
    public static String today()
    {
        if (mode == FESTIVAL_TEST_MODE) return festivalDates[5];
        if (mode == OFFSEASON_TEST_MODE) return "2099-12-31"; 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.formatLocal(System.currentTimeMillis());
    }
}
