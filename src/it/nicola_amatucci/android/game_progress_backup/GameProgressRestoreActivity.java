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
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class GameProgressRestoreActivity extends GameProgressScriptsCommonActivity {
	
	String package_name;
	
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
				doRestore();
				goBack();
			}
        	
        });        
        
        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);

        Bundle b = this.getIntent().getExtras();
        package_name = b.getString("package_name");
        
        if (package_name != null && package_name.equals("") == false && checkIfInAssets("/help/backup_scripts/"+package_name+".html") )
        {
        	browser.loadUrl("file:///android_asset/help/backup_scripts/"+package_name+".html");
        }
        else
        {
        	browser.loadUrl("file:///android_asset/help/backup_scripts/default.html");
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
    
	public void doRestore()
	{
		String RELPATH = null;
		String relative_path = null;

		String url = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/game_progress_backup/";
		final String full_url = url + package_name + "/";

		if (new File(full_url).exists() == false) {
			new MessageBoxDialog(GameProgressRestoreActivity.this,
					GameProgressRestoreActivity.this
							.getString(R.string.no_backup_found))
					.showWithoutException();
			return;
		}

		ProgressDialog progressDialog = new ProgressDialog(
				GameProgressRestoreActivity.this);
		progressDialog.setTitle(R.string.loading_title);
		progressDialog.setMessage(getString(R.string.loading_please_wait));
		progressDialog.setCancelable(false);
		progressDialog.show();

		try {
			// carica lo script
			ArrayList<Command> commands = this.readBackupScriptFile(package_name);

			// controlla se si puo' procedere (tutti i file esistono)
			boolean ok = true;

			
			// controlla l'esistenza dei file nella cartella di sistema
			for (Command cmd : commands) {
				if (cmd.command.equals("!RESTORE_SKIP_SCRIPT_TEST")) {
					break;
				}

				if (cmd.command.equals("USE_RELPATH")) {
					RELPATH = cmd.args.get(0);
				}

				if (cmd.command.equals("END_RELPATH")) {
					RELPATH = null;
				}

				if (cmd.command.equals("CP")) {
					String file = cmd.args.get(0);

					// prende il path del file in relazione alla home
					// dell'applicazione
					int pathLen = 0;

					if (RELPATH == null) {
						pathLen = file.indexOf(package_name);
						relative_path = file.substring(pathLen);
					} else {
						pathLen = file.indexOf(RELPATH);
						relative_path = package_name + "/"
								+ file.substring(pathLen);
					}

					Log.v("", url + relative_path);
					Log.v("", file);

					// controlla l'esistenza dei file
					if (new File(url + relative_path).exists() == true
							&& new File(file).exists() == false) {
						ok = false;
					}
				}
			}

			if (ok == true) {
								
				// esegue lo script
				for (Command cmd : commands) {
					if (cmd.command.equals("CREATE_DIR_IF_NOT_EXISTS")) {
						String dir_to_create = cmd.args.get(0);
						
						if (new File(dir_to_create).exists() == false)
							new File(dir_to_create).mkdir();
					}
					
					if (cmd.command.equals("CREATE_PATH_IF_NOT_EXISTS")) {
						String path_to_create = cmd.args.get(0);
						
						if (new File(path_to_create).exists() == false)
							new File(path_to_create).mkdirs();
					}
					
					if (cmd.command.equals("USE_RELPATH")) {
						RELPATH = cmd.args.get(0);
					}

					if (cmd.command.equals("END_RELPATH")) {
						RELPATH = null;
					}

					if (cmd.command.equals("CP")) {
						String file = cmd.args.get(0);

						// prende il path del file in relazione alla home
						// dell'applicazione
						int pathLen = 0;

						if (RELPATH == null) {
							pathLen = file.indexOf(package_name);
							relative_path = file.substring(pathLen);
						} else {
							pathLen = file.indexOf(RELPATH);
							relative_path = package_name + "/"
									+ file.substring(pathLen);
						}

						this.fileCopy((url + relative_path), file);
					}
				}

				progressDialog.dismiss();
				new MessageBoxDialog(GameProgressRestoreActivity.this,
						GameProgressRestoreActivity.this
								.getString(R.string.restore_ok))
						.showWithoutException();
			} else {
				progressDialog.dismiss();
				new MessageBoxDialog(GameProgressRestoreActivity.this,
						GameProgressRestoreActivity.this
								.getString(R.string.before_restore_message))
						.showWithoutException();
			}
		} catch (IOException e) {
			progressDialog.dismiss();
			new MessageBoxDialog(GameProgressRestoreActivity.this,
					GameProgressRestoreActivity.this
							.getString(R.string.restore_fail))
					.showWithoutException();
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
            mapList = Arrays.asList(getAssets().list(""));
        }
        catch (IOException e) {}
    
        return mapList.contains(assetName) ? true : false;
	}
}
