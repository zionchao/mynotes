package com.gionee.note.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
//Gionee <wangpan> <2014-02-17> modify for CR01033300 begin
import android.os.*;
//Gionee <wangpan> <2014-02-17> modify for CR01033300 end
import android.os.Environment;

import com.gionee.note.HomeActivity;
import com.gionee.note.ImportExportActivity;
import com.gionee.note.R;
import com.gionee.note.content.Constants;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.ExportItem;
import com.gionee.note.domain.MediaInfo;
import com.gionee.note.domain.Note;
//Gionee <pengwei><2013-11-01> modify for CR00941779 begin
public class ImportExportUtils {

	// Singleton stuff
	private static ImportExportUtils sInstance;

	// gn lilg 2012-12-08 modify for optimization begin
	private static DBOperations dbo;
	// gn lilg 2012-12-08 modify for optimization end

	public static synchronized ImportExportUtils getInstance(Context context) {
		if (sInstance == null) {
			Log.i("sInstance == null");
			sInstance = new ImportExportUtils(context);
		}
		return sInstance;
	}


	// gn lilg 20121105 add for memory overflow start
	public static void close(){
		if(sInstance != null){
			sInstance = null;
		}
	}
	// gn lilg 20121105 add for memory  overflow end

	/**
	 * Following states are signs to represents backup or restore
	 * status
	 */
	// Currently, the sdcard is not mounted
	public static final int STATE_SD_CARD_UNMOUONTED           = 0;
	// The backup file not exist
	public static final int STATE_BACKUP_FILE_NOT_EXIST        = 1;
	// The data is not well formated, may be changed by other programs
	public static final int STATE_DATA_DESTROIED               = 2;
	// Some run-time exception which causes restore or backup fails
	public static final int STATE_SYSTEM_ERROR                 = 3;
	// Backup or restore success
	public static final int STATE_SUCCESS                      = 4;

	private TextExport mTextExport;
	private TextImport mTextImport;

	public static Date importExportTime;
	public static String exportFileName;
	
	// Gionee <lilg><2013-03-19> add for record the state of export or import begin
	private static boolean isExporting = false;
	public static boolean isImporting = false;
	// Gionee <lilg><2013-03-19> add for record the state of export or import end
	
	private ImportExportUtils(Context context) {
		mTextExport = new TextExport(context);
		mTextImport = new TextImport(context);
		dbo = DBOperations.getInstances(context);
	}

	public static void setExporting(boolean isExporting) {
        ImportExportUtils.isExporting = isExporting;
    }

    public static boolean isExporting() {
        return isExporting;
    }


    private static boolean externalStorageAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public int exportToText(File fileDir,String noteId, String parentId){
		return mTextExport.exportNoteToText(fileDir, noteId,parentId);
	}
	
	public int exportToText(File fileDir, Note note, ExportItem folder){
		return mTextExport.exportNoteToText(fileDir, note, folder);
	}

	public long importFromText(int importType, String path, String checkedFile){
		return mTextImport.importFromText(importType, path, checkedFile);
	}
	
	public void importNoteMedia(MediaInfo mediaInfo){
		mTextImport.importNoteMedia(mediaInfo);
	}

	public String getExportedTextFileName() {
		return mTextExport.mFileName;
	}

	public File getExportedTextFileDir(int exportType) {
		return mTextExport.checkForldAndFile(exportType);
	}

	public static String getExportFileName(Context context){

		importExportTime = new Date();
		exportFileName = new SimpleDateFormat(context.getString(R.string.format_date_ymdhms)).format(importExportTime);

		return exportFileName;
	}

	private static class TextExport {

		private Context mContext;
		private String mFileName;
		private String mFileDirectory;

		public TextExport(Context context) {
			mContext = context;
			mFileName = "";
			mFileDirectory = "";
		}
		
		public int exportNoteToText(File fileDir, Note note, ExportItem folder) {
			Log.i("ImportExportUtils------export note to text start!");
			
			if(note == null){
				return STATE_SYSTEM_ERROR;
			}
			if(fileDir == null){
				return STATE_SYSTEM_ERROR;
			}
			if (!externalStorageAvailable()) {
				Log.i("ImportExportUtils------Media was not mounted!");
				return STATE_SD_CARD_UNMOUONTED;
			}
			int id = -1;
			try{
				id = Integer.parseInt(note.getId());
			}catch(Exception e){
				Log.e("ImportExportUtils------get print stream error!");
				return STATE_SYSTEM_ERROR;
			}
			
			// Gionee <lilg><2013-03-11> add for export media info begin
			// init export path
			String fileName = String.valueOf(System.currentTimeMillis());
			File filePath = new File(fileDir.getPath() + "/" + fileName);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			// Gionee <lilg><2013-03-11> add for export media info end
			
			if(id != -1){
				// Gionee <lilg><2013-03-11> add for export media info begin
				PrintStream ps = getExportToTextPrintStream(filePath, fileName);
				// Gionee <lilg><2013-03-11> add for export media info end
				
				if (ps == null) {
					Log.e("ImportExportUtils------get print stream error!");
					return STATE_SYSTEM_ERROR;
				}
				
				if(note.getId() != null){
					ps.println(formatEnter((folder.getTitle() == null ? "" : folder.getTitle())) + Constants.CONTENT_SPLIT + formatEnter((note.getTitle() == null ? "" : note.getTitle())) + Constants.CONTENT_SPLIT + formatEnter(note.getContent()));
					ps.close();
					
					// Gionee <lilg><2013-03-11> add for export media info begin
					List<MediaInfo> mediaInfoList = dbo.queryMeidas(mContext, note.getId());
					if (mediaInfoList != null && mediaInfoList.size() > 0) {
						String mediaFileName = "";
						File mediaFile = null;
						for (MediaInfo mediaInfo : mediaInfoList) {
							mediaFileName = mediaInfo.getMediaFileName();
//							mediaFile = new File(mediaFileName);
							mediaFile = new File(mediaFileName.substring(1));
							if (mediaFile.exists()) {
								int res = FileUtils.copyMediaFile(mediaFileName.substring(1), filePath.getPath()	+ mediaFileName.substring(mediaFileName.lastIndexOf("/")));
								if (res == FileUtils.STATE_COPY_FILE_IOEXCEPTION) {
									return STATE_SYSTEM_ERROR;
								}
							}
						}
					}
					// Gionee <lilg><2013-03-11> add for export media info end
				}
			}
			
			Log.d("ImportExportUtils------export note to text end!");
			return STATE_SUCCESS;
		}

		public int exportNoteToText(File fileDir,String noteId, String parentId) {
			Log.i("exportNoteToText------export note to text start!");
			if(noteId == null || "".equals(noteId) || parentId == null || "".equals(parentId)){
				return STATE_SYSTEM_ERROR;
			}

			if(fileDir == null){
				return STATE_SYSTEM_ERROR;
			}

			if (!externalStorageAvailable()) {
				Log.i("exportNoteToText------Media was not mounted!");
				return STATE_SD_CARD_UNMOUONTED;
			}

			int id = 0;
			try{
				id = Integer.parseInt(noteId);
			}catch(Exception e){
				Log.e("exportNoteToText------get print stream error!");
				return STATE_SYSTEM_ERROR;
			}

			// Gionee <lilg><2013-03-12> add for export media info begin
			// init export path
			String fileName = String.valueOf(System.currentTimeMillis());
			File filePath = new File(fileDir.getPath() + "/" + fileName);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}

			PrintStream ps = getExportToTextPrintStream(filePath, fileName);
			// Gionee <lilg><2013-03-12> add for export media info end
			if (ps == null) {
				Log.e("exportNoteToText------get print stream error!");
				return STATE_SYSTEM_ERROR;
			}

			if(Constants.NO_FOLDER.equals(parentId)){
				// root note
				Note note = dbo.queryOneNote(mContext, id);
				if(note.getId() != null){
					ps.println(formatEnter((note.getTitle() == null ? "" : note.getTitle())) + formatEnter(Constants.CONTENT_SPLIT + note.getContent()));
					ps.close();
					
					// Gionee <lilg><2013-03-12> add for export media info begin
					List<MediaInfo> mediaInfoList = dbo.queryMeidas(mContext, note.getId());
					if (mediaInfoList != null && mediaInfoList.size() > 0) {
						String mediaFileName = "";
						File mediaFile = null;
						for (MediaInfo mediaInfo : mediaInfoList) {
							mediaFileName = mediaInfo.getMediaFileName();
//							mediaFile = new File(mediaFileName);
							mediaFile = new File(mediaFileName.substring(1));
							if (mediaFile.exists()) {
								int res = FileUtils.copyMediaFile(mediaFileName.substring(1), filePath.getPath() + mediaFileName.substring(mediaFileName.lastIndexOf("/")));
								if (res == FileUtils.STATE_COPY_FILE_IOEXCEPTION) {
									return STATE_SYSTEM_ERROR;
								}
							}
						}
					}
					// Gionee <lilg><2013-03-12> add for export media info end
				}
			}else{
				// note in folder
				int pid = 0;
				try{
					pid = Integer.parseInt(parentId);
				}catch(Exception e){
					Log.e("exportNoteToText------e.getMessage(): "+ e.getMessage());
					return STATE_SYSTEM_ERROR;
				}
				Note note = dbo.queryOneNote(mContext, id);
				if(note.getId() != null){
					Note folder = dbo.queryOneNote(mContext, pid);
					ps.println(formatEnter((folder.getTitle() == null ? "" : folder.getTitle())) + Constants.CONTENT_SPLIT + formatEnter((note.getTitle() == null ? "" : note.getTitle())) + Constants.CONTENT_SPLIT + formatEnter(note.getContent()));
					ps.close();

					// Gionee <lilg><2013-03-12> add for export media info begin
					List<MediaInfo> mediaInfoList = dbo.queryMeidas(mContext, note.getId());
					if (mediaInfoList != null && mediaInfoList.size() > 0) {
						String mediaFileName = "";
						File mediaFile = null;
						for (MediaInfo mediaInfo : mediaInfoList) {
							mediaFileName = mediaInfo.getMediaFileName();
//							mediaFile = new File(mediaFileName);
							mediaFile = new File(mediaFileName.substring(1));
							if (mediaFile.exists()) {
								int res = FileUtils.copyMediaFile(mediaFileName.substring(1), filePath.getPath()	+ mediaFileName.substring(mediaFileName.lastIndexOf("/")));
								if (res == FileUtils.STATE_COPY_FILE_IOEXCEPTION) {
									return STATE_SYSTEM_ERROR;
								}
							}
						}
					}
					// Gionee <lilg><2013-03-12> add for export media info end
				}
			}
			Log.d("exportNoteToText------export note to text end!");
			return STATE_SUCCESS;
		}
		
		//Gionee <pengwei><20130922> modify for CR00871269 begin
		public File checkForldAndFile(int exportType) {
			File fileDir = null;
			fileDir = getFilePath(exportType);
			return fileDir;
		}
		
		//Gionee <pengwei><20131010> modify for CR00871269 begin
		
		private File getFilePath(int exportType){
				File fileDir;
				Log.v("getMTKFilePath------exportType: " + exportType);
				if(exportType == ImportExportActivity.EXPORT_TYPE_SDCARD){
					fileDir = generateFileMountedOnSDcard(mContext, R.string.file_path,	R.string.file_name_txt_format);

				}else {
					fileDir = generateFileMountedOnInternalMemory(mContext, R.string.file_path_internalmemory, R.string.file_name_txt_format);

				}	
				return fileDir;
		}
		//Gionee <pengwei><20131010> modify for CR00871269 end

		//Gionee <pengwei><20130922> modify for CR00871269 end
		private PrintStream getExportToTextPrintStream(File fileDir,String fileName) {

			File file = null;

			if (fileDir == null) {
				Log.e("getExportToTextPrintStream------create file to exported failed!");
				return null;
			}else{
				file = new File(fileDir, fileName + ".txt");
			}
			mFileName = file.getName();
			mFileDirectory = mContext.getString(R.string.file_path);
			PrintStream ps = null;
			try {
				FileOutputStream fos = new FileOutputStream(file);
                ps = new PrintStream(fos,true,"UTF-8");
			}catch (UnsupportedEncodingException e) {
				Log.e("getExportToTextPrintStream-Unsupported----e.getMessage(): "+e.getMessage());
			}catch (FileNotFoundException e) {
				Log.e("getExportToTextPrintStream-FileNotFound-----e.getMessage(): "+e.getMessage());
				return null;
			} catch (NullPointerException e) {
				Log.e("getExportToTextPrintStream-NullPointer-----e.getMessage(): "+e.getMessage());
				return null;
			}
			return ps;
		}
	}

	private static class TextImport{

		private Context mContext;

		public TextImport(Context context) {
			mContext = context;
		}

		public long importFromText(int importType, String path, String checkedFile) {
			Log.i("ImportExportUtils------import from text start!");

			long noteInsertedId = -1;
			
			Log.v("ImportExportUtils------importFromText---path == " + path);
			BufferedReader br = getBufferedReader(importType, path + "/" + checkedFile);
			if(br == null){
				Log.e("ImportExportUtils------BufferedReader == null in importFromText!");
				return noteInsertedId;
			}

			String line = "";
			try{
				while((line = br.readLine()) != null){
					Log.i("ImportExportUtils------line in file: " + line);

					if("".equals(line)){
						continue;
					}
//					noteInsertedId = importNote(mContext, line, getPathByType(mContext, importType) + path);
                    // Gionee <wangpan><2014-06-06> modify for CR01273806 begin
					if(importType == ImportExportActivity.EXPORT_TYPE_SDCARD){
						noteInsertedId = importNote(mContext, line);
					}else if(importType == ImportExportActivity.EXPORT_TYPE_INTERNAL_MEMORY){
						noteInsertedId = importNote(mContext, line);
					}
                    // Gionee <wangpan><2014-06-06> modify for CR01273806 end
				}
			}catch(Exception e){
				Log.e("ImportExportUtils------message: " + e.getMessage());
			}
			Log.d("ImportExportUtils------import from text end!");
			
			return noteInsertedId;
		}
		
		public void importNoteMedia(MediaInfo mediaInfo){
			dbo.insertMedia(mContext, mediaInfo);
		}

		private BufferedReader getBufferedReader(int importType, String fileName){

			BufferedReader br = null;

			if(importType == ImportExportActivity.EXPORT_TYPE_SDCARD){

				if (!externalStorageAvailable()) {
					Log.e("ImportExportUtils------Media was not mounted when import data!");
					return null;
				}

				StringBuilder sb = new StringBuilder();
				//				sb.append(Environment.getExternalStorageDirectory());

				//Gionee <wangpan><2014-02-27> modify for CR01070892 begin
				sb.append(getSdcardRealPath());
                //Gionee <wangpan><2014-02-27> modify for CR01070892 end
				sb.append(mContext.getString(R.string.file_path));
				Log.i("ImportExportUtils------file dir: " + sb.toString());

				//				File filedir = new File(sb.toString());
				sb.append(fileName);
				File file = new File(sb.toString());
				Log.d("ImportExportUtils------file name: " + sb.toString());

				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Constants.CHARSET_UTF8));
				} catch (Exception e) {
					Log.e("ImportExportUtils------get BufferedReader error!",e);
				}

				return br;
			}else if(importType == ImportExportActivity.EXPORT_TYPE_INTERNAL_MEMORY){

				//判断外置sd卡是否存在
				File sdCardPath = new File(FileUtils.PATH_SDCARD2);
				//外部SD卡
				Log.i("ImportExportUtils------sd card path exists: " + sdCardPath.exists());
				Log.i("ImportExportUtils------sd card total store: " + FileUtils.getTotalStore(sdCardPath.getPath()));

				File fileDir = null;
				//Gionee <pengwei><20130923> modify for CR00871269 begin
                //Gionee <wangpan><2014-02-27> modify for CR01070892 begin
                fileDir = getFilePathForMethodFileList(mContext, sdCardPath);				
                //Gionee <wangpan><2014-02-27> modify for CR01070892 end
				//Gionee <pengwei><20130923> modify for CR00871269 begin
				Log.d("ImportExportUtils------file dir: " + fileDir.getPath());

				File file = new File(fileDir.toString() + "/" + fileName);

				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Constants.CHARSET_UTF8));
				} catch (Exception e) {
					Log.e("ImportExportUtils------get BufferedReader error!",e);
				}

				return br;
			}else{
				// error
				Log.e("ImportExportUtils------error typeSelect: " + importType);
			}

			return br;
		}

		private long importNote(Context context,String contentLine){
			long noteInsertedId = -1;
			String[] lineArr = null;
			String folderTitle = "";
			String noteTitle = null;
			String noteContent = "";
			//Gionee caojiangbo 20121019 modify for CR00715879 begin
			lineArr = contentLine.split(String.valueOf(Constants.CONTENT_SPLIT), -1);
			//Gionee caojiangbo 20121019 modify for CR00715879 end
			if(lineArr == null || (lineArr.length != 2 && lineArr.length != 3)){
				Log.d("ImportExportUtils------content line: " + contentLine);
				return noteInsertedId;
			}
			if(lineArr.length == 2){
				// root note
				if(!"".equals(lineArr[0])){
					noteTitle = lineArr[0];
				}
				noteContent = parseEnter(lineArr[1]);

				Note note = new Note();
				note.setContent(noteContent);
				note.setTitle(noteTitle);
				note.setUpdateDate(dbo.getDate());
				note.setUpdateTime(dbo.getTime());
				note.setBgColor("0");
				note.setIsFolder("no");
				note.setParentFile("no");
				note.setNoteFontSize("12");
				note.setWidgetId("-1");
				note.setWidgetType("-1");
				note.setHaveNoteCount(0);
				// Gionee <wangpan><2014-06-04> modify for CR01273806 begin
                // note.setMediaFolderName(path);
                note.setMediaFolderName("-1");
                // Gionee <wangpan><2014-06-04> modify for CR01273806 end

				noteInsertedId = dbo.createNote(mContext, note);
				note.setId(noteInsertedId + "");
				// Gionee <wangpan><2014-06-06> delete for CR01273806 begin
//				int posInt = UtilsQueryDatas.sortNote(note,HomeActivity.mTempNoteList);
//				HomeActivity.mTempNoteList.add(posInt,note);
				// Gionee <wangpan><2014-06-06> delete for CR01273806 end
				Log.i("ImportExportUtils------import a root note!");

			}else if(lineArr.length == 3){
				// note in a folder
				folderTitle = lineArr[0];
				if(!"".equals(lineArr[1])){
					noteTitle = lineArr[1];
				}
				noteContent = parseEnter(lineArr[2]);

				Note note = new Note();
				note.setContent(noteContent);
				note.setTitle(noteTitle);
				note.setUpdateDate(dbo.getDate());
				note.setUpdateTime(dbo.getTime());
				note.setBgColor("0");
				note.setIsFolder("no");
				note.setNoteFontSize("12");
				note.setWidgetId("-1");
				note.setWidgetType("-1");
				note.setHaveNoteCount(0);
                // Gionee <wangpan><2014-06-04> modify for CR01273806 begin
                // note.setMediaFolderName(path);
                note.setMediaFolderName("-1");
                // Gionee <wangpan><2014-06-04> modify for CR01273806 end
				
				Note folder = dbo.queryNoteByFolderTitle(mContext, folderTitle);
				if(folder.getId() == null || "".equals(folder.getId())){
					// 数据库中没有此文件夹
					Note noteFolder = getCreateFolder(folderTitle);
					long folderId = dbo.createNote(mContext, noteFolder);
					noteFolder.setId(folderId + "");
					note.setParentFile(String.valueOf(folderId));
					int posFolderInt = UtilsQueryDatas.sortFolder(noteFolder,HomeActivity.mTempNoteList);
					UtilsQueryDatas.addNoteCountInFolder(noteFolder);
					HomeActivity.mTempNoteList.add(posFolderInt,noteFolder);
				}else{
					// 数据库中已有此文件夹
					note.setParentFile(folder.getId());
					Note noteFolder = UtilsQueryDatas.queryNoteByID(folder.getNoteId(), HomeActivity.mTempNoteList);
					UtilsQueryDatas.addNoteCountInFolder(noteFolder);
				}

				Log.d("ImportExportUtils------haveNoteCount: " + folder.getHaveNoteCount());

				noteInsertedId = dbo.createNote(mContext, note);
				note.setId(noteInsertedId + "");
				// Gionee <wangpan><2014-06-06> delete for CR01273806 begin
//				int posInt = UtilsQueryDatas.sortNote(note,HomeActivity.mTempNoteList);
//				HomeActivity.mTempNoteList.add(posInt,note);
				// Gionee <wangpan><2014-06-06> delete for CR01273806 end
				// update folder haveNoteCount
				//				Note updateFolder = new Note();
				//				updateFolder.setId(note.getParentFile());
				//				updateFolder.setHaveNoteCount(folder.getHaveNoteCount() + 1);
				//				
				//				dbo.updateNote(mContext, updateFolder);
				Log.d("ImportExportUtils---import a note with folder id is " + note.getParentFile() + " !");

			}else{
				Log.e("ImportExportUtils------error content line: " + contentLine);
			}
//			HomeActivity.updateAdapter();
			return noteInsertedId;
		}
		
		private Note getCreateFolder(String folderTitle){
			Note note = new Note();
			note.setUpdateDate(dbo.getDate());
			note.setUpdateTime(dbo.getTime());
			note.setIsFolder("yes");
			note.setParentFile("no");
			note.setHaveNoteCount(0);
			note.setTitle(folderTitle);
			return note;
		}
	}

	/**
	 * Generate the text file to store imported data
	 */
	private static File generateFileMountedOnSDcard(Context context, int filePathResId, int fileNameFormatResId) {
		StringBuilder sb = new StringBuilder();
		//		sb.append(Environment.getExternalStorageDirectory());
        sb.append(getSdcardRealPath());
		sb.append(context.getString(filePathResId));
//GN pengwei 2012-11-23 modify  for find bugs start
//		File filedir = new File(sb.toString());
//GN pengwei 2012-11-23 modify  for find bugs end

		sb.append(exportFileName);
		File file = new File(sb.toString());
		Log.d("generateFileMountedOnSDcard------sb.toString(): "+sb.toString());

		try {
			if (!file.exists()) {
				file.mkdirs();
			}
			return file;
		} catch (SecurityException e) {
			Log.e("generateFileMountedOnSDcard------e.getMessage(): "+ e.getMessage());
		}

		return null;
	}


    //Gionee <wangpan> <2014-02-17> modify for CR01033300 begin
    private static String getSdcardRealPath() {
        String sdcardPath = null;
        if(SystemProperties.get("ro.gn.gn2sdcardswap","no").equals("yes")){
            sdcardPath = FileUtils.PATH_SDCARD;
        }else{
            sdcardPath = FileUtils.PATH_SDCARD2;
        }
        return sdcardPath;
    }
    //Gionee <wangpan> <2014-02-17> modify for CR01033300 end

	private static File generateFileMountedOnInternalMemory(Context context, int filePathResId, int fileNameFormatResId){
		//判断外置sd卡是否存在
		File sdCardPath = new File(FileUtils.PATH_SDCARD2);
		//外部SD卡
		Log.i("generateFileMountedOnInternalMemory------sd card path exists: " + sdCardPath.exists());
		Log.i("generateFileMountedOnInternalMemory------sd card total store: " + FileUtils.getTotalStore(sdCardPath.getPath()));

		File filedir = null;
		//Gionee <pengwei><20130923> modify for CR00871269 begin
        //Gionee <wangpan><2014-02-26> modify for CR01070892 begin
        filedir = getFilePathForMethodFileList(context, sdCardPath);
        //Gionee <wangpan><2014-02-26> modify for CR01070892 end
		//Gionee <pengwei><20130923> modify for CR00871269 begin
		File file = new File(filedir.toString() + "/" + exportFileName);
		Log.d("generateFileMountedOnInternalMemory------file: " + file.toString());

		try {
			if (!file.exists()) {
				file.mkdirs();
			}
			return file;
		} catch (SecurityException e) {
			Log.e("generateFileMountedOnInternalMemory------e.getMessage(): "+e.getMessage());
		} 

		return file;
	}

    //Gionee <wangpan> <2014-02-17> modify for CR01033300 begin
    private static String getInternalRealPath() {
        String path;
        if(SystemProperties.get("ro.gn.gn2sdcardswap","no").equals("yes")){
            path = FileUtils.PATH_SDCARD2;
        }else{
            path = FileUtils.PATH_SDCARD;
        }
		return path;
	}
    //Gionee <wangpan> <2014-02-17> modify for CR01033300 end
	
	//Gionee <pengwei><20131011> modify for CR00871269 begin
    //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 begin
	private static File getFilePathForMethodFileList(Context context,File sdCardPath) {
		File filedir;
		if (FileUtils.getTotalStore(sdCardPath.getPath()) <= 0) {
			// sd card不存在或不可用
			filedir = new File(FileUtils.PATH_SDCARD + context.getResources().getString(R.string.file_path));
		} else {
			String internalRealPath = getInternalRealPath();
			filedir = new File(internalRealPath + context.getResources().getString(R.string.file_path));
			// filedir = new File(FileUtils.PATH_SDCARD +
			// context.getResources().getString(R.string.file_path));
		}
		return filedir;
	}
    //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 end
	//Gionee <pengwei><20131011> modify for CR00871269 end
	/**
	 * 
	 * @param context
	 * @param typeSelect
	 * @return key is the directory, value is the files in the directory
	 */
	public Map<String, List<String>> getDirectoryList(Context context, int typeSelect){
		Log.v("getDirectoryList---typeSelect: " + typeSelect);
		if(typeSelect == ImportExportActivity.EXPORT_TYPE_SDCARD){
			// sd card
			StringBuilder sb = new StringBuilder();
            //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 begin
			sb.append(getSdcardRealPath());
            //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 end
			sb.append(context.getString(R.string.file_path));

			File filedir = new File(sb.toString());
			if(!filedir.exists()){
				Log.d("ImportExportUtils------file dir in sd card exists: " + filedir.exists());
				return null;
			}

			File[] directoryList = filedir.listFiles();

			return getDirectoryAndFiles(directoryList);
		}else if(typeSelect == ImportExportActivity.EXPORT_TYPE_INTERNAL_MEMORY){
			//internal memory
            //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 begin
            //File sdCardPath = getSDPath();
            File sdCardPath = new File(FileUtils.PATH_SDCARD2);	

			File filedir = null;
			
			filedir = getFilePathForMethodFileList(context, sdCardPath);
			
            //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 end
			File[] directoryList = filedir.listFiles();

			return getDirectoryAndFiles(directoryList);
		}else{
			// error
			Log.e("ImportExportUtils---error typeSelect: " + typeSelect);
		}

		return null;
	}


	
	/**
	 * 
	 * @param directoryList
	 * @return
	 */
	private  Map<String, List<String>> getDirectoryAndFiles(File[] directoryList){

		if(directoryList == null || directoryList.length <= 0){
			Log.i("ImportExportUtils------no directorys or files!");
			return null;
		}

		Map<String, List<String>> directoryMap = new HashMap<String, List<String>>();
		String[] files = null;
		for(File d : directoryList){
			if(!d.isDirectory()){
				continue;
			}
			// for each directory
			files = d.list();
			List<String> fileList = null;
			if(files != null && files.length > 0){
				fileList = Arrays.asList(files);
			}else{
				fileList = new ArrayList<String>();
			}

			directoryMap.put(d.getName(), fileList);
		}

		return directoryMap;
	}

	//将便签内容中的回车字符"\n"替换为用户不能直接操作的字符，便于数据的保存
	private static String formatEnter(String content){

		if(content == null || "".equals(content)){
			return content;
		}

		if(content.contains(Constants.STR_ENTER)){
			content = content.replaceAll(Constants.STR_ENTER, String.valueOf(Constants.ENTER_REPLACE));
		}

		return content;
	}

	//将保存的数据中的回车解析为"\n"
	private static String parseEnter(String content){

		if(content == null || "".equals(content)){
			return content;
		}

		if(content.contains(String.valueOf(Constants.ENTER_REPLACE))){
			content = content.replaceAll(String.valueOf(Constants.ENTER_REPLACE), Constants.STR_ENTER);
		}

		return content;
	}
	
	/**
	 * get the absolute path by the give type
	 * @param type 0 is sdcare, 1 is internal memory
	 * @return
	 */
	public static String getPathByType(Context context, int type){
		
		if(type == ImportExportActivity.EXPORT_TYPE_SDCARD){
			
			if (!externalStorageAvailable()) {
				Log.e("ImportExportUtils------Media was not mounted when import data!");
				return null;
			}
			Log.v("ImportExportUtils------getPathByType-1---" + FileUtils.PATH_SDCARD2);
			return getFilePathByDiffPlatForm(context);
		}else if(type == ImportExportActivity.EXPORT_TYPE_INTERNAL_MEMORY){
			
			//判断外置sd卡是否存在
			File sdCardPath = new File(FileUtils.PATH_SDCARD2);
			//外部SD卡
			Log.i("ImportExportUtils------sd card path exists: " + sdCardPath.exists());
			Log.i("ImportExportUtils------sd card total store: " + FileUtils.getTotalStore(sdCardPath.getPath()));

			File fileDir = null;

            //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 begin
			fileDir = getFilePathForMethodFileList(context, sdCardPath);
            //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 end
			Log.d("ImportExportUtils------getPathByType-2---" + fileDir.getPath());
			return fileDir.getPath();
		}else {
			Log.e("ImportExportUtils------type: " + type);
		}
		
		return null;
	}

    //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 begin

	private static String getFilePathByDiffPlatForm(Context context){
		StringBuilder sb = new StringBuilder();
		sb.append(getSdcardRealPath());
		sb.append(context.getString(R.string.file_path));
		return sb.toString();
		
	}
    //Gionee <wangpan> <2014-02-17> modify for CR01033300 CR01070892 end

}
//Gionee <pengwei><2013-11-01> modify for CR00941779 end
