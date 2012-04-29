package it.nicola_amatucci.android.game_progress_backup.apks;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class ApkDescriptor {
	public String appname = "";
	public String pname = "";
	public String versionName = "";
	public int versionCode = 0;
	public Drawable icon;
	
	public ApkDescriptor(PackageManager pMan, PackageInfo p)
	{
		super();
		this.appname = p.applicationInfo.loadLabel(pMan).toString();
		this.pname = p.packageName;
		this.versionName = p.versionName;
		this.versionCode = p.versionCode;
        this.icon = p.applicationInfo.loadIcon(pMan);
	}
}
