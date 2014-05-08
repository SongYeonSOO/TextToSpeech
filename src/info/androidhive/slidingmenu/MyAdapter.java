package info.androidhive.slidingmenu;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
    
	private LayoutInflater myInflater;
    private List<RSSItem>   myRssItemList;

    
    public MyAdapter(Activity a,List<RSSItem> _rssitem) {
        myInflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myRssItemList=_rssitem;
    }

    public int getCount() {
        return myRssItemList.size();
    }

    public Object getItem(int position) {
        return myRssItemList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View satirView=convertView;
        if(convertView==null)
            satirView = myInflater.inflate(R.layout.list_row, null);

        TextView title = (TextView)satirView.findViewById(R.id.title); 
        TextView link = (TextView)satirView.findViewById(R.id.link); 
        TextView desc = (TextView)satirView.findViewById(R.id.desc); 
       
        RSSItem current_rssitem =myRssItemList.get(position);
        title.setText(current_rssitem.getTitle());
        link.setText(current_rssitem.getLink());
        desc.setText(current_rssitem.getDesc());
        return satirView;
    }
}