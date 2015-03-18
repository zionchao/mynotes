package com.gionee.note;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gionee.note.domain.Note;
import com.gionee.note.utils.AlarmUtils;
import com.gionee.note.utils.Log;

public class AlarmInitReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i("AlarmInitReceiver------onReceive!");
		AlarmUtils.setReceiverAlarm(context, new Note());
		
		//        long currentTime = System.currentTimeMillis();
		//        //TODO lilg 需考虑时区问题
		//        List<Note> noteList = DBOperations.getInstances().queryAllNotesWithAlarm(context, currentTime);
		//
		//        if(noteList != null && noteList.size() > 0){
		//        	
		//        	AlarmManager alermManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		//        	
		//        	for(Note note : noteList){
		//        		if(Log.LOGV){
		//        			Log.i("AlarmInitReceiver---the info of the note from db: " + note.toString());
		//        		}
		//        		
		//        		
		//        		long alertTime;
		//        		try{
		//        			alertTime = Long.parseLong(note.getAlarmTime());
		//        		}catch(Exception e){
		//        			if(Log.LOGV){
		//            			Log.e("AlarmInitReceiver---e.getMessage()"+e.getMessage());
		//            		}
		//        			
		//        			continue;
		//        		}
		//        		
		//        		int noteId;
		//        		try{
		//        			noteId = Integer.parseInt(note.getId());
		//        		}catch(Exception e){
		//        			if(Log.LOGV){
		//            			Log.e("AlarmInitReceiver---e.getMessage()"+e.getMessage());
		//            		}
		//        			
		//        			continue;
		//        		}
		//        		
		//                Intent sender = new Intent(context, AlarmReceiver.class);
		//        		sender.setData(ContentUris.withAppendedId(NotesProvider.CONTENT_URI, noteId));
		//        		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, sender, 0);
		//        		// TODO lilg 返回值处理
		//        		alermManager.set(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent);
		
		// Gionee <lilg><2013-05-16> add for CR00798957 begin
		// note will exit when after init alarms set before.
		Log.d("AlarmInitReceiver------note will exit.");
		System.exit(0);
		// Gionee <lilg><2013-05-16> add for CR00798957 end
	}

}
