package it.nicola_amatucci.android.game_progress_backup.games;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GamesList extends ArrayList<GameDescriptor>
{
	private static final long serialVersionUID = 1L;
	
	public GamesList() { }
	
	public GamesList(JSONArray list) throws JSONException, IOException
	{
		this.loadFromJson(list);
	}
	
	public void loadFromJson(JSONArray games) throws JSONException, IOException
	{		
		if (games != null && games.length() > 0)
		{
			for(int i = 0; i < games.length(); i++)
			{
				JSONObject game = games.getJSONObject(i);
				
				GameDescriptor gd = new GameDescriptor();
				gd.setName(game.getString("name"));
				gd.setVersion(game.getString("version"));
				gd.setPackageName(game.getString("package_name"));
				gd.setRoot(game.getInt("root"));
				gd.setRootWrite(game.getInt("root_write"));
				gd.setVerified(game.getInt("verified"));
				
				this.add(gd);
			}
		}
		
	}
}
