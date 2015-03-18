package com.gionee.note.content;

/*
 * Statistical Value
 * The singleton pattern
 * @version 1.0
 * @author pengwei
 * @since 2012-11-26
 * */
public class StatisticalValue {
	// value
	private int mainAppSearch = 0;// mainapplication-search
	private int mainAppNewFolder = 0;// mainapplication-create new folder
	private int mainAppOperations = 0;// mainapplication-batch operation
	private int mainAppOperationDel = 0;// mainapplication-batch operation
										// delete
	private int mainAppOperationShare = 0;// mainapplication-batch operation
											// share
	private int mainAppOperationMove = 0;// mainapplication-batch operation move
	private int mainAppOerationSwitch = 0;// mainapplication-switching
	private int mainApplongDel = 0;// mainapplication-long press delete
	private int mainApplongShare = 0;// mainapplication-long press delete
	private int mainApplongMoveInFolder = 0;// mainapplication-long press move
											// into folder
	private int mainApplongMoveOutofFolder = 0;// mainapplication-long press
												// move out of folder
	private int mainAppExport = 0;// mainapplication-export
	private int mainAppImport = 0;// mainapplication-import
	private int mainAppFolderSearch = 0;// mainapplication-under folder search
	private int mainAppFolderOperations = 0;// mainapplication-under folder
											// batch operation
	private int mainAppFolderOperationsDel = 0;// mainapplication-under folder
												// batch operation delete
	private int mainAppFolderOperationsShare = 0;// mainapplication-under folder
													// batch operation share
	private int mainAppFolderOperationsMove = 0;// mainapplication-under folder
												// batch operation move
	private int mainAppFolderOperationSwitch = 0;// mainapplication-under folder
													// batch operation switch
	private int mainAppFolderExport = 0;// mainapplication-under folder batch
										// operation export
	private int mainAppFolderImport = 0;// mainapplication-under folder batch
										// operation import
	private int about = 0;// about
	private int folderInputTitle = 0;// to the folder input title
	private int noteInputTitle = 0;// to the note input title
	private int noteAddnote = 0;// add note
	private int noteDelnote = 0;// delete note
	private int noteEdit = 0;// edit note
	private int noteAlarm = 0;// note reminds
	private int notePositioning = 0;// note positioning
	private int noteChangeBackground = 0;// note change background
	private int noteSentToDesktop = 0;// note sent to desktop
	private int noteShare = 0;// note share
	private int noteFullScreen = 0;
	private int noteAlarmLook = 0;
	private int noteAlarmClose = 0;
	private int noteAlarmDel = 0;
	private int modeThumbnail = 0;
	private int modeList = 0;
	private int noteBackgroundA = 0;// note background information
	private int noteBackgroundB = 0;// note background information
	private int noteBackgroundC = 0;// note background information
	private int noteBackgroundD = 0;// note background information

	private StatisticalValue() {

	}

	private static StatisticalValue single = null;

	public synchronized static StatisticalValue getInstance() {
		if (single == null) {
			single = new StatisticalValue();
		}
		return single;
	}

	public int getMainAppSearch() {
		return mainAppSearch;
	}

	public void setMainAppSearch(int mainAppSearch) {
		this.mainAppSearch = mainAppSearch;
	}

	public int getMainAppNewFolder() {
		return mainAppNewFolder;
	}

	public void setMainAppNewFolder(int mainAppNewFolder) {
		this.mainAppNewFolder = mainAppNewFolder;
	}

	public int getMainAppOperations() {
		return mainAppOperations;
	}

	public void setMainAppOperations(int mainAppOperations) {
		this.mainAppOperations = mainAppOperations;
	}

	public int getMainAppOperationDel() {
		return mainAppOperationDel;
	}

	public void setMainAppOperationDel(int mainAppOperationDel) {
		this.mainAppOperationDel = mainAppOperationDel;
	}

	public int getMainAppOperationShare() {
		return mainAppOperationShare;
	}

	public int getNoteAlarmLook() {
		return noteAlarmLook;
	}

	public void setNoteAlarmLook(int noteAlarmLook) {
		this.noteAlarmLook = noteAlarmLook;
	}

	public int getNoteAlarmClose() {
		return noteAlarmClose;
	}

	public void setNoteAlarmClose(int noteAlarmClose) {
		this.noteAlarmClose = noteAlarmClose;
	}

	public int getNoteAlarmDel() {
		return noteAlarmDel;
	}

	public void setNoteAlarmDel(int noteAlarmDel) {
		this.noteAlarmDel = noteAlarmDel;
	}

	public void setMainAppOperationShare(int mainAppOperationShare) {
		this.mainAppOperationShare = mainAppOperationShare;
	}

	public int getMainAppOperationMove() {
		return mainAppOperationMove;
	}

	public void setMainAppOperationMove(int mainAppOperationMove) {
		this.mainAppOperationMove = mainAppOperationMove;
	}

	public int getMainAppOerationSwitch() {
		return mainAppOerationSwitch;
	}

	public void setMainAppOerationSwitch(int mainAppOerationSwitch) {
		this.mainAppOerationSwitch = mainAppOerationSwitch;
	}

	public int getMainApplongDel() {
		return mainApplongDel;
	}

	public void setMainApplongDel(int mainApplongDel) {
		this.mainApplongDel = mainApplongDel;
	}

	public int getMainApplongShare() {
		return mainApplongShare;
	}

	public void setMainApplongShare(int mainApplongShare) {
		this.mainApplongShare = mainApplongShare;
	}

	public int getMainApplongMoveInFolder() {
		return mainApplongMoveInFolder;
	}

	public void setMainApplongMoveInFolder(int mainApplongMoveInFolder) {
		this.mainApplongMoveInFolder = mainApplongMoveInFolder;
	}

	public int getMainApplongMoveOutofFolder() {
		return mainApplongMoveOutofFolder;
	}

	public void setMainApplongMoveOutofFolder(int mainApplongMoveOutofFolder) {
		this.mainApplongMoveOutofFolder = mainApplongMoveOutofFolder;
	}

	public int getMainAppExport() {
		return mainAppExport;
	}

	public void setMainAppExport(int mainAppExport) {
		this.mainAppExport = mainAppExport;
	}

	public int getMainAppImport() {
		return mainAppImport;
	}

	public void setMainAppImport(int mainAppImport) {
		this.mainAppImport = mainAppImport;
	}

	public int getMainAppFolderSearch() {
		return mainAppFolderSearch;
	}

	public void setMainAppFolderSearch(int mainAppFolderSearch) {
		this.mainAppFolderSearch = mainAppFolderSearch;
	}

	public int getMainAppFolderOperations() {
		return mainAppFolderOperations;
	}

	public void setMainAppFolderOperations(int mainAppFolderOperations) {
		this.mainAppFolderOperations = mainAppFolderOperations;
	}

	public int getMainAppFolderOperationsDel() {
		return mainAppFolderOperationsDel;
	}

	public void setMainAppFolderOperationsDel(int mainAppFolderOperationsDel) {
		this.mainAppFolderOperationsDel = mainAppFolderOperationsDel;
	}

	public int getMainAppFolderOperationsShare() {
		return mainAppFolderOperationsShare;
	}

	public void setMainAppFolderOperationsShare(int mainAppFolderOperationsShare) {
		this.mainAppFolderOperationsShare = mainAppFolderOperationsShare;
	}

	public int getMainAppFolderOperationsMove() {
		return mainAppFolderOperationsMove;
	}

	public void setMainAppFolderOperationsMove(int mainAppFolderOperationsMove) {
		this.mainAppFolderOperationsMove = mainAppFolderOperationsMove;
	}

	public int getMainAppFolderOperationSwitch() {
		return mainAppFolderOperationSwitch;
	}

	public void setMainAppFolderOperationSwitch(int mainAppFolderOperationSwitch) {
		this.mainAppFolderOperationSwitch = mainAppFolderOperationSwitch;
	}

	public int getMainAppFolderExport() {
		return mainAppFolderExport;
	}

	public void setMainAppFolderExport(int mainAppFolderExport) {
		this.mainAppFolderExport = mainAppFolderExport;
	}

	public int getMainAppFolderImport() {
		return mainAppFolderImport;
	}

	public void setMainAppFolderImport(int mainAppFolderImport) {
		this.mainAppFolderImport = mainAppFolderImport;
	}

	public int getAbout() {
		return about;
	}

	public void setAbout(int about) {
		this.about = about;
	}

	public int getFolderInputTitle() {
		return folderInputTitle;
	}

	public void setFolderInputTitle(int folderInputTitle) {
		this.folderInputTitle = folderInputTitle;
	}

	public int getNoteInputTitle() {
		return noteInputTitle;
	}

	public void setNoteInputTitle(int noteInputTitle) {
		this.noteInputTitle = noteInputTitle;
	}

	public int getNoteAddnote() {
		return noteAddnote;
	}

	public void setNoteAddnote(int noteAddnote) {
		this.noteAddnote = noteAddnote;
	}

	public int getNoteDelnote() {
		return noteDelnote;
	}

	public void setNoteDelnote(int noteDelnote) {
		this.noteDelnote = noteDelnote;
	}

	public int getNoteEdit() {
		return noteEdit;
	}

	public void setNoteEdit(int noteEdit) {
		this.noteEdit = noteEdit;
	}

	public int getNoteAlarm() {
		return noteAlarm;
	}

	public void setNoteAlarm(int noteAlarm) {
		this.noteAlarm = noteAlarm;
	}

	public int getNotePositioning() {
		return notePositioning;
	}

	public void setNotePositioning(int notePositioning) {
		this.notePositioning = notePositioning;
	}

	public int getNoteChangeBackground() {
		return noteChangeBackground;
	}

	public void setNoteChangeBackground(int noteChangeBackground) {
		this.noteChangeBackground = noteChangeBackground;
	}

	public int getNoteSentToDesktop() {
		return noteSentToDesktop;
	}

	public void setNoteSentToDesktop(int noteSentToDesktop) {
		this.noteSentToDesktop = noteSentToDesktop;
	}

	public int getNoteShare() {
		return noteShare;
	}

	public void setNoteShare(int noteShare) {
		this.noteShare = noteShare;
	}

	public int getNoteFullScreen() {
		return noteFullScreen;
	}

	public void setNoteFullScreen(int noteFullScreen) {
		this.noteFullScreen = noteFullScreen;
	}

	public int getModeThumbnail() {
		return modeThumbnail;
	}

	public void setModeThumbnail(int modeThumbnail) {
		this.modeThumbnail = modeThumbnail;
	}

	public int getModeList() {
		return modeList;
	}

	public void setModeList(int modeList) {
		this.modeList = modeList;
	}

	public int getNoteBackgroundA() {
		return noteBackgroundA;
	}

	public void setNoteBackgroundA(int noteBackgroundA) {
		this.noteBackgroundA = noteBackgroundA;
	}

	public int getNoteBackgroundB() {
		return noteBackgroundB;
	}

	public void setNoteBackgroundB(int noteBackgroundB) {
		this.noteBackgroundB = noteBackgroundB;
	}

	public int getNoteBackgroundC() {
		return noteBackgroundC;
	}

	public void setNoteBackgroundC(int noteBackgroundC) {
		this.noteBackgroundC = noteBackgroundC;
	}

	public int getNoteBackgroundD() {
		return noteBackgroundD;
	}

	public void setNoteBackgroundD(int noteBackgroundD) {
		this.noteBackgroundD = noteBackgroundD;
	}

	// --------------------------------------------------------------------------
	public String getKeyMainAppSearch() {
		return StatisticalName.MAIN_APP_SEARCH;
	}

	public String getKeyMainAppNewFolder() {
		return StatisticalName.MAIN_APP_NEW_FOLDER;
	}

	public String getKeyMainAppOperations() {
		return StatisticalName.MAIN_APP_OPERATIONS;
	}

	public String getKeyMainAppOperationDel() {
		return StatisticalName.MAIN_APP_OPERATION_DEL;
	}

	public String getKeyMainAppOperationShare() {
		return StatisticalName.MAIN_APP_OPERATION_SHARE;
	}

	public String getKeyMainAppOperationMove() {
		return StatisticalName.MAIN_APP_OPERATION_MOVE;
	}

	public String getKeyMainAppOerationSwitch() {
		return StatisticalName.MAIN_APP_OPERATION_SWITCH;
	}

	public String getKeyMainApplongDel() {
		return StatisticalName.MAIN_APP_LONG_DEL;
	}

	public String getKeyMainApplongShare() {
		return StatisticalName.MAIN_APP_LONG_SHARE;
	}

	public String getKeyMainApplongMoveInFolder() {
		return StatisticalName.MAIN_APP_LONG_MOVE_IN_FOLDER;
	}

	public String getKeyMainApplongMoveOutofFolder() {
		return StatisticalName.MAIN_APP_LONG_MOVE_OUT_FOLDER;
	}

	public String getKeyMainAppExport() {
		return StatisticalName.MAIN_APP_EXPORT;
	}

	public String getKeyMainAppImport() {
		return StatisticalName.MAIN_APP_IMPORT;
	}

	public String getKeyMainAppFolderSearch() {
		return StatisticalName.MAIN_APP_FOLDER_SEARCH;
	}

	public String getKeyMainAppFolderOperations() {
		return StatisticalName.MAIN_APP_FOLDER_OPERATIONS;
	}

	public String getKeyMainAppFolderOperationsDel() {
		return StatisticalName.MAIN_APP_FOLDER_OPERATIONS_DEL;
	}

	public String getKeyMainAppFolderOperationsShare() {
		return StatisticalName.MAIN_APP_FOLDER_OPERATIONS_SHARE;
	}

	public String getKeyMainAppFolderOperationsMove() {
		return StatisticalName.MAIN_APP_FOLDER_OPERATIONS_MOVE;
	}

	public String getKeyMainAppFolderOperationSwitch() {
		return StatisticalName.MAIN_APP_FOLDER_OPERATION_SWITCH;
	}

	public String getKeyMainAppFolderExport() {
		return StatisticalName.MAIN_APP_FOLDER_EXPORT;
	}

	public String getKeyMainAppFolderImport() {
		return StatisticalName.MAIN_APP_FOLDER_IMPORT;
	}

	public String getKeyAbout() {
		return StatisticalName.ABOUT;
	}

	public String getKeyFolderInputTitle() {
		return StatisticalName.FOLDER_INPUT_TITLE;
	}

	public String getKeyNoteInputTitle() {
		return StatisticalName.NOTE_INPUT_TITLE;
	}

	public String getKeyNoteAddnote() {
		return StatisticalName.NOTE_ADDNOTE;
	}

	public String getKeyNoteDelnote() {
		return StatisticalName.NOTE_DELNOTE;
	}

	public String getKeyNoteEdit() {
		return StatisticalName.NOTE_EDIT;
	}

	public String getKeyNoteAlarm() {
		return StatisticalName.NOTE_ALARM;
	}

	public String getKeyNotePositioning() {
		return StatisticalName.NOTE_POSITIONING;
	}

	public String getKeyNoteChangeBackground() {
		return StatisticalName.NOTE_CHANGE_BACKGROUND;
	}

	public String getKeyNoteSentToDesktop() {
		return StatisticalName.NOTE_SENT_TO_DESKTOP;
	}

	public String getKeyNoteShare() {
		return StatisticalName.NOTE_SHARE;
	}

	public String getKeyNoteFullScreen() {
		return StatisticalName.NOTE_FULL_SCREEN;
	}

	public String getKeyModeThumbnail() {
		return StatisticalName.MODE_THUMBNAIL;
	}

	public String getKeyModeList() {
		return StatisticalName.MODE_LIST;
	}

	public String getKeyNoteBackgroundA() {
		return StatisticalName.NOTE_BACKGROUND_A;
	}

	public String getKeyNoteBackgroundB() {
		return StatisticalName.NOTE_BACKGROUND_B;
	}

	public String getKeyNoteBackgroundC() {
		return StatisticalName.NOTE_BACKGROUND_C;
	}

	public String getKeyNoteBackgroundD() {
		return StatisticalName.NOTE_BACKGROUND_D;
	}
	
	public String getKeyNoteAlarmLook() {
		return StatisticalName.NOTE_ALARM_LOOK;
	}


	public String getKeyNoteAlarmClose() {
		return StatisticalName.NOTE_ALARM_CLOSE;
	}



	public String getKeyNoteAlarmDel() {
		return StatisticalName.NOTE_ALARM_DEL;
	}

}
