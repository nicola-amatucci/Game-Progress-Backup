package it.nicola_amatucci.android.game_progress_backup.scripts;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScriptsList extends ArrayList<ScriptDescriptor>
{
	private static final long serialVersionUID = 1L;
	
	public ScriptsList() { }
	
	public ScriptsList(JSONArray list) throws JSONException, IOException
	{
		this.loadFromJson(list);
	}

	public void loadFromJson(JSONArray scripts) throws JSONException, IOException
	{	
		if (scripts != null && scripts.length() > 0)
		{
			for(int i = 0; i < scripts.length(); i++)
			{
				JSONObject script = scripts.getJSONObject(i);
				
				ScriptDescriptor sd = new ScriptDescriptor();
				sd.setTitle(script.getString("title"));
				sd.setDescription(script.getString("description"));
				sd.setScript(script.getString("script"));
				sd.setRoot(script.getInt("root"));
				
				this.add(sd);
			}
		}
	}
	
}
