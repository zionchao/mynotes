package com.gionee.note.content;

import java.util.List;

import amigo.app.AmigoActivity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.gionee.note.domain.Note;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.WidgetUtils;
//import  amigo.theme.AmigoThemeManager;
import android.os.SystemProperties;

public class NoteApplication extends Application {

	// gn jiating 20121009 GN_GUEST_MODE begin
	public static boolean  GN_GUEST_MODE;
	// gn jiating 20121009 GN_GUEST_MODE end
	
	// Gionee jiating 2012-10-22 modify for CR00716677 begin
	private static NoteApplication instance;
	// Gionee jiating 2012-10-22 modify for CR00716677 end

	// Gionee <lilg><2013-04-10> add for note upgrade begin
	public static boolean isUpgradeSupport = false;
	public boolean mHaveUpgradeInfo = false;
	public AmigoActivity mRunningActivity;
	// Gionee <lilg><2013-04-10> add for note upgrade end
	
	@Override
	public void onCreate() {
		Log.i("NoteApplication------onCreate!");
		// Gionee jiating 2012-10-22 modify for CR00716677 begin
		if (instance == null) {
			instance = this;
		}
		// Gionee jiating 2012-10-22 modify for CR00716677 end
		
		// Gionee <lilg><2013-05-10> add for CR00807787 begin
		WidgetUtils.updateWidget(instance.getApplicationContext());
		// Gionee <lilg><2013-05-10> add for CR00807787 end
		
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		PackageManager packageManager = getPackageManager();
		if (packageManager != null) {
			try {
				packageManager.getApplicationInfo("com.gionee.appupgrade", PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
				setUpgradeSupport(true);
			} catch (NameNotFoundException e) {
                setUpgradeSupport(false);
			}
			Log.d("NoteApplication------UPGRADE_SUPPORT: " + isUpgradeSupport);
		}
		// Gionee <lilg><2013-04-10> add for note upgrade end
	}

	public static boolean isUpgradeSupport() {
        return isUpgradeSupport;
    }

    public static void setUpgradeSupport(boolean isUpgradeSupport) {
        NoteApplication.isUpgradeSupport = isUpgradeSupport;
    }

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("NoteApplication------onConfigurationChanged!");
		super.onConfigurationChanged(newConfig);
	}

	
	// Gionee jiating 2012-10-22 modify for CR00716677 begin
	public static NoteApplication getAppliactionInstance() {
		return instance;
	}
	// Gionee jiating 2012-10-22 modify for CR00716677 end

	// Gionee <lilg><2013-04-10> add for note upgrade begin
	public void setHaveUpgradeInfo(boolean flag) {
		Log.d("NoteApplication------setHaveUpgradeInfo, flag: " + flag);
		
		mHaveUpgradeInfo = flag;
		if (mHaveUpgradeInfo) {
			if (mRunningActivity != null) {
				startVersionActivity(mRunningActivity);
				mHaveUpgradeInfo = false;
			}
		}
	}
	
	public void startVersionActivity(AmigoActivity activity) {
		Log.i("NoteApplication------startVersionActivity!");
		
		if (activity != null) {
			Intent intent = new Intent("com.gionee.appupgrade.action.GN_APP_UPGRAGE_SHOW_DIALOG");
			intent.putExtra("package", getPackageName());
			activity.startActivity(intent);
		}
	}
	
	public void registerVersionCallback(AmigoActivity activity) {
		Log.i("NoteApplication------registerVersionCallback!");
		
		mRunningActivity = activity;
		if (mHaveUpgradeInfo) {
			startVersionActivity(mRunningActivity);
			mHaveUpgradeInfo = false;
		}
	}

	public static void setGueseMode(boolean guestMode){
	    GN_GUEST_MODE = guestMode;
	}
	public void unregisterVersionCallback(AmigoActivity activity) {
		Log.i("NoteApplication------unregisterVersionCallback!");
		mRunningActivity = null;
	}
	
	// Gionee <lilg><2013-04-10> add for note upgrade end
}
