package com.gionee.note.noteMedia.record;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.text.TextUtils;

import com.gionee.note.NoteActivity;
import com.gionee.note.R;
import com.gionee.note.content.Constants;
import com.gionee.note.utils.FileUtils;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.PlatForm;

/**
 * note media manager
 * @author lilg
 * 2013-02-19
 *
 */
public class NoteMediaManager {

	private static NoteMediaManager sInstance;
	public static final String SUFFIX_MP3 = ".mp3";
	public static final String SUFFIX_TXT = ".txt";
	
	public static final String TAG_PREFIX = "<gionee_media:";
	public static final String TAG_MIDDLE = ":";
	public static final String TAG_STORE_SUFFIX = "/>>";
	public static final String TAG_PARSE_SUFFIX = "/>";
	public static final String TAG_LARGE_STRING = ">";
	
	public static final String TYPE_MEDIA_RECORD = "0";
	
	public static final String PATH_TYPE_SD_CARD = "0";
	public static final String PATH_TYPE_INTERNAL_MEMORY = "1";
	
	public static final int SUCCESS_RECORDER_START = 100;
	public static final int ERROR_RECORDER_PREPARE = 101;
	public static final int ERROR_RECORDER_START = 102;
	public static final int SUCCESS_PLAYER_START = 103;
	public static final int ERROR_PLAYER_START = 104;
	public static final int ERROR_DURATION = -1;
	
	public static final int RECORD_TIME_MAX_MINUTE = 30;
	public static final int RECORD_TIME_MAX_SECOND = 0;
	
	private Context mContext;
	private Handler mHandler;
	private MediaRecorder mMediaRecorder;
	private MediaPlayer mMediaPlayer;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
	private boolean recording = false;
	private int mPlayBackPosition = 0;
	private String mRecordPathType; // sdcard or internal memory
	private String mRecordFilePath;
	private String mRecordFileName;
	private long mMediaItemIdCurrentPlaying;

	private String mMediaItemFileNameCurrentPlaying;

	private NoteMediaManager() {

	}

	private NoteMediaManager(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
	}

	public static synchronized NoteMediaManager getInstances(Context context, Handler handler) {
		if (sInstance == null) {
			sInstance = new NoteMediaManager(context, handler);
		}
		return sInstance;
	}

	// Gionee <lilg><2013-07-01> add for CR00830261 begin
	public void release() {
		if(mMediaRecorder != null){
			mMediaRecorder.release();
		}
		if(mMediaPlayer != null){
			mMediaPlayer.release();
		}
		clearInstance();
	}
	// Gionee <lilg><2013-07-01> add for CR00830261 end

    private synchronized static void clearInstance() {
        sInstance = null;
    }


	
	public int startRecording(String noteMediaFolderName, String path, String pathType) {

		initRecordFile(noteMediaFolderName, path, pathType);

		mMediaRecorder = new MediaRecorder();
		// 设置音源为Micphone
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 设置封装格式
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mMediaRecorder.setOutputFile(mRecordFilePath + "/" + mRecordFileName);
		// 设置编码格式
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		mMediaRecorder.setOnErrorListener(new android.media.MediaRecorder.OnErrorListener(){
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				Log.e("NoteMediaManager------media recorder recording error!");
				// send a message to stop media recorder
				if(mHandler != null){
					mHandler.sendEmptyMessage(NoteActivity.ERROR_RECORDER_RECORDING);
				}
			}
		});
		
		try {
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			recording = true;
		} catch (IllegalStateException e1) {
			Log.e("NoteMediaManager------media record prepare IllegalStateException!", e1);
			return ERROR_RECORDER_PREPARE;
		} catch (IOException e1) {
			Log.e("NoteMediaManager------media record prepare IOException!", e1);
			return ERROR_RECORDER_PREPARE;
		} catch (Exception e1) {
			Log.e("NoteMediaManager------media record start Exception!", e1);
			return ERROR_RECORDER_START;
		}
		
		return SUCCESS_RECORDER_START;
	}
	
	private void initRecordFile(String noteMediaFolderName,String path, String pathType){
		
		if(!TextUtils.isEmpty(noteMediaFolderName) && !"-1".equals(noteMediaFolderName)){
			mRecordFilePath = noteMediaFolderName.substring(1);
		}else{
			mRecordFilePath = path + mContext.getResources().getString(R.string.path_note_media) + "/" + System.currentTimeMillis();
		}
		mRecordPathType = pathType;
		
		Log.d("NoteMediaManager------mRecordFilePath-8: " + mRecordFilePath + " "+ mRecordPathType);
		mRecordFilePath = FileUtils.replaceTwoDivideToOne(mRecordFilePath);
		Log.d("NoteMediaManager------mRecordFilePath-9: " + mRecordFilePath);
		File recordFilePath = new File(mRecordFilePath);
		if(!recordFilePath.exists()){
			recordFilePath.mkdirs();
		}
		mRecordFileName = createMediaFileName();
		Log.d("NoteMediaManager------mRecordFileName: " + mRecordFileName);
		File recordFileName = new File(mRecordFilePath + "/" + mRecordFileName);
		if(recordFileName.exists()){
			recordFileName.delete();
		}
	}
	
	private String createMediaFileName() {
		return mSimpleDateFormat.format(new Date()) + SUFFIX_MP3;
	}

	/**
	 * stop recording
	 */
	public void stopRecording() {
		try{
			if (mMediaRecorder != null) {
				recording = false;
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
		}catch(Exception e){
			Log.e("NoteMediaManager------media recorder stop Exception!", e);
		}
	}


	
	public int startPlaying(String mediaFileName) {

		if(TextUtils.isEmpty(mediaFileName)){
			Log.e("NoteMediaManager------the file to play is null!");
			return ERROR_PLAYER_START;
		}

		mMediaItemFileNameCurrentPlaying = mediaFileName.substring(mediaFileName.lastIndexOf("/") + 1);

		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
				@Override
				public void onCompletion(MediaPlayer mp) {
					Log.i("NoteMediaManager------player play complete!");
					// send a message to stop media player
					if(mHandler != null){
						mHandler.sendEmptyMessage(NoteActivity.SUCCESS_PLAYER_PLAYING);
					}
				}
			});
			mMediaPlayer.setOnErrorListener(new OnErrorListener(){
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					Log.e("NoteMediaManager------player play error!");
					// send a message to stop media player
					if(mHandler != null){
						mHandler.sendEmptyMessage(NoteActivity.ERROR_PLAYER_PLAYING);
					}
					// True if the method handled the error, false if it didn't.
					// Returning false, or not having an OnErrorListener at all, will cause the OnCompletionListener to be called.
					// Gionee <lilg><2013-04-08> modify for CR00790499 begin
					return true;
					// Gionee <lilg><2013-04-08> modify for CR00790499 end
				}
			});
			
			mMediaPlayer.setDataSource(mediaFileName);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			Log.e("NoteMediaManager------media player start exception!", e);
			return ERROR_PLAYER_START;
		}

		return SUCCESS_PLAYER_START;
	}


	
	/**
	 * stop playing
	 */
	public void stopPlaying() {
		try{
			if (mMediaPlayer != null) {
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
		}catch(Exception e){
			Log.e("NoteMediaManager------media player stop Exception!", e);
		}
	}

	/**
	 * if is media recording now
	 * @return
	 */
	public boolean isRecording() {
		return recording;
	}
	
	/**
	 * if is media playing now
	 * @return
	 */
	public boolean isPlaying(){
		if(mMediaPlayer != null){
			return mMediaPlayer.isPlaying();
		}
		return false;
	}

	/**
	 * return the recorded file path
	 * @return
	 */
	public String getmRecordFilePath() {
		return mRecordFilePath;
	}

	/**
	 * return the recorded file name
	 * @return
	 */
	public String getmRecordFileName() {
		return mRecordFileName;
	}
	
	
	public String getmRecordPathType() {
		return mRecordPathType;
	}

	/**
	 * return the media player current position
	 * @return
	 */
	public int getMediaPlayerCurrentPosition(){
		if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
			return mMediaPlayer.getCurrentPosition();
		}
		return ERROR_DURATION;
	}
	
	/**
	 * return the media player duration
	 * @return
	 */
	public int getMediaPlayerDuration(){
		if(mMediaPlayer != null){
			return mMediaPlayer.getDuration();
		}
		return ERROR_DURATION;
	}


	public String getmMediaItemFileNameCurrentPlaying() {
		return mMediaItemFileNameCurrentPlaying;
	}
	
		public MediaRecorder getmMediaRecorder() {
			return mMediaRecorder;
		}
	 
}
