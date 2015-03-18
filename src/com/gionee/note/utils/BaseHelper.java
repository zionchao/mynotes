/*******************************************************************************
 * Filename:
 * ---------
 *  BaseHelper.java
 *
 * Project:
 * --------
 *   Browser 1.2
 *
 * Description:
 * ------------
 *  工具类 
 *
 * Author:
 * -------
 *  2012.07.19 Han Yong 
 *
 ****************************************************************************/

package com.gionee.note.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.gionee.note.R;

import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import android.app.Dialog;
import amigo.app.AmigoProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;

public class BaseHelper {

	/**
	 * 流转字符串方法
	 * 
	 * @param is
	 * @return
	 */
	/*public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}*/

	/**
	 * 显示dialog
	 * 
	 * @param context
	 *            环境
	 * @param strTitle
	 *            标题
	 * @param strText
	 *            内容
	 * @param icon
	 *            图标
	 */
	/*public static void showDialog(AmigoActivity context, String strTitle,
			String strText, int icon) {
		AmigoAlertDialog.Builder tDialog = new AmigoAlertDialog.Builder(context);
		if(-1 != icon){
			tDialog.setIcon(icon);
		}
		if(strTitle != null){
			tDialog.setTitle(strTitle);
		}
		tDialog.setMessage(strText);
		tDialog.setPositiveButton(R.string.Ok, null);
		try{
			tDialog.show();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * 获取权限
	 * 
	 * @param permission
	 *            权限
	 * @param path
	 *            路径
	 */
	/*public static void chmod(String permission, String path) {
		try {
			Log.i("--s--", "before chmod");
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	// show the progress bar.
	/**
	 * 显示进度条
	 * 
	 * @param context
	 *            环境
	 * @param title
	 *            标题
	 * @param message
	 *            信息
	 * @param indeterminate
	 *            确定性
	 * @param cancelable
	 *            可撤销
	 * @return
	 */
	public static AmigoProgressDialog showProgress(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		AmigoProgressDialog dialog = new AmigoProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(false);
		// dialog.setDefaultButton(false);
		// dialog.setOnCancelListener(new AlixDemo.AlixOnCancelListener(
		// (AmigoActivity) context));
		try{
			dialog.show();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}

	/**
	 * 字符串转json对象
	 * 
	 * @param str
	 * @param split
	 * @return
	 */
	/*public static JSONObject string2JSON(String str, String split) {
		JSONObject json = new JSONObject();
		try {
			String[] arrStr = str.split(split);
			for (int i = 0; i < arrStr.length; i++) {
				String[] arrKeyValue = arrStr[i].split("=");
				json.put(arrKeyValue[0],
						arrStr[i].substring(arrKeyValue[0].length() + 1));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}*/

	/**
	 * 安装安全支付服务，安装assets文件夹下的apk
	 * 
	 * @param context
	 *            上下文环境
	 * @param fileName
	 *            apk名称
	 * @param path
	 *            安装路径
	 * @return
	 */
	/*public static boolean retrieveApkFromAssets(Context context,
			String fileName, String path) {
		boolean bRet = false;

		try {
			InputStream is = context.getAssets().open(fileName);

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}*/


	/*public static String getStorageDirectory(Context mContext) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
			StatFs statfs = new StatFs(path.getPath());
			long blocSize = statfs.getBlockSize();
			long availaBlock = statfs.getAvailableBlocks();
			long size = availaBlock * blocSize / 1024 / 1024;// MB
			if (size > 5) {
				String cachepath = path.getAbsolutePath() + "/" + CommonUtils.DOWNLOAD_PATH;
				File f = new File(cachepath);
				if (!f.exists()) {
					f.mkdir();
				}
				return cachepath;
			}
		}
		return mContext.getFilesDir().getAbsolutePath();
	}*/

	/*public static String getVersionName(Context context) throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}*/

	/**
     * 获取未安装的APK信息
     * 
     * @param context
     * @param archiveFilePath
     *            APK文件的路径。如：/sdcard/download/XX.apk
     */
    /*public static PackageInfo getApkInfo(Context context,
            String archiveFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath,
                PackageManager.GET_META_DATA);
        return apkInfo;
    }*/
    
    /*public static String getApkVersionByFilePath(Context context,
            String archiveFilePath) {
        PackageInfo apkInfo = getApkInfo(context, archiveFilePath);
        return apkInfo.versionName;
    }*/
    /*public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) 
        {
            NetworkInfo networkinfo = cm.getActiveNetworkInfo();
            return networkinfo != null && networkinfo.isAvailable();
        }
        
        return false;
    }*/
    /**
     * 对话框
     */
    /*public static Dialog showAlertDlg(Context context, int resId,String title, String msg, 
            String okButton, DialogInterface.OnClickListener clickedOk,
            String cancelButton, DialogInterface.OnClickListener clickedCancel){
			AmigoAlertDialog.Builder builer = new AmigoAlertDialog.Builder(context);
			if(-1 != resId){
				builer.setIcon(resId);
			}
			if( title != null && title.length()>0 )
			builer.setTitle(title);
			
			if( msg!=null && msg.length()>0 ){
			builer.setMessage(msg);
			}
			
			if( okButton!=null && clickedOk != null ){
			builer.setPositiveButton(okButton, clickedOk);
			}
			
			if( cancelButton!=null && clickedCancel != null ){
			builer.setNegativeButton(cancelButton, clickedCancel);
			}
			
			AmigoAlertDialog adlg = builer.create();
			adlg.setCancelable(false);
			try {
				adlg.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return adlg;
    }*/
    
    public static void dialogCancel(Dialog dialog){
		try {
			com.gionee.note.utils.Log.d("BaseHelper------start");
			if(dialog != null){
				com.gionee.note.utils.Log.d("BaseHelper------dialogCancel");
				dialog.cancel();
			}
			com.gionee.note.utils.Log.d("BaseHelper------end");
		} catch (Exception e) {
			com.gionee.note.utils.Log.d("BaseHelper------Exception");
			e.printStackTrace();
		}finally{
			dialog = null;
		}
		
	}
	public static void dialogShow(Dialog dialog){
		try {
			if(dialog != null){
				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static boolean isFileExit(String path) {
        File f = new File(path);
        if (f.exists()) {
            return true;
        } else {
            return false;
        }
    }
    
}
