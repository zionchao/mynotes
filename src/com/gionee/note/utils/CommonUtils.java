package com.gionee.note.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import amigo.provider.AmigoSettings;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.note.NoteActivity;
import com.gionee.note.R;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.MediaInfo;
import com.gionee.note.domain.Note;
import com.gionee.note.noteMedia.record.NoteMediaManager;
import android.view.inputmethod.InputMethodManager;
import amigo.widget.AmigoEditText;
//tangzz add for inputmethod begin CR01442825 
import android.os.Handler;
//tangzz add for inputmethod end CR01442825 

public class CommonUtils {

	public static final String STR_YES = "yes";
	public static final String STR_NO = "no";
	public static final String DEFAULT_MIMETYPE = "text/plain";

	public static final int RESULT_ERROR = -2;
	public static final int RESULT_OK = 0;
	public static final int RESULT_NOTE_LIST_NULL = -1;
	public static final int RESULT_NOTE_NULL = -1;

	// GN pengwei 2012-11-13 add for View no refresh begin
	public static final int INTENT_DEL = 0;// Click the delete
	public static final int INTENT_LOOK = 1;// Click the check
	public static final int INTENT_CLOSE = 2;// Click on close
	// GN pengwei 2012-11-13 add for View no refresh begin

	// GN pengwei 2012-11-12 add for after Turn off the alarm clock tooltip
	// NoteActivity interface not refresh begin
	public static final String ALARMREFRESH = "refreshalarm";
	public static final String NOTEACTIVITY_REFRESH = "refreshview";
	public static String noteID = "";
	// GN pengwei 2012-11-12 add for after Turn off the alarm clock tooltip
	// after NoteActivity interface not refresh begin
	private static Toast mToast;

	// CR00733764
	public static final int REQUEST_ImportExportActivity = 0x11;
	public static final int REQUEST_ImportTypeSelectActivity = 0x12;
	public static final int REQUEST_ExportTypeSelectActivity = 0x13;

	public static final int RESULT_ImportItemSelectActivity = 0x15;
	public static final int RESULT_ExportItemSelectActivity = 0x16;
	public static final int RESULT_ImportTypeSelectActivity = 0x17;
	public static final int RESULT_ExportTypeSelectActivity = 0x18;
	// gn pengwei 20121205 add for update begin
	public static final String DOWNLOAD_PATH = "GnPay";
	public static final String FLAG_TEST_ENVIRONMENT = "/sdcard/GioneeNote";
	public static final String URL_UPDATE_TEST = "http://test1.gionee.com/synth/open/checkUpgrade.do?test=true&product=com.gionee.note&version=";
	public static final String URL_UPDATE = "http://update.gionee.com/synth/open/checkUpgrade.do?product=com.gionee.note&version=";
	public static final String RESULT_CODE_FALSE = "4006";
	public static final String RESULT_CODE_SUCESS = "9000";
	public static final String RESULT_CODE_CANCEL = "6001";
	public static final String RESULT_CODE_NET_EXCETION = "6002";
	public static final String RESULT_CODE_SERVER_EXCETION = "6000";
	public static final String RESULT_CODE_DATA_EXCETION = "4001";
	public static final String RESULT_CODE_UPDATE_EXCETION = "4002";
	// gn pengwei 20121205 add for update end
	
	// Gionee <lilg><2013-04-28> add for CR00803305 begin
	public static final String LANGUAGE_ZH = "zh";
	public static final String LANGUAGE_EN = "en";
	// Gionee <lilg><2013-04-28> add for CR00803305 end
	public static int sCurrentNoteID = -1;
	public static int sCurrentFloderID = -1;
	public static int GUEST_MODE_DEFAULT = 0;
	
	public static void setNoteID(String noteID) {
        CommonUtils.noteID = noteID;
    }

    /**
	 * 
	 * @param context
	 * @param mimeType
	 * @param noteContent
	 * @return -1为分享失败 0为分享成功
	 */
	public static int shareNote(Context context, String mimeType,
			String noteContent) {

		Log.i("CommonUtils------share note start!");

		if (context == null) {
			Log.i("CommonUtils------context is null!");
			return RESULT_ERROR;
		}

		if (noteContent == null || "".equals(noteContent)) {
			Log.i("CommonUtils------note content is null!");
			return RESULT_NOTE_NULL;
		}
		
		// gn lilg 2013-03-02 modify for CR00774631 begin 
		String tempNoteContent = CommonUtils.noteContentPreDeal(noteContent);
		if("".equals(tempNoteContent)){
			return RESULT_NOTE_NULL;
		}

		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType(mimeType == null || "".equals(mimeType) ? DEFAULT_MIMETYPE : mimeType);
		//Gionee <pengwei><20131010> modify for CR00909096 begin
//		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, noteContent);
//		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, tempNoteContent);
//		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, noteContent);
		//Gionee <pengwei><20131010> modify for CR00909096 end
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, tempNoteContent);
		context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share)));
		Log.d("CommonUtils------share note: " + tempNoteContent + "!");
		Log.i("CommonUtils------share note end!");
		// gn lilg 2013-03-02 modify for CR00774631 end

		return RESULT_OK;
	}

	

	/**
	 * 
	 * @param context
	 * @param mimeType
	 * @param noteList
	 *            便签列表，其中不包含文件夹信息
	 * @return -1为分享失败 0为分享成功
	 */
	public static int shareNote(Context context, String mimeType,
			List<Note> noteList) {
		Log.i("CommonUtils------share note start!");

		if (context == null) {
			Log.i("CommonUtils------context is null!");
			return RESULT_ERROR;
		}

		if (noteList == null || noteList.size() <= 0) {
			Log.i("CommonUtils------the note list is null or the size is <= 0!");
			return RESULT_NOTE_LIST_NULL;
		}

		StringBuilder builder = new StringBuilder();
		String noteContent = "";
		int noteSize = noteList.size();
		for (int i = 0; i < noteSize; i++) {
			noteContent = noteList.get(i).getContent();
			if (!"".equals(noteContent)) {
				builder.append(noteContent);
				if (i != (noteSize - 1)) {
					builder.append("\n");
				}
			}
		}
		
		// gn lilg 2013-03-02 modify for CR00774631 begin 
		String tempNoteContent = CommonUtils.noteContentPreDeal(builder.toString());
		// Gionee <wangpan><2014-04-04> modify for CR01166211 CR01166253 begin
		if("".equals(tempNoteContent) || null == tempNoteContent){
			return RESULT_NOTE_LIST_NULL;
		}
        // Gionee <wangpan><2014-04-04> modify for CR01166211 CR01166253 end

		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType(mimeType == null || "".equals(mimeType) ? DEFAULT_MIMETYPE : mimeType);
		//Gionee <pengwei><20131010> modify for CR00909096 begin
//		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, builder.toString());
		// Gionee <wangpan> <2014-02-17> modify for CR01035233 begin 
		//shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, tempNoteContent);
		// Gionee <wangpan> <2014-02-17> modify for CR01035233 end 
//		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,	builder.toString());
		//Gionee <pengwei><20131010> modify for CR00909096 end
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,	tempNoteContent);
		context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share)));
		Log.d("CommonUtils------share note: " + tempNoteContent + "!");
		Log.i("CommonUtils------share note end!");
		// gn lilg 2013-03-02 modify for CR00774631 end

		return RESULT_OK;
	}

//Gionee liuliang 2014-5-30 modify for CR01272675 begin(wanghaiyan 2015-2-5 merge for CR01444933 begin)
	public static String getNoteData(Context context, String updatedata, String updateTime) {
		Log.d("CommonUtils------updatedate: " + updatedata + ", updateTime: " + updateTime);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String nowTime = format.format(new Date());

		if (nowTime.equals(updatedata)) {
			return updateTime;
		//	String[] time = updateTime.split(":");

			// Gionee <lilg><2013-04-28> modify for CR00803305 begin
		//	String currentLanguage = context.getResources().getConfiguration().locale.getLanguage();
			
		//	if (Integer.parseInt(time[0]) > 12) {
		//		if(LANGUAGE_ZH.equals(currentLanguage)){
		//			return context.getResources().getString(R.string.time_pm) + (Integer.parseInt(time[0]) - 12) + ":" + (time[1]);
		//		}else{
		//			return (Integer.parseInt(time[0]) - 12) + ":" + (time[1]) + context.getResources().getString(R.string.time_pm);
		//		}
		//	} else {
		//		if(LANGUAGE_ZH.equals(currentLanguage)){
		//			return context.getResources().getString(R.string.time_am) + (Integer.parseInt(time[0])) + ":" + (time[1]);
		//		}else{
		//			return (Integer.parseInt(time[0])) + ":" + (time[1]) + context.getResources().getString(R.string.time_am);
		//		}
		//	}
			// Gionee <lilg><2013-04-28> modify for CR00803305 end
		} else {
			// gn pengwei 20121218 add for CR00748107 begin
			if(null == updatedata || "".equals(updatedata)){
				updatedata = nowTime;
			}
			// gn pengwei 20121218 add for CR00748107 end
			return updatedata;
		}
	}
 //Gionee liuliang modify for CR01272675 end(wanghaiyan 2015-2-5 merge for CR01444933 end)
	public static void showToast(Context context, String message) {

		Log.d("CommonUtils------mToast == null: " + (mToast == null)
				+ ", msg: " + message);

		if (mToast == null) {
			mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(message);
		}
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();

	}

	/*
	 * That can add button
	 * 
	 * @param addImg To change the ImageView isEnable Whether buy ash
	 * 
	 * @return void
	 */
	public static void isAbledAdd(Context context, ImageView item,
			boolean isEnable) {
		if (item != null) {
			item.setEnabled(isEnable);
			if (isEnable) {
				item.setImageResource(R.drawable.note_detail_add);
			} else {
				item.setImageResource(R.drawable.note_detail_add_disable);
			}
		}
	}

	/*
	 * That can add button
	 * 
	 * @param addImg To change the ImageView isEnable Whether buy ash
	 * 
	 * @return void
	 */
	public static void isAbledAdd(Context context, MenuItem item,
			boolean isEnable) {
		if (item != null) {
			item.setEnabled(isEnable);
		}
	}
	
	// gn pengwei 20121212 add for Common control begin
	/*
	 * Theme Settings
	 * 
	 * @param context Context environment of AmigoActivity
	 * 
	 * @return void
	 */
	public static void setTheme(Context context) {
		context.setTheme(R.style.GnNoteLightTheme);
	}

	// gn pengwei 20121212 add for Common control end

	

	public static int getTheme(){
		return AmigoAlertDialog.THEME_AMIGO_LIGHT;
//		return AmigoAlertDialog.THEME_HOLO_LIGHT;
	}
	
	public static int getFullScreenTheme(){
		return AmigoAlertDialog.THEME_AMIGO_FULLSCREEN;
//		return AmigoAlertDialog.THEME_HOLO_LIGHT;
	}
	
	//gn pengwei 20130125 modify for CR00768042 begin
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        //Gionee <pengwei><2013-3-21> add for CR00787297 begin
        if(lastClickTime > time){
        	lastClickTime = time;
        	Log.v("CommonUtils---isFastDoubleClick---lastClickTime > time---" + lastClickTime);
        	return false;
        }
        //Gionee <pengwei><2013-3-21> add for CR00787297 end
        if ( time - lastClickTime < 1000) {
        	Log.v("CommonUtils---isFastDoubleClick---time - lastClickTime < 1000---" + lastClickTime);
            return true;   
        }   
        lastClickTime = time;
    	Log.v("CommonUtils---isFastDoubleClick---time - lastClickTime > 1000---" + lastClickTime);
        return false;   
    }
	//gn pengwei 20130125 modify for CR00768042 end
    
    // gn lilg 2013-02-27 add for delete the media info in the note content begin
    public static String noteContentPreDeal(String noteContent){
    	Log.d("CommonUtils------noteContentPreDeal begin!");
    	if(TextUtils.isEmpty(noteContent)){
    		return null;
    	}
    	if(!noteContent.contains(NoteMediaManager.TAG_PREFIX) || !noteContent.contains(NoteMediaManager.TAG_PARSE_SUFFIX)){
    		return noteContent;
    	}

    	// Gionee <lilg><2013-04-08> add for CR00794484 begin
    	noteContent = noteContent.replaceAll("\n", "");
    	// Gionee <lilg><2013-04-08> add for CR00794484 end
    	
    	StringBuffer buffer = new StringBuffer();

    	int lastPosEnd = 0;
    	int posBegin = noteContent.indexOf(NoteMediaManager.TAG_PREFIX, lastPosEnd);
    	Log.d("CommonUtils------posBegin: " + posBegin);
    	if(posBegin != -1){
    		do{
    			int posEnd = noteContent.indexOf(NoteMediaManager.TAG_PARSE_SUFFIX, posBegin);
    			Log.d("CommonUtils------posEnd: " + posEnd);
    			if(posEnd != -1){

    				Log.d("CommonUtils------lastPosEnd + NoteMediaManager.TAG_SUFFIX.length(): " + lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length());
    				if(posBegin == 0 || (posBegin == lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length() && lastPosEnd != 0)){

    				}else{

    					Log.d("CommonUtils------here in else!");

    					String otherStr = "";
						if(lastPosEnd == 0){
							otherStr = noteContent.substring(lastPosEnd, posBegin);
						}else{
							otherStr = noteContent.substring(lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length(), posBegin);
						}
						buffer.append(otherStr.substring(otherStr.indexOf(NoteMediaManager.TAG_LARGE_STRING) + NoteMediaManager.TAG_LARGE_STRING.length()));
						
    				}
    			}else{
    				if(noteContent.indexOf(NoteMediaManager.TAG_STORE_SUFFIX, lastPosEnd) == lastPosEnd){
    					buffer.append(noteContent.substring(lastPosEnd + NoteMediaManager.TAG_STORE_SUFFIX.length()));
					}else{
						buffer.append(noteContent.substring(lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length()));
					}
    				return buffer.toString();
    			}
    			lastPosEnd = posEnd;
				Log.d("CommonUtils------lastPosEnd: " + posEnd);
				posBegin = noteContent.indexOf(NoteMediaManager.TAG_PREFIX, lastPosEnd);
				Log.d("CommonUtils------posBegin: " + posBegin);
    		}while(posBegin != -1);
    	}

    	// 
    	Log.d("CommonUtils------lastPosEnd: " + lastPosEnd + ", text.length(): " + (noteContent.length()));
    	if(lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length() < noteContent.length()){
    		String temp = noteContent.substring(lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length());
    		buffer.append(temp.substring(temp.indexOf(NoteMediaManager.TAG_LARGE_STRING) + NoteMediaManager.TAG_LARGE_STRING.length()));
    	}

    	Log.d("CommonUtils------noteContentPreDeal end!");
    	return buffer.toString();
    }
    // gn lilg 2013-02-27 add for delete the media info in the note content begin
	//Gionee liuliang 2014-7-1 add for CR01296371 begin
     public static String noteContentPreDealForWidget(String noteContent){
    	Log.d("CommonUtils------noteContentPreDeal begin!");
    	if(TextUtils.isEmpty(noteContent)){
    		return null;
    	}
    	if(!noteContent.contains(NoteMediaManager.TAG_PREFIX) || !noteContent.contains(NoteMediaManager.TAG_PARSE_SUFFIX)){
    		return noteContent;
    	}

    	
    	StringBuffer buffer = new StringBuffer();

    	int lastPosEnd = 0;
    	int posBegin = noteContent.indexOf(NoteMediaManager.TAG_PREFIX, lastPosEnd);
    	Log.d("CommonUtils------posBegin: " + posBegin);
    	if(posBegin != -1){
    		do{
    			int posEnd = noteContent.indexOf(NoteMediaManager.TAG_PARSE_SUFFIX, posBegin);
    			Log.d("CommonUtils------posEnd: " + posEnd);
    			if(posEnd != -1){

    				Log.d("CommonUtils------lastPosEnd + NoteMediaManager.TAG_SUFFIX.length(): " + lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length());
    				if(posBegin == 0 || (posBegin == lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length() && lastPosEnd != 0)){

    				}else{

    					Log.d("CommonUtils------here in else!");

    					String otherStr = "";
						if(lastPosEnd == 0){
							otherStr = noteContent.substring(lastPosEnd, posBegin);
						}else{
							otherStr = noteContent.substring(lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length(), posBegin);
						}
						buffer.append(otherStr.substring(otherStr.indexOf(NoteMediaManager.TAG_LARGE_STRING) + NoteMediaManager.TAG_LARGE_STRING.length()));
						
    				}
    			}else{
    				if(noteContent.indexOf(NoteMediaManager.TAG_STORE_SUFFIX, lastPosEnd) == lastPosEnd){
    					buffer.append(noteContent.substring(lastPosEnd + NoteMediaManager.TAG_STORE_SUFFIX.length()));
					}else{
						buffer.append(noteContent.substring(lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length()));
					}
    				return buffer.toString();
    			}
    			lastPosEnd = posEnd;
				Log.d("CommonUtils------lastPosEnd: " + posEnd);
				posBegin = noteContent.indexOf(NoteMediaManager.TAG_PREFIX, lastPosEnd);
				Log.d("CommonUtils------posBegin: " + posBegin);
    		}while(posBegin != -1);
    	}

    	// 
    	Log.d("CommonUtils------lastPosEnd: " + lastPosEnd + ", text.length(): " + (noteContent.length()));
    	if(lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length() < noteContent.length()){
    		String temp = noteContent.substring(lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length());
    		buffer.append(temp.substring(temp.indexOf(NoteMediaManager.TAG_LARGE_STRING) + NoteMediaManager.TAG_LARGE_STRING.length()));
    	}

    	Log.d("CommonUtils------noteContentPreDeal end!");
    	return buffer.toString();
    }
	//Gionee liuliang 2014-7-1 add for CR01296371 end
    public static int dip2px(Context context, float dpValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
    	Log.v("CommonUtils---dip2px--- scale == " + scale);
        return (int) (dpValue * scale + 0.5f);

}
    
	public static void setTextForTextView(Context context, String text,
			TextView showNoteContent, Note note) {
		Log.d("CommonUtils------setTextForTextView, text: " + text);
		DBOperations dbo = DBOperations.getInstances(context);
		if (showNoteContent == null) {
		    return;
		}
		if (!text.contains(NoteMediaManager.TAG_PREFIX)
		        || !text.contains(NoteMediaManager.TAG_PARSE_SUFFIX)) {
		    // not contains media info
		    showNoteContent.setText(text);
		} else {
		    // contains media info

		    // clear noteTextView
		    showNoteContent.setText("");

		    // Gionee <lilg><2013-04-08> add for CR00794516 begin
//		    text = text.replaceAll("\n", "");
		    // Gionee <lilg><2013-04-08> add for CR00794516 end

		    int lastPosEnd = 0;
		    int posBegin = text.indexOf(NoteMediaManager.TAG_PREFIX,
		            lastPosEnd);
		    Log.d("CommonUtils------posBegin: " + posBegin);
		    while (posBegin != -1) {
		        int posEnd = text.indexOf(
		                NoteMediaManager.TAG_PARSE_SUFFIX, posBegin);
		        Log.d("CommonUtils------posEnd: " + posEnd);
		        if (posEnd != -1) {

		            String[] mediaInfoArray = text
		            .substring(
		                    posBegin
		                    + NoteMediaManager.TAG_PREFIX
		                    .length(),
		                    posEnd).split(
		                            NoteMediaManager.TAG_MIDDLE);
		            if (mediaInfoArray != null) {
		                Log.d("CommonUtils------mediaInfoType: "
		                        + mediaInfoArray[0] + ", mediaInfoId: "
		                        + mediaInfoArray[1]
		                                         + ", mediaInfoMinute: "
		                                         + mediaInfoArray[2]
		                                                          + ", mediaInfoSecond: "
		                                                          + mediaInfoArray[3]);
		                try {
		                    Note tmpNote = dbo.queryOneNote(context,
		                            Integer.parseInt(note.getId()));
		                    if (tmpNote != null) {
		                        Log.d("CommonUtils------mediaFolderName: "
		                                + tmpNote.getMediaFolderName());

		                        Log.d("CommonUtils------lastPosEnd + NoteMediaManager.TAG_SUFFIX.length(): "
		                                + lastPosEnd
		                                + NoteMediaManager.TAG_PARSE_SUFFIX
		                                .length());
		                        // Gionee <lilg><2013-06-15> modify for CR00811864 begin
		                        if (posBegin == 0
		                                || (posBegin == lastPosEnd
		                                        + NoteMediaManager.TAG_STORE_SUFFIX
		                                        .length() && lastPosEnd != 0)) {
		                            // Gionee <lilg><2013-06-15> modify for CR00811864 end
		                            // insert midia info as a drawable
		                            // into edit text
		                            Log.d("CommonUtils------here in if!");

		                            Bitmap mediaRecordBitmap = initMediaRecordBitmap(
		                                    context,
		                                    Integer.parseInt(mediaInfoArray[2]),
		                                    Integer.parseInt(mediaInfoArray[3]),
		                                    mediaInfoArray[1]
		                                                   .substring(
		                                                           0,
		                                                           mediaInfoArray[1]
		                                                                          .indexOf(".")),
		                                                                          showNoteContent);
		                            insertMediaRecordIntoTextView(
		                                    context,
		                                    mediaInfoArray[1],
		                                    mediaRecordBitmap,
		                                    Integer.parseInt(mediaInfoArray[2]),
		                                    Integer.parseInt(mediaInfoArray[3]),
		                                    showNoteContent);

		                        } else {

		                            Log.d("CommonUtils------here in else!");

		                            String otherStr = "";
		                            if (lastPosEnd == 0) {
		                                otherStr = text.substring(
		                                        lastPosEnd, posBegin);
		                            } else {
		                                otherStr = text
		                                .substring(
		                                        lastPosEnd
		                                        + NoteMediaManager.TAG_PARSE_SUFFIX
		                                        .length(),
		                                        posBegin);
		                            }

		                            String tempStr = otherStr
		                            .substring(otherStr
		                                    .indexOf(NoteMediaManager.TAG_LARGE_STRING)
		                                    + NoteMediaManager.TAG_LARGE_STRING
		                                    .length());
		                            showNoteContent.append(tempStr);
		                            // Gionee <lilg><2013-05-11> add for CR00809190 begin
	                                //Gionee <wangpan><2014-03-28> delete for CR01147125 begin
//		                            showNoteContent.append("\n");
	                                //Gionee <wangpan><2014-03-28> delete for CR01147125 end
		                            // Gionee <lilg><2013-05-11> add for CR00809190 end
		                            // insert midia info as a drawable
		                            // into edit text

		                            Bitmap mediaRecordBitmap = initMediaRecordBitmap(
		                                    context,
		                                    Integer.parseInt(mediaInfoArray[2]),
		                                    Integer.parseInt(mediaInfoArray[3]),
		                                    mediaInfoArray[1]
		                                                   .substring(
		                                                           0,
		                                                           mediaInfoArray[1]
		                                                                          .indexOf(".")),
		                                                                          showNoteContent);
		                            insertMediaRecordIntoTextView(
		                                    context,
		                                    mediaInfoArray[1],
		                                    mediaRecordBitmap,
		                                    Integer.parseInt(mediaInfoArray[2]),
		                                    Integer.parseInt(mediaInfoArray[3]),
		                                    showNoteContent);

		                        }
		                        // Gionee <lilg><2013-06-15> modify for CR00811864 begin
                                //Gionee <wangpan><2014-03-28> delete for CR01147125 begin
//		                        showNoteContent.append("\n");
                                //Gionee <wangpan><2014-03-28> delete for CR01147125 end
		                        // Gionee <lilg><2013-06-15> modify for CR00811864 end
		                    }
		                } catch (Exception e) {
		                    Log.e("CommonUtils------setTextForTextView exception!",
		                            e);
		                }
		            }
		        } else {
		            if (text.indexOf(NoteMediaManager.TAG_STORE_SUFFIX,
		                    lastPosEnd) == lastPosEnd) {
		                showNoteContent
		                .append(text
		                        .substring(lastPosEnd
		                                + NoteMediaManager.TAG_STORE_SUFFIX
		                                .length()));
		            } else {
		                showNoteContent
		                .append(text
		                        .substring(lastPosEnd
		                                + NoteMediaManager.TAG_PARSE_SUFFIX
		                                .length()));
		            }
		            return;
		        }
		        lastPosEnd = posEnd;
		        Log.d("CommonUtils------lastPosEnd: " + posEnd);
		        posBegin = text.indexOf(NoteMediaManager.TAG_PREFIX,
		                lastPosEnd);
		        Log.d("CommonUtils------posBegin: " + posBegin);
		    };
		    //Gionee <wangpan><2014-03-28> add for CR01147125 begin
		    showNoteContent.append("\n");
            //Gionee <wangpan><2014-03-28> add for CR01147125 end
		    //
		    Log.d("CommonUtils------lastPosEnd: " + lastPosEnd
		            + ", text.length(): " + (text.length()));
		    if (lastPosEnd + NoteMediaManager.TAG_PARSE_SUFFIX.length() < text
		            .length()) {
		        String temp = text.substring(lastPosEnd
		                + NoteMediaManager.TAG_PARSE_SUFFIX.length());
		        showNoteContent.append(temp.substring(temp
		                .indexOf(NoteMediaManager.TAG_LARGE_STRING)
		                + NoteMediaManager.TAG_LARGE_STRING.length()));
		    }
		}
	}
    
		private static Bitmap initMediaRecordBitmap(Context context,int mMediaMinute, int mMediaSecond,
				String recordFileName,TextView showNoteContent) {

			// record info view
			View voiceView = LayoutInflater.from(context).inflate(
					R.layout.note_voice_item, null);
			RelativeLayout linearLayout = (RelativeLayout) voiceView
					.findViewById(R.id.note_voice_label);
			
			// Gionee <lilg><2013-05-11> modify for CR00802473 begin
//			int picWid = com.gionee.note.content.Session.screenWight - paddingLeft - paddingRight;
		    // Gionee <wangpan><2014-05-15> modify for CR01249465 begin
			int picWid = com.gionee.note.content.Session.getScreenWight() - showNoteContent.getPaddingLeft()*2 - showNoteContent.getPaddingRight() * 2;
		    // Gionee <wangpan><2014-05-15> modify for CR01249465 end
			linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(picWid, LayoutParams.MATCH_PARENT));
			// Gionee <lilg><2013-05-11> modify for CR00802473 end

			TextView textViewTimeInfo = (TextView) linearLayout
					.findViewById(R.id.voice_text);
			textViewTimeInfo.setText(getDisplayTime(mMediaMinute, mMediaSecond));
			textViewTimeInfo.setTextColor(context.getResources().getColor(
					R.color.note_media_recorder_text_corlor));
			TextView textViewNameInfo = (TextView) linearLayout
					.findViewById(R.id.voice_name);
			textViewNameInfo.setText(recordFileName);
			// view to bitmap
			linearLayout.setDrawingCacheEnabled(true);
			linearLayout.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

			linearLayout.layout(0, 0, picWid, linearLayout.getMeasuredHeight());

			linearLayout.buildDrawingCache();
			Bitmap cacheBitmap = linearLayout.getDrawingCache();

			if (cacheBitmap != null) {
				Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
				linearLayout.setDrawingCacheEnabled(false);
				if (bitmap != null) {
					return bitmap;
				} else {
					Log.e("NoteAcitivy------bitmap == null!");
				}
			} else {
				Log.e("NoteAcitivy------cacheBitmap == null!");
			}

			return null;
		}
		
		private static String getDisplayTime(int mMediaMinute, int mMediaSecond) {
			return (mMediaMinute < 10 ? "0" + mMediaMinute : mMediaMinute) + ":"
					+ (mMediaSecond < 10 ? "0" + mMediaSecond : mMediaSecond);
		}
		
		private static void insertMediaRecordIntoTextView(Context context,String recordFileName, Bitmap mediaRecordBitmap, int mMediaMinute, int mMediaSecond,TextView showNoteContent){

			// create current media into edit text
			String mediaType = NoteMediaManager.TYPE_MEDIA_RECORD;
			String recordNameTag = NoteMediaManager.TAG_PREFIX + mediaType + NoteMediaManager.TAG_MIDDLE + recordFileName + NoteMediaManager.TAG_MIDDLE + getDisplayTime(mMediaMinute, mMediaSecond) + NoteMediaManager.TAG_STORE_SUFFIX;

			final SpannableString spannableString = new SpannableString(recordNameTag);
			ImageSpan imageSpan = new ImageSpan(context, mediaRecordBitmap);
			spannableString.setSpan(imageSpan, 0, recordNameTag.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

			showNoteContent.append(spannableString);

			Log.d("NoteAcitivy------recordNameTag: " + recordNameTag);
		}
	
		// Gionee <lilg><2013-04-18> add for CR00795054 begin
		public static int getNoteCount(Note note) {
			Log.i("CommonUtils------getNoteCount!");

			if (note == null) {
				return -1;
			}

			int noteCount = 0;
			if (CommonUtils.STR_YES.equals(note.getIsFolder())) {
				// is folder
				noteCount = note.getHaveNoteCount();
			} else if (CommonUtils.STR_NO.equals(note.getIsFolder())) {
				// is note
				noteCount = 1;
			} else {
				Log.e("CommonUtils------note.getIsFolder(): " + note.getIsFolder());
			}

			return noteCount;
		}

		public static int getNoteCount(List<Note> noteList) {
			Log.i("CommonUtils------getNoteCount!");

			if (noteList == null || noteList.size() <= 0) {
				return -1;
			}

			int noteCount = 0;
			for (Note note : noteList) {

				if (CommonUtils.STR_YES.equals(note.getIsFolder())) {
					// is folder
					noteCount += note.getHaveNoteCount();
				} else if (CommonUtils.STR_NO.equals(note.getIsFolder())) {
					// is note
					noteCount += 1;
				} else {
					Log.e("CommonUtils------note.getIsFolder(): " + note.getIsFolder());
				}
			}

			return noteCount;
		}
		// Gionee <lilg><2013-04-18> add for CR00795054 end
		
	    // Gionee <pengwei><2013-08-26> add for CR00873485 begin
	    public static List<String> getMediasFromNoteContent(String noteContent){
	    	Log.i("CommonUtils------getMediasFromNoteContent begin!");
	    	if(TextUtils.isEmpty(noteContent)){
	    		Log.e("CommonUtils------noteContent is: " + noteContent);
	    		return null;
	    	}
	    	if(!noteContent.contains(NoteMediaManager.TAG_PREFIX) || !noteContent.contains(NoteMediaManager.TAG_PARSE_SUFFIX)){
	    		return null;
	    	}

	    	List<String> mediaInfoList = new ArrayList<String>();
	    	
	    	noteContent = noteContent.replaceAll("\n", "");
	    	int lastPosEnd = 0;
	    	int posBegin = noteContent.indexOf(NoteMediaManager.TAG_PREFIX, lastPosEnd);
	    	Log.d("CommonUtils------posBegin: " + posBegin);
	    	if(posBegin != -1){
	    		do{
	    			int posEnd = noteContent.indexOf(NoteMediaManager.TAG_PARSE_SUFFIX, posBegin);
	    			Log.d("CommonUtils------posEnd: " + posEnd);
	    			if(posEnd != -1){

	    				String mediaInfo = noteContent.substring(posBegin + NoteMediaManager.TAG_PREFIX.length(), posEnd);
	    				mediaInfoList.add(mediaInfo);
	    				
	    			}else{
	    				return null;
	    			}
	    			lastPosEnd = posEnd;
					Log.d("CommonUtils------lastPosEnd: " + posEnd);
					posBegin = noteContent.indexOf(NoteMediaManager.TAG_PREFIX, lastPosEnd);
					Log.d("CommonUtils------posBegin: " + posBegin);
	    		}while(posBegin != -1);
	    	}
	    	
	    	Log.i("CommonUtils------getMediasFromNoteContent end!");
	    	return mediaInfoList;
	    }
	    
	    private static final String SAVE_NOTE_ID = "SaveNoteID";
	    public static final String NOTE_ID = "NoteID";
	
		
	    // Gionee <pengwei><2013-08-26> add for CR00873485 end
	    
		public static Uri getUri(){
			return AmigoSettings.getUriFor(AmigoSettings.GUEST_MODE);
		}
		
		public static boolean getIsGuestMode(ContentResolver contentResolver){
			return AmigoSettings.getInt(contentResolver,
					AmigoSettings.GUEST_MODE, GUEST_MODE_DEFAULT) == 1;
		}

        //Gionee <wangpan><2014-05-21> add for CR01268739 begin
		public static String getTitleString(String title) {
	        if (title == null) {
	            return "";
	        }
	        if (title.length() <= 7) {
	            return title;
	        } else {
	            return title.substring(0, 7) + "...";
	        }
	    }
        //Gionee <wangpan><2014-05-21> add for CR01268739 end
        //tangzz add for inputmethod begin CR01442825 
        private static final int INPUT_SHOW_DELAY_TIME=150; 
        public static void showInputMethod(final AmigoEditText editText){ 
            new Handler().postDelayed(new Runnable(){ 
                    public void run() { 
                        showKeyboard(editText); 
                    } 
                }, INPUT_SHOW_DELAY_TIME); 
            } 
        private static void showKeyboard(AmigoEditText editText) { 
            Log.d("showKeyboard begin!"); 
            if(editText==null){ 
                Log.e("showKeyboard editText is null!"); 
                return; 
            } 
            editText.setFocusable(true); 
            editText.setFocusableInTouchMode(true); 
            editText.requestFocus(); 
            InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
            inputManager.showSoftInput(editText, 0); 
        } 
        //tangzz add for inputmethod end CR01442825

	}
