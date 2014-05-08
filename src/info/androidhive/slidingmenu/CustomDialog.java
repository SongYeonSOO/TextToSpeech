package info.androidhive.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class CustomDialog extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog);
		TextView txt1 = (TextView) findViewById(R.id.lblHaberBaslik);
		txt1.setText("Merhaba");
	}
}
