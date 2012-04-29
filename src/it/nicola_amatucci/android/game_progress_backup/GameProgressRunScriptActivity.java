package it.nicola_amatucci.android.game_progress_backup;

import it.nicola_amatucci.android.game_progress_backup.scripts.Command;
import it.nicola_amatucci.android.utils.MessageBoxDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class GameProgressRunScriptActivity extends GameProgressScriptsCommonActivity {
	
	String script_name;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.run_script_activity);
        
        WebView browser = (WebView)findViewById(R.id.yourwebview1);
        Button cancelScriptBtn = (Button)findViewById(R.id.cancelScriptButton);
        Button runScriptBtn = (Button)findViewById(R.id.runScriptButton);
        
        cancelScriptBtn.setOnClickListener( new OnClickListener() {

			public void onClick(View v) {
				goBack();
			}
        	
        });
        
        runScriptBtn.setOnClickListener( new OnClickListener() {

			public void onClick(View v) {
				doWork();
				goBack();
			}
        	
        });        
        
        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);

        Bundle b = this.getIntent().getExtras();
        script_name = b.getString("script_name");
        
        if (script_name != null && script_name.equals("") == false && checkIfInAssets(script_name+".html") )
        {
        	browser.loadUrl("file:///android_asset/help/scripts/"+script_name+".html");
        }
        else
        {
        	browser.loadUrl("file:///android_asset/help/scripts/default.html");
        }
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	goBack();
	    }

	    return super.onKeyDown(keyCode, event);
	}
    
    public void goBack()
    {
    	this.finish();
    }
    
	public void doWork()
	{
		ProgressDialog progressDialog = new ProgressDialog(GameProgressRunScriptActivity.this);
		progressDialog.setTitle(R.string.script_run_title);
		progressDialog.setMessage(getString(R.string.script_run_please_wait));
		progressDialog.setCancelable(false);
		progressDialog.show();
    	
    	try {
			ArrayList<Command> commands = this.readScriptFile( script_name );
			
			for (Command cmd : commands) {
				if (cmd.command.equals("COPY")) {
					String file_src = cmd.args.get(0);
					String file_dst = cmd.args.get(1);
					
					this.fileCopy(file_src, file_dst);
				}
				
				if (cmd.command.equals("EXISTS_FILE"))
				{
					String file = cmd.args.get(0);
					
					if (new File(file).exists() == false)
					{
						progressDialog.dismiss();
						new MessageBoxDialog( GameProgressRunScriptActivity.this, GameProgressRunScriptActivity.this.getString(R.string.script_run_fail)).showWithoutException();
						return;
					}
				}
			}
			
			progressDialog.dismiss();
			new MessageBoxDialog( GameProgressRunScriptActivity.this, GameProgressRunScriptActivity.this.getString(R.string.script_run_ok)).showWithoutException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if an asset exists.
	 *
	 * @param assetName
	 * @return boolean - true if there is an asset with that name.
	 */
	public boolean checkIfInAssets(String assetName)
	{
		List<String> mapList = null;
		
        try {
            mapList = Arrays.asList(getAssets().list("help/scripts"));
        }
        catch (IOException e) {}
    
        for (String s : mapList)
        	Log.v("", s);
        
        return mapList.contains(assetName) ? true : false;
	}
}
