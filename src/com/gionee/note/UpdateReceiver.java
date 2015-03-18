package com.gionee.note;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gionee.note.content.NoteApplication;
import com.gionee.note.utils.Log;

/**
 * note update receiver
 * @author lilg 2013-04-10 
 *
 */
public class UpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("UpdateReceiver------onReceive()!");
		Log.d("UpdateReceiver------intent action: " + intent.getAction());

		boolean haveVersion = intent.getBooleanExtra("result", false);
		Log.d("UpdateReceiver------haveVersion: " + haveVersion);
		
		if (NoteApplication.isUpgradeSupport()) {
			NoteApplication application = (NoteApplication) context.getApplicationContext();
			application.setHaveUpgradeInfo(haveVersion);
		}

	}

}
