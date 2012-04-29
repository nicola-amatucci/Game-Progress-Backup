package it.nicola_amatucci.android.game_progress_backup.scripts.listview;

import it.nicola_amatucci.android.game_progress_backup.R;
import it.nicola_amatucci.android.game_progress_backup.scripts.ScriptDescriptor;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScriptListAdapter extends ArrayAdapter<ScriptDescriptor>
{
	private static final String TAG = "ScriptListAdapter";

	public ScriptListAdapter(Context context, int textViewResourceId, List<ScriptDescriptor> objects)
	{
		super(context, textViewResourceId, objects);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		TextView scriptNameTxt;
		
		//elaboro layout xml
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.simple_listitem, parent, false);			
		}
		
		ScriptDescriptor item = getItem(position);
		scriptNameTxt = (TextView) row.findViewById(R.id.simple_list_item_txt);
				
		scriptNameTxt.setText(item.getTitle());
				
		
		return row;
	}
}
