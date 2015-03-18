package com.gionee.note.content;

import android.view.Menu;

public interface Constants {

	static final String SETTINGS = "user_configurations";


	static final String IS_FOLDER="yes";
	static final String NO_FOLDER="no";

	static final String PARENT_FILE_ROOT="no";
	static final String START_FOLDER_ACTIVITY_ACTION="com.gionee.note.start.folder.activity";

	static final String MEDIA_FOLDER_NAME="-1";

	static final int MEDIA_UNDELETE=0;
	static final int MEDIA_DELETED=1;

	static final int NOTE_NO_MEDIA=0;


	//gn lilg 2013-02-21 add for media begin
	static final String TYPE_MEDIA_BUTTON = "8";
	//gn lilg 2013-02-21 add for media end
	
	static final String IS_IN_FOLDER="isInFolder";
	static final int DIALOG_DELETE_NOTEPAD_LIST = 1;
	static final String HOME_SHOW_VIEW_MODE="homeViewMode";

	// gn lilg 20120719 start
	// export group check box state

	// gn lilg 20121106 modify for CR00725217 start
	static final char CONTENT_SPLIT = '\u00AB';
	static final char ENTER_REPLACE = '\u00BB';
	// gn lilg 20121106 modify for CR00725217 end

	static final String STR_ENTER = "\n";

	// Gionee <lilg><2013-05-22> modify for CR00809680 begin
//	static final String PATH_SDCARD = "/mnt/sdcard/";
//	static final String PATH_SDCARD2 = "/mnt/sdcard2/";
	
	// Gionee <lilg><2013-05-22> modify for CR00809680 end
	static final String CHARSET_UTF8 = "utf-8";
	// gn lilg 20120719 end
	
	// Gionee <lilg><2013-03-28> add begin
	// Gionee <lilg><2013-05-22> modify for CR00809680 begin
//	static final String PATH_SD_CARD = "/mnt/sdcard";
//	static final String PATH_SD_CARD2 = "/mnt/sdcard2";
	
	// Gionee <lilg><2013-05-22> modify for CR00809680 end
	static final String FILE_PATH_BACK_UP = "/备份/便签";
	static final String FILE_PATH_NOTE_MEDIA = "/备份/便签多媒体";
	// Gionee <lilg><2013-03-28> add end

	static final String INIT_ALARM_TIME="0";

	public static final String KEY_REQUEST_FALG = "install_widget_flagId";
	public static final String KEY_REQUEST_PACKAGE_NAME = "intall_widget_packageName";
	public static final String KEY_REQUEST_CLASS_NAME = "intall_widget_className";

	public static final String KEY_RESULT_FALG = "install_widget_result_flag";
	public static final String KEY_RESULT_WIDGET_ID = "_install_widget_result_widget_id";
	public static final String KEY_RESULT_REQUEST_FLAG = "install_widget_request_flag";
	static final String PACKAGE_NAME = "com.gionee.note";
	static final String CLASS_NAME = "com.gionee.note.widget.NoteWidgetProvider_2x";
	static final String SEND_LUNCHER = "com.gionee.launcher.action.INSTALL_WIDGET";

	static final String SEND_INSTALL_WIDGET_RESULT = "com.gionee.launcher.send.INSTALL_WIDGET_RESULT";
	static final int FLAG_INSTALL_WIDGET_SUCCESS = 0;
	static final int FLAG_SCREEN_FULL = 1;
	static final int FLAG_BIND_WIDGET_FAIL = 2;
	static final int FLAG_OTHER = 3;
	static final String RECEIVER_WIDGET_REVEIVER_ACTION="com.gionee.launcher.send.INSTALL_WIDGET_RESULT";
	
	// Gionee <lilg><2013-04-18> modify for CR00795054 begin
	public static int SHARE_NOTE_MAX_COUNT = 10;
	// Gionee <lilg><2013-04-18> modify for CR00795054 end
}
