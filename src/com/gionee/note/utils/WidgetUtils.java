package com.gionee.note.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


import com.gionee.note.content.Notes;
import com.gionee.note.widget.NoteWidgetProvider_2x;
import com.gionee.note.widget.NoteWidgetProvider_4x;

public class WidgetUtils {

	public static void updateWidget(Context context, int widgetId, int widgetType){
		Log.i("WidgetUtils------update widget start!");

		Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		if(widgetType == Notes.TYPE_WIDGET_2X){
			Log.i("WidgetUtils------widgetType: " + Notes.TYPE_WIDGET_2X);
			updateIntent.setClass(context, NoteWidgetProvider_2x.class);
		}else if(widgetType == Notes.TYPE_WIDGET_4X){
			Log.i("WidgetUtils------widgetType: " + Notes.TYPE_WIDGET_4X);
			updateIntent.setClass(context, NoteWidgetProvider_4x.class);
		}else{
			Log.e("WidgetUtils------Unspported widget type: " + widgetType);
			return;
		}

		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { widgetId });
		Log.d("WidgetUtils------the id of widget to update: " + widgetId);

		context.sendBroadcast(updateIntent);
		Log.i("WidgetUtils------update widget end!");
	}

	// GN pengwei 2012-11-12 add  for CR00725641 begin 
	//	/*overwrite widget refresh
	//	 * params：
	//	 * context
	//	 * widgetIds -- the id of the refresh widget
	//	 * widgetType -- the type of the refresh widget
	//	 * */
	//	public static void updateWidget(Context context, int[] widgetIds, int widgetType){
	//		if(Log.LOGV){
	//			Log.i("WidgetUtils---update widget start!---");
	//		}
	//	
	//		
	//		Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	//		if(widgetType == Notes.TYPE_WIDGET_2X){
	//			if(Log.LOGV){
	//				Log.i("WidgetUtils---Notes.TYPE_WIDGET_2X---");
	//			}
	//		
	//			updateIntent.setClass(context, NoteWidgetProvider_2x.class);
	//		}else if(widgetType == Notes.TYPE_WIDGET_4X){
	//			if(Log.LOGV){
	//				Log.i("WidgetUtils---Notes.TYPE_WIDGET_4X----");
	//			}
	//		
	//			updateIntent.setClass(context, NoteWidgetProvider_4x.class);
	//		}else{
	//			if(Log.LOGV){
	//				Log.i("WidgetUtils---Unspported widget type: " + widgetType + " ---");
	//			}
	//		
	//		
	//			return;
	//		}
	//
	//		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
	//		if(Log.LOGV){
	//			Log.i("WidgetUtils---the id of widget to update: " + widgetIds + "!---");
	//		}
	//	
	//		
	//		context.sendBroadcast(updateIntent);
	//		
	//		
	//	}

	public static void updateWidget(Context context, Map<Integer, Set<Integer>> widgetTypeIdMap){
		Log.i("WidgetUtils------update widget start!");

		if(widgetTypeIdMap == null || widgetTypeIdMap.size() <= 0 ){
			Log.i("WidgetUtils------widgetTypeIdMap is null or size <= 0!");
			return;
		}

		Set<Entry<Integer, Set<Integer>>> entrySet = widgetTypeIdMap.entrySet();
		for(Entry<Integer, Set<Integer>> entry : entrySet){

			Integer widgetType = entry.getKey();
			Set<Integer> widgetId = entry.getValue();

			Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			if(widgetType == Notes.TYPE_WIDGET_2X){
				Log.i("WidgetUtils------widgetType: " + Notes.TYPE_WIDGET_2X);
				updateIntent.setClass(context, NoteWidgetProvider_2x.class);
			}else if(widgetType == Notes.TYPE_WIDGET_4X){
				Log.i("WidgetUtils------widgetType: " + Notes.TYPE_WIDGET_4X);
				updateIntent.setClass(context, NoteWidgetProvider_4x.class);
			}else{
				Log.e("WidgetUtils------Unspported widget type: " + widgetType);
				continue;
			}

			int[] widgetIds = new int[widgetId.size()];
			int i = 0;
			for(int id : widgetId){
				widgetIds[i++] = id;
				Log.i("WidgetUtils------id: "+ id);
			}

			updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
			context.sendBroadcast(updateIntent);
			Log.i("WidgetUtils------send broadcast!");
		}
		Log.d("WidgetUtils------update widget end!");
	}


	//gn jiating 20121009 GN_GUEST_MODE begin
	public static void updateWidget(Context context) {
		Log.i("WidgetUtils------updateWidget_Gusert start!");
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = mgr.getAppWidgetIds(new ComponentName(context,
				NoteWidgetProvider_2x.class));
		int[] appWidgetIds4 = mgr.getAppWidgetIds(new ComponentName(context,
				NoteWidgetProvider_4x.class));

		Intent updateIntent = new Intent(
				AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.setClass(context, NoteWidgetProvider_2x.class);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetIds);
		context.sendBroadcast(updateIntent);

		Intent updateIntent2 = new Intent(
				AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent2.setClass(context, NoteWidgetProvider_4x.class);
		updateIntent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
				appWidgetIds4);
		context.sendBroadcast(updateIntent2);
		Log.i("WidgetUtils------updateWidget_Gusert end!");
	}
	//gn jiating 20121009 GN_GUEST_MODE end


	/*public static void updateWidget(Context context, Map<Integer, List<Integer>> widgetTypeIdMap){
		if (DBG) {Log.d(LOG_TAG, "---update widget start!---");}

		if(widgetTypeIdMap == null || widgetTypeIdMap.size() <= 0 ){
			Log.e(LOG_TAG, "widgetTypeIdMap is null or size <= 0!");
			return;
		}

		Set<Entry<Integer, List<Integer>>> entrySet = widgetTypeIdMap.entrySet();
		for(Entry<Integer, List<Integer>> entry : entrySet){

			Integer widgetType = entry.getKey();
			List<Integer> widgetId = entry.getValue();

			Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			if(widgetType == Notes.TYPE_WIDGET_2X){
				if(DBG){Log.d(LOG_TAG, "---Notes.TYPE_WIDGET_2X---");}
				updateIntent.setClass(context, NoteWidgetProvider_2x.class);
			}else if(widgetType == Notes.TYPE_WIDGET_4X){
				if(DBG){Log.d(LOG_TAG, "---Notes.TYPE_WIDGET_4X---");}
				updateIntent.setClass(context, NoteWidgetProvider_4x.class);
			}else{
				Log.e(LOG_TAG, "---Unspported widget type: " + widgetType + " ---");
				continue;
			}

			int[] widgetIds = new int[widgetId.size()];
			for(int i = 0; i < widgetId.size(); i++){
				widgetIds[0] = widgetId.get(i);
				if(DBG){Log.d(LOG_TAG, widgetId.get(i) + "");}
			}

			updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
			context.sendBroadcast(updateIntent);
			if(DBG){Log.d(LOG_TAG, "send broadcast!");}
		}

		if (DBG) {Log.d(LOG_TAG, "---update widget end!---");}
	}*/

}