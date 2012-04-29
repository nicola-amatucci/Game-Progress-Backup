package it.nicola_amatucci.android.game_progress_backup;

import it.nicola_amatucci.android.game_progress_backup.scripts.ScriptDescriptor;
import it.nicola_amatucci.android.game_progress_backup.scripts.ScriptsList;
import it.nicola_amatucci.android.game_progress_backup.scripts.listview.ScriptListAdapter;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class GameProgressScriptsActivity extends GameProgressScriptsCommonActivity {
	
	ListView scriptsListView;
	TextView textView1;
	
	public ScriptsList scriptsList;
	ScriptListAdapter scriptListAdapter;
	
	
	JSONObject jsonDatabase;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.scripts_activity);
        textView1 = (TextView) findViewById(R.id.scriptTextView1);
        scriptsListView = (ListView) findViewById(R.id.scriptsLV);
        
        scriptsList = new ScriptsList();
        
        try
        {
        	//legge il database json
			jsonDatabase = readJSONFile(getResources().getAssets().open("scripts.json"));
			
			scriptsListView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
					final ScriptDescriptor sd = (ScriptDescriptor) scriptsListView.getItemAtPosition(position);
					
					AlertDialog dialog = new AlertDialog.Builder(GameProgressScriptsActivity.this)
					.setTitle(sd.getTitle())
					.setMessage(sd.getDescription() + "\n\n" + getString(R.string.run_script_question))
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							runScript(sd);
						}
						
					})
					.setNegativeButton(android.R.string.no, null)
					.show();
		        }
	        	
	        });
			
			new LoadListTask().execute();
        }
        catch (NotFoundException e)
        {
        	textView1.setText("Database file non found!");
			e.printStackTrace();
		}
        catch (JSONException e)
        {
        	textView1.setText("Database file error!");
			e.printStackTrace();
		}
        catch (IOException e)
        {
        	textView1.setText("Database file error!");
			e.printStackTrace();
		}
    }
    
    private class LoadListTask extends AsyncTask<String, Void, Object>
    {
    	ProgressDialog progressDialog;
    	
    	@Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog( GameProgressScriptsActivity.this );
            progressDialog.setTitle(R.string.loading_title);
            progressDialog.setMessage( getString(R.string.loading_please_wait) );
            progressDialog.setCancelable(false);
            progressDialog.show();
    	}
    	
		@Override
		protected Object doInBackground(String... params) {
			
			try {
				scriptsList = new ScriptsList( jsonDatabase.getJSONArray("scripts") );
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
    	
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			
			if (scriptsList.size() > 0)
			{
				//aggiorna la view qui (per la concorrenza)
				scriptListAdapter = new ScriptListAdapter(getApplicationContext(), R.layout.game_listitem, scriptsList);				
				scriptsListView.setAdapter(scriptListAdapter);
			}
			else
			{
				textView1.setText(R.string.no_script_found);
			}
			
			//chiude dialog attesa
			progressDialog.dismiss();
		}
    }
    
    public void runScript(ScriptDescriptor sd)
    {
		Bundle b = new Bundle();
		b.putString("script_name", sd.getScript());
		Intent i = new Intent(GameProgressScriptsActivity.this.getApplicationContext(), GameProgressRunScriptActivity.class);
		i.putExtras(b);
		startActivity(i);
    }
}
