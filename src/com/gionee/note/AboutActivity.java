package com.gionee.note;

import java.text.DecimalFormat;

import com.gionee.note.R;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.Session;
import com.gionee.note.content.StatisticalValue;
import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.utils.BaseHelper;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.GnUpgrade;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.Statistics;

import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import amigo.app.AmigoProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/*
 * The view of the about
 * @version 1.0
 * @author pengwei
 * @since 2012-11-26
 * */
public class AboutActivity extends AmigoActivity implements
		android.view.View.OnClickListener {
	private RelativeLayout aboutTitle;
	private TextView titleTv;
	private RelativeLayout about_linerlayout;
	private TextView tv_update;
	// gn pengwei 20121205 add for update begin
	private GnUpgrade mGnUpgrade;
	private AmigoProgressDialog mCheckPogressDialog = null;
	private AmigoProgressDialog mDownLoadPogressDialog = null;
	private AmigoAlertDialog mShowNewVersionDialog = null;
	private String payResultCode = CommonUtils.RESULT_CODE_FALSE;
	private LinearLayout line_update;
	private View mCustomView;
	private TextView mHomeTitle;
	// gn pengwei 20121205 add for update end
	protected void onCreate(Bundle savedInstanceState) {

		CommonUtils.setTheme(this);
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about_activity_layout_white);
		initActionBar();

		initResources();
		setTitle();
	}

	private void initResources() {
		Log.d("AboutActivity---init resources start!");
		titleTv = (TextView) findViewById(R.id.tv_content_title);
		tv_update = (TextView) findViewById(R.id.about_update_button);
		line_update = (LinearLayout) findViewById(R.id.line_update);
	}

	private void setTitle() {
		try {
			if (Statistics.versionName == null) {
				Statistics.getInfos(this);
			}
			String versionTitle = getResources().getString(R.string.about_version);
			String versionContent = Statistics.versionName == null ? "1.3.1"
					: Statistics.versionName;
			versionTitle = versionTitle + versionContent;
			titleTv.setText(versionTitle);
			tv_update.setOnClickListener(this);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("AboutActivity---setTitle!");
			e.printStackTrace();
		}
	}
	
	//gn pengwei 20121220 add for Common control begin
	private AmigoActionBar actionbar;
	private void initActionBar() {
		actionbar = this.getAmigoActionBar();
		// Gionee <lilg><2013-05-24> modify for CR00809680 begin
		// actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_title_bg_white));
		// Gionee <lilg><2013-05-24> modify for CR00809680 end
		mCustomView = LayoutInflater.from(actionbar.getThemedContext())
				.inflate(R.layout.gn_actionbar_title_view, null);
		actionbar.setCustomView(mCustomView, new AmigoActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mHomeTitle = (TextView) mCustomView
				.findViewById(R.id.actionbar_title);
        mHomeTitle.setText(getResources().getString(R.string.about_mode));
		actionbar.setIcon(R.drawable.gn_note_actionbar_icon);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayShowTitleEnabled(false);
		//actionbar.setTitle(this.getResources().getString(R.string.about_mode));
	}
	//gn pengwei 20121220 add for Common control end

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.about_update_button:
			boolean networkBool = judgeNetwork();
			if(!networkBool){
				return;
			}
			mCheckPogressDialog = BaseHelper.showProgress(this,
					getString(R.string.pay_show_title),
					getString(R.string.message_update_check), false, false);
			mGnUpgrade = new GnUpgrade(this, handler);
			mGnUpgrade.startCheck();
			break;
		default:

			break;
		}

	}

	// gn pengwei 20121205 add for update begin
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case GnUpgrade.MESSAGE_CHECK_RESULT_OK:
				BaseHelper.dialogCancel(mCheckPogressDialog);
				BaseHelper.dialogCancel(mShowNewVersionDialog);
				AmigoAlertDialog.Builder builder = new AmigoAlertDialog.Builder(
						AboutActivity.this);
				builder.setCancelable(true);
				builder.setTitle(R.string.upgrade_title);
				LayoutInflater layoutInflater = LayoutInflater
						.from(AboutActivity.this);
				View myView = layoutInflater.inflate(
						R.layout.gn_havenewversiondialog, null);
				builder.setView(myView);
				TextView nowVersionView = (TextView) myView
						.findViewById(R.id.now_version);

				float size = Float.parseFloat(mGnUpgrade.getFileSize());
				DecimalFormat df = new DecimalFormat("0.00");
				String fileSize = df.format(size / (1024 * 1024));
				StringBuilder sb = new StringBuilder();
				sb.append(AboutActivity.this.getString(R.string.now_version))
						.append(GnUpgrade.getVersion(AboutActivity.this, ""))
						.append("\n")

						.append(AboutActivity.this
								.getString(R.string.new_version))
						.append(mGnUpgrade.getNewVersion())
						.append(" ("
								+ AboutActivity.this
										.getString(R.string.file_size) + " "
								+ fileSize + "MB)")
						.append("\n")

						.append(AboutActivity.this
								.getString(R.string.new_feature_desc))
						.append("\n")

						.append(mGnUpgrade.getNewVersionDesc())
						.append("\n")
						.append(AboutActivity.this
								.getString(R.string.new_down_desc1))
						.append("\n")
						.append(AboutActivity.this
								.getString(R.string.new_down_desc2))
						.append("\n")
						.append(AboutActivity.this
								.getString(R.string.new_down_desc3))
						.append("\n");
				nowVersionView.setText(sb.toString());

				TextView tv = (TextView) myView.findViewById(R.id.nodisplay);

				final CheckBox checkbox = (CheckBox) myView
						.findViewById(R.id.CheckBox);
				// checkbox.setChecked(!mGnUpgrade.isNotify());
				final RelativeLayout rlCheckBox = (RelativeLayout) myView
						.findViewById(R.id.rlCheckBox);
				rlCheckBox.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (checkbox.isChecked()) {
							checkbox.setChecked(false);
							mGnUpgrade.setNotifyVersion("0");

						} else {
							checkbox.setChecked(true);
							mGnUpgrade.setNotifyVersion(mGnUpgrade
									.getNewVersion());
						}
					}
				});
				if (mGnUpgrade.isForceUpdate()
						|| mGnUpgrade.getNotifyVersion().equals(
								mGnUpgrade.getNewVersion())) {
					checkbox.setVisibility(View.GONE);
					tv.setVisibility(View.GONE);
				}

				builder.setPositiveButton(R.string.download_now,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mGnUpgrade.startDownload();
								dialog.dismiss();
							}
						});
				builder.setNegativeButton(R.string.donot_download,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (mGnUpgrade.isForceUpdate()) {

									AmigoAlertDialog.Builder forcebuilder = new AmigoAlertDialog.Builder(
											AboutActivity.this);
									forcebuilder.setCancelable(false);
									forcebuilder
											.setTitle(R.string.upgrade_title);
									forcebuilder.setMessage(R.string.force);
									forcebuilder
											.setPositiveButton(
													R.string.download_now,
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															mGnUpgrade
																	.startDownload();
															dialog.dismiss();
														}
													});
									forcebuilder
											.setNegativeButton(
													R.string.donot_download,
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															finish();
															// android.os.Process
															// .killProcess(android.os.Process
															// .myPid());

														}
													});
									forcebuilder.create().show();
								} else {
									handler.sendEmptyMessage(GnUpgrade.MESSAGE_NO_NEED_UPDATE);
								}
							}
						});
				// Gionee liujianyu 2012-10-30 add for CR00722735 start
				// if (mGnUpgrade.isForceUpdate()) {
				// builder.setCancelable(false);
				// }
				//
				mShowNewVersionDialog = builder.create();
				// if (mGnUpgrade.isForceUpdate()) {
				mShowNewVersionDialog.setCancelable(false);
				// }
				// Gionee liujianyu 2012-10-30 add for CR00722735 end
				BaseHelper.dialogShow(mShowNewVersionDialog);
				Log.d("AboutActivity------MESSAGE_CHECK_RESULT_OK");
				break;

			case GnUpgrade.MESSAGE_CHECK_RESULT_NO_UPDATE:
				BaseHelper.dialogCancel(mCheckPogressDialog);
				Toast.makeText(AboutActivity.this, getString(R.string.gn_about_no_need_update),
						Toast.LENGTH_SHORT).show();
				Log.d("AboutActivity------MESSAGE_CHECK_RESULT_NO_UPDATE");
				break;
			case GnUpgrade.MESSAGE_NO_NEED_UPDATE:
				BaseHelper.dialogCancel(mCheckPogressDialog);
				Toast.makeText(AboutActivity.this, getString(R.string.gn_about_no_need_update),
						Toast.LENGTH_SHORT).show();
				Log.d("AboutActivity------MESSAGE_NO_NEED_UPDATE");
				break;
			case GnUpgrade.MESSAGE_MESSAGE_DOWNLOAD_NETWORK_ERROR:
				BaseHelper.dialogCancel(mCheckPogressDialog);
				BaseHelper.dialogCancel(mDownLoadPogressDialog);
				AmigoAlertDialog.Builder downloadDialog = new AmigoAlertDialog.Builder(
						AboutActivity.this);
				downloadDialog.setTitle(R.string.pay_show_title);
				downloadDialog.setMessage(R.string.message_update_error);
				downloadDialog.setPositiveButton(R.string.download_retry,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mGnUpgrade.startDownload();
							}
						});
				downloadDialog.setNegativeButton(R.string.Cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				downloadDialog.setCancelable(true);
				BaseHelper.dialogShow(downloadDialog.create());
				payResultCode = CommonUtils.RESULT_CODE_NET_EXCETION;
				Log.d("AboutActivity------MESSAGE_MESSAGE_DOWNLOAD_NETWORK_ERROR");
				break;

			case GnUpgrade.MESSAGE_CHECK_RESULT_NETWORK_ERROR:
				BaseHelper.dialogCancel(mCheckPogressDialog);

				// AmigoAlertDialog.Builder tDialog = new
				// AmigoAlertDialog.Builder(HomeActivity.this);
				// tDialog.setTitle(R.string.pay_show_title);
				// tDialog.setMessage(R.string.message_network_error);
				// tDialog.setPositiveButton(R.string.Ok, new
				// DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// HomeActivity.this.finish();
				// }
				// });
				// tDialog.setCancelable(false);

				// BaseHelper.dialogShow(tDialog.create());
				// payResultCode = CommonUtils.RESULT_CODE_NET_EXCETION;
				Log.d("AboutActivity------MESSAGE_CHECK_RESULT_NETWORK_ERROR");
				break;

			case GnUpgrade.MESSAGE_START_DOWNLOAD:
				mDownLoadPogressDialog = BaseHelper.showProgress(
						AboutActivity.this, getString(R.string.pay_show_title),
						getString(R.string.message_update_apk), false, false);
				Log.d("AboutActivity------MESSAGE_START_DOWNLOAD");
				break;

			case GnUpgrade.MESSAGE_NO_ENOUGH_SPACE:

				BaseHelper.dialogCancel(mDownLoadPogressDialog);

				AmigoAlertDialog.Builder noEnoughSpace = new AmigoAlertDialog.Builder(
						AboutActivity.this);
				noEnoughSpace.setTitle(R.string.pay_show_title);
				noEnoughSpace.setMessage(R.string.message_update_error_nospace);
				noEnoughSpace.setPositiveButton(R.string.Ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								AboutActivity.this.finish();
							}
						});
				noEnoughSpace.setCancelable(false);
				BaseHelper.dialogShow(noEnoughSpace.create());
				payResultCode = CommonUtils.RESULT_CODE_UPDATE_EXCETION;
				Log.d("AboutActivity------MESSAGE_NO_ENOUGH_SPACE");
				break;

			case GnUpgrade.MESSAGE_NO_SDCARD:

				BaseHelper.dialogCancel(mDownLoadPogressDialog);

				AmigoAlertDialog.Builder noSdcard = new AmigoAlertDialog.Builder(
						AboutActivity.this);
				noSdcard.setTitle(R.string.pay_show_title);
				noSdcard.setMessage(R.string.message_update_error_nosdcard);
				noSdcard.setPositiveButton(R.string.Ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								AboutActivity.this.finish();
							}
						});
				noSdcard.setCancelable(false);
				BaseHelper.dialogShow(noSdcard.create());
				payResultCode = CommonUtils.RESULT_CODE_UPDATE_EXCETION;
				Log.d("AboutActivity------MESSAGE_NO_SDCARD");
				break;

			case GnUpgrade.MESSAGE_DOWNLOAD_COMPLETE:
				BaseHelper.dialogCancel(mCheckPogressDialog);
				BaseHelper.dialogCancel(mDownLoadPogressDialog);
				mGnUpgrade.installNow();
				Log.d("AboutActivity------MESSAGE_DOWNLOAD_COMPLETE");
				break;
//			case GnUpgrade.MESSAGE_START_TO_INSTALL:
//				BaseHelper.dialogCancel(mCheckPogressDialog);
//				Log.d("AboutActivity------MESSAGE_START_TO_INSTALL");
//				break;
			default:
				break;
			}
		}

	};

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
//	      mShowNewVersionDialog.cancel();
//	      return false;
//	      }
//        return super.onKeyDown(keyCode, event);
//	};
	
		private boolean judgeNetwork() {
			ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable() && info.isConnected()) {
				return true;
			} else {
				Toast.makeText(this, getString(R.string.gn_location_nonet),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			default:
				break;
			}
			return true;
		}
	
		@Override
		protected void onResume() {
			super.onResume();
			Log.i("AboutActivity------onResume()!");
			
			// Gionee <lilg><2013-04-10> add for note upgrade begin
			((NoteApplication) getApplication()).registerVersionCallback(this);
			// Gionee <lilg><2013-04-10> add for note upgrade end
		}
		
		@Override
		protected void onPause() {
			Log.i("AboutActivity------onPause()!");
			
			// Gionee <lilg><2013-04-10> add for note upgrade begin
			((NoteApplication) getApplication()).unregisterVersionCallback(this);
			// Gionee <lilg><2013-04-10> add for note upgrade end
			
			super.onPause();
		}
}
