/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gionee.note.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class GnUpgrade {
	private static final int GIONEE_CONNECT_TIMEOUT = 30 * 1000;

	public static final String AUTOOPENACTION = "gionee.intent.action.updateAutoOpen";

	private static String sConnectionMobileDefaultHost = "10.0.0.172";
	private static int sConnectionMobileDefaultPort = 80;

	private static final int CONNECTION_TYPE_IDLE = 0;
	private static final int CONNECTION_TYPE_CMWAP = 1;
	private static final int CONNECTION_TYPE_CMNET = 2;
	private static final int CONNECTION_TYPE_WIFI = 3;

	public static final int MESSAGE_CHECK_RESULT_OK = 230;
	public static final int MESSAGE_CHECK_RESULT_NETWORK_ERROR = 231;
	public static final int MESSAGE_CHECK_RESULT_NO_UPDATE = 232;
	public static final int MESSAGE_START_DOWNLOAD = 233;
	public static final int MESSAGE_ERROR_NO_SPACE = 234;
	public static final int MESSAGE_ERROR_NO_CARD = 235;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 236;
	public static final int MESSAGE_NO_ENOUGH_SPACE = 237;
	public static final int MESSAGE_NO_SDCARD = 238;
	public static final int MESSAGE_NO_NEED_UPDATE = 239;
	public static final int MESSAGE_MESSAGE_DOWNLOAD_NETWORK_ERROR = 240;
	public static final int MESSAGE_START_TO_INSTALL = 241;

	private static final String HOST = "update.gionee.com:8080";
	private static final String FILE_NAME = "GN_Note.apk";

	private Thread mThd = null;
	private Thread mDownloadThread = null;

	private Context mContext;
	private SharedPreferences mPrefs;
	private Handler mHandler;
	private State mStatus = State.INITIAL;
	private Object mSyncObject = new Object();

	private boolean mIsForceUpdate = false;

	public static final String UPGRADE_PREFERENCES = "upgrade_preferences";
	public static final String UPGRADE_DL_ID = "upgrade_downloadId";
	public static final String UPGRADE_BROWSER_IS_ACTIVE = "upgrade_browserIsActive";
	public static final String UPGRADE_NEED_INSTALL_BACKGROUND = "upgrade_needInstall";
	private static final String UPGRADE_DL_URL = "upgrade_downloadURL";
	private static final String UPGRADE_DL_FILE_SIZE = "upgrade_downloadFileSize";
	private static final String UPGRADE_RELEASE_NOTE = "upgrade_releaseNote";
	private static final String UPGRADE_DISPLAY_VERSION = "upgrade_displayVersion";
	private static final String UPGRADE_STATE = "upgrade_state";
	private static final String UPGRADE_DONOT_DISPLAY_THIS_VERSION = "upgrade_donotDisplayThisVersion";
	private static final String UPGRADE_DONOT_DISPLAY_THIS_VERSION_STRING = "upgrade_donotDisplayThisVersionString";

	private static final int FORCE_UPDATE = 1;

	public enum State {
		INITIAL(1), // 起始状态
		CHECKING(2), // 检查
		READY_TO_DOWNLOAD(3), // 准备下载
		DOWNLOADING(4), // 下载
		DOWNLOAD_WAITING(5), // 下载
		DOWNLOAD_PAUSE(6), // 下载暂停
		DOWNLOADCOMPLETE(7), // 下载完成
		BACKUP(8), // 备份
		BACKUPING(9), // 备份中
		BACKUPCOMPLETE(10), // 备份完成
		INSTALLING(11), // 安装
		INSTALLCOMPLETE(12); // 安装完成

		private int mValue;

		State(int arg1) {
			mValue = arg1;
		}

		public int getValue() {
			return mValue;
		}
	}

	public GnUpgrade(Context context) {
		mContext = context;
		mPrefs = mContext.getSharedPreferences(UPGRADE_PREFERENCES,
				Context.MODE_PRIVATE);
		// prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public GnUpgrade(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		mPrefs = mContext.getSharedPreferences(UPGRADE_PREFERENCES,
				Context.MODE_PRIVATE);
		int i = mPrefs.getInt(UPGRADE_STATE, State.INITIAL.getValue());
		for (State temp : State.values()) {
			if (temp.getValue() == i) {
				mStatus = temp;
				break;
			}
		}
	}

	public Boolean startCheck() {
		Log.d("GnUpgrade--------startCheck----------");
		mThd = new Thread(new Runnable() {
			public void run() {
				String currentVersionInfo = getClientUpgradeInfo();
				Log.i("info" + currentVersionInfo);
				if (currentVersionInfo != null) {
					try {
						String strUrl = "";
						String releaseNote = "";
						String displayVersion = "";
						String fileSize = "";
						int type = 0;
						JSONObject jsonObj = new JSONObject(currentVersionInfo);
						releaseNote = jsonObj.getString("releaseNote");
						displayVersion = jsonObj.getString("displayVersion");
						mPrefs.edit()
								.putString(UPGRADE_DISPLAY_VERSION,
										displayVersion).commit();

						JSONArray jsonObjs = jsonObj.getJSONArray("models");
						try {
							type = jsonObj.getInt("upgrademode");

							if (type == FORCE_UPDATE) {
								if (mContext == null) {
									return;
								}
								SharedPreferences sp = mContext
										.getSharedPreferences("force",
												Context.MODE_PRIVATE);
								final Editor editor = sp.edit();
								editor.putString("version", displayVersion);
								editor.commit();
								mIsForceUpdate = true;
							} else {
								mIsForceUpdate = false;
								if (displayVersion.equals(getNotifyVersion())) {
									if (mHandler == null) {
										return;
									}
									mHandler.sendMessage(mHandler
											.obtainMessage(MESSAGE_NO_NEED_UPDATE));
									return;
								}

							}
						} catch (Exception e) {
							mIsForceUpdate = false;
							e.printStackTrace();
						}
						for (int i = 0; i < jsonObjs.length(); i++) {
							JSONObject jsonObj1 = ((JSONObject) jsonObjs.get(i)); // .getJSONObject();
							strUrl = jsonObj1.getString("path");
							fileSize = jsonObj1.getString("size");

						}
						synchronized (mSyncObject) {

							String rootpath = Environment
									.getExternalStorageDirectory().getPath()
									+ "/" + CommonUtils.DOWNLOAD_PATH + "/";
							long apkSize = Long.parseLong(fileSize);

							boolean checkResult = checkDownloadApk(
									displayVersion, rootpath + FILE_NAME,
									apkSize);

							// Download completed, but not installed
							if (checkResult) {
								if (mHandler == null) {
									return;
								}
								mHandler.sendMessage(mHandler
										.obtainMessage(MESSAGE_DOWNLOAD_COMPLETE));
							} else {
								if (mPrefs == null) {
									return;
								}
								mPrefs.edit()
										.putString(UPGRADE_RELEASE_NOTE,
												releaseNote).commit();
								mPrefs.edit()
										.putString(UPGRADE_DISPLAY_VERSION,
												displayVersion).commit();
								mPrefs.edit().putString(UPGRADE_DL_URL, strUrl)
										.commit();
								mPrefs.edit()
										.putString(UPGRADE_DL_FILE_SIZE,
												fileSize).commit();
								if (mHandler == null) {
									return;
								}
								mHandler.sendMessage(mHandler
										.obtainMessage(MESSAGE_CHECK_RESULT_OK));
							}
						}
					} catch (JSONException e) {
						synchronized (mSyncObject) {
							if (mHandler == null) {
								return;
							}
							mHandler.sendMessage(mHandler
									.obtainMessage(MESSAGE_CHECK_RESULT_NO_UPDATE));
						}
					}
				} else {
					synchronized (mSyncObject) {
						if (mHandler == null) {
							return;
						}
						mHandler.sendMessage(mHandler
								.obtainMessage(MESSAGE_CHECK_RESULT_NETWORK_ERROR));
					}
				}
			}
		});
		mThd.setPriority(Thread.MIN_PRIORITY);
		mThd.start();
		return false;
	}

	public boolean needUpdate() {
		return true;
	}

	private State getStatus() {
		return mStatus;
	}

	public String getNotifyVersion() {
		return mPrefs.getString(UPGRADE_DONOT_DISPLAY_THIS_VERSION_STRING, "0");
	}

	public void setNotifyVersion(String version) {
		mPrefs.edit()
				.putString(UPGRADE_DONOT_DISPLAY_THIS_VERSION_STRING, version)
				.commit();
	}

	public Boolean isNotify() {
		return mPrefs.getBoolean(UPGRADE_DONOT_DISPLAY_THIS_VERSION, true);
	}

	public void setNotify(Boolean bNotify) {
		mPrefs.edit().putBoolean(UPGRADE_DONOT_DISPLAY_THIS_VERSION, bNotify)
				.commit();
	}

	public Boolean isActive() {
		return mPrefs.getBoolean(UPGRADE_BROWSER_IS_ACTIVE, false);
	}

	public void setActive(Boolean bActive) {
		mPrefs.edit().putBoolean(UPGRADE_BROWSER_IS_ACTIVE, bActive).commit();
		if (mPrefs.getBoolean(UPGRADE_NEED_INSTALL_BACKGROUND, true)
				&& !bActive && State.DOWNLOADCOMPLETE == getStatus()) {
			install();
		}
	}

	public String getNewVersionDesc() {
		return mPrefs.getString(UPGRADE_RELEASE_NOTE, "");
	}

	public String getNewVersion() {
		return mPrefs.getString(UPGRADE_DISPLAY_VERSION, "");
	}

	public String getFileSize() {
		return mPrefs.getString(UPGRADE_DL_FILE_SIZE, "0");
	}

	public void installLater() {
		mPrefs.edit().putBoolean(UPGRADE_NEED_INSTALL_BACKGROUND, true)
				.commit();
	}

	public void installNow() {
		Log.d("GnUpgrade--------installNow----------");
		mPrefs.edit().putBoolean(UPGRADE_NEED_INSTALL_BACKGROUND, false)
				.commit();
		install();
	}

	public void install() {
		try {
			Log.d("GnUpgrade--------install----------");
			String rootpath = Environment.getExternalStorageDirectory()
					.getPath() + "/" + CommonUtils.DOWNLOAD_PATH + "/";

			if (mContext == null) {
				return;
			}
			// PackageManager mPm = mContext.getPackageManager();
			// mPm.installPackage(Uri.parse(rootpath + FILE_NAME),
			// null, PackageManager.INSTALL_REPLACE_EXISTING,
			// mContext.getPackageName());
			// if (mHandler == null) {
			// return;
			// }
			// mHandler.sendMessage(mHandler
			// .obtainMessage(
			// MESSAGE_START_TO_INSTALL
			// ));
			Intent intent = new Intent(Intent.ACTION_VIEW);
			// 设置目标应用安装包路径
			intent.setDataAndType(Uri.fromFile(new File(rootpath + FILE_NAME)),
					"application/vnd.android.package-archive");
			mContext.startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
			com.gionee.note.utils.Log.v("GnUpgrade----install()----Exception----------");
		}
	}

	private boolean checkDownloadApk(String serviceApkVersion, String path,
			long apkSize) {
		Log.d("GnUpgrade--------checkDownloadApk----------");
		File file = new File(path);
		if (file.exists()) {
			if (mContext == null) {
				return true;
			}
			String version = getApkVersionByFilePath(mContext, path);
			long size = file.length();
			if (version.compareTo(serviceApkVersion) == 0 && apkSize == size) {
				return true;
			} else {
				file.delete();
				mPrefs.edit().clear();
				return false;
			}
		} else {
			return false;
		}
	}

	private String getApkVersionByFilePath(Context context,
			String archiveFilePath) {
		try {
			PackageInfo apkInfo = getApkInfo(context, archiveFilePath);
			return apkInfo.versionName;
		} catch (Exception e) {
			return "";
		}
	}

	private PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_META_DATA);
		return apkInfo;
	}

	public void startDownload() {
		Log.d("GnUpgrade--------startDownload----------");
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Environment.getExternalStoragePublicDirectory(
					CommonUtils.DOWNLOAD_PATH).mkdirs();
			urlDownloadToFile();
			if (mHandler == null) {
				return;
			}
			mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_START_DOWNLOAD));
		} else {
			if (mHandler == null) {
				return;
			}
			mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_NO_SDCARD));
		}
	}

	public void urlDownloadToFile() {
		Log.d("GnUpgrade--------urlDownloadToFile----------");
		mDownloadThread = new Thread(new Runnable() {
			@Override
			public void run() {

				HttpURLConnection conn = null;
				InputStream is = null;
				FileOutputStream fos=null;

				String fileName = Environment.getExternalStorageDirectory()
						.getPath()
						+ "/"
						+ CommonUtils.DOWNLOAD_PATH
						+ "/"
						+ FILE_NAME;
				try {
				    URL url = new URL(mPrefs.getString(UPGRADE_DL_URL, ""));
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(GIONEE_CONNECT_TIMEOUT);
                    conn.setReadTimeout(GIONEE_CONNECT_TIMEOUT);
                    conn.setDoInput(true);

                    conn.connect();
                    is = conn.getInputStream();
                    
					File file = new File(fileName);
					if (file.exists()) {
						file.delete();
					}
					file.createNewFile();
					fos = new FileOutputStream(file);

					byte[] temp = new byte[1024];
					int i = 0;
					while ((i = is.read(temp)) > 0) {
						fos.write(temp, 0, i);
					}

					if (mHandler == null) {
						return;
					}
					mHandler.sendMessage(mHandler
							.obtainMessage(MESSAGE_DOWNLOAD_COMPLETE));
				} catch (Exception e) {
					if (mHandler == null) {
						return;
					}
					mHandler.sendMessage(mHandler
							.obtainMessage(MESSAGE_MESSAGE_DOWNLOAD_NETWORK_ERROR));
				}finally{
                    try {
                        if(null != fos){
                            fos.close();
                        }
                        if(null != is){
                            is.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}
			}
		});
		mDownloadThread.start();
	}

	// gionee hanyong 20120614 for test=true; start
	private String getClientUpgradeInfo() {
		if (mContext == null) {
			return "";
		}

		String url = null;
		if (BaseHelper.isFileExit(CommonUtils.FLAG_TEST_ENVIRONMENT)) {
			url = CommonUtils.URL_UPDATE_TEST;
		} else {
			url = CommonUtils.URL_UPDATE;
		}

		return getJSONStringFromURLGET(mContext, url, CONNECTION_TYPE_WIFI);
	}

	// gionee hanyong 20120614 for test=true; end

	private String getUrlStringByXML(String result) {
		int start = result.indexOf("<go href=\"") + "<go href=\"".length();
		int end = result.indexOf("\"></go>", start);
		return result.substring(start, end);
	}

	private String getJSONStringFromURLGET(Context context, String url,
			int connectType) {
		try {
			String checkurl = url + getVersion(context, "1.0.0");
			URL downUrl = new URL(checkurl);
			HttpURLConnection http = (HttpURLConnection) downUrl
					.openConnection();
			Log.v("GnUpgrade "+downUrl + "");

			http.setConnectTimeout(GIONEE_CONNECT_TIMEOUT);
			if (connectType == CONNECTION_TYPE_CMWAP) {
				http.setReadTimeout(GIONEE_CONNECT_TIMEOUT);
				http.setRequestProperty("Host", sConnectionMobileDefaultHost);
				http.setRequestProperty("X-Online-Host", HOST);
			} else {
				http.setReadTimeout(GIONEE_CONNECT_TIMEOUT);
			}

			http.connect();

			int code = http.getResponseCode();
			if (code == 200) {
				int size = 1024; // server is not implemented now ,
									// http.getContentLength();
				byte[] buffer = new byte[size];
				InputStream inStream = http.getInputStream();
				int tsize = 0, totalSize = 0;
				while (totalSize < size) {
					if ((tsize = inStream.read(buffer, totalSize, size
							- totalSize)) == -1) {
						break;
					}
					totalSize += tsize;
				}
				inStream.close();
				http.disconnect();
				String result = new String(buffer, "UTF-8");
				if (result.indexOf("<?xml version=\"1.0\"?>") != -1) {
					String nurl = getUrlStringByXML(result);
					return getJSONStringFromURLGET(context, nurl, connectType);
				}
				return result;
			} else if (code == 400) {
				int size = http.getContentLength();

				byte[] buffer = new byte[size];
				InputStream inStream = http.getInputStream();
				int tsize = 0, totalSize = 0;
				while (totalSize < size) {
					if ((tsize = inStream.read(buffer, totalSize, size
							- totalSize)) == -1) {
						break;
					}
					totalSize += tsize;
				}
				inStream.close();
				http.disconnect();
			} else {
				Log.d("GnUpgrade "+"getJSONStringFromURL() result "
								+ http.getResponseCode());
			}
		} catch (Exception e) {
			Log.d("GnUpgrade "+"getJSONStringFromURL() Exception " + e.toString());
		}
		return null;
	}

	public static String getVersion(Context context, String version) {
		Log.v("GnUpgrade "+version);
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return version;
		}
	}

	public static int getConnectType(Context context) {
		ConnectivityManager lcm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State wifi = lcm.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == NetworkInfo.State.CONNECTED
				|| wifi == NetworkInfo.State.CONNECTING) {
			return CONNECTION_TYPE_WIFI;
		} else {
			NetworkInfo.State mobile = lcm.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState();
			if (mobile == NetworkInfo.State.CONNECTED
					|| mobile == NetworkInfo.State.CONNECTING) {
				Uri uri = Uri.parse("content://telephony/carriers/preferapn");
				Cursor mCursor = context.getContentResolver().query(uri, null,
						null, null, null);
				try {
					if (null != mCursor && mCursor.moveToFirst()) {
						int index = mCursor.getColumnIndex("apn");
						String connectType = null;
						if (index > -1) {
							connectType = mCursor.getString(index)
									.toLowerCase();
						}
						if (connectType != null
								&& (connectType.equals("3gnet")
										|| connectType.equals("cmnet") || connectType
											.equals("uninet"))) {
							return CONNECTION_TYPE_CMNET;
						} else {
							index = mCursor.getColumnIndex("proxy");
							if (index > -1) {
								connectType = mCursor.getString(index)
										.toLowerCase();
							}
							if (connectType != null
									&& connectType.trim().length() > 0) {
								sConnectionMobileDefaultHost = connectType;
								index = mCursor.getColumnIndex("port");
								sConnectionMobileDefaultPort = index > -1 ? mCursor
										.getInt(index)
										: sConnectionMobileDefaultPort;
								return CONNECTION_TYPE_CMWAP;
							} else {
								return CONNECTION_TYPE_CMNET;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null != mCursor) {
						mCursor.close();
					}
				}
				// gionee hanyong 20120621 start
				// default return CONNECTION_TYPE_CMNET
				return CONNECTION_TYPE_CMNET;
				// gionee hanyong 20120621 end
			}
		}
		return CONNECTION_TYPE_IDLE;
	}

	public boolean isForceUpdate() {
		return mIsForceUpdate;
	}

	public void setForceUpdate(boolean isForceUpdate) {
		this.mIsForceUpdate = isForceUpdate;
	}

	public void onDestroy() {
		try {
			mThd.stop();
			mDownloadThread.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mContext = null;
		mHandler = null;
	}

}
