package com.gionee.note;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gionee.note.R;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.Note;
import com.gionee.note.noteMedia.RemindActivity;
import com.gionee.note.utils.AlarmAlertWakeLock;
import com.gionee.note.utils.AlarmUtils;
import com.gionee.note.utils.Log;

public class AlarmReceiver extends BroadcastReceiver {

	public static final String UPDATE_DIALOG_RECEIVER = "com.gionee.note.receiver.updatedialog";
	private static int i=0;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AlarmReceiver------onReceive start!");
		/**
		 * 获得cpu锁，保证notifucation()方法完全执行。
		 */
		AlarmAlertWakeLock.acquireCpuWakeLock(context);

		startRemindActivity(context, intent);

		AlarmAlertWakeLock.releaseCpuLock();
		Log.d("AlarmReceiver------onReceive end!");
	}

	private void startRemindActivity(Context context, Intent intent) {
		Log.i("AlarmReceiver------start remind activity begin!");

		String time = intent.getStringExtra(AlarmUtils.ALARM_TIME);
		
		// gn lilg 2012-12-08 modify for optimization begin
		List<Note> noteList = DBOperations.getInstances(context).queryAllNotesByAlarm(context, time);
		// gn lilg 2012-12-08 modify for optimization end
		
		Intent notificationNoteActivity=new Intent("com.gionee.noteActivity.alarm");
		context.sendBroadcast(notificationNoteActivity);
		AlarmUtils.setReceiverAlarm(context, new Note());
		if (noteList.size() < 1) {
			return;
		}
		Bundle bundle = new Bundle();
		ArrayList list = new ArrayList();// 这个arraylist是可以直接在bundle里传的，所以我们可以借用一下它的功能
		list.add(noteList);// 这个list2才是你真正想要传过去的list。我们把它放在arraylis中，借助它传过去
		bundle.putParcelableArrayList("list", list);
		//		if (isDialogShow(context)) {
		//			Intent receiverIntent = new Intent(UPDATE_DIALOG_RECEIVER);
		//			receiverIntent.putExtras(bundle);
		//			context.sendBroadcast(receiverIntent);
		//		} else {
		Intent remindIntent = new Intent(context, RemindActivity.class);
		remindIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		remindIntent.putExtras(bundle);
		context.startActivity(remindIntent);
		//		}
		Log.d("AlarmReceiver------start remind activity end!");
	}

	private boolean isDialogShow(Context context) {
		ActivityManager activityManager = (ActivityManager) context
		.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> forGroundActivity = activityManager
		.getRunningTasks(1);
		RunningTaskInfo currentActivity;
		currentActivity = forGroundActivity.get(0);
		String activityName = currentActivity.topActivity.getClassName();
		Log.i("AlarmReceiver------activityName: " + activityName);
		if (RemindActivity.class.getName().equals(activityName)) {
			return true;
		}
		return false;
	}

}
