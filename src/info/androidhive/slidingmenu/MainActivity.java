package info.androidhive.slidingmenu;
import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.model.NavDrawerItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
										/*
										 * Fragment'ten --> Activity'e Metin Okutma Kodlarý 
										 */
	private ProgressDialog pDialog;
	public static String okunabilirMetin;
	public static RSSParser rssParser=new RSSParser(); 
    									// JSON Parse Nesnesi Yaratýlýyor
    public static JSONParser jParser = new JSONParser();// bunun tekrar oluþmasýna gerek yok
    ArrayList<HashMap<String, String>> makaleJSONArrayList;
    									// url to get all makaleJSONArray list
    private String url_send_data 							= "http://guneydogugucsistemleri.com/ts/index.php";    							
    private static final String TAG_SUCCESS 				= "success";// JSON Node names
    private static final String TAG_MAKALE 					= "gidenData";
    private static final String TAG_DATALINK 				= "link";
    private static final String TAG_NAME 					= "name";
    
    JSONArray makaleJSONArray = null;
    
    private static MediaPlayer player;    
    private String mp3Adi="";			// .mp3 adýný taþýyan string deðiþken
    private boolean tekrarCal=false; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Log.d("tur", "mainactivity normal fonksiyon");
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		
		/*
		 * Fragment'ten --> Activity'e Seslendirme kodlarý
		 * 
		 */
		tekrarCal=false;
        
	}	
	
	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.play:
			onClick();
			return true;
		case R.id.stop:
			onStop();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Displaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new FavoriListesiFragment();
			break;
		case 2:
			fragment = new RssReaderFragment();
			break;
		case 3:
			fragment = new CommunityFragment();
			break;
		case 4:
			fragment = new PagesFragment();
			break;
		case 5:
			fragment = new WhatsHotFragment();
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
		tekrarCal=false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/*
	 * Fragment'ten --> Activity'e Seslendirme Kodlarý 
	 * */
	public void onClick() {
		 try{

	        	// Listeden hashmap'e aktarým yapýyoruz...
	            makaleJSONArrayList = new ArrayList<HashMap<String, String>>();     
	            // Loading makaleJSONArray in Background Thread
	            new MetniSeseDonustur().execute();	
		 }catch(Exception e){Log.d("Hata Oluþtu", e.toString());}
	    	   
	    }
	
	public static String metin_isleme(String metin){
		
		metin = metin.replaceAll("\\<.*?\\>", "");
    	String[] eskiler={"\r\n","\n","\r","\t","&nbsp;","'","’","\"","\\","!","  ","   ","”","“","‘"};
    	for (int i = 0; i < eskiler.length; i++) {
    		metin= metin.replace(eskiler[i], " ");
		}
    	
    	/*tr karakterler için*/
    	String[] tr1={"ý","ð","ü","þ","ö","ç","Ý","Ð","Ü","Þ","Ö","Ç"};
    	String[] tr2={"&#305;","&#287;","&uuml;", "&#351;", "&ouml;", "&ccedil;","&#304;", "&#286;", "&Uuml;", "&#350;","&Ouml;", "&Ccedil;"};
    	for (int i = 0; i < 12; i++) {
    		metin=metin.replace(tr1[i], tr2[i]);
    	}
    	/*tr karakterler için*/
    	
    	StringTokenizer tokens = new StringTokenizer(metin , " "); 
    	metin="";
    	String ufakparca="";
    	while (tokens.hasMoreElements()) {  
    		ufakparca=tokens.nextElement().toString();
    		if(ufakparca !="")
    		{
    			metin+=ufakparca.trim() + " ";
    		}
    	}  
    	
    	metin=metin.replace(" ", "%20");
    	return metin;
	}
	
	 class MetniSeseDonustur extends AsyncTask<String, String, String> {
		 
	        /**
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(MainActivity.this);
	            pDialog.setMessage("Metin Sese Dönüþtürülüyor\nLütfen Bekleyiniz.");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	        }
	 
	        
	        protected String doInBackground(String... args) {
	        	
	        	String metin = okunabilirMetin;
	            if(metin !=null){
	        		if(tekrarCal==false){
	        			List<NameValuePair> params = new ArrayList<NameValuePair>();
			            metin = metin_isleme(metin);
			            
			            params.add(new BasicNameValuePair("name", metin));
			            // URL ile JSON Nesnesi Göndereceðiz *******************************************
			            JSONObject json = jParser.makeHttpRequest(url_send_data, "POST", params);
			 
			            // Oluþan JSON Ýsteðini LOGCAT'da görüntüle
			            //Log.d("Oluþturma Ýsteði: ", json.toString());
			 
			            try {
			                // Checking for SUCCESS TAG
			                int success = json.getInt(TAG_SUCCESS);
			 
			                if (success == 1) {
			                    makaleJSONArray = json.getJSONArray(TAG_MAKALE);		 
			                    
			                    for (int i = 0; i < makaleJSONArray.length(); i++) {
			                        JSONObject c = makaleJSONArray.getJSONObject(i);
			 
			                        // Storing each json item in variable
			                        String id = c.getString(TAG_DATALINK);
			                        String name = c.getString(TAG_NAME);
			                        
			                        HashMap<String, String> map = new HashMap<String, String>();
			                        map.put(TAG_DATALINK, id);
			                        map.put(TAG_NAME, name);
			                        makaleJSONArrayList.add(map);
			                        break;
			                    }
			                }
			                else {
			                    // giden metin yoksa hiçbirþey yapma
			                }
			            } catch (JSONException e) {
			                e.printStackTrace();
			            }
			            tekrarCal=true;
	        		}
	        		else if(tekrarCal==true){//tekrar cal
	        			initializeMediaPlayer();
	        			startPlaying();
	        			tekrarCal=false;
	        		}
	        	}
	            return null;
	        }
	 
	        /**
	         * Arkaplan iþlemi bittikten sonra progress dialogu yoket
	         * **/
	        protected void onPostExecute(String file_url) {
	            pDialog.dismiss();

	            runOnUiThread(new Runnable() {
	                public void run() {
	                    /**
	                     * Updating parsed JSON data into ListView
	                     * */      	
	                	for(Map<String, String> map : makaleJSONArrayList)
	                    {
	                        mp3Adi = map.get(TAG_NAME);
	                    }
	                	initializeMediaPlayer();
	                 	startPlaying();
	                }
	            });
	        }
	    }
	    
	  private void startPlaying() {
	    	if(mp3Adi!=""){
	        player.prepareAsync();

	        player.setOnPreparedListener(new OnPreparedListener() {

	            public void onPrepared(MediaPlayer mp) {
	                player.start();
	            }
	        });
	    	}
	    }
	    
	    private void initializeMediaPlayer() {
	        player = new MediaPlayer();
	        try {
	            player.setDataSource("http://www.guneydogugucsistemleri.com/ts/mp3/"+mp3Adi);
	            
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        } catch (IllegalStateException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

	            public void onBufferingUpdate(MediaPlayer mp, int percent) {
	                //playSeekBar.setSecondaryProgress(percent);
	                Log.i("Buffering", "" + percent);
	            }
	        });
	    }

	    @Override
		public void onStop() {
	        super.onStop();
	        if (player.isPlaying()==true) {
	            player.stop();
	        }
	        player.reset();
	    }
}
