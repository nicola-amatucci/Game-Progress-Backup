package it.nicola_amatucci.android.game_progress_backup;

import it.nicola_amatucci.android.game_progress_backup.scripts.Command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.KeyEvent;

public class GameProgressScriptsCommonActivity extends Activity
{
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	showExitDialog();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}    
	
    public void showExitDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.exit_confirm_title);
		alert.setMessage(R.string.exit_confirm_message);
		alert.setCancelable(false);
		alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				finish();
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
	
    final String SPACE_TOKEN = "%20%";
    final String SD_CARD_TOKEN = "%SD_CARD%";
	final String INTERNAL_DATA_PATH_TOKEN = "%INTERNAL_DATA_PATH%";
	final String EXTERNAL_DATA_PATH_TOKEN = "%EXTERNAL_DATA_PATH%";
	final String SD_CARD = "/sdcard";
	final String INTERNAL_DATA_PATH = "/data/data";
	final String EXTERNAL_DATA_PATH = "/sdcard/Android/data";	
	
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();

		if ( Environment.MEDIA_MOUNTED.equals(state) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void fileCopy(String src, String dest)
	{
		//copia il file se esiste
 		if (new File(src).exists())
 		{
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new FileInputStream(src);
				out = new FileOutputStream(dest);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (Exception e) {
				//Log.e("tag", e.getMessage());
				e.printStackTrace();
			}
 		}
	}
	
	public JSONObject readJSONFile(InputStream inputStream) throws JSONException, IOException
	{
		//legge il file
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder total = new StringBuilder();
		
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		
		try { r.close(); } catch(Exception ex){};
		
		//converte in oggetto json
		return new JSONObject(total.toString());
	}
	
	public ArrayList<Command> readBackupScriptFile(String script_name) throws IOException {
		return _readScriptFile("backup_scripts/"+script_name);
	}
	
	public ArrayList<Command> readScriptFile(String script_name) throws IOException {
		return _readScriptFile("scripts/"+script_name);
	}
	
	private ArrayList<Command> _readScriptFile(String script_name_path) throws IOException
	{
		ArrayList<Command> ret = new ArrayList<Command>();
		
		
		BufferedReader r = new BufferedReader( new InputStreamReader( getResources().getAssets().open(script_name_path) ) );		
		
		String line;
		while ((line = r.readLine()) != null)
		{
			if (line.equals("") || line.startsWith("#"))
				continue;
			
		    String[] tokens = line.split(" ");
		    
		    Command cmd = new Command();
		    cmd.command = tokens[0];
		    
		    for (int i = 1; i < tokens.length; i++)
		    {
		    	//rimpiazza le variabili
		    	tokens[i] = tokens[i].replace(SD_CARD_TOKEN, SD_CARD);
		    	tokens[i] = tokens[i].replace(INTERNAL_DATA_PATH_TOKEN, INTERNAL_DATA_PATH);
		    	tokens[i] = tokens[i].replace(EXTERNAL_DATA_PATH_TOKEN, EXTERNAL_DATA_PATH);
		    	tokens[i] = tokens[i].replace(SPACE_TOKEN, " ");
		    	//aggiunge il comando
		    	cmd.args.add(tokens[i]);
		    }
		    ret.add(cmd);
		}
		
		try { r.close(); } catch(Exception ex){};
		return ret;		
	}
	
	public void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            deleteRecursive(child);

	    //Log.v("GameProgressBackup", fileOrDirectory.getName());
	    
	    fileOrDirectory.delete();
	}
	
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
}
