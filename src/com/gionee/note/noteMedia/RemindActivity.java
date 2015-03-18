package com.gionee.note.noteMedia;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gionee.note.HomeActivity;
import com.gionee.note.NoteActivity;
import com.gionee.note.R;
import com.gionee.note.content.Session;
import com.gionee.note.content.StatisticalValue;
import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.Statistics;
import com.gionee.note.utils.UtilsQueryDatas;
import com.gionee.note.utils.WidgetUtils;

import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import amigo.app.AmigoAlertDialog.Builder;
import amigo.widget.AmigoButton;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class RemindActivity extends AmigoActivity {

	private ViewPager pager;
	private TextView noteNum;
	private ImageView mBtnDelete;
	private List<Note> noteList;
	private String removeNoteID = "";
	// private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// if (AlarmReceiver.UPDATE_DIALOG_RECEIVER.equals(intent.getAction())) {
	// Log.i("RemindActivity--onReceive");
	// Bundle bundle = intent.getExtras();
	// ArrayList<Parcelable> list = bundle
	// .getParcelableArrayList("list");
	// List<Note> receiverList = (List<Note>) list.get(0);//
	// 强转成你自己定义的list，这样list2就是你传过来的那个list了。
	// noteList.addAll(receiverList);
	// updateViews();
	// }
	// }
	//
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_remind);
		
		// Gionee <wangpan><2014-05-15> add for CR01249465 begin
		// Gionee <wangpan><2014-05-15> add for CR01291772 begin
		Session session = new Session();
        session.setScreenSize(RemindActivity.this);
        // Gionee <wangpan><2014-05-15> add for CR01291772 end
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams params = getWindow().getAttributes();  
		params.height = LayoutParams.WRAP_CONTENT;  
		params.width = Session.getScreenWight();
		getWindow().setAttributes(params);  
		dialogWindow.setGravity(Gravity.BOTTOM);
		// Gionee <wangpan><2014-05-15> add for CR01249465 end
        
		Statistics.setReportCaughtExceptions(true);
		// registerReceiver(mReceiver, new IntentFilter(
		// AlarmReceiver.UPDATE_DIALOG_RECEIVER));
		Bundle bundle = getIntent().getExtras();
		ArrayList<Parcelable> list = bundle.getParcelableArrayList("list");
		noteList = (List<Note>) list.get(0);// 强转成你自己定义的list，这样list2就是你传过来的那个list了。
		initViews();
		updateViews();
		registerBroadcast();
		int phoneInt = judgePhoneState();
		if(phoneInt == 0){
			addRing();
		}
		refreshAllWidget();
		// pengwei
		setPageIcon(curPage);
		// pengwei
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Bundle bundle = intent.getExtras();
		ArrayList<Parcelable> list = bundle.getParcelableArrayList("list");
		List<Note> receiverList = (List<Note>) list.get(0);
		noteList.addAll(receiverList);
		updateViews();
		registerBroadcast();
		int phoneInt = judgePhoneState();
		if(phoneInt == 0){
			addRing();
		}
		refreshAllWidget();
		// pengwei
		setPageIcon(curPage);
		// pengwei
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Statistics.onResume(this);
	}
	
	/* Registered to telephone closed ring broadcast */
	private void registerBroadcast() {
		// GN pengwei 2012-11-14 add for Call the bell ring then
		// end
		// begin
		IntentFilter colseRingRefFilter = new IntentFilter();
		colseRingRefFilter.addAction("android.intent.action.PHONE_STATE");
		colseRingRefFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(closeRingReceiver, colseRingRefFilter);
		// GN pengwei 2012-11-14 add for Call the bell ring then
		// end
		//Gionee <wangpan><2014-03-08> add for CR01035175 begin
		IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homePressedReceiver, filter );
        //Gionee <wangpan><2014-03-08> add for CR01035175 end
	}

    //Gionee <wangpan><2014-03-08> add for CR01035175 begin
    public BroadcastReceiver homePressedReceiver = new BroadcastReceiver() {
        
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RemindActivity-homePressedReceiver-onReceive: "+intent.getAction());
            String action = intent.getAction();
            if (!Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                return;
            }
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null) {
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // press home
                    Log.i("RemindActivity-press home");
                    if (removeNoteID.equals(CommonUtils.noteID)) {
                        closeCurNoteActivity(CommonUtils.INTENT_DEL, removeNoteID);
                    }
                    finish();
                }
            }
        }
    };
    //Gionee <wangpan><2014-03-08> add for CR01035175 end
	/* Judgment telephone state
	 * return: 0 -- Telephone free，1 -- Telephone busy
	 * */
	private int judgePhoneState() {
		TelephonyManager mTelephonyManager = (TelephonyManager) this
		.getSystemService(Service.TELEPHONY_SERVICE);
		if (mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
			return 0;
		}
		return 1;
	}

	// GN pengwei 2012-11-08 add for no voice begin
	private Ringtone rt;

	/* Give the alarm clock and the bell，start--modifier：pengwei */
	private void addRing() {
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int RingerMode = audio.getRingerMode();// Get contextual model，
		// RINGER_MODE_NORMAL:standard,outdoor,RINGER_MODE_SILENT:Forbidden sound，RINGER_MODE_VIBRATE:The meeting
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Log.v("addRing uri"
				+ uri);
		switch (RingerMode) {
		case AudioManager.RINGER_MODE_NORMAL:
			//gn pengwei 20130109 modify for CR00762174 begin
			if (null != rt) {
				Log.v("GN_Note---RemindActivity---addRing---rt---"
						+ rt);
				Log.v("GN_Note---RemindActivity---addRing---rt.isPlaying()---"
						+ rt.isPlaying());
				if (!rt.isPlaying()) {
					rt.play();
				}
			}else{
				rt = RingtoneManager.getRingtone(this, uri);
				rt.play();
				Log.v("GN_Note---RemindActivity---addRing---rt.isPlaying()---"
						+ rt.isPlaying());
			}
			//gn pengwei 20130109 modify for CR00762174 end
			break;
		case AudioManager.RINGER_MODE_SILENT:
			break;
		case AudioManager.RINGER_MODE_VIBRATE:
			break;
		default:
			break;
		}
	}

	// GN pengwei 2012-11-08 add for no voice begin

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Statistics.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
	    Log.i("RemingActivity-onDestroy");
		// GN pengwei 2012-11-08 add for no voice begin
		stopRing();
		// GN pengwei 2012-11-13 add begin
		if (closeRingReceiver != null) {
			unregisterReceiver(closeRingReceiver);
			closeRingReceiver = null;
		}
		// GN pengwei 2012-11-13 add end
		// GN pengwei 2012-11-08 add for no voice end
		// unregisterReceiver(mReceiver);

	    //Gionee <wangpan><2014-03-08> add for CR01035175 begin
		if(null != homePressedReceiver){
		    unregisterReceiver(homePressedReceiver);
		    homePressedReceiver = null;
		}
	    //Gionee <wangpan><2014-03-08> add for CR01035175 end
		super.onDestroy();
	}

	private void updateViews() {
		if (noteList.size() <= 0) {
			// GN pengwei 2012-11-13 add begin
			closeCurNoteActivity(CommonUtils.INTENT_DEL, removeNoteID);
			// GN pengwei 2012-11-13 add end
			finish();
			return;
		}
		ArrayList<View> views = new ArrayList<View>();
		for (int i = 0; i < noteList.size(); i++) {
			
			// gn lilg 2013-03-01 modify for CR00774636 begin
//			String content = noteList.get(i).getContent();
			String content = CommonUtils.noteContentPreDeal(noteList.get(i).getContent());
			// gn lilg 2013-03-01 modify for CR00774636 end
			
			String time = noteList.get(i).getAlarmTime();
			String oldStr = "";
			//gn pengwei 20130108 modify for CR00761070 begin
			if (time != null) {
				String nowStr = DateFormat.getInstance().format(new Date());
				oldStr = DateFormat.getInstance().format(
						new Date(Long.parseLong(time)));
				String now[] = nowStr.split(" ");
				String old[] = oldStr.split(" ");
				if (now[0].equals(old[0])) {
					if (old != null && old.length > 2) {
						oldStr = old[2] + " " + old[1];
					} else {
						oldStr = getResources().getString(R.string.alarm_today)
								+ old[1];
					}
				}

			//Gionee liuliang 2014-4-28 add for CR01200484 begin 
              oldStr = formatTo24or12TimeFormat(time);
			//Gionee liuliang 2014-4-28 add for CR01200484 end
			}
			//gn pengwei 20130108 modify for CR00761070 end
			View v = getLayoutInflater().inflate(R.layout.pager_remind, null);

			TextView timeText = (TextView) v.findViewById(R.id.pager_time);
			TextView contentText = (TextView) v
					.findViewById(R.id.pager_content);
			timeText.setText(oldStr);
			contentText.setText(content);

			views.add(v);
		}

		pager.setAdapter(new ViewPagerAdapter(views));

		noteNum.setText((pager.getCurrentItem() + 1) + "/" + noteList.size());
	}


   //Gionee liuliang 2014-4-28 add for CR01200484 begin
 	String formatTo24or12TimeFormat(String alarmTime){

	    if(alarmTime==null)
			return null;
		String oldStr = android.text.format.DateFormat.getTimeFormat(this).format(
				new Date(Long.parseLong(alarmTime)));
		Log.v("formatTo24or12TimeFormat oldStr "+oldStr);

		oldStr = getResources().getString(R.string.alarm_today)+oldStr;
		return oldStr;
		
		}
   //Gionee liuliang 2014-4-28 add for CR01200484 end

	// pengwei
	private ImageView iv_left;
	private ImageView iv_right;
	private int curPage = 1;
	private void initViews() {
		pager = (ViewPager) findViewById(R.id.pager);
		AmigoButton check = (AmigoButton) findViewById(R.id.btn_check);
		AmigoButton mBtnDelete = (AmigoButton) findViewById(R.id.dialog_delete);
		noteNum = (TextView) findViewById(R.id.dialog_note_num);
		ImageView close = (ImageView) findViewById(R.id.btn_close);
		// pengwei
		iv_left = (ImageView) findViewById(R.id.gn_iv_remind_left);
		iv_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (curPage > 1) {
					curPage--;
					setPageIcon(curPage);
					noteNum.setText(curPage + "/" + noteList.size());
					pager.setCurrentItem(curPage - 1);
					pager.invalidate();
				}
			}
		});
		iv_right = (ImageView) findViewById(R.id.gn_iv_remind_right);
		iv_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (curPage < noteList.size()) {
					curPage++;
					setPageIcon(curPage);
					noteNum.setText(curPage + "/" + noteList.size());
					pager.setCurrentItem(curPage - 1);
					pager.invalidate();
				}
			}
		});
		// pengwei
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				noteNum.setText((arg0 + 1) + "/" + noteList.size());
				curPage = arg0 + 1;
				// pengwei
				setPageIcon(curPage);
				// pengwei
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//gn pengwei 20121126 add for statistics begin
				Statistics.onEvent(RemindActivity.this, Statistics.NOTE_ALARM_LOOK);
				//gn pengwei 20121126 add for statistics end
				Intent noteIntent = new Intent(RemindActivity.this,
						NoteActivity.class);
				int pagerItem = pager.getCurrentItem();
				String noteId = noteList.get(pagerItem).getId();
				int parentId = noteList.get(pagerItem).getParentId();
				noteIntent.putExtra(DBOpenHelper.ID, Integer.parseInt(noteId));
				noteIntent.putExtra(DBOpenHelper.PARENT_FOLDER,parentId);
				noteIntent.putExtra(UtilsQueryDatas.enterIntoEditStr,UtilsQueryDatas.enterIntoEdit);
				// GN pengwei 2012-12-12 add for view no refresh begin
//				if (CommonUtils.noteID != null && !CommonUtils.noteID.equals(noteId)) {
//					noteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				} else {
				//gn pengwei 20120123 modify for CR00766943 begin
					noteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//gn pengwei 20120123 modify for CR00766943 end
//				}
				// GN pengwei 2012-12-12 add for view no refresh end
				startActivity(noteIntent);
				finish();
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//gn pengwei 20121126 add for statistics begin
				Statistics.onEvent(RemindActivity.this, Statistics.NOTE_ALARM_CLOSE);
				//gn pengwei 20121126 add for statistics end
				// GN pengwei 2012-11-13 add begin
				if (removeNoteID.equals(CommonUtils.noteID)) {
					closeCurNoteActivity(CommonUtils.INTENT_DEL, removeNoteID);
				}
				// GN pengwei 2012-11-13 add end
				finish();
			}
		});
		mBtnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//gn pengwei 20121126 add for statistics begin
				Statistics.onEvent(RemindActivity.this, Statistics.NOTE_ALARM_DEL);
				//gn pengwei 20121126 add for statistics end
				popupDialog();

			}
		});
		
	}

	// pengwei
	private void setPageIcon(int curPage) {
		if (curPage == 1) {
			iv_left.setEnabled(false);
			iv_left.setImageResource(R.drawable.gn_remind_left_dis);
		} else {
			iv_left.setEnabled(true);
			iv_left.setImageResource(R.drawable.gn_remind_left);
		}
		if (curPage == noteList.size()) {
			iv_right.setEnabled(false);
			iv_right.setImageResource(R.drawable.gn_remind_right_dis);
		} else {
			iv_right.setEnabled(true);
			iv_right.setImageResource(R.drawable.gn_remind_right);
		}
	}

	// pengwei
	
	private void popupDialog() {
		AmigoAlertDialog dialog;
		Builder builder = new AmigoAlertDialog.Builder(this);
		builder.setTitle(R.string.delete_note_dialog_title);
		builder.setMessage(R.string.delete_note_dialog_body);
		// gionee lilg 2013-01-16 modify for new demands begin
//		builder.setIcon(android.R.drawable.ic_dialog_alert);
		// gionee lilg 2013-01-16 modify for new demands end
		builder.setPositiveButton(R.string.delete_note_dialog_sure,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int pagerItem = pager.getCurrentItem();
				Note note = noteList.get(pagerItem);
				removeNoteID = note.getId();
				
				// gn lilg 2012-12-08 modify for optimization begin
				DBOperations.getInstances(RemindActivity.this).deleteNote(RemindActivity.this, note);
				// gn lilg 2012-12-08 modify for optimization end
				
				noteList.remove(note);
				if(note.getId() != null){
					Note noteUpdate = UtilsQueryDatas.queryNoteByID(Integer.valueOf(note.getId()),HomeActivity.mTempNoteList);
					if(noteUpdate != null){
						UtilsQueryDatas.deleteNote(noteUpdate, HomeActivity.mTempNoteList);
					}
				}
				updateViews();
				// GN pengwei 2012-11-13 add for View no refresh
				refreshWidget(note);
				//pengwei begin
				curPage = pager.getCurrentItem() + 1;
				setPageIcon(curPage);
				//pengwei end
				// closeCurNoteActivity(CommonUtils.INTENT_DEL,note.getId());
				// GN pengwei 2012-11-13 add for View no refresh begin
			}
		});
		builder.setNegativeButton(R.string.delete_note_dialog_cancle, null);
		builder.setCancelable(true);
		dialog = builder.create();
		dialog.show();
	}

	// GN pengwei 2012-11-12 add for CR00729333 begin
	private final String isAlarmStr = "0";

	/*
	 * Refresh various interface
	 */
	private void refreshAllWidget() {
		ArrayList<String> noteIDList = new ArrayList<String>();
		// gn lilg 2012-12-08 modify for optimization begin
		DBOperations dbo = DBOperations.getInstances(RemindActivity.this);
		// gn lilg 2012-12-08 modify for optimization end
		
		for (int i = 0; i < noteList.size(); i++) {
			//gn pengwei 20130110 modify for CR00762174 begin
			String tempAlarmStr = noteList.get(i).getAlarmTime();
			//gn pengwei 20130110 modify for CR00762174 end
			Note note = noteList.get(i);
			note.setAlarmTime(isAlarmStr);
			noteIDList.add(note.getId());
			dbo.updateNoteField(this, noteList.get(i),DBOpenHelper.ALARM_TIME);
			try {
				Note noteUpdate = UtilsQueryDatas.queryNoteByID(
						Integer.valueOf(note.getId()), HomeActivity.mTempNoteList);
				if (noteUpdate != null) {
					noteUpdate.setAlarmTime(isAlarmStr);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			//gn pengwei 20130110 modify for CR00762174 begin
			noteList.get(i).setAlarmTime(tempAlarmStr);
			//gn pengwei 20130110 modify for CR00762174 end
			WidgetUtils.updateWidget(this,
					Integer.parseInt(noteList.get(i).getWidgetId()),
					Integer.parseInt(noteList.get(i).getWidgetType()));
		}
		Intent intent = new Intent(CommonUtils.ALARMREFRESH);
		//gionee 20121226 pengwei modify for begin
		intent.putStringArrayListExtra("noteIDList", noteIDList);
		//gionee 20121226 pengwei modify for end
		sendBroadcast(intent);
	}

	private void refreshWidget(Note note) {
		WidgetUtils.updateWidget(this, Integer.parseInt(note.getWidgetId()),
				Integer.parseInt(note.getWidgetType()));
	}

	// GN pengwei 2012-11-12 add for CR00729333 end

	// GN pengwei 2012-11-13 add for View no refresh begin
	/*
	 * Send broadcast，Refresh NoteActivity interface
	 * optInt --
	 * The execution of operation, the reference CommonUtils INTENT_DEL, INTENT_LOOK, INTENT_CLOSE
	 */
	private void closeCurNoteActivity(int optInt, String noteID) {
		// Specified broadcast target Action
		Intent intent = new Intent(CommonUtils.NOTEACTIVITY_REFRESH);
		intent.putExtra("opt", optInt);
		intent.putExtra("noteID", noteID);
		// Send broadcast news
		sendBroadcast(intent);
	}

	// GN pengwei 2012-11-13 add for View no refresh end

	// GN pengwei 2012-11-12 add for CR00732487
	// begin
	/* Radio, used for receiving noteActivity refresh news */
	private BroadcastReceiver closeRingReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				// If it is to electric (dial out)
				stopRing();
			} else {
				// calls
				int stateInt = judgePhoneState();
				if(stateInt == 1){
					stopRing();
				}
			}

		}
	};

	private void stopRing() {
		if (null != rt) {
			rt.stop();
			rt = null;
		}
	}
	// GN pengwei 2012-11-12 add for CR00732487
	// end

	// GN pengwei 2012-12-26 add begin
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return true;
	}
	// GN pengwei 2012-12-26 add end
	
}
