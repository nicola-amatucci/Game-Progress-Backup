package it.nicola_amatucci.android.game_progress_backup.games.listview;

import it.nicola_amatucci.android.game_progress_backup.R;
import it.nicola_amatucci.android.game_progress_backup.games.GameDescriptor;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameListAdapter extends ArrayAdapter<GameDescriptor>
{
	private static final String TAG = "GameListAdapter";

	public GameListAdapter(Context context, int textViewResourceId, List<GameDescriptor> objects)
	{
		super(context, textViewResourceId, objects);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		ImageView gameIconImg;
		TextView gameNameTxt;
		TextView gameOtherTxt;
		
		//elaboro layout xml
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.game_listitem, parent, false);			
		}
		
		GameDescriptor item = getItem(position);
		gameIconImg = (ImageView) row.findViewById(R.id.game_icon);
		gameNameTxt = (TextView) row.findViewById(R.id.game_name);
		gameOtherTxt = (TextView) row.findViewById(R.id.game_other);
		
		gameNameTxt.setText(item.getName());
		
		if (item.getApk() != null && item.getApk().icon != null)
		{
			gameIconImg.setImageDrawable(item.getApk().icon);
		}
		else
		{
			Drawable d = getContext().getResources().getDrawable(R.drawable.ic_launcher);
			gameIconImg.setImageDrawable(d);
		}
	
		/*
		if (item.getRoot() == 0)
		{
			gameOtherTxt.setText("");
			//gameOtherTxt.setText("$ user");
			//gameOtherTxt.setTextColor(Color.GREEN);
		}
		else
		{
			gameOtherTxt.setText("#root");
			//gameOtherTxt.setTextColor(Color.RED);
		}
		*/
		if (item.saved)
		{
			gameOtherTxt.setText("SAVED");
			gameOtherTxt.setTextColor(Color.GREEN);
		}
		
		return row;
	}
}
