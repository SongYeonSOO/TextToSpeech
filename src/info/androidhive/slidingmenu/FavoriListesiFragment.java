package info.androidhive.slidingmenu;




import java.util.HashMap;
import java.util.List;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class FavoriListesiFragment extends Fragment{
	
	public FavoriListesiFragment(){}
	
	View rootView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_find_people, container, false);
       
        return rootView;
    }
	
	
}

