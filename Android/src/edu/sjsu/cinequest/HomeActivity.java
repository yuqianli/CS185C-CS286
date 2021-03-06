package edu.sjsu.cinequest;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import edu.sjsu.cinequest.android.AndroidPlatform;
import edu.sjsu.cinequest.comm.Callback;
import edu.sjsu.cinequest.comm.ImageManager;
import edu.sjsu.cinequest.comm.Platform;
import edu.sjsu.cinequest.comm.QueryManager;
import edu.sjsu.cinequest.comm.cinequestitem.Filmlet;
import edu.sjsu.cinequest.comm.cinequestitem.MobileItem;
import edu.sjsu.cinequest.comm.cinequestitem.Section;
import edu.sjsu.cinequest.comm.cinequestitem.User;

// TODO: Add click for each item; show the section info

/**
 * The home screen of the app
 * 
 * @author Prabhjeet Ghuman
 * 
 */
public class HomeActivity extends Activity {
	private ListView list;
	ImageView title_image;

	private static QueryManager queryManager;
	private static ImageManager imageManager;
	private static User user;

	private static final String QR_CINEQUEST = "CINEQUEST";
	private static final String QR_FILM = "FILM";
	private static final String QR_TEL = "TEL";
	private static final String QR_SMS = "SMSTO";
	private static final String QR_VCARD = "VCARD";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_layout);

		// TODO: Remove this to turn on test mode
		// DateUtils.setMode(DateUtils.FESTIVAL_TEST_MODE);
		Context context = getApplicationContext();
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					getPackageName(), 0);
			String version = pi.versionName;
			setTitle("Cinequest" + (version == null ? "" : " " + version));
		} catch (NameNotFoundException ex) {
			// We tried...
		}

		Platform.setInstance(new AndroidPlatform(getApplicationContext()));
		queryManager = new QueryManager();
		imageManager = new ImageManager();
		user = new User();

		queryManager.getFestivalDates(new ProgressMonitorCallback(this) {
			public void invoke(Object result) {
				super.invoke(result);
				DateUtils.setFestivalDates((String[]) result);
			}
		});

		title_image = (ImageView) this
				.findViewById(R.id.homescreen_title_image);
		title_image.setImageDrawable(getResources().getDrawable(
				R.drawable.creative));

		list = (ListView) this.findViewById(R.id.home_newslist);
		list.setAdapter(new SeparatedListAdapter(this));
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MobileItem item = (MobileItem) list.getItemAtPosition(position);
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this, FilmDetail.class);
				intent.putExtra("target", item);
				startActivity(intent);
			}
		});

		Button festivalButton = (Button) findViewById(R.id.goto_festival_button);
		festivalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, MainTab.class);
				i.putExtra("open_tab", MainTab.FILMS_TAB);
				startActivity(i);
			}
		});

		Button dvdButton = (Button) findViewById(R.id.goto_dvd_button);
		dvdButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, MainTab.class);
				i.putExtra("open_tab", MainTab.DVDS_TAB);
				startActivityForResult(i, 0);
			}
		});

		Button qrcodeButton = (Button) findViewById(R.id.qrcode);
		qrcodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startQRCodeScanner();
			}
		});
	}

	/**
	 * Called when activity resumes
	 */
	@Override
	public void onResume() {
		super.onResume();
		queryManager.getSpecialScreen("ihome", new Callback() {
			@Override
			public void invoke(Object result) {
				populateNewsEventsList((Vector<Section>) result);
				// TODO: Why doesn't this work???
				queryManager.prefetchFestival();
			}

			@Override
			public void starting() {
			}

			@Override
			public void failure(Throwable t) {
				Platform.getInstance().log(t);
			}
		});
	}

	protected void onStop() {
		user.persistSchedule();
		imageManager.close();
		Platform.getInstance().close();
		super.onStop();
	}

	/**
	 * Display the header image and schedule to the user with section-title
	 * being separator-header.
	 */
	private void populateNewsEventsList(Vector<Section> newsSections) {
		// if there is no news to display, return
		if (newsSections.size() == 0) {
			// Clear the items of previous list being displayed (if any)
			list.setAdapter(new SeparatedListAdapter(this));
			return;
		}

		// create our list and custom adapter
		SeparatedListAdapter separatedListAdapter = new SeparatedListAdapter(
				this);

		for (int i = 0; i < newsSections.size(); i++) {
			Section s = newsSections.get(i);
			String sectionTitle = s.getTitle();
			Vector items = s.getItems();

			ArrayList<MobileItem> newsEvents = new ArrayList<MobileItem>();

			if (i == 0) {
				if (items.size() > 0) {
					String imageURL = ((MobileItem) items.get(0)).getImageURL();
					getImageManager().getImage(imageURL, new Callback() {
						@Override
						public void invoke(Object result) {
							title_image.setImageBitmap((Bitmap) result);
						}

						@Override
						public void starting() {
						}

						@Override
						public void failure(Throwable t) {
							Platform.getInstance().log(t);
						}
					}, null, false);
				}
			} else {
				for (int j = 0; j < items.size(); j++) {
					newsEvents.add((MobileItem) items.get(j));
				}
				separatedListAdapter.addSection(sectionTitle,
						new MobileItemAdapter(this, R.layout.home_event_row,
								newsEvents));
			}
		}

		list.setAdapter(separatedListAdapter);
	}

	/**
	 * Get the QueryManager
	 * 
	 * @return queryManager
	 */
	public static QueryManager getQueryManager() {
		return queryManager;
	}

	public static ImageManager getImageManager() {
		return imageManager;
	}

	/**
	 * Get the User
	 * 
	 * @return user
	 */
	public static User getUser() {
		return user;
	}

	/**
	 * Create a menu to be displayed when user hits Menu key on device
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.homeactivity_menu, menu);

		return true;
	}

	/** Menu Item Click Listener */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_option_about:
			DialogPrompt.showAppAboutDialog(this);
			return true;
		case R.id.menu_option_qrcode:
			startQRCodeScanner();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void startQRCodeScanner() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES, this,
				com.google.zxing.client.android.CaptureActivity.class);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, data);
		if (scanResult != null) {
			HandleQRCodeEvents(scanResult);
		}

	}

	/*
	 * Xiaohan: Handle the events from QRCodeScanner
	 */
	private void HandleQRCodeEvents(IntentResult scanResult) {

		System.out.println("Format:" + scanResult.getFormatName()
				+ ", Content:" + scanResult.getContents());

		try {
			String content = scanResult.getContents();
			String parts[] = content.split(":");
			String schema = parts[0];
			if (schema.equals(QR_CINEQUEST)) {
				String fun = parts[1];
				if (fun.equals(QR_FILM)) {
					String id = parts[2];
					Filmlet film = new Filmlet();
					film.setId(Integer.parseInt(id));
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this, FilmDetail.class);
					intent.putExtra("target", film);
					startActivity(intent);
				}

			} else if (schema.equals(QR_SMS)) {
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.setType("vnd.android-dir/mms-sms");
				sendIntent.putExtra("address", parts[1]);
				sendIntent.putExtra("sms_body", parts[2]);
				startActivity(sendIntent);
			} else if (schema.equals(QR_TEL)) {
				Intent telIntent = new Intent(Intent.ACTION_DIAL);
				telIntent.setData(Uri.parse("tel:" + parts[1]));
				startActivity(telIntent);
			} else {
				Uri uri = Uri.parse(content);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}
		} catch (Exception e) {
			Toast.makeText(this,
					"Can't process this content" + scanResult.getContents(),
					Toast.LENGTH_LONG).show();
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Custom List-Adapter to show the MobileItems in news event list
	 */
	private class MobileItemAdapter extends ArrayAdapter<MobileItem> {

		private ArrayList<MobileItem> itemsList;
		private int view_resource_id; // id of the list's row's layout xml

		public MobileItemAdapter(Context context, int textViewResourceId,
				ArrayList<MobileItem> list) {
			super(context, textViewResourceId, list);
			this.itemsList = list;
			this.view_resource_id = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(view_resource_id, null);
			}
			MobileItem result = itemsList.get(position);

			if (result != null) {
				// get text from list, and fill it into the row
				TextView title = (TextView) v
						.findViewById(R.id.news_event_title);

				// Set title text
				if (title != null)
					title.setText(result.getTitle());
			}

			return v;
		}

	}
}
