package com.gionee.note.utils;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.Note;
import com.gionee.note.view.DateTimeDialog;

public class AlarmUtils {

	public static final int STATE_SUCCESS = 0;
	public static final int STATE_ERROR = 1;
	public static final String ALARM_TIME = "com.gionee.alarmTime";
	private static DBOperations dbo;
	public static final String ALARM_ACTION="com.gionee.alarmset";

	/**
	 * set a alarm for the gaving note
	 * 
	 * @param context
	 * @param note
	 * @return
	 */
	public static int setAlarm(final Context context, final Note note) {

		if (context == null || note == null) {
			Log.i("AlarmUtils------context is null or note is null!");
			return STATE_ERROR;
		}
		Log.d("AlarmUtils------set alarm note info: " + note.toString());
		Calendar cal;
		if (Constants.INIT_ALARM_TIME.equals(note.getAlarmTime())) {
			cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 10);
		} else {
			cal = Calendar.getInstance();
			cal.setTimeInMillis(Long.parseLong(note.getAlarmTime()));
		}

		// gn lilg 2012-11-20 modify for theme changed start
		DateTimeDialog alarmDialog = null;
		
		// white theme
		alarmDialog = new DateTimeDialog(context,CommonUtils.getTheme(), new DateTimeDialog.OnDateTimeSetListener() {
		    @Override
		    public void onDateTimeSet(Calendar calendar) {
		        // GN pengwei 2012-11-12 add for Can't ring on time begin
		        calendar.set(Calendar.SECOND, 0);
		        calendar.set(Calendar.MILLISECOND, 0);
		        // GN pengwei 2012-11-12 add for Can't ring on time begin
		        Note NoteSetTime = null;
		        
		        note.setAlarmTime(Long.toString(calendar.getTimeInMillis()));
		        // gionee <wangpan> <2013-12-30> modify for CR00992740 CR00993149 begin
		        DBOperations.getInstances(context).updateNoteField(context, note, DBOpenHelper.ALARM_TIME);
		        // gionee <wangpan> <2013-12-30> modify for CR00992740 CR00993149 end
		        NoteSetTime = calauteRecentAlarm(context, note);
		        Intent intent = new Intent(ALARM_ACTION);
		        intent.putExtra(ALARM_TIME, NoteSetTime.getAlarmTime());
		        
		        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				 int sdkVersion = context.getApplicationInfo().targetSdkVersion;
            	if (sdkVersion > 19) {
		        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setExact(AlarmManager.RTC_WAKEUP, Long.parseLong(NoteSetTime.getAlarmTime()), pIntent);
            	} else {
 		        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, Long.parseLong(NoteSetTime.getAlarmTime()), pIntent);
 	           	}
		    }
		}, cal);
		
		if(alarmDialog != null){
		    alarmDialog.show();
		}

		return STATE_SUCCESS;
	}

	/**
	 * cancel a alarm for the gaving note
	 * 
	 * @param context
	 * @param note
	 * @return
	 */
	public static int cancelAlarm(final Context context, final Note note) {

		if (context == null || note == null) {
			Log.i("AlarmUtils------context is null or note is null!");
			return STATE_ERROR;
		}
		Log.d("AlarmUtils------cancel alarm note info: " + note.toString());

		Intent intent = new Intent(ALARM_ACTION);
		intent.putExtra(ALARM_TIME, note.getAlarmTime());
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(pIntent);
		Note NoteSetTime = null;

		NoteSetTime = calauteRecentAlarm(context, note);
		if (!NoteSetTime.getAlarmTime().equals(Constants.INIT_ALARM_TIME)) {
			intent = new Intent(ALARM_ACTION);
			intent.putExtra(ALARM_TIME, NoteSetTime.getAlarmTime());

			pIntent = PendingIntent.getBroadcast(context, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			int sdkVersion = context.getApplicationInfo().targetSdkVersion;
           if (sdkVersion > 19) {
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
			.setExact(AlarmManager.RTC_WAKEUP,
					Long.parseLong(NoteSetTime.getAlarmTime()), pIntent);
			}else{
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
			.set(AlarmManager.RTC_WAKEUP,
					Long.parseLong(NoteSetTime.getAlarmTime()), pIntent);
			}
		}


		return STATE_SUCCESS;
	}

	public static int setReceiverAlarm(final Context context, final Note note) {

		if (context == null || note == null) {
			Log.i("AlarmUtils---context is null or note is null!");
			return STATE_ERROR;
		}
		Log.d("AlarmUtils---set alarm note info: " + note.toString());

		Note NoteSetTime = null;

		NoteSetTime = calauteRecentAlarm(context, note);
		if (!NoteSetTime.getAlarmTime().equals(Constants.INIT_ALARM_TIME)) {
			Intent intent = new Intent(ALARM_ACTION);
			intent.putExtra(ALARM_TIME, NoteSetTime.getAlarmTime());

			PendingIntent pIntent = PendingIntent.getBroadcast(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        	if (sdkVersion > 19) {
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
			.setExact(AlarmManager.RTC_WAKEUP,
					Long.parseLong(NoteSetTime.getAlarmTime()), pIntent);
			}else{
			((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
			.set(AlarmManager.RTC_WAKEUP,
					Long.parseLong(NoteSetTime.getAlarmTime()), pIntent);
			}
		}

		return STATE_SUCCESS;
	}

	private static Note calauteRecentAlarm(final Context context, Note note) {
		long minTime = Long.parseLong(note.getAlarmTime());
		Note noteRecentTime = note;

		// gn lilg 2012-12-08 modify for optimization begin
		dbo = DBOperations.getInstances(context);
		// gn lilg 2012-12-08 modify for optimization end
		
		ArrayList<Note> noteList = (ArrayList<Note>) dbo.queryAllNotesHaveAlarm(context);

		for (Note notetemp : noteList) {
			long timeTemp = Long.parseLong(notetemp.getAlarmTime());
			if ((timeTemp < minTime) || minTime == 0) {
				minTime = timeTemp;
				noteRecentTime = notetemp;
			}
		}
		Log.d("AlarmUtils------noteRecentTime == null: " + (noteRecentTime == null));
		return noteRecentTime;
	}

}
