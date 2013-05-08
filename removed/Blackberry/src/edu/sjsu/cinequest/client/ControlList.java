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

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.blackberry.api.mail.Session;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.HParser;
import edu.sjsu.cinequest.comm.cinequestitem.Film;
import edu.sjsu.cinequest.comm.cinequestitem.MobileItem;
import edu.sjsu.cinequest.comm.cinequestitem.ProgramItem;
import edu.sjsu.cinequest.comm.cinequestitem.Schedule;
import edu.sjsu.cinequest.comm.cinequestitem.Section;
import edu.sjsu.cinequest.comm.cinequestitem.UserSchedule;
import edu.sjsu.cinequest.comm.cinequestitem.VenueLocation;

/**
 * ControlList is a screen that is constructed from objects in the cinequestitem
 * package It can handle a Vector of them, as well as a single instance.
 * 
 * Currently this class recognizes the CinequestItems Schedule, Film,
 * VenueLocation, DVD, SpecialItem and Home
 * 
 * @author Justin Holst
 * @author Cay Horstmann
 */
public class ControlList extends CinequestScreen
{
    /** 	
     * Creates a screen suitable for displaying an item or a vector of items.
     * @param in the item: a Film, Schedule, VenueLocation, SpecialItem, Home,
     * or Vector of such items
     */
    public ControlList(Object in)
    {
        vfm = new VerticalFieldManager();
        this.add(vfm);
        if (in instanceof Vector)
        {
            Vector inputs = (Vector) in;
            for (int i = 0; i < inputs.size(); i++)
            {
                if (i > 0)
                    vfm.add(new LabelField(" "));
                process(inputs.elementAt(i));
            }
        }
        else
            process(in);
    }

    /**
     * Processes the item
     * @param current a single item of a type that ControlList can handle
     */
    private void process(Object current)
    {
        if (current instanceof ProgramItem)
        {
            ProgramItem item = (ProgramItem) current;
            String link = PRG_QUERY_STRING + item.getId();
            addEmailMenuItem(item.getTitle(), link);
            Vector v = item.getFilms();
            if (v.size() == 1) // Program item with single film
                this.film((Film) v.elementAt(0));
            else
                this.programItem(item);
        }
        else if (current instanceof Film)
        {
        	Film film = (Film) current;
        	this.film(film);
        	if(film.isDownload() || film.isDVD())
        	{
        		String link = DVD_QUERY_STRING + film.getId();
        		addEmailMenuItem(film.getTitle(), link);
        	}
        }
        else if (current instanceof VenueLocation)
        {
            this.venueLocation((VenueLocation) current);
        }
        else if (current instanceof MobileItem)
        {
            this.specialItem((MobileItem) current);
        }
        else if (current instanceof Section)
        {
            this.section((Section) current);
        }
    }

    private void makeBlankLine()
    {
        vfm.add(new LabelField("", Field.NON_FOCUSABLE));
    }

    /**
     * Adds text to the rich text field that is currently built up
     * @param text the text to add
     * @param style the style (0 for default, or an HParser style value)
     */
    private void addText(String text, int style)
    {
        if (text == null || text.length() == 0)
            return;
        if (buffer == null)
        {
            buffer = new StringBuffer();
            regions = 0;
        }
        buffer.append(text);
        attrs[regions] = (byte) style;
        off[regions + 1] = off[regions] + text.length();
        regions++;
    }

    /**
     * Adds a large header to the rich text field that is currently built up
     * @param header the header to add
     */
    private void addHeader(String header)
    {
        if (header == null || header.length() == 0)
            return;
        addText(header + "\n", HParser.LARGE);
    }

    /**
     * Adds an entry with a bold label to the rich text field that is currently
     * built up
     * @param label the label (such as "Director")
     * @param value the labeled string (such as the director)
     */
    private void addEntry(String label, String value)
    {
        if (value != null && value.length() > 0)
        {
            addText(label + ": ", HParser.BOLD);
            addText(value + "\n", 0);
        }
    }

    /**
     * Adds a showing time field, with the time formatted to the current locale,
     * and the venue in parentheses
     * @param time the time to show
     * @param venue the venue (or null if none is known)
     */
    private void addShowingTime(final Schedule s)
    {
        final String time = s.getStartTime();
        final String venue = s.getVenue();
        String formattedTime = dateFormatter.format(time,
                DateFormat.DATETIME_DEFAULT);
        HorizontalFieldManager hfm = new HorizontalFieldManager();
        hfm.add(new LabelField(formattedTime));
        if (venue != null && venue.length() > 0)
        {
            hfm.add(new LabelField(" - "));
            hfm.add(new ClickableField(venue, new Runnable()
            {
                public void run()
                {
                    Main.showVenue(venue);                    
                }
            }));
        }
        
        hfm.add(new LabelField(" - "));
        if (Main.getUser().getSchedule().getType(s) == UserSchedule.NOT_PRESENT)
           hfm.add(new ClickableField("Add", new Runnable()
           {
               public void run()
               {
                  Main.getUser().getSchedule().add(s); 
                  Ui.getUiEngine().popScreen(Ui.getUiEngine().getActiveScreen());
               }
           }));
        else
           hfm.add(new ClickableField("Remove", new Runnable()
           {
               public void run()
               {
                  Main.getUser().getSchedule().remove(s);                    
                  Ui.getUiEngine().popScreen(Ui.getUiEngine().getActiveScreen());
               }
           }));
     
       vfm.add(hfm);
    }

    /**
     * Makes a RichTextField from the items that have been added.
     */
    private void makeField()
    {
        if (buffer == null)
            return;
        int[] off2 = new int[regions + 1];
        System.arraycopy(off, 0, off2, 0, off2.length);
        byte[] attrs2 = new byte[regions];
        System.arraycopy(attrs, 0, attrs2, 0, attrs2.length);
        vfm.add(new RichTextField(buffer.toString(), off2, attrs2, fonts,
                RichTextField.TEXT_ALIGN_LEFT));
        buffer = null;
        regions = 0;
    }

    /**
     * Makes a rich text from a string containing HTML markup
     * @param htmlText the text to be parsed and displayed
     */
    private void makeHTMLField(String htmlText)
    {
        if (htmlText == null || htmlText.length() == 0)
            return;
        hparser.parse(htmlText);
        vfm.add(new ActiveRichTextField(hparser.getResultString(), hparser
                .getOffsets(), hparser.getAttributes(), fonts,
                foregroundColors, backgroundColors,
                RichTextField.TEXT_ALIGN_LEFT));
        makeImages(hparser.getImageURLs());
    }

    /**
     * Makes a field with a single image
     * @param url the image URL
     */
    private void makeImage(String url)
    {
        if (url == null || url.length() == 0)
            return;
        String imageUrl = Main.getQueryManager().resolveRelativeImageURL(url);
        final BitmapField image = new BitmapField();
        vfm.add(image);
        Bitmap bitmap = (Bitmap) Main.getImageManager().getImage(imageUrl,
                new Callback()
                {
                    public void invoke(Object result)
                    {
                        image.setBitmap((Bitmap) result);
                        invalidate();
                    }
                    
                    public void starting() 
                    {
                    }

                    public void failure(Throwable t)
                    {
                        image.setBitmap(null);
                        invalidate();
                    }
                }, "fetching.png", true /* persistent */);
        image.setBitmap(bitmap);
    }

    /**
     * Makes fields with a sequence of images
     * @param images the image URLs
     */
    private void makeImages(Vector images)
    {
        if (images != null && images.size() > 0)
        {
            Vector resolvedImages = new Vector();
            for (int i = 0; i < images.size(); i++)
                resolvedImages.addElement(Main.getQueryManager()
                        .resolveRelativeImageURL((String) images.elementAt(i)));
            Main.getImageManager().getImages(resolvedImages, new Callback()
            {
                public void starting() 
                {
                }

                public void invoke(Object result)
                {
                	Vector images = (Vector) result;
                	for (int i = 0; i < images.size(); i++)
                	{
                		BitmapField image = new BitmapField();
                		image.setBitmap((Bitmap) images.elementAt(i));
                		vfm.add(image);
                	}
                    invalidate();
                }

                public void failure(Throwable t)
                {
                }
            });
        }
    }

    /**
     * Displays all pertinent information supplied by a VenueLocation item
     * @param VenueLocation object
     */
    private void venueLocation(VenueLocation in)
    {
        makeImage(in.getImageURL());
        addHeader(in.getVenueAbbreviation());
        addEntry("Location", in.getLocation());
        addEntry("Description", in.getDescription());
        makeField();
        final String directionsURL = in.getDirectionsURL();
        if (directionsURL != null)
        {
            Field directionsField = new LabelField("Directions", Field.FOCUSABLE)
            {
                public boolean trackwheelClick(int status, int time)
                {
                    BrowserSession browserSession = Browser.getDefaultSession();
                    browserSession.displayPage(directionsURL);
                    return true;
                }
            };
            directionsField.setFont(directionsField.getFont().derive(
                    Font.UNDERLINED | Font.DOTTED_UNDERLINED));
            add(directionsField);
        }
    }

    /**
     * Displays all pertinent information supplied by a SpecialItem item
     * @param in SpecialItem object
     */
    private void specialItem(final MobileItem in)
    {
        makeImage(in.getImageURL());
        String title = in.getTitle();
        if (title == null || title.length() == 0)
            title = in.getTitle();
        /*
         * If the item has a link, then make the title clickable. Otherwise make
         * it bold
         */
        if (title != null && title.length() > 0)
        {
            final String linkType = in.getLinkType();
            final String linkURL = in.getLinkURL();
            if (linkType == null && linkURL == null)
            {
                addText(title, HParser.BOLD);
                makeField();
            }
            else
            {
                LabelField clickableTitle = new LabelField(title,
                        Field.FOCUSABLE)
                {
                    public boolean trackwheelClick(int status, int time)
                    {
                        if (linkURL != null)
                        {
                            BrowserSession browserSession = Browser
                                    .getDefaultSession();
                            browserSession.displayPage(linkURL);
                        }
                        else
                        {
                            Callback callback = new ControlListCallback();
                            // TODO: Obsolete--always call getProgramItem
                            if (linkType.equals("mobile_item_id") || linkType.equals("item"))
                                Main.getQueryManager().getMobileItem(
                                        in.getLinkId(), callback);
                            else if (linkType.equals("program_item_id"))
                               Main.getQueryManager().getProgramItem(
                                       in.getLinkId(), callback);
                            else if (linkType.equals("film"))
                                Main.getQueryManager().getFilm(in.getLinkId(),
                                        callback);
                            else if (linkType.equals("dvd"))
                                Main.getQueryManager().getDVD(in.getLinkId(),
                                        callback);
                        }
                        return true;
                    }
                };
                clickableTitle.setFont(clickableTitle.getFont().derive(
                        Font.UNDERLINED | Font.DOTTED_UNDERLINED));
                vfm.add(clickableTitle);
            }
        }
               
        makeHTMLField(in.getDescription());
    }

    /**
     * Creates a horizontal slice of screen based on a Schedule object. Lists
     * pertinent Schedule data first, and then lists all film names that are
     * owned by that Schedule's ProgramItem. Venues and Titles are clickable
     * (currently just report the String that was clicked)
     * @param Schedule object to display
     */
    protected void programItem(ProgramItem item)
    {
        addHeader(item.getTitle());
        makeField();
        makeHTMLField(item.getDescription());
        final Vector films = item.getFilms();
        if (films.size() > 0)
        {
            addText("Film Titles:", HParser.BOLD);
            makeField();
            ObjectListField filmNameField = new ObjectListField()
            {
                public boolean trackwheelClick(int status, int time)
                {
                    Ui.getUiEngine()
                            .pushScreen(
                                    new ControlList(films
                                            .elementAt(getSelectedIndex())));
                    return true;
                }
            };
            final String[] filmNames = new String[films.size()];
            for (int i = 0; i < films.size(); i++)
                filmNames[i] = ((Film) films.elementAt(i)).getTitle();
            filmNameField.set(filmNames);
            vfm.add(filmNameField);
        }
    }

    /**
     * Constructs a slice of screen based on a film object, currently just puts
     * everything into a RichTextField
     * @param Film object to display
     */
    protected void film(Film in)
    {
        makeImage(in.getImageURL());
        Vector schedules = in.getSchedules();
        addHeader(in.getTitle());
        addText(in.getTagline(), 0);
        makeField();
        if (schedules.size() > 0)
        {
            addText("Showtimes:", HParser.BOLD);
            makeField();
            for (int k = 0; k < schedules.size(); k++)
            {
                addShowingTime((Schedule) schedules.elementAt(k));
            }
            makeBlankLine();
        }
        makeHTMLField(in.getDescription());
        makeBlankLine();
        addEntry("Director", in.getDirector());
        addEntry("Producer", in.getProducer());
        addEntry("Editor", in.getEditor());
        addEntry("Writer", in.getWriter());
        addEntry("Cinematographer", in.getCinematographer());
        addEntry("Cast", in.getCast());
        addEntry("Country", in.getCountry());
        addEntry("Language", in.getLanguage());
        addEntry("Genre", in.getGenre());
        addEntry("Film Info", in.getFilmInfo());
        makeField();
    }

    /**
     * Displays a section consisting of a title and a vertical sequence of items
     * @param section the Section object to display
     */
    private void section(Section section)
    {
        Vector items = section.getItems();
        if (items == null || items.size() == 0)
            return;
        addHeader(section.getTitle());
        makeField();
        for (int i = 0; i < items.size(); i++)
        {
            process(items.elementAt(i));
        }
    }
    
    /**
     * Adds an item to the menu allowing an e-mail to be created with a link
     * to information regarding a particular ProgramItem (the ticketing page)
     * or DVD (the download/dvd purchasing page).
     * @param title the name of the ProgramItem or DVD, for the subject of the message
     * @param link the link to include in the body of the message
     */
    private void addEmailMenuItem(final String title, final String link)
    {
    	final String body = title + "\n\nLink: " + link + "\n\nSee more " +
    			"information about this item at the link above or at www.cinequest.org";
    	
    	addMenuItem(new MenuItem("Email this info", 4, 100)
    	{
    		public void run()
    		{
    			String email = Session.getDefaultInstance().getStore()
    					.getServiceConfiguration().getEmailAddress();
    			Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES,
    					new MessageArguments(MessageArguments.ARG_NEW, email, 
    							"Cinequest Mobile: " + title, body));
    		}
    	});
    	addMenuItem(MenuItem.separator(1));
    }
        
    // TODO: Put into more visible place
    private final String PRG_QUERY_STRING = "http://mobile.cinequest.org/event_view.php?eid=";
    private final String DVD_QUERY_STRING = "http://www.cinequestonline.org/theater/detail_view.php?m=";

    private static final int MAX_REGIONS = 100;
    private int[] off = new int[MAX_REGIONS + 1];
    private byte[] attrs = new byte[MAX_REGIONS];
    private DateUtils dateFormatter = new DateUtils();
    private int regions;
    private StringBuffer buffer;
    private HParser hparser = new HParser();
    private VerticalFieldManager vfm;
    private static Font[] fonts;
    private static int[] foregroundColors;
    private static int[] backgroundColors;
    static
    {
        Font defaultFont = Font.getDefault();
        int defaultSize = defaultFont.getHeight();
        int largeSize = defaultSize * 6 / 5;
        fonts = new Font[16];
        foregroundColors = new int[16];
        backgroundColors = new int[16];
        for (int i = 0; i < 8; i++)
        {
            fonts[i] = defaultFont.derive(((i & HParser.BOLD) != 0 ? Font.BOLD
                    : 0)
                    + ((i & HParser.ITALIC) != 0 ? Font.ITALIC : 0),
                    (i & HParser.LARGE) != 0 ? largeSize : defaultSize);
            fonts[8 + i] = fonts[i];
            foregroundColors[i] = 0;
            foregroundColors[8 + i] = 0xff0000;
            backgroundColors[i] = 0xffffff;
            backgroundColors[8 + i] = 0xffffff;
        }
    }
}