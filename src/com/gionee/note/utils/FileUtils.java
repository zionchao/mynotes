package com.gionee.note.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import android.os.*;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.gionee.note.R;
import com.gionee.note.content.Constants;
import com.gionee.note.domain.MediaInfo;
import com.gionee.note.noteMedia.record.NoteMediaManager;
import android.os.storage.StorageVolume;
//Gionee <pengwei><2013-11-01> modify for CR00941779 begin
public class FileUtils {

	public static final int STATE_COPY_FILE_SUCCESS = 0; 
	public static final int STATE_COPY_FILE_ERROR = 1;
	public static final int STATE_COPY_FILE_IOEXCEPTION = 2;
	
	public static final int ERROR_SDCARD_NOT_EXISTS_OR_UNAVAILABLE = 1005;
	public static final int ERROR_SDCARD_MIN_AVAILABLE_STORE = 1006;
	public static final int SUCCESS_SDCARD_STATE = 1007;
	public static final int ERROR_INTERNAL_MEMORY_NOT_EXISTS_OR_UNAVAILABLE = 1005;
	public static final int ERROR_INTERNAL_MEMORY_MIN_AVAILABLE_STORE = 1006;
	public static final int SUCCESS_INTERNAL_MEMORY_STATE = 1007;
	
	
	public static final int FLAG_OTHER = -1;
	public static final int FLAG_SDCARD = 0;
	public static final int FLAG_INTERNAL_MEMORY = 1;
	
	public static final int ZERO_STORE = 0;
	public static final int MIN_AVAILABLE_STORE = 3;
	
	private static String path = "/mnt/sdcard/log/";
	private static String file = "log.txt";
	private static PrintWriter pw = null;
//20150107 gionee taofp add for CR01450444 begin
	public static final String PATH_SDCARD = "/storage/sdcard0/";
	public static final String PATH_SDCARD2 = "/storage/sdcard1/";
//20150107 gionee taofp add for CR01450444 end	
	static {
		try {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File f = new File(path + file);
			if (!f.exists()) {
				f.createNewFile();
			}

			pw = new PrintWriter(new FileOutputStream(f));
		} catch (Exception e) {
			Log.e("FileUtils------e.getMessage="+e.getMessage());
		}
	}

	/**
	 * 返回字节数
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getAvailableStore(String filePath) {
	    // Gionee <wangpan> <2014-05-06> add for CR01228724 begin
	    File file = new File(filePath);
        if(!file.exists()){
            return 0;
        }
        // Gionee <wangpan> <2014-05-06> add for CR01228724 end
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(filePath);

		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();

		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();

		long availableSpare = availaBlock * blocSize;

		return availableSpare;
	}

	public static long getTotalStore(String filePath) {
	    Log.d("FileUtils-getTotalStore");
        // Gionee <wangpan> <2014-05-06> add for CR01228724 begin
	    File file = new File(filePath);
        if(!file.exists()){
            return 0;
        }
        // Gionee <wangpan> <2014-05-06> add for CR01228724 end
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(filePath);

		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();

		// 获取BLOCK数量
		long totalBlocks = statFs.getBlockCount();

		long total = totalBlocks * blocSize;

		return total;
	}

	public static void logToFile(String msg) {
		pw.println(msg);
		pw.flush();
	}

	public static void closePrintWriter() {
		if (pw != null) {
			pw.close();
		}
	}
	
	public static boolean deleteFile(String fileName){
		if(!TextUtils.isEmpty(fileName)){
			File file = new File(fileName);
			if(file.exists()){
				Log.i("FileUtils------delete file: " + fileName);
				return file.delete();
			}
		}
		return false;
	}
	
	/**
	 * copy sourceFile to targetFile
	 * @param sourceFile
	 * @param targetFile
	 * @return
	 */
	public static int copyMediaFile(String sourceFile, String targetFile){
		
		if(TextUtils.isEmpty(sourceFile) || TextUtils.isEmpty(targetFile)){
			Log.e("FileUtils------copy media file false! sourceFile is " + sourceFile + ", targetFile: " + targetFile + ".");
			return STATE_COPY_FILE_ERROR;
		}
		
		InputStream is = null;
		OutputStream os = null;
		
		try{
			
			is = new FileInputStream(sourceFile);
			os = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024];
			int num = 0;
			while((num = is.read(buffer)) != -1){
				os.write(buffer, 0, num);
			}
			os.flush();
			
		}catch(FileNotFoundException e){
			Log.e("FileUtils------copy media file exception!", e);
			return STATE_COPY_FILE_ERROR;
		}catch(IOException e2){
			Log.e("FileUtils------copy media file exception!", e2);
			return STATE_COPY_FILE_IOEXCEPTION;
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					Log.e("FileUtils------close InputStream exception!", e);
				}
			}
			if(os != null){
				try {
					os.close();
				} catch (IOException e) {
					Log.e("FileUtils------close OutputStream exception!", e);
				}
			}
		}
		return STATE_COPY_FILE_SUCCESS;
	}
	
	public static int checkSDCardState() {
		Log.i("FileUtils------check sdcard state!");

		File sdCard2Path = new File(FileUtils.PATH_SDCARD2);
		Log.d("FileUtils------sd card path exists: " + sdCard2Path.exists()	+ ", sd card total store: "	+ FileUtils.getTotalStore(sdCard2Path.getPath()));
		if (FileUtils.getTotalStore(sdCard2Path.getPath()) <= ZERO_STORE) {
			// sdcard not exists or unavailable
			Log.e("FileUtils------sdcard not exists or unavailable!");
			return ERROR_SDCARD_NOT_EXISTS_OR_UNAVAILABLE;
		}

		//Gionee <wangpan> <2014-03-22> modify for CR01131012 begin
		//File sdCardPath = new File(FileUtils.PATH_SDCARD);
		File sdCardPath = new File(getSdcardRealPath());
		//Gionee <wangpan> <2014-03-22> modify for CR01131012 end
		Log.d("FileUtils---sd card min available store: " + FileUtils.getAvailableStore(sdCardPath.getPath()) + ", " + FileUtils.getAvailableStore(sdCardPath.getPath()) / 1024	/ 1024);
		if ((FileUtils.getAvailableStore(sdCardPath.getPath()) / 1024 / 1024) < MIN_AVAILABLE_STORE) {
			Log.e("FileUtils------sd card min available store < " + MIN_AVAILABLE_STORE + "M!");
			return ERROR_SDCARD_MIN_AVAILABLE_STORE;
		}
		return SUCCESS_SDCARD_STATE;
	}
    //Gionee <wangpan> <2014-02-17> modify for CR01033300 begin
    public static String getSdcardRealPath() {
        String sdcardPath = null;
        Log.d("ro.gn.gn2sdcardswap: "+SystemProperties.get("ro.gn.gn2sdcardswap","no"));
        if(SystemProperties.get("ro.gn.gn2sdcardswap","no").equals("yes")){
            sdcardPath = FileUtils.PATH_SDCARD;
        }else{
            sdcardPath = FileUtils.PATH_SDCARD2;
        }
        return sdcardPath;
    }
    //Gionee <wangpan> <2014-02-17> modify for CR01033300 end
	public static int checkInternalMemoryState(){
		Log.i("FileUtils------check internal memory state!");

		File internalMemoryPath = getInternalMemoryPath();

		if(FileUtils.getTotalStore(internalMemoryPath.getPath()) <= ZERO_STORE){
			Log.e("FileUtils------internal memory exists: false");
			return ERROR_INTERNAL_MEMORY_NOT_EXISTS_OR_UNAVAILABLE;
		}
		Log.d("FileUtils------internal memory min available store: " + FileUtils.getAvailableStore(internalMemoryPath.getPath()) + ", " + FileUtils.getAvailableStore(internalMemoryPath.getPath())/1024/1024);
		if((FileUtils.getAvailableStore(internalMemoryPath.getPath())/1024/1024) < MIN_AVAILABLE_STORE ){
			Log.e("FileUtils------internal memory min available store < " + MIN_AVAILABLE_STORE  + "M!");
			return ERROR_INTERNAL_MEMORY_MIN_AVAILABLE_STORE;
		}
		return SUCCESS_INTERNAL_MEMORY_STATE;
	}
	

	public static File getInternalMemoryPath(){
		//首先判断外部SD卡是否存在
		File sdCard2Path = new File(FileUtils.PATH_SDCARD2);
		File internalMemoryPath = null;
		//Gionee <wangpan> <2014-03-22> modify for CR01131012 begin
		/*Log.d("FileUtils------sd card total store: " + FileUtils.getTotalStore(sdCard2Path.getPath()));
		if(PlatForm.isMTK()){
			internalMemoryPath = getInternalMemoryFilePathForMTK(sdCard2Path);
		}else{
			internalMemoryPath = getInternalMemoryFilePathForQCOM();
		}*/
        if (FileUtils.getTotalStore(sdCard2Path.getPath()) > ZERO_STORE) {
            // sdcard not exists or unavailable
		    String internalRealPath = getInternalRealPath();
		    internalMemoryPath = new File(internalRealPath);
		}else{
		    internalMemoryPath = new File(FileUtils.PATH_SDCARD);
		}
		//Gionee <wangpan> <2014-03-22> modify for CR01131012 end
		
		return internalMemoryPath;
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
	
	private static File getInternalMemoryFilePathForMTK(File sdCard2Path) {
		File internalMemoryPath;
		if(FileUtils.getTotalStore(sdCard2Path.getPath()) <= ZERO_STORE){
			// 不存在
			internalMemoryPath = new File(FileUtils.PATH_SDCARD);
			Log.d("FileUtils------internalMemoryPath: " + FileUtils.PATH_SDCARD);
		}else{
			// 存在
			internalMemoryPath = new File(FileUtils.PATH_SDCARD2);
			Log.d("FileUtils------nternalMemoryPath: " + FileUtils.PATH_SDCARD2);
		}
		
		return internalMemoryPath;
	}

	private static File getInternalMemoryFilePathForQCOM() {
		File internalMemoryPath = new File(FileUtils.PATH_SDCARD);
		return internalMemoryPath;
	}		

	private static final String SEPARATE_SIGN = "/";
	public static String getPathByPathType(String pathType){
		if(TextUtils.isEmpty(pathType)){
			return null;
		}
		if(NoteMediaManager.PATH_TYPE_SD_CARD.equals(pathType)){
		//Gionee <wangpan> <2014-03-22> modify for CR01131012 begin
			/*if(PlatForm.isMTK()){
				Log.d("NoteMediaManager------getPathByPathType: " + FileUtils.PATH_SDCARD + SEPARATE_SIGN);
				return FileUtils.PATH_SDCARD + SEPARATE_SIGN;
			}else{
				Log.d("NoteMediaManager------getPathByPathType: " + FileUtils.PATH_SDCARD2 + SEPARATE_SIGN);
				return FileUtils.PATH_SDCARD2 + SEPARATE_SIGN;
			}*/
		    String sdcardRealPath = getSdcardRealPath();
		    return sdcardRealPath + SEPARATE_SIGN;
		//Gionee <wangpan> <2014-03-22> modify for CR01131012 end
		}else if(NoteMediaManager.PATH_TYPE_INTERNAL_MEMORY.equals(pathType)){
			return getInternalMemoryPath().getPath() + SEPARATE_SIGN;
		}
		
		return null;
	}
	
	
	
	public static String getSubPath(Context context, String path){
        Log.d("FileUtils------getSubPath, path: " + path);
        // Gionee <wangpan><2014-08-19> modify for CR01359336 begin
        if (TextUtils.isEmpty(path) || context == null || !path.contains("/")) {
            return null;
        }
        // Gionee <caody><2014-09-27> modify for CR01390284 begin
        String result = "";
//20150107 gionee taofp add for CR01450444 begin
        if (path.contains(PATH_SDCARD)) {
            int index = path.indexOf(PATH_SDCARD);
            result = path.substring(index + PATH_SDCARD.length());
        }
        if (path.contains(PATH_SDCARD2)) {
            int index = path.indexOf(PATH_SDCARD2);
            result = path.substring(index + PATH_SDCARD2.length());
        }
//20150107 gionee taofp add for CR01450444 end
        // Gionee <caody><2014-09-27> modify for CR01390284 end
        Log.d("FileUtils------getSubPath, result: " + result);
        return result;
        // Gionee <wangpan><2014-04-09> modify for CR01173529 end
	}
	
	public static String getSubPathAndFileName(String path){
		if(TextUtils.isEmpty(path)){
			return null;
		}
		String[] pathArray = path.split("/");
		if(pathArray != null && pathArray.length == 7){
			return pathArray[3] + "/" + pathArray[4] + "/" + pathArray[5] + "/" + pathArray[6];
		}
		return null;
	}
	
	// Gionee <lilg><2013-05-14> add begin
	public static boolean isExternalStorageExists(){
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	// Gionee <lilg><2013-05-14> add end
	
	public static String replaceTwoDivideToOne(String divideStr){
		String str = "";
		str = divideStr.replace("//", "/");
		return str;
	}

    // Gionee <lilg><2014-09-26> modify for CR01390390 begin
    public static Boolean isGn2SdcardSwap(){
        boolean isSwap = false;
        String swap = SystemProperties.get("ro.gn.gn2sdcardswap","no");
        if("yes".equals(swap)){
            isSwap = true;
        }
        return isSwap;
    }
    // Gionee <lilg><2014-09-26> modify for CR01390390 end
}
//Gionee <pengwei><2013-11-01> modify for CR00941779 end
