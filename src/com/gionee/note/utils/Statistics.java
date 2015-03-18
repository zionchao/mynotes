package com.gionee.note.utils;
import java.util.Map;

import amigo.app.AmigoActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import amigo.preference.AmigoPreferenceManager;

import com.gionee.note.ExportItemSelectActivity;
import com.gionee.note.content.Constants;
//import com.youju.statistics.YouJuAgent;

/*
 * Statistical information and to the server
 * 
 * @version 1.0
 * 
 * @author pengwei
 * 
 * @since 2012-11-26
 * */
//Gionee <pengwei><20130809> modify for CR00834587 begin
public class Statistics {

	public static final String MAIN_APP_SEARCH = "主应用-搜索";//Mainapplication-search
	public static final String MAIN_APP_NEW_FOLDER = "主应用-新建文件夹";//Mainapplication-create new folder
	public static final String MAIN_APP_OPERATIONS = "主应用-批量操作";//Mainapplication-batch operation
	public static final String MAIN_APP_OPERATION_DEL = "主应用-批量操作-删除";//Mainapplication-batch operation delete
	public static final String MAIN_APP_OPERATION_SHARE = "主应用-批量操作-分享";//Mainapplication-batch operation share
	public static final String MAIN_APP_OPERATION_MOVE = "主应用-批量操作-便签移动";//Mainapplication-batch operation move
	public static final String MAIN_APP_OPERATION_SWITCH = "主应用-缩略图模式列表模式切换";//Mainapplication-switching
	public static final String MAIN_APP_LONG_DEL = "主应用-长按-删除";//Mainapplication-long press delete
	public static final String MAIN_APP_LONG_SHARE = "主应用-长按-分享";//Mainapplication-long press delete
	public static final String MAIN_APP_LONG_MOVE_IN_FOLDER = "主应用-长按-便签移动-移入文件夹";//Mainapplication-long press move into folder
	public static final String MAIN_APP_LONG_MOVE_OUT_FOLDER = "主应用-长按-便签移动-移出文件夹";//Mainapplication-long press move out of folder
	public static final String MAIN_APP_EXPORT = "主应用-导入导出-导出";//Mainapplication-export
	public static final String MAIN_APP_IMPORT = "主应用-导入导出-导入";//Mainapplication-IMPORT
	public static final String MAIN_APP_FOLDER_SEARCH = "主应用-文件夹下-搜索";//Mainapplication-under folder search
	public static final String MAIN_APP_FOLDER_OPERATIONS = "主应用-文件夹下-批量操作";//Mainapplication-under folder batch operation
	public static final String MAIN_APP_FOLDER_OPERATIONS_DEL = "主应用-文件夹下-批量操作-删除";//Mainapplication-under folder batch operation delete
	public static final String MAIN_APP_FOLDER_OPERATIONS_SHARE = "主应用-文件夹下-批量操作-分享";//Mainapplication-under folder batch operation share
	public static final String MAIN_APP_FOLDER_OPERATIONS_MOVE = "主应用-文件夹下-批量操作-移动";//Mainapplication-under folder batch operation move
	public static final String MAIN_APP_FOLDER_OPERATION_SWITCH = "主应用-文件夹下-缩略图模式与列表模式切换";//Mainapplication-under folder batch operation switch
	public static final String MAIN_APP_FOLDER_EXPORT = "主应用-文件夹下-导入导出-导出";//Mainapplication-under folder batch operation export
	public static final String MAIN_APP_FOLDER_IMPORT = "主应用-文件夹下-导入导出-导入";//Mainapplication-under folder batch operation import
	public static final String ABOUT = "关于";//about
	public static final String FOLDER_INPUT_TITLE = "输入标题-文件夹";//To the folder input title
	public static final String NOTE_INPUT_TITLE = "输入标题-便签";//To the note input title
	public static final String NOTE_ADDNOTE = "便签详情-添加便签";//add note
	public static final String NOTE_DELNOTE = "便签详情-删除便签";//delete note
	public static final String NOTE_EDIT = "便签详情-编辑";//edit note
	public static final String NOTE_ALARM = "便签详情-闹钟";//note reminds
	public static final String NOTE_POSITIONING = "便签详情-定位";//note positioning
	public static final String NOTE_CHANGE_BACKGROUND = "便签详情-更改背景";//note change background
	public static final String NOTE_SENT_TO_DESKTOP = "便签详情-发送到桌面";//note sent to desktop
	public static final String NOTE_SHARE = "便签详情-分享";//note share
	public static final String NOTE_FULL_SCREEN = "DJ";//full screen
	public static final String NOTE_ALARM_LOOK = "便签闹钟提示-查看闹钟";
	public static final String NOTE_ALARM_CLOSE = "便签闹钟提示-关闭闹钟";
	public static final String NOTE_ALARM_DEL = "便签闹钟提示-删除闹钟";
	public static final String MODE_THUMBNAIL = "便签查看模式-缩略图模式";
	public static final String MODE_LIST = "便签查看模式-列表模式";
	public static final String NOTE_BACKGROUND_A = "便签详情-更改背景-背景1";//note background information
	public static final String NOTE_BACKGROUND_B = "便签详情-更改背景-背景2";//note background information
	public static final String NOTE_BACKGROUND_C = "便签详情-更改背景-背景3";//note background information
	public static final String NOTE_BACKGROUND_D = "便签详情-更改背景-背景4";//note background information
	public static final String NOTE_CLICK_ATTACHMENT= "便签详情-点击附件";
	public static final String NOTE_ADD_RECORD = "便签详情-添加录音";
	public static final String NOTE_PLAY_RECORD = "便签详情-播放录音";
	public static final String NOTE_DEL_RECORD = "便签详情-删除录音";
	public static int isFold = 0;//Whether in the folder��0 is not in the folder
	// gn pengwei 20121126 add for statistics end
	
	public static String versionName = null;
	public static String packageName = null;

	/*
	 * Get VersionName
	 * 
	 * @param context Context environment of Activity
	 * 
	 * @return void
	 * 
	 * @throws NameNotFoundException
	 */
	public static void getInfos(Context context) {
		try {
			PackageManager pmManager = context.getPackageManager();
			PackageInfo pinfo;
			pinfo = pmManager.getPackageInfo(context.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			versionName = pinfo.versionName;
			packageName = pinfo.packageName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("Statistics---getInfos---error", e);
		}
	}
	
    // call when oncreate()
    public static void setAssociateUserImprovementPlan(Context content,
            boolean isAssociate) {
		Log.v("Statistics---setAssociateUserImprovementPlan---");
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 begin
        // YouJuAgent.setAssociateUserImprovementPlan(content, isAssociate);
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 end
    }

    // call when oncreate()
    public static void setReportCaughtExceptions(Boolean enabled) {
		Log.v("Statistics---setReportCaughtExceptions---enabled == " + enabled);
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 begin
        // YouJuAgent.setReportUncaughtExceptions(enabled);
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 end
    }

    public static void onResume(final Context con) {
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 begin
        /*new Thread() {
            public void run() {
        		Log.v("Statistics---onResume---");
                YouJuAgent.onResume(con);
            }
        }.start();*/
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 end
    }

    public static void onPause(final Context con) {
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 begin
        /*new Thread() {
            public void run() {
        		Log.v("Statistics---onPause---");
                YouJuAgent.onPause(con);
            }
        }.start();*/
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 end
    }

    public static void onEvent(final AmigoActivity activity, final String EVENT_ID) {
        Log.i("Statistics---onEvent1 == " + EVENT_ID);
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 begin
        /*new Thread() {
            public void run() {
                YouJuAgent.onEvent(activity, EVENT_ID);
            }
        }.start();*/
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 end
    }

    public static void onEvent(final AmigoActivity activity, final String EVENT_ID,
            final String EVENT_LABEL) {
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 begin
        /*new Thread() {
            public void run() {
                Log.i("Statistics---onEvent2 == " + EVENT_ID + " EVENT_LABEL == " + EVENT_LABEL);
                YouJuAgent.onEvent(activity, EVENT_ID, EVENT_LABEL);
            }
        }.start();*/
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 end
    }

    public static void onEvent(final AmigoActivity activity, final String EVENT_ID,
            final String EVENT_LABEL, final Map<String, Object> map) {
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 begin
        /*new Thread() {
            public void run() {
                Log.i("Statistics---onEvent3 == " + EVENT_ID + " EVENT_LABEL == " + EVENT_LABEL);
                YouJuAgent.onEvent(activity, EVENT_ID, EVENT_LABEL, map);
            }
        }.start();*/
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 end
    }

    public static void onError(final AmigoActivity activity,
            final Throwable throwable) {
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 begin
        /*new Thread() {
            public void run() {
                YouJuAgent.onError(activity, throwable);
            }
        }.start();*/
        // Gionee <wangpan> <2014-05-06> delete for CR01238211 end
    }
    
}
//Gionee <pengwei><20130809> modify for CR00834587 end