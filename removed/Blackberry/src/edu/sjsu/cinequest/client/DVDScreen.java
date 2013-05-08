package edu.sjsu.cinequest.client;

import java.util.Vector;

import net.rim.device.api.ui.MenuItem;
import edu.sjsu.cinequest.comm.cinequestitem.Filmlet;

/**
 * This class describes the DVDs screen. It holds a list of DVDs, which are
 * returned as Films; it basically operates the same as the FilmsScreen. The
 * screen supports prefix searching, that is, if a user types a letter or
 * letters, the first instance of the film beginning with that sequence is
 * highlighted. Currently a user will reach this screen through the menu.
 * 
 * @author Ian Macauley
 * 
 */
public class DVDScreen extends FilmListScreen
{
   /**
    * Constructor for the DVDScreen
    * @param dvdsAndDownloads a Vector of Filmlets representing DVDs and Downloads
    */
    public DVDScreen(final Vector dvdsAndDownloads)
    {
        super(dvdsAndDownloads, "Showing all DVDs and Downloads:");
        int dvdsCount = 0;
        int downloadsCount = 0;
        final Filmlet[] all = getList();
        for (int i = 0; i < all.length; i++)
        {
            Filmlet item = all[i];
            if (item.isDVD())
                dvdsCount++;
            if (item.isDownload())
                downloadsCount++;
        }
        final Filmlet[] dvds = new Filmlet[dvdsCount];
        final Filmlet[] downloads = new Filmlet[downloadsCount];
        dvdsCount = 0;
        downloadsCount = 0;
        for (int i = 0; i < all.length; i++)
        {
            Filmlet item = all[i];
            if (item.isDVD())
                dvds[dvdsCount++] = item;
            if (item.isDownload())
                downloads[downloadsCount++] = item;
        }
        addMenuItem(new MenuItem("DVDs only", 1, 100)
        {
            public void run()
            {
                setList(dvds);
            }
        });
        addMenuItem(new MenuItem("Downloads", 2, 100)
        {
            public void run()
            {
                setList(downloads);
            }
        });
        addMenuItem(new MenuItem("Show All", 3, 100)
        {
            public void run()
            {
                setList(all);
            }
        });
        addMenuItem(MenuItem.separator(3));
    }
    
    public void makeDetailQuery(Filmlet f)
    {
        Main.getQueryManager().getDVD(f.getId(),
                new ControlListCallback());        
    }
}