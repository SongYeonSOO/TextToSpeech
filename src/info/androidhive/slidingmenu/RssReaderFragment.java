package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.MainActivity;
import info.androidhive.slidingmenu.MyAdapter;
import info.androidhive.slidingmenu.R;
import info.androidhive.slidingmenu.RSSFeed;
import info.androidhive.slidingmenu.RSSItem;
import info.androidhive.slidingmenu.MainActivity.MetniSeseDonustur;
import info.androidhive.slidingmenu.databaseHandles.HaberYapisi;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
public class RssReaderFragment extends Fragment {
public RssReaderFragment(){
		
	}

	List<RSSItem> rssItemList = new ArrayList<RSSItem>();
	private ProgressDialog pDialog;
	
	
    RSSFeed rssFeed;
    ListView listemiz;
    private static final int DIALOG_CONTACT_DATA = 0;
    private static int selected_item_position = -1;
    public static int secili_haber_id;
    private String url_favori_send_data 					= "http://guneydogugucsistemleri.com/ts/tts_db/haber_ekle.php";
    private static final String TAG_SUCCESS 				= "success";// JSON Node names
    private static final String TAG_FAVORI					= "favori";
    private static final String TAG_FAVORI_LINK				= "link";
    private static final String TAG_FAVORI_BASLIK			= "baslik";
    private static final String TAG_FAVORI_KATEGORI			= "kategori";
    private static final String TAG_FAVORI_BILDIRIMZAMANI	= "bildirimZamani";
    JSONArray favoriJSONArray = null;
    ArrayList<HashMap<String, String>> favoriJSONArrayList;
    String haber_link="";
    String haber_baslik="";
    String haber_kategori="";
    String haber_bildirimZamani="";
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        final View rootView = inflater.inflate(R.layout.fragment_rss_reader, container, false);

        new loadRSSFeedItems().execute("http://www.milliyet.com.tr/D/rss/rss/Rss_23.xml");//http://www.milliyet.com.tr/D/rss/rss/RssY.xml");
		listemiz = (ListView) rootView.findViewById(R.id.liste);
		listemiz.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	String rss_makalesi_linki =rssItemList.get(position).toString();
	        	Toast.makeText(rootView.getContext(), "Makale Açýlýyor",Toast.LENGTH_LONG).show();
	        	listemiz.setVisibility(View.GONE);
	        	WebView wv = (WebView)rootView.findViewById(R.id.wvRss);
	        	TextView tv = (TextView)rootView.findViewById(R.id.tvRss);
	            if(rss_makalesi_linki.length()>0){
	            	GetWebContentClass cek = new GetWebContentClass(wv, tv, rss_makalesi_linki);
	            }
	            else{
	            	Toast.makeText(getActivity(), "Adres Linki Alýnamadý", Toast.LENGTH_SHORT).show();
	            }
	        }
	    });
	    listemiz.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
	            return onLongListItemClick(v,pos,id);
	        }
	    });
        return rootView;
    }
	
 	protected boolean onLongListItemClick(View v, int pos, long id) {
		// custom dialog
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Haber Zamanla");
			
		RSSItem secili_haber = rssItemList.get(pos);
		haber_link=secili_haber.getLink();
		haber_baslik= secili_haber.getTitle();
		// set the custom dialog components - text, image and button
		final TextView lblHaberBaslik = (TextView) dialog.findViewById(R.id.lblHaberBaslik);
		lblHaberBaslik.setText(haber_baslik);
		final EditText txtSaat = (EditText) dialog.findViewById(R.id.txtSaat);
		txtSaat.setText(zamanGetir());
		final Spinner cmbKategori = (Spinner) dialog.findViewById(R.id.cmbKategori);
		final Button dialogIptalButton = (Button) dialog.findViewById(R.id.btnIptal);
		// if button is clicked, close the custom dialog
			dialogIptalButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		final Button dialogTamamButton = (Button) dialog.findViewById(R.id.btnTamam);
		// tamam button
		dialogTamamButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d("OLUMLU DURUM:", "Haber Verileri:" +lblHaberBaslik.getText() + " " + txtSaat.getText()  + " "+ haber_link);
			//haberKaydet(haber_link, haber_baslik, cmbKategori.getSelectedItem().toString(), txtSaat.getText().toString());
			try{
				favoriJSONArrayList = new ArrayList<HashMap<String, String>>();     
				haber_baslik=MainActivity.metin_isleme(haber_baslik);
	            haber_kategori=MainActivity.metin_isleme(cmbKategori.getSelectedItem().toString());
	            haber_bildirimZamani=MainActivity.metin_isleme(txtSaat.getText().toString());
				new SendCurrentContentData().execute(haber_link,haber_baslik,haber_kategori,haber_bildirimZamani);
				dialog.dismiss();
			}catch(Exception e){
				Log.d("Hata Oluþtu", e.toString());
			} 
		}
		});
		dialog.show();
	    return true;
	}
	private String zamanGetir(){
		String gorunum;
		Calendar c = Calendar.getInstance(); 
		int hours = c.get(Calendar.HOUR);
		int minutes = c.get(Calendar.MINUTE);
		gorunum = String.valueOf(hours) +":"+ String.valueOf(minutes);
		return gorunum;
	} 
	
	class loadRSSFeedItems extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Makaleler Yükleniyor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting all recent articles and showing them in listview
         * */
        @Override
        protected String doInBackground(String... args) {
            // rss link url
            String rss_url = args[0];
             
            // list of rss items
           // rssItemList = ((MainActivity)getActivity()).rssParser.getRSSFeedItems(rss_url);
             rssItemList=MainActivity.rssParser.getRSSFeedItems(rss_url); 
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                	MyAdapter adaptorumuz=new MyAdapter(getActivity(), rssItemList);
            	    listemiz.setAdapter(adaptorumuz);
                }
            });
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String args) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
    }
	
	class SendCurrentContentData extends AsyncTask<String, String, String> {
		 	/**
		 	 * Listelenen haberlerden birine uzun týklandýðýnda açýlan menünün içeriðinin sunucuya gönderilmesi iþlemleri yapýlacak
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(getActivity());
	            pDialog.setMessage("Veri Sunucuya Yazýlýyor. Lütfen Bekleyiniz");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	        }
	        
	        protected String doInBackground(String... args) {
	        	
	        	//haberKaydet(haber_link, haber_baslik, cmbKategori.getSelectedItem().toString(), txtSaat.getText().toString());
	        	String haberLink=args[0]; String haberBaslik = args[1]; String haberKategori=args[2];String haberSaat = args[3];
	        	List<NameValuePair> JsonParametreler = new ArrayList<NameValuePair>();
	        	JsonParametreler.add(new BasicNameValuePair(TAG_FAVORI_LINK, haberLink));
	        	JsonParametreler.add(new BasicNameValuePair(TAG_FAVORI_BASLIK, haberBaslik));
	        	JsonParametreler.add(new BasicNameValuePair(TAG_FAVORI_KATEGORI, haberKategori));
	        	JsonParametreler.add(new BasicNameValuePair(TAG_FAVORI_BILDIRIMZAMANI, haberSaat));
	        	try {
	        		JSONObject json = MainActivity.jParser.makeHttpRequest(url_favori_send_data, "POST", JsonParametreler);//burada veriler gönderildi!!
	        		int success = json.getInt(TAG_SUCCESS);
	   			 
	                if (success == 1) {
	                    favoriJSONArray = json.getJSONArray(TAG_FAVORI);		 
	                    
	                    for (int i = 0; i < favoriJSONArray.length(); i++) {
	                        JSONObject c = favoriJSONArray.getJSONObject(i);
	 
	                        // Storing each json item in variable
	                        haberLink = c.getString(TAG_FAVORI_LINK);
	                        haberBaslik  = c.getString(TAG_FAVORI_BASLIK);
	                        
	                        HashMap<String, String> map = new HashMap<String, String>();
	                        map.put(TAG_FAVORI_LINK, haberLink);
	                        map.put(TAG_FAVORI_BASLIK, haberBaslik);
	                        favoriJSONArrayList.add(map);
	                        break;
	                    }
	                }
	        	} catch (Exception e) {
	        		e.printStackTrace();
				}
	        	return null;
	        }
	 
	        /**
	         * Arkaplan iþlemi bittikten sonra progress dialogu yoket
	         * **/
	        protected void onPostExecute(String args) {
	            pDialog.dismiss();

	            getActivity().runOnUiThread(new Runnable() {
	                public void run() {
	                    /**
	                     * Updating parsed JSON data into ListView
	                     * */      	
	                	for(Map<String, String> map : favoriJSONArrayList)
	                    {
	                       //Log.d("Gelen", "");
	                    }
	                }
	            });
	        }
	    }
}
