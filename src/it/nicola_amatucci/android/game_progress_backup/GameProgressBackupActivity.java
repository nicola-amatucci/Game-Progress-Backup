package it.nicola_amatucci.android.game_progress_backup;

import it.nicola_amatucci.android.game_progress_backup.apks.ApkDescriptor;
import it.nicola_amatucci.android.game_progress_backup.apks.ApksList;
import it.nicola_amatucci.android.game_progress_backup.games.GameDescriptor;
import it.nicola_amatucci.android.game_progress_backup.games.GamesList;
import it.nicola_amatucci.android.game_progress_backup.games.listview.GameListAdapter;
import it.nicola_amatucci.android.game_progress_backup.scripts.Command;
import it.nicola_amatucci.android.utils.MessageBoxDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class GameProgressBackupActivity extends GameProgressScriptsCommonActivity
{
	public GamesList gamesList;
	public ApksList apksList;

	public GamesList installedGamesList;
	ListView gameListView;
	GameListAdapter gameListAdapter;
	TextView textView1;
	
	JSONObject jsonDatabase;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        textView1 = (TextView) findViewById(R.id.textView1);
        gameListView = (ListView) findViewById(R.id.gamesLV);
        
        try
        {
        	//legge il database json
			jsonDatabase = readJSONFile(getResources().getAssets().open("games.json"));
			
	        gameListView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parentView, View childView, int position, long id)
				{
					final GameDescriptor gd = (GameDescriptor) gameListView.getItemAtPosition(position);
					
					//mostra la dialog per le azioni disponibili				
					if (GameProgressBackupActivity.this.isExternalStorageWritable())
					{
						AlertDialog.Builder builder = new AlertDialog.Builder( GameProgressBackupActivity.this );
						builder.setTitle(GameProgressBackupActivity.this.getString(R.string.app_menu_title));
						
						CharSequence[] items = null;
						if (GameProgressBackupActivity.this.backupExists(gd.getPackageName()))
						{
							items = new CharSequence[3];
							items[0] = GameProgressBackupActivity.this.getString(R.string.backup_to_external);
							items[1] = GameProgressBackupActivity.this.getString(R.string.restore_from_external);
							items[2] = GameProgressBackupActivity.this.getString(R.string.delete_from_external);
						}
						else
						{
							items = new CharSequence[1];
							items[0] = GameProgressBackupActivity.this.getString(R.string.backup_to_external);
						}
						
						builder.setItems(items, new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int item) {
						        switch(item)
						        {
						        	//backup
						        	case 0: {
						        		GameProgressBackupActivity.this.backupGameToSD(gd);
						        		break;
						        	}
						        	
						        	//restore
						        	case 1: {
						        		GameProgressBackupActivity.this.restoreGameFromSD(gd);
						        		break;
						        	}
						        	
						        	case 2: {
						        		GameProgressBackupActivity.this.deleteGameFromSD(gd);
						        		break;
						        	}
						        }
						    }
						});
						
						builder.create().show();
					}
					else
					{
						new MessageBoxDialog( GameProgressBackupActivity.this, GameProgressBackupActivity.this.getString(R.string.no_external_storage_found)).showWithoutException();
					}
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
            progressDialog = new ProgressDialog( GameProgressBackupActivity.this );
            progressDialog.setTitle(R.string.loading_title);
            progressDialog.setMessage( getString(R.string.loading_please_wait) );
            progressDialog.setCancelable(false);
            progressDialog.show();
            
            installedGamesList = new GamesList();
    	}
    	
		@Override
		protected Object doInBackground(String... params)
		{
			try {
				//carica la lista locale				
				gamesList = new GamesList( jsonDatabase.getJSONArray("games") );				
				//scriptsList = new ScriptsList( jsonDatabase.getJSONArray("scripts") );
				//carica la lista delle app installate (non di sistema)
				apksList = this.getInstalledApps();

				//riempie la lista delle applicazioni installate				
				if (apksList.size() > 0)
					for (ApkDescriptor apk : apksList)
						for (GameDescriptor game : gamesList)
						{							
							if (game.getPackageName().equals(apk.pname))
							{
								game.setApk( apk );								
								installedGamesList.add(game);
							}
						}
			}
			catch (JSONException ex)
			{
				ex.printStackTrace();
			}
			catch (IOException e)
			{				
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			
			if (installedGamesList.size() > 0)
			{
				for(GameDescriptor gd : installedGamesList)
					gd.saved = backupExists(gd.getPackageName());
				
				//aggiorna la view qui (per la concorrenza)
				gameListAdapter = new GameListAdapter(getApplicationContext(), R.layout.game_listitem, installedGamesList);				
				gameListView.setAdapter(gameListAdapter);
			}
			else
			{
				textView1.setText(R.string.no_game_found);
			}
			
			//chiude dialog attesa
			progressDialog.dismiss();
		}
		
		private ApksList getInstalledApps()
		{
			ApksList ret = new ApksList();
			
			//prende i package installati
		    List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);		    
		    for(int i=0;i<packs.size();i++)
		    {
		        PackageInfo pack = packs.get(i);

		        //se il package e' di sistema, non lo include
		        if (pack.versionName == null)
		            continue ;		        
		        
		        //carica informazioni singolo package
		        ret.add( new ApkDescriptor(getPackageManager(), pack) );
		    }
		    
		    return ret; 
		}		
		
    }
    
	public synchronized void backupGameToSD(final GameDescriptor gd)
	{
		ProgressDialog progressDialog = new ProgressDialog(GameProgressBackupActivity.this);
		progressDialog.setTitle(R.string.loading_title);
		progressDialog.setMessage(getString(R.string.loading_please_wait));
		progressDialog.setCancelable(false);
		progressDialog.show();

		final String url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/game_progress_backup/";
		final String full_url = url + gd.getPackageName() + "/";

		// crea la directory se non esiste
		if (new File(url).exists() == false)
			new File(url).mkdir();

		// se esiste chiede se sovrascrivere
		if (new File(full_url).exists() == true)
		{
			AlertDialog dialog = new AlertDialog.Builder(GameProgressBackupActivity.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.replace_backup_title)
			.setMessage(R.string.replace_backup_text)
			.setPositiveButton(android.R.string.yes,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {

							// cancella il contenuto della cartella
							GameProgressBackupActivity.this.deleteRecursive(new File(full_url));
							
							_backupGameToSD(gd, url);
						}

					}
			)
			.setNegativeButton(android.R.string.no, null)
			.show();
        }
		else
		{
			_backupGameToSD(gd, url);
		}
		
		updateBackupDoneList();
		progressDialog.dismiss();
	}
	
	private void _backupGameToSD(final GameDescriptor gd, String url)
	{
		String RELPATH = null;
		String relative_path = null;
		
		try {
			// carica lo script
			ArrayList<Command> commands = this.readBackupScriptFile(gd.getPackageName());
	
			// esegue lo script
			for (Command cmd : commands)
			{
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
					
					if (RELPATH == null)
					{
						pathLen = file.indexOf(gd.getPackageName());
						relative_path = file.substring(pathLen);
					}
					else
					{
						pathLen = file.indexOf(RELPATH);
						relative_path = gd.getPackageName() + "/" + file.substring(pathLen);
					}
					 
	
					// ricostruisce l'albero delle directory se necessario
					String[] tokens = relative_path.split("/");
	
					if (tokens.length > 1) {
						int i = 0;
						String temp_url = new String(url);
						for (i = 0; i < (tokens.length - 1); i++) // salta il nome
																	// del file
						{
							temp_url = temp_url + tokens[i] + "/";
							if (new File(temp_url).exists() == false)
								new File(temp_url).mkdir();
						}
					}
	
					// copia il file se esiste
					this.fileCopy(file, (url + relative_path));
				}
			}
			new MessageBoxDialog( GameProgressBackupActivity.this, GameProgressBackupActivity.this.getString(R.string.backup_ok)).showWithoutException();
		}
		catch (IOException e)
		{
	
			new MessageBoxDialog( GameProgressBackupActivity.this, GameProgressBackupActivity.this.getString(R.string.backup_fail)).showWithoutException();
			e.printStackTrace();
		}
	}
	
	/*
					if (cmd.args.size() > 1)
					{
						if ( cmd.args.get(1).equalsIgnoreCase("REQUIRED") )
						{
							if (new File(file).exists())
								ok = false;
								break;
						}
					}
	 */
	public void restoreGameFromSD(final GameDescriptor gd)
	{
		AlertDialog dialog = new AlertDialog.Builder(GameProgressBackupActivity.this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(R.string.restore_backup_title)
		.setMessage(R.string.restore_backup_text)
		.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {						
						
						Bundle b = new Bundle();
						b.putString("package_name", gd.getPackageName());
						Intent i = new Intent(GameProgressBackupActivity.this.getApplicationContext(), GameProgressRestoreActivity.class);
						i.putExtras(b);
						startActivity(i);
					}
				}
		)
		.setNegativeButton(android.R.string.no, null)
		.show();		
	}
	
	public void deleteGameFromSD(final GameDescriptor gd)
	{
		AlertDialog dialog = new AlertDialog.Builder(GameProgressBackupActivity.this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(R.string.delete_backup_title)
		.setMessage(R.string.delete_backup_text)
		.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {						
						_deleteGameFromSD(gd);
					}
				}
		)
		.setNegativeButton(android.R.string.no, null)
		.show();
	}
	
	private void _deleteGameFromSD(GameDescriptor gd)
	{
		String url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/game_progress_backup/";
		final String full_url = url + gd.getPackageName() + "/";
		
		if (new File(full_url).exists() == false)
		{
			new MessageBoxDialog( GameProgressBackupActivity.this, GameProgressBackupActivity.this.getString(R.string.no_backup_found)).showWithoutException();
			return;
		}
		
		GameProgressBackupActivity.this.deleteRecursive(new File(full_url));
		updateBackupDoneList();
		new MessageBoxDialog( GameProgressBackupActivity.this, GameProgressBackupActivity.this.getString(R.string.delete_backup_ok)).showWithoutException();		
	}
	
	public boolean backupExists(String name)
	{
		String url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/game_progress_backup/";
		final String full_url = url + name + "/";
		
		return ( new File(full_url).exists() );
	}

	public void updateBackupDoneList()
	{
		if (installedGamesList != null && installedGamesList.size() > 0)
		{
			for(GameDescriptor gd : installedGamesList)
				gd.saved = backupExists(gd.getPackageName());
			
			//aggiorna la view qui (per la concorrenza)
			gameListAdapter = new GameListAdapter(getApplicationContext(), R.layout.game_listitem, installedGamesList);				
			gameListView.setAdapter(gameListAdapter);
		}
	}
}