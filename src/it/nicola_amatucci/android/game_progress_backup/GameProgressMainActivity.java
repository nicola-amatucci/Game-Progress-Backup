package it.nicola_amatucci.android.game_progress_backup;

import it.nicola_amatucci.android.utils.Configuration;
import it.nicola_amatucci.android.utils.MessageBoxDialog;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class GameProgressMainActivity extends TabActivity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_container);
 
        Configuration configurazione = Configuration.getInstance(this.getBaseContext());
        if (configurazione.get("first_run") == null)
        {
        	new MessageBoxDialog( GameProgressMainActivity.this, GameProgressMainActivity.this.getString(R.string.disclaimer)).showWithoutException();
        	configurazione.put("first_run", "done");
        }
        
        
        TabHost tabHost = getTabHost();
        
        TabSpec gamesTabSpec = tabHost.newTabSpec("Games");
        gamesTabSpec.setIndicator("Compatible Games");
        Intent gamesIntent = new Intent(this, GameProgressBackupActivity.class);
        gamesTabSpec.setContent(gamesIntent);
     
        TabSpec scriptsTabSpec = tabHost.newTabSpec("Scripts");
        scriptsTabSpec.setIndicator("Scripts");
        Intent scriptsIntent = new Intent(this, GameProgressScriptsActivity.class);
        scriptsTabSpec.setContent(scriptsIntent);

        TabSpec helpTabSpec = tabHost.newTabSpec("Help");
        helpTabSpec.setIndicator("Help");
        Intent helpIntent = new Intent(this, GameProgressHelpActivity.class);
        helpTabSpec.setContent(helpIntent);
        
        tabHost.addTab(gamesTabSpec);
        tabHost.addTab(scriptsTabSpec);
        tabHost.addTab(helpTabSpec);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
   	}
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item){

    	switch (item.getItemId())
    	{
    		case R.id.menu_exit:
    			showExitDialog();
    	}
    	
    	return true;
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	showExitDialog();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}    
    
	public void showExitDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				GameProgressMainActivity.this);
		alert.setTitle(R.string.exit_confirm_title);
		alert.setMessage(R.string.exit_confirm_message);
		alert.setCancelable(false);
		alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				GameProgressMainActivity.this.finish();
			}
		});
		alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alert.create();
		alert.show();
	}
}
