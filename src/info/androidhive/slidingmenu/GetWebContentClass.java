package info.androidhive.slidingmenu;



import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


public class GetWebContentClass {

	@SuppressLint("SetJavaScriptEnabled")
	public GetWebContentClass(WebView wv,TextView tv, String link){
		WebView _wv=wv;
		TextView _tv=tv;
		
		_wv.getSettings().setJavaScriptEnabled(true);
		_wv.addJavascriptInterface(new JavaScriptInterface(tv), "INTERFACE");
		_wv.setWebViewClient(new WebViewClient(){
			
			@Override 
            public void onPageFinished(WebView view, String url) 
            { 
                view.loadUrl("javascript:window.INTERFACE.processContent(document.getElementsByTagName('body')[0].innerText);");
            }
		});
		wv.loadUrl(link);
		
		wv.setVisibility(View.GONE);
	}
	 
	class JavaScriptInterface { 
         private TextView contentView;

         public JavaScriptInterface(TextView aContentView)
         {
             contentView = aContentView;             
         }

         @SuppressWarnings("unused") 

         public void processContent(String aContent) 
         { 
             final String content = aContent;
             contentView.post(new Runnable() 
             {    
                 public void run() 
                 {     
                     contentView.setText(content);
                     MainActivity.okunabilirMetin=(String) content;
                     
                     contentView.setTextColor(Color.DKGRAY);
                 }     
             });
         } 
     } 
}
