package it.nicola_amatucci.android.game_progress_backup.games;

import it.nicola_amatucci.android.game_progress_backup.apks.ApkDescriptor;

import java.util.ArrayList;

public class GameDescriptor
{	
	String name;
	String package_name;
	String version;
	int root;
	int root_write;
	int verified;
	
	/* 
	 * settato dal thread di ricerca nell'Activity principale
	 */
	ApkDescriptor apk;	
	public boolean isInstalled() { return apk != null; }
	public void setApk(ApkDescriptor apk) { this.apk = apk; }
	public ApkDescriptor getApk() { return this.apk; }
	
	/* 
	 * settato dal thread di ricerca nell'Activity principale
	 * 
	 * indica se c'è un salvataggio del gioco
	 */
	public boolean saved = false;	
	
	public GameDescriptor() {
		super();
	}
	
	public GameDescriptor(String name, String package_name, String version, int root, int root_write, int verified)
	{
		super();
		this.name = name;
		this.package_name = package_name;
		this.version = version;
		this.root = root;
		this.root_write = root_write;
		this.verified = verified;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return package_name;
	}
	public void setPackageName(String package_name) {
		this.package_name = package_name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getRoot() {
		return root;
	}
	public void setRoot(int root) {
		this.root = root;
	}
	public int getRootWrite() {
		return root_write;
	}
	public void setRootWrite(int root_write) {
		this.root_write = root_write;
	}
	public int getVerified() {
		return verified;
	}
	public void setVerified(int verified) {
		this.verified = verified;
	}
	@Override
	public String toString() {
		return this.name;
	}	
}
