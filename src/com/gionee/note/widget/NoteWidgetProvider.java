/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gionee.note.widget;

import java.util.List;
import java.util.zip.Inflater;

import amigo.provider.AmigoSettings;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.gionee.note.HomeActivity;
import com.gionee.note.NoteActivity;
import com.gionee.note.R;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.Notes;
import com.gionee.note.content.ResourceParser;
import com.gionee.note.content.Session;
import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.UtilsQueryDatas;
import com.gionee.note.utils.WidgetUtils;

public abstract class NoteWidgetProvider extends AppWidgetProvider {

	public static final String WIDGET_ID = "com.gionee.widget.widgetId";
	public static final String WIDGET_TYPE = "com.gionee.widget.widgetType";
//	public static final int occupancyScreen_2x = 1/3;
//	public static final int occupancyScreen_4x = 2/3;
//	public static final int occupancyWidget_2x = 3/4;
//	public static final int occupancyWidget_4x = 4/5;
	public static String defaultNoteText;

	// GN pengwei 2012-11-09 add for CR00725669 begin
	private final int widgetDisable = -1;// the value of widgetID
	// GN pengwei 2012-11-09 add for CR00725669 end

	// gn lilg 2012-11-15 add for GN_GUEST_MODE start
	private static boolean isRegister = false;
	// gn lilg 2012-11-15 add for GN_GUEST_MODE end

	// gn lilg 2012-12-08 modify for optimization begin
	private DBOperations dbo;
	// gn lilg 2012-12-08 modify for optimization end

	private ContentObserver mGusestWidgetModeObserver = new ContentObserver(
			new Handler(Looper.getMainLooper())) {

		@Override
		public void onChange(boolean selfChange) {
			//gn pengwei 20130117 modify for guest mode begin
			Log.i("NoteWidgetProvider------guest mode changed!---mode---" +  NoteApplication.GN_GUEST_MODE);
			//gn pengwei 20130117 modify for guest mode end
			// Gionee jiating 2012-10-22 modify for CR00716677 begin
			WidgetUtils.updateWidget(NoteApplication.getAppliactionInstance()
					.getApplicationContext());
			// Gionee jiating 2012-10-22 modify for CR00716677 end
			//gn pengwei 20130117 modify for guest mode begin
			Log.v("--------onChange---mode---" +  NoteApplication.GN_GUEST_MODE);
			//gn pengwei 20130117 modify for guest mode end
		}

	};

	public static void setRegister(boolean isRegister) {
        NoteWidgetProvider.isRegister = isRegister;
    }

    @Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i("NoteWidgetProvider------onUpdate start!");
		//gn pengwei 20130117 modify for guest mode begin
		Log.v("--------onUpdate---mode---" +  NoteApplication.GN_GUEST_MODE);
		//gn pengwei 20130117 modify for guest mode end
		// gn jiating 20121009 GN_GUEST_MODE begin
		// gn lilg 2012-11-15 add for GN_GUEST_MODE start
		if (!isRegister) {
			context.getContentResolver().registerContentObserver(
					CommonUtils.getUri(), false,
					mGusestWidgetModeObserver);
			setRegister(true);
		}
		// gn lilg 2012-11-15 add for GN_GUEST_MODE end
		NoteApplication.setGueseMode(CommonUtils.getIsGuestMode(context.getContentResolver()));
		// gn jiating 20121009 GN_GUEST_MODE end

		for (int i = 0; i < appWidgetIds.length; i++) {
			// gn lilg 2012-11-09 add for CR00725627 start
			//gn pengwei 20130117 modify for CR00764945 begin
			int isShowAlarmIcon = View.GONE;
			//gn pengwei 20130117 modify for CR00764945 end
			// gn lilg 2012-11-09 add for CR00725627 end
			if (appWidgetIds[i] != AppWidgetManager.INVALID_APPWIDGET_ID) {

				int bgId = ResourceParser.getDefaultBgId(context);
				// Gionee jiating 2012-10-22 modify for CR00716068 begin
				String noteText;
				// Gionee jiating 2012-10-22 modify for CR00716068 end
				Intent clickIntent;
				// gn jiating 20121009 GN_GUEST_MODE begin
				if (NoteApplication.GN_GUEST_MODE) {
					Log.d("NoteWidgetProvider------GN_GUEST_MODE: "
							+ NoteApplication.GN_GUEST_MODE);
					noteText = context.getResources().getString(
							R.string.note_safe_mode);
					clickIntent = new Intent(context, HomeActivity.class);
				} else {
					Log.d("NoteWidgetProvider------GN_GUEST_MODE: "
							+ NoteApplication.GN_GUEST_MODE);
					// gn jiating 20121009 GN_GUEST_MODE end
					// Gionee jiating 2012-10-22 modify for CR00716068 begin
					noteText = context.getResources().getString(
							R.string.widget_click_add_note);
					// Gionee jiating 2012-10-22 modify for CR00716068 end
					clickIntent = new Intent(context, NoteActivity.class);
					clickIntent.putExtra(WIDGET_ID, appWidgetIds[i]);
					clickIntent.putExtra(WIDGET_TYPE, getWidgetType());

					List<Note> noteList = getNoteWidgetInfo(context,
							appWidgetIds[i]);

					if (noteList.size() <= 0) {
						Log.i("NoteWidgetProvider------new note!");
					} else {
						Log.i("NoteWidgetProvider------edit note!");
						if (noteList.size() > 1) {
							Log.d("NoteWidgetProvider------Multiple message with same widget id:"
									+ appWidgetIds[i] + "!");
							return;
						}
						Note note = noteList.get(0);
						if (note == null) {
							Log.d("NoteWidgetProvider------note == null");
							return;
						}
						int noteId = 0;
						try {
							noteId = Integer.parseInt(note.getId());
						} catch (NumberFormatException e) {
							Log.e("NoteWidgetProvider------number format exception for note id: "
									+ note.getId() + "!");
							return;
						}
						clickIntent.putExtra(DBOpenHelper.ID, noteId);

						// gn lilg 2013-03-01 modify for CR00774631 begin
						// gn lilg 2012-11-09 modify for CR00725538 start
						//Gionee liuliang 2014-7-1 modify for CR01296371 begin
						noteText = note.getTitle() != null ? note.getTitle() : CommonUtils.noteContentPreDealForWidget(note.getContent());
						//Gionee liuliang 2014-7-1 modify for CR01296371 end
						// gn lilg 2012-11-09 modify for CR00725538 end
						if(null == noteText){
							noteText = context.getResources().getString(R.string.note_new_title_label);
						}
						// gn lilg 2013-03-01 modify for CR00774631 end
						try {
							bgId = Integer.parseInt(note.getBgColor());
						} catch (Exception e) {
							Log.e("NoteWidgetProvider------e.getMessage(): "
									+ e.getMessage());
						}

						// gn lilg 2012-11-09 add for CR00725627 start
						long alarmTime = 0L;
						try {
							alarmTime = Long.parseLong(note.getAlarmTime());
						} catch (Exception e) {
							Log.e("NoteWidgetProvider------e.getMessage(): "
									+ e.getMessage());
						}
						if (alarmTime > 0) {
							isShowAlarmIcon = View.VISIBLE;
						}
						// gn lilg 2012-11-09 add for CR00725627 end

						Log.d("NoteWidgetProvider------note id: "
								+ note.getId() + "!");
						Log.i("NoteWidgetProvider------note text: " + noteText
								+ "!");
						Log.i("NoteWidgetProvider------note bg color: " + bgId
								+ "!");
					}
					Log.i("NoteWidgetProvider------widget id: "
							+ appWidgetIds[i] + "!");
					Log.d("NoteWidgetProvider------widget type: "
							+ getWidgetType() + "!");
				}

				RemoteViews rv = new RemoteViews(context.getPackageName(),
						getLayoutId());
				//gn pengwei 20121205 modify  begin
/*				rv.setImageViewResource(R.id.widget_bg_image,
						getBgResourceId(bgId));*/
				//LinearLayout to set the background picture
				rv.setInt(R.id.widget_bg_image, "setBackgroundResource", getBgResourceId(bgId));
				//gn pengwei 20121205 modify  end
				//gn pengwei 20130301 add for CR00772647 begin
				int lines = 6;
				if(getWidgetType() == Notes.TYPE_WIDGET_2X){
					View widgetView =  LayoutInflater.
							from(context).inflate(R.layout.widget_2x, null); 
					TextView textview = (TextView) widgetView.findViewById(R.id.widget_text);
					float lineHei = textview.getLineHeight() + textview.getLineSpacingMultiplier();
					lines = ((Session.getScreenHeight() * 1/3 ) / (int)lineHei) * 2 / 3;
				}else{
					View widgetView =  LayoutInflater.
							from(context).inflate(R.layout.widget_4x, null); 
					TextView textview = (TextView) widgetView.findViewById(R.id.widget_text);
					float lineHei = textview.getLineHeight() + textview.getLineSpacingMultiplier();
					lines = ((Session.getScreenHeight() * 2/3 ) / (int)lineHei) * 11 / 12;
				}
				Log.d("NoteWidgetProvider------widget lines: "
						+ lines);
				// Gionee <pengwei><2013-4-8> modify for CR00794211 begin
				// Gionee <pengwei><2013-6-15> modify for CR00823878 begin

				if(lines <= 0){
					lines = 6;
				}else if(lines > 3){
					lines = lines - 1;
				}
				// Gionee <pengwei><2013-6-15> modify for CR00823878 end
				// Gionee <pengwei><2013-4-8> modify for CR00794211 end
				rv.setInt(R.id.widget_text,"setLines",lines);
				//gn pengwei 20130301 add for CR00772647 end
				rv.setTextViewText(R.id.widget_text, noteText);
				rv.setViewVisibility(R.id.widget_alarm_icon, isShowAlarmIcon);
				// GN pengwei 2012-11-09 add begin
				// Click on the desktop widget returns, guarantee the Activity
				// will not repeat the stack
				clickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// GN pengwei 2012-11-09 add end
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, appWidgetIds[i], clickIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				rv.setOnClickPendingIntent(R.id.widget_bg_image, pendingIntent);

				appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

			}
			super.onUpdate(context, appWidgetManager, appWidgetIds);
			Log.i("NoteWidgetProvider------onUpdate end!");

		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i("NoteWidgetProvider------onDeleted start!");

		// gn lilg 2012-12-08 modify for optimization begin
		if(dbo == null){
			dbo = DBOperations.getInstances(context);
		}
		// gn lilg 2012-12-08 modify for optimization end
		
		ContentValues values = new ContentValues();

		// GN pengwei 2012-11-09 modify for CR00725669 begin
		values.put(DBOpenHelper.WIDGET_ID, widgetDisable);
		// GN pengwei 2012-11-09 modify for CR00725669 end

		for (int i = 0; i < appWidgetIds.length; i++) {
			dbo.updateWidget(context, appWidgetIds[i], values);
			UtilsQueryDatas.updateNoteByWidgetID(appWidgetIds[i], HomeActivity.mTempNoteList, widgetDisable);
		}

		super.onDeleted(context, appWidgetIds);
		Log.i("NoteWidgetProvider------onDeleted end!");
	}

	// gn lilg 2012-11-15 add for GN_GUEST_MODE start
	@Override
	public void onEnabled(Context context) {
		Log.i("NoteWidgetProvider------onEnabled start!");
		context.getContentResolver().registerContentObserver(
				CommonUtils.getUri(), false,
				mGusestWidgetModeObserver);
		setRegister(true);
		Log.d("NoteWidgetProvider------onEnabled end!");
	}

	@Override
	public void onDisabled(Context context) {
		Log.i("NoteWidgetProvider------onDisabled start!");
		context.getContentResolver().unregisterContentObserver(
				mGusestWidgetModeObserver);
		setRegister(false);
		Log.d("NoteWidgetProvider------onDisabled end!");
	}

	// gn lilg 2012-11-15 add for GN_GUEST_MODE end

	private List<Note> getNoteWidgetInfo(Context context, int widgetId) {
		// gn lilg 2012-12-08 modify for optimization begin
		if(dbo == null){
			dbo = DBOperations.getInstances(context);
		}
		// gn lilg 2012-12-08 modify for optimization begin
		return dbo.queryNoteByWidgetId(context, widgetId);
	}

	protected abstract int getLayoutId();

	protected abstract int getBgResourceId(int bgId);

	protected abstract int getWidgetType();
	
	private void setWidgetLines(){
		
	}
	
}
