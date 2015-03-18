package com.gionee.note;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import android.animation.ObjectAnimator;
import amigo.app.AmigoActionBar;
import amigo.app.AmigoActivity;
import amigo.app.AmigoAlertDialog;
import amigo.app.AmigoProgressDialog;
import amigo.app.AmigoAlertDialog.Builder;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import amigo.preference.AmigoPreferenceManager;
import amigo.provider.AmigoSettings;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.StaticLayout;
import android.text.TextWatcher;

import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.CheckBox;
import amigo.widget.AmigoEditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.gionee.note.R;
import com.gionee.note.adapter.DragGridViewReal;
import com.gionee.note.adapter.DragListViewReal;
import com.gionee.note.adapter.ViewAdapter;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.Notes;
import com.gionee.note.content.Session;
import com.gionee.note.content.StatisticalName;
import com.gionee.note.content.StatisticalValue;
import com.gionee.note.database.DBOpenHelper;
import com.gionee.note.database.DBOperations;
import com.gionee.note.domain.MediaInfo;
import com.gionee.note.domain.Note;
import com.gionee.note.noteMedia.location.GNLocateService2;
import com.gionee.note.utils.BaseHelper;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.FileUtils;
import com.gionee.note.utils.GnUpgrade;
import com.gionee.note.utils.Log;
import com.gionee.note.utils.PlatForm;
import com.gionee.note.utils.Statistics;
import com.gionee.note.utils.UtilsQueryDatas;
import com.gionee.note.utils.WidgetUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
 * the main list view: all folder and note without the father
 */
//Gionee <pengwei><20130805> modify for CR00844807 begin
public class HomeActivity extends AmigoActivity {

	private ViewAdapter adapter;
	public static boolean isGridView = true; // The current view mode
	private static boolean isInFolder = false; // The current is in the folder
	private static int folderId = -1; // The current in where folder
	private DBOperations dbo;
	
	private List<Note> mDataSource;
//	private TextView mFolderTitle;
//	private TextView mHomeTitleView;
	private DragListViewReal mListview;
	private DragGridViewReal mGridView;
//	private ImageButton menu_button;
	private View mDelete_button;
	private View mShareButton;
//	private ImageButton mAdd_note_button;
	private TextView mNoNoteText;
	private ImageView mNoNoteImage;
	private View homeOprateLayout;
//	private View mHomeFolderTitleIcon;
//	private View mHomeTitleIcon;
//	private View mTitleDiverView;
	//gionee 20121226 jiating modify for theme begin
//	private View mListDiverBottom;
	//gionee 20121226 jiating modify for theme end
	private AmigoProgressDialog mProgressDialog;
	private static int TITLE_MAX_LENGTH = 60; // folder title maxLength
	// gn lilg 2012-11-19 modify for CR00734123 start
	// be show when long click and drag to delete note
	private Dialog dialog;
	// gn lilg 2012-11-19 modify for CR00734123 end
	

	//gn pengwei 20121205 add for update end
	//gionee 20121214 jiating modfity for theme begin
	private AmigoActionBar  mActionBar;
	private Menu mOptionsMenu = null;
	//gionee 20121218 jiating modfity for theme begin

	private MenuItem  mSearch;
	private MenuItem mAddNote;
	private MenuItem mNewFolder;
	private MenuItem mAllOperator;
	private MenuItem mInportExport;
	private MenuItem mAboutNote;
	private View mCustomView;
	private ImageButton mChangeListMode;
	private ImageButton mChangeGridMode;
	//gionee 20121218 jiating modfity for theme end

	private ActionMode mActionMode; 
	private TextView mHomeTitle;
	private TextView mFolderTitleText;
	private View mFileCountInFolder;

	// gionee lilg 2013-01-11 add for default note begin
	private static final String SHARED_NAME = "home_activity_shared_data";
	private static final String SHARED_KEY = "isFirstInNote";
	private static final String SHOW_DAILOG = "showDailog";
	public static final long delayTime = 1000;
	private boolean startDialog = false;
	// gionee lilg 2013-01-11 add for default note end
	//gionee 20121214 jiating modfity for theme end
	public static List<Note> mTempNoteList = null;
	
	// Gionee <lilg><2013-05-11> add for CR00809745 begin
	private AmigoAlertDialog alertDialog;
	// Gionee <lilg><2013-05-11> add for CR00809745 end
	
	
	
	// Receive from the search interface, click on the folder
	private BroadcastReceiver mSearchViewClickNoteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			isInFolder = intent.getBooleanExtra(Constants.IS_IN_FOLDER, false);
			folderId = intent.getIntExtra(DBOpenHelper.ID, -1);
			// titleName = intent.getStringExtra(DBOpenHelper.NOTE_TITLE);
			updateDisplay();
		}
	};
	// gn jiating 20121009 GN_GUEST_MODE begin
	private ContentObserver mGusestModeObserver = new ContentObserver(
			new Handler(Looper.getMainLooper())) {

		@Override
		public void onChange(boolean selfChange) {
			Log.d("HomeActivity------Guest mode changed, selfChange: " + selfChange);

			NoteApplication.setGueseMode(CommonUtils.getIsGuestMode(getContentResolver()));
			// gionee 20121026 jiating CR00718021 begin
			// gn jiating 20121009 GN_GUEST_MODE begin
			if (NoteApplication.GN_GUEST_MODE) {
				isInFolder = false;
			}
			// gn jiating 20121009 GN_GUEST_MODE end
			if (!isInFolder) {
				folderId = -1;
			}
			//Gionee <wangpan><2014-07-25> modify for CR01323896 begin
			runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateDisplay();
                }
            });
            //Gionee <wangpan><2014-07-25> modify for CR01323896 end
			// gionee 20121026 jiating CR00718021 end
			
			// Gionee <lilg><2013-05-11> add for CR00809745 begin
			if(alertDialog != null && alertDialog.isShowing()){
				alertDialog.dismiss();
			}
			// Gionee <lilg><2013-05-11> add for CR00809745 end
		}

	};
	//Gionee <pengwei><20130615> modify for CR00825786 begin
	// Gionee <wangpan><2014-08-19> modify for CR01358209 begin 
	private Handler homeProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Bundle b = msg.getData();
                Boolean isShowDialog = b.getBoolean(SHOW_DAILOG);
                if (isShowDialog && startDialog) {
                    if (mProDialog != null && !mProDialog.isShowing()) {
                        mProDialog
                                .setProgressStyle(AmigoProgressDialog.STYLE_SPINNER);
                        mProDialog.setMessage(getResources().getString(
                                R.string.data_loading));
                        mProDialog.setCancelable(false);
                        if (!isFinishing()) {
                            mProDialog.show();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("HomeActivity---handleMessage---e == " + e);
            }
        }
    };
    // Gionee <wangpan><2014-08-19> modify for CR01358209 end
    //Gionee <pengwei><20130615> modify for CR00825786 end
	private AmigoProgressDialog mProDialog;

	// gn jiating 20121009 GN_GUEST_MODE end
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("HomeActivity------onCreate");

		CommonUtils.setTheme(this);
		
		registerReceiver(mSearchViewClickNoteReceiver, new IntentFilter(
				Constants.START_FOLDER_ACTIVITY_ACTION));

		// gn jiating 20121009 GN_GUEST_MODE begin
		getContentResolver().registerContentObserver(
				CommonUtils.getUri(), false,
				mGusestModeObserver);

		NoteApplication.setGueseMode(CommonUtils.getIsGuestMode(getContentResolver()));
		// gn jiating 20121009 GN_GUEST_MODE end

		super.onCreate(savedInstanceState);
		// cancel title
		//gionee 20121214 jiating modfity for theme begin
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		boolean gridView = AmigoPreferenceManager.getDefaultSharedPreferences(this)
		.getBoolean(Constants.HOME_SHOW_VIEW_MODE, true);
		setIsGridView(gridView);
   	
		//gionee 20121214 jiating modfity for theme end
		setContentView(R.layout.home_view_white);
		initActionBar();
		Session session = new Session();
		session.setScreenSize(HomeActivity.this);
		initViews();
		// gn lilg 2012-12-30 modify for CR00754900 begin
		//		folderId = -1;
		// gn lilg 2012-12-30 modify for CR00754900 end
		
		// titleName = getResources().getString(R.string.home_title);

		mDataSource = new ArrayList<Note>();
		adapter = new ViewAdapter(this, mDataSource);
		setShowViewMode();
		
		// gn lilg 2013-01-08 add for net alert begin
		initGPRSAlert();
		// gn lilg 2013-01-08 add for net alert end
		
		// gionee lilg 2013-01-10 add for default note begin
		initDefaultNote();
		// gionee lilg 2013-01-10 add for default note end
		mProDialog = new AmigoProgressDialog(HomeActivity.this,CommonUtils.getTheme());
		startDialog = true;
		//Gionee <pengwei><20130615> modify for CR00824222 begin
		new UpdateHomeViewTask(this).executeOnExecutor((ExecutorService)Executors.newCachedThreadPool());
		//Gionee <pengwei><20130615> modify for CR00824222 end
        
        // Gionee <lilg><2013-04-10> add for note upgrade begin
        if (NoteApplication.isUpgradeSupport()) {
        	Log.d("HomeActivity------start app upgrade version check service!");
			Intent checkIntent = new Intent("android.intent.action.GN_APP_UPGRADE_CHECK_VERSION");
			checkIntent.putExtra("package", getApplicationContext().getPackageName());
			startService(checkIntent);
		}
        // Gionee <lilg><2013-04-10> add for note upgrade end
	}

	private static void setIsGridView(boolean isGridView){
	    HomeActivity.isGridView = isGridView;
	}
    private void initGPRSAlert(){
		// gn lilg 2013-01-08 add for net alert begin
		Intent alertIntent = new Intent("gn.android.intent.action.PopupNetworkAlert");
		alertIntent.putExtra("appname",getPackageName());
        sendBroadcast(alertIntent);
	}
	
	private void initDefaultNote(){
		Log.i("HomeActivity------initDefaultNote begin!");
		SharedPreferences shared = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);
		boolean isFirstInNote = shared.getBoolean(SHARED_KEY, true);
		Log.d("HomeActivity------isFirstInNote: " + isFirstInNote);
		if(isFirstInNote){
			createDefaultNote();
			SharedPreferences.Editor editor = shared.edit();
			editor.putBoolean(SHARED_KEY, false);
			editor.commit();
		}
		Log.d("HomeActivity------initDefaultNote end!");
	}
	
	private void createDefaultNote(){
		Log.i("HomeActivity------createDefaultNote begin!");
		dbo = DBOperations.getInstances(HomeActivity.this);
		Note note = new Note();
		note.setTitle(getResources().getString(R.string.default_note_title));
		note.setContent(getResources().getString(R.string.default_note_content));
		note.setUpdateDate(dbo.getDate());
		note.setUpdateTime(dbo.getTime());
		note.setBgColor("0");
		note.setIsFolder("no");
		note.setParentFile("no");
		note.setWidgetId("-1");
		note.setWidgetType("-1");
		dbo.createNote(this, note);
		Log.d("HomeActivity------createDefaultNote end!");
	}
	
	//gionee 20121218 jiating modify for theme begin
	private ImageButton mHomeBack;
	private void initActionBar() {
		mActionBar = this.getAmigoActionBar();
		//mCustomView = getLayoutInflater().inflate(R.layout.gn_note_home_actionbar_view, null);
		mCustomView = LayoutInflater.from(mActionBar.getThemedContext())
				.inflate(R.layout.gn_note_home_actionbar_view, null);
		mActionBar.setCustomView(mCustomView, new AmigoActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mActionBar.setIcon(R.drawable.gn_note_actionbar_icon);
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);

		mHomeBack = (ImageButton) mCustomView
				.findViewById(R.id.actionbar_home_folder_back);
		mHomeBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isInFolder) {
					// in folder click backIcon
					isInFolder = false;
					folderId = -1;
					updateDisplay();
					Log.v("DayView---onOptionsItemSelected---");
				}
			}
		});
		mHomeTitle = (TextView) mCustomView
				.findViewById(R.id.actionbar_home_title);
		mFolderTitleText = (TextView) mCustomView
				.findViewById(R.id.actionbar_home_folder_title);
		mChangeGridMode = (ImageButton) mCustomView
				.findViewById(R.id.gn_note_grid_mode);
		mChangeListMode = (ImageButton) mCustomView
				.findViewById(R.id.gn_note_list_mode);
		//gionee 20121224  jiating modify for theme begin
		if(isGridView){
			mChangeListMode.setVisibility(View.VISIBLE);
			mChangeGridMode.setVisibility(View.GONE);
		}else{
			mChangeListMode.setVisibility(View.GONE);
			mChangeGridMode.setVisibility(View.VISIBLE);
		}
		//gionee 20121224  jiating modify for theme begin
		mFolderTitleText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("HomeActivity------mFolderTitleText onClick, isInFolder: " + isInFolder);
				
				if (isInFolder) {
					newOrEditFolder(false);
				}

			}
		});
		mChangeGridMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			    // Gionee <wangpan><2014-10-22> add for CR01396370 begin
			    if(null != DragGridViewReal.moveNote || null != DragListViewReal.moveNote){
			        return;
			    }
                // Gionee <wangpan><2014-10-22> add for CR01396370 begin
				mChangeListMode.setVisibility(View.VISIBLE);
				mChangeGridMode.setVisibility(View.GONE);
				if (isInFolder) {
					Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_FOLDER_OPERATION_SWITCH);
				} else {
					Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_OPERATION_SWITCH);
				}
				// gn pengwei 20121126 add for statistics end

				isGridView = true;
				setShowViewMode();
			}
		});
		
		mChangeGridMode.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
                // Gionee <wangpan><2014-10-22> add for CR01396370 begin
                if(null != DragGridViewReal.moveNote || null != DragListViewReal.moveNote){
                    return true;
                }
                // Gionee <wangpan><2014-10-22> add for CR01396370 end
				CommonUtils.showToast(HomeActivity.this,getResources().getString(R.string.select_gridview_mode));
				return true;
			}
		});

		mChangeListMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                // Gionee <wangpan><2014-10-22> add for CR01396370 begin
                if(null != DragGridViewReal.moveNote || null != DragListViewReal.moveNote){
                    return;
                }
                // Gionee <wangpan><2014-10-22> add for CR01396370 end
				mChangeListMode.setVisibility(View.GONE);
				mChangeGridMode.setVisibility(View.VISIBLE);
				if (isInFolder) {
					Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_FOLDER_OPERATION_SWITCH);
				} else {
					Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_OPERATION_SWITCH);
				}
				// gn pengwei 20121126 add for statistics end

				isGridView = false;
				setShowViewMode();
			}
		});
		
		mChangeListMode.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
                // Gionee <wangpan><2014-10-22> add for CR01396370 begin
                if(null != DragGridViewReal.moveNote || null != DragListViewReal.moveNote){
                    return true;
                }
                // Gionee <wangpan><2014-10-22> add for CR01396370 end
				CommonUtils.showToast(HomeActivity.this,getResources().getString(R.string.select_listview_mode));
				return true;
			}
		});

		// Gionee <lilg><2013-05-24> modify for CR00809680 begin
		// mActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_title_bg_white));
		// Gionee <lilg><2013-05-24> modify for CR00809680 end
		
	}
	//gionee 20121218 jiating modify for theme end
	/*
	 * init the view
	 */
	private void initViews() {
		Log.i("HomeActivity------initViews");

		mNoNoteImage = (ImageView) findViewById(R.id.home_no_note_image);
		mNoNoteText = (TextView) findViewById(R.id.home_no_note_text);
//		mHomeFolderTitleIcon = findViewById(R.id.home_folder_title_icon);
//		mListDiverBottom = findViewById(R.id.list_divide_Bottom);
		// homeFolderTitleView=findViewById(R.id.home_folder_title_view);
//		mTitleDiverView = findViewById(R.id.dividerBotton);
		mGridView = (DragGridViewReal) findViewById(R.id.home_gridview);
		mListview = (DragListViewReal) findViewById(R.id.home_listview);
//		mFolderTitle = (TextView) findViewById(R.id.home_folder_title);
//		mHomeTitleView = (TextView) findViewById(R.id.home_title);
		mDelete_button = (View) findViewById(R.id.home_delete_btn);
		mShareButton = (View) findViewById(R.id.home_share_btn);
//		mAdd_note_button = (ImageButton) findViewById(R.id.home_add_note_button);
		homeOprateLayout = findViewById(R.id.home_oprate_layout);
		homeOprateLayout.setVisibility(View.GONE);
//		mAdd_note_button.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// new Note Button Click
//				newNote();
//			}
//		});

//		mHomeFolderTitleIcon.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// in folder click backIcon
//				isInFolder = false;
//				folderId = -1;
//				updateDisplay();
//
//			}
//		});
		
		
		//gionee 20121214 jiating modfity for theme begin
		/**
		// title.setWidth(7 / 8 * width);
		menu_button = (ImageButton) findViewById(R.id.home_menu_button);
		if (ViewConfiguration.get(this).hasPermanentMenuKey()) {
			menu_button.setVisibility(View.GONE);

		}
		menu_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final PopupMenu popupMenu = new PopupMenu(HomeActivity.this, v);
				final Menu menu = popupMenu.getMenu();
				popupMenu.inflate(R.menu.setting_menu);
				final MenuItem searchMenuItem = menu.findItem(R.id.search_mode);
				searchMenuItem
						.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

				final MenuItem mNewFolderMenuItem = menu
						.findItem(R.id.new_folder_mode);
				mNewFolderMenuItem
						.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

				final MenuItem mAllOperator = menu
						.findItem(R.id.all_operate_mode);
				mAllOperator
						.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

				final MenuItem mInportOrExport = menu
						.findItem(R.id.inport_export_mode);
				mInportOrExport
						.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

				// gn pengwei 20121122 add for CR00735355 start
				final MenuItem mAboutMode = menu.findItem(R.id.about_mode);
				mAboutMode
						.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);
				// gn pengwei 20121122 add for CR00735355 end

				final MenuItem addContactOptionMenuItem = menu
						.findItem(R.id.select_mode);
				if (isGridView) {
					addContactOptionMenuItem.setTitle(getResources().getString(
							R.string.select_listview_mode));
				} else {
					addContactOptionMenuItem.setTitle(getResources().getString(
							R.string.select_gridview_mode));

				}
				// gn jiating 20121009 GN_GUEST_MODE begin
				if (NoteApplication.GN_GUEST_MODE) {
					searchMenuItem.setEnabled(false);
					mAllOperator.setEnabled(false);
					addContactOptionMenuItem.setEnabled(false);
				} else {
					// gn jiating 20121009 GN_GUEST_MODE end
					searchMenuItem.setEnabled(true);
					mAllOperator.setEnabled(true);
					addContactOptionMenuItem.setEnabled(true);
					if (isInFolder) {
						// mNewFolderMenuItem.setEnabled(false);
						mNewFolderMenuItem.setVisible(false);
					}
					if (mDataSource.size() < 1 && !isInFolder) {

						searchMenuItem.setEnabled(false);

					}
					if (mDataSource.size() < 1) {
						mAllOperator.setEnabled(false);
						addContactOptionMenuItem.setEnabled(false);
					}
				}
				addContactOptionMenuItem
						.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);
				// addContactOptionMenuItem.setIntent(new Intent(mActivity,
				// SettingsActivity.class));
				popupMenu.show();
			}
		});
		*/
		//gionee 20121214 jiating modfity for theme begin
		mListview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				clickGridOrListView(position);

			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("HomeActivity------setOnItemClickListener, " + "Position: "
						+ position);

				clickGridOrListView(position);
			}
		});
//gionee 20121219 jiating modify for theme begin
//		mFolderTitle.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// in folder click folderTitle update folderTitle
//				if (isInFolder) {
//					newOrEditFolder(false);
//				}
//			}
//		});
//gionee 20121219 jiating modify for theme end

	}

	public View getmShareButton() {
		return mShareButton;
	}

	public View getDelete_button() {
		return mDelete_button;
	}
	//gionee 20121214 jiating modfity for theme begin

	/**
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu != null) {
			menu.clear();
		}
		if (ViewConfiguration.get(this).hasPermanentMenuKey()) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.setting_menu, menu);
		} else {
			// CR00703854 jiating 20120925 begin
			return true;
			// CR00703854 jiating 20120925 end
		}
//		final MenuItem searchMenuItem = menu.findItem(R.id.search_mode);
//		searchMenuItem
//				.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

		final MenuItem mNewFolderMenuItem = menu.findItem(R.id.new_folder_mode);
		mNewFolderMenuItem
				.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

		final MenuItem mAllOperator = menu.findItem(R.id.all_operate_mode);
		mAllOperator
				.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

		final MenuItem mInportOrExport = menu.findItem(R.id.inport_export_mode);
		mInportOrExport
				.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

		// gn pengwei 20121122 add for CR00735355 start
		final MenuItem mAboutMode = menu.findItem(R.id.about_mode);
		mAboutMode
				.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);
		// gn pengwei 20121122 add for CR00735355 end

//		final MenuItem addContactOptionMenuItem = menu
//				.findItem(R.id.select_mode);
//		if (isGridView) {
//			addContactOptionMenuItem.setTitle(getResources().getString(
//					R.string.select_listview_mode));
//		} else {
//			addContactOptionMenuItem.setTitle(getResources().getString(
//					R.string.select_gridview_mode));
//
//		}
		// gn jiating 20121009 GN_GUEST_MODE begin
		if (NoteApplication.GN_GUEST_MODE) {
//			searchMenuItem.setEnabled(false);
			mAllOperator.setEnabled(false);
			// Gionee jiating 2012-10-22 modify for CR00716139 begin
//			addContactOptionMenuItem.setEnabled(false);
			// Gionee jiating 2012-10-22 modify for CR00716139 end
		} else {
			// gn jiating 20121009 GN_GUEST_MODE end
//			searchMenuItem.setEnabled(true);
			mAllOperator.setEnabled(true);
			// Gionee jiating 2012-10-22 modify for CR00716139 begin
//			addContactOptionMenuItem.setEnabled(true);
			// Gionee jiating 2012-10-22 modify for CR00716139 end
			if (isInFolder) {
				// mNewFolderMenuItem.setEnabled(false);
				mNewFolderMenuItem.setVisible(false);
			}
//			if (mDataSource.size() < 1 && !isInFolder) {
//
//				searchMenuItem.setEnabled(false);
//
//			}
			if (mDataSource.size() < 1) {
				mAllOperator.setEnabled(false);
//				addContactOptionMenuItem.setEnabled(false);
			}

		}
//		addContactOptionMenuItem
//				.setOnMenuItemClickListener(mFilterOptionsMenuItemClickListener);

		return super.onPrepareOptionsMenu(menu);
	}

*/	
	//gionee 20121214 jiating modfity for theme end
	@Override
	protected void onResume() {
		Log.d("HomeActivity------onResume, folderId: "+folderId+", isInFolder: "+isInFolder);
		notifyAdapter();
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).registerVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		super.onResume();
		// gn jiating 20121009 GN_GUEST_MODE begin
		if (NoteApplication.GN_GUEST_MODE) {
			isInFolder = false;
		}
		// gn jiating 20121009 GN_GUEST_MODE end
		if (!isInFolder) {
			folderId = -1;
		}

		updateDisplay();
		Statistics.onResume(this);
	}

//	/**
//	 * update the list view
//	 */
//
//	private void updateDisplay() {
//		Log.d("HomeActivity------updateDisplay" + ", isFolder=" + isInFolder);
//
//		// gn lilg 2012-12-08 modify for optimization begin
//		dbo = DBOperations.getInstances(HomeActivity.this);
//		// gn lilg 2012-12-08 modify for optimization end
//		
//		if (isInFolder) {
//			dbo.queryFromFolderForUpdate(HomeActivity.this, folderId, mDataSource);
//
//		} else {
//			dbo.queryFoldersAndNotesForUpdate(HomeActivity.this, mDataSource);
//		}
//
//		updateTitle();
//		setComponetViewState();
//	    updateMenuState();
//		// CQ679151 jiating 20120921 begin
//		if (isGridView) {
//			if (DragGridViewReal.isStartDrag) {
//				mGridView.stopDrag();
//				DragGridViewReal.moveNote = null;
//				homeOprateLayout.setVisibility(View.GONE);
//				//gionee 20121214 jiating modfity for theme begin
////				mSearch.setEnabled(true);
//				mSearch.setVisible(true);
//				mAddNote.setVisible(true);
//				mNewFolder.setVisible(true);
//				mAllOperator.setVisible(true);
//				mInportExport.setVisible(true);
//				mAboutNote.setVisible(true);
////				mHomeOperateLayoutAlwayshow.setVisibility(View.VISIBLE);
//				//gionee 20121214 jiating modfity for theme end
//			}
//		} else {
//			if (DragListViewReal.isStartDrag) {
//				mListview.stopDrag();
//				DragListViewReal.moveNote = null;
//				homeOprateLayout.setVisibility(View.GONE);
//				//gionee 20121214 jiating modfity for theme begin
//				mSearch.setVisible(true);
//				mAddNote.setVisible(true);
//				mNewFolder.setVisible(true);
//				mAllOperator.setVisible(true);
//				mInportExport.setVisible(true);
//				mAboutNote.setVisible(true);
////				mHomeOperateLayoutAlwayshow.setVisibility(View.VISIBLE);
//				//gionee 20121214 jiating modfity for theme end
//			}
//		}
//		// CQ679151 jiating 20120921 end
//		// jiating begin add 通过移动选中便签删除或分享时，推到后台时通过widget删除便签，回到主界面的状态
//		if ((DragGridViewReal.moveNote != null)
//				|| (DragListViewReal.moveNote != null)) {
//			Note note;
//			if (isGridView) {
//				note = dbo.queryOneNote(HomeActivity.this,
//						Integer.parseInt(DragGridViewReal.moveNote.getId()));
//			} else {
//				note = dbo.queryOneNote(HomeActivity.this,
//						Integer.parseInt(DragListViewReal.moveNote.getId()));
//			}
//			if (note.getId() != null) {
//				mDataSource.remove(note);
//				Log.d("HomeActivity------mDataSource.size(): "
//						+ mDataSource.size());
//			}
//
//		}
//		setViewState();
//
//	}
	
	private void updateDatas() {
		try {
			Log.d("HomeActivity------updateDisplay" + ", isFolder="
					+ isInFolder);
			updateTitle();
			setComponetViewState();
			updateMenuState();
			// CQ679151 jiating 20120921 begin
			if (isGridView) {
				if (DragGridViewReal.isStartDrag()) {
					mGridView.stopDrag();
					DragGridViewReal.moveNote = null;
				    //Gionee <wangpan><2014-03-20> modify for CR01126031 begin
					showOptionMenu();
				    //Gionee <wangpan><2014-03-20> modify for CR01126031 end
				}
			} else {
				if (DragListViewReal.isStartDrag()) {
					mListview.stopDrag();
					DragListViewReal.moveNote = null;
                    //Gionee <wangpan><2014-03-20> modify for CR01126031 begin
					showOptionMenu();
                    //Gionee <wangpan><2014-03-20> modify for CR01126031 end
				}
			}
			onCreateOptionsMenu(mOptionsMenu);
			// CQ679151 jiating 20120921 end
			// jiating begin add 通过移动选中便签删除或分享时，推到后台时通过widget删除便签，回到主界面的状态
			if ((DragGridViewReal.moveNote != null)
					|| (DragListViewReal.moveNote != null)) {
				Note note;
				if (isGridView) {
					note = dbo
							.queryOneNote(HomeActivity.this,
									Integer.parseInt(DragGridViewReal.moveNote
											.getId()));
				} else {
					note = dbo
							.queryOneNote(HomeActivity.this,
									Integer.parseInt(DragListViewReal.moveNote
											.getId()));
				}
				if (note.getId() != null) {
					mDataSource.remove(note);
					notifyAdapter();
					// Gionee <lilg><2013-03-29> add for CR00791189 end
					Log.d("HomeActivity------mDataSource.size(): "
							+ mDataSource.size());
					

				}

			}
			setViewState();
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("HomeActivity------updateDatas: " + e);
		}
	}
	
	/**
	 * update the list view
	 */

	private void updateDisplay() {
		if(mDataSource != null){
			mDataSource.clear();
		}
		notifyAdapter();
		Log.d("HomeActivity---updateDisplay---1---" + System.currentTimeMillis());
		UtilsQueryDatas.queryNotesIsInFolder(folderId, mTempNoteList, mDataSource);
		Log.d("HomeActivity---updateDisplay---2---" + System.currentTimeMillis());
		
		notifyAdapter();
		
		updateDatas();

	}

	private void notifyAdapter() {
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	public static void setInFolder(boolean isInFolder) {
		HomeActivity.isInFolder = isInFolder;
	}

	//gionee 20121219 jiating modify for theme begin
	//Gionee <pengwei><20130617> modify for CR00809131 begin
	private void updateMenuState() {
	    Log.d("HomeActivity-updateMenuState");
	    if(null == mOptionsMenu){
	    Log.d("HomeActivity-updateMenuState mOptionsMenu is null");
	        return;
	    }

		if (NoteApplication.GN_GUEST_MODE) {
			if (mAllOperator != null && mSearch != null && mInportExport != null) {
				mAllOperator.setEnabled(false);
				mSearch.setEnabled(false);
				mInportExport.setEnabled(false);
				mSearch.setIcon(R.drawable.gn_note_search_button_on);
			}

		} else {
			
			if(mInportExport != null){
				mInportExport.setEnabled(true);
			}

			if (mNewFolder != null) {
				if (isInFolder) {
					mNewFolder.setVisible(false);

				} else {
					mNewFolder.setVisible(true);
				}
			}

			if (mAllOperator != null) {
				if (mDataSource.size() < 1) {
					// mSearch.setEnabled(false);
					mAllOperator.setEnabled(false);
				} else {
					// mSearch.setEnabled(true);
					mAllOperator.setEnabled(true);
				}
			}
			if(mSearch!=null){
			    if (mDataSource.size() < 1 && !isInFolder) {
			        Log.d("HomeActivity-updateMenuState enable false");
			        mSearch.setEnabled(false);
			        mSearch.setIcon(R.drawable.gn_note_search_button_on);
			    } else {
			        Log.d("HomeActivity-updateMenuState enable true");
			        mSearch.setEnabled(true);
			        //Gionee <wangpan><2014-03-14> add for CR01111817 begin
			        //mSearch.setIcon(R.drawable.gn_note_search_button_off);
			        mSearch.setIcon(R.drawable.gn_note_home_searchbutton_onoff_white);
			        //Gionee <wangpan><2014-03-14> add for CR01111817 end
			    }
			}

		}
//		onCreateOptionsMenu(mOptionsMenu);
	}
	//Gionee <pengwei><20130617> modify for CR00809131 end
	//gionee 20121219 jiating modify for theme end
	
	// 更新当前的标题栏的titile
	private void updateTitle() {
		// String title=titleName.replace("\n", " ");
		String titleName;
		// gn jiating 20121009 GN_GUEST_MODE begin
		if (NoteApplication.GN_GUEST_MODE) {
			titleName = getResources().getString(R.string.home_title);
			// mFolderTitle.setVisibility(View.GONE);
			// mHomeTitleView.setVisibility(View.VISIBLE);
			// mHomeTitleIcon.setVisibility(View.VISIBLE);
			// mHomeFolderTitleIcon.setVisibility(View.GONE);
			// mTitleDiverView.setVisibility(View.GONE);
			// mHomeTitleView.setText(titleName);
			if (mHomeTitle != null) {
				mHomeTitle.setVisibility(View.VISIBLE);
				mHomeTitle.setText(titleName);
			}
			if (mFolderTitleText != null) {
				mFolderTitleText.setVisibility(View.GONE);
			}
			if(mHomeBack != null){
				mHomeBack.setVisibility(View.GONE);
			}
			mActionBar.setDisplayShowHomeEnabled(false);
			mActionBar.setDisplayShowTitleEnabled(false);
			mActionBar.setDisplayHomeAsUpEnabled(false);

		} else {
			// gn jiating 20121009 GN_GUEST_MODE end
			if (isInFolder) {
				Note mFolder = dbo.queryOneNote(HomeActivity.this, folderId);
				titleName = mFolder.getTitle();
				// mFolderTitle.setVisibility(View.VISIBLE);
				// mHomeTitleView.setVisibility(View.GONE);
				// mHomeTitleIcon.setVisibility(View.GONE);
				// mHomeFolderTitleIcon.setVisibility(View.VISIBLE);
				// mTitleDiverView.setVisibility(View.VISIBLE);
				if (mHomeTitle != null) {
					mHomeTitle.setVisibility(View.GONE);
					mHomeTitle.setText(titleName);
				}

				if (mFolderTitleText != null) {
					mFolderTitleText.setVisibility(View.VISIBLE);
					if (titleName.length() > 7) {
						mFolderTitleText.setText(titleName.substring(0, 7)
								+ "..." + "(" + mDataSource.size() + ")");
					} else {
						mFolderTitleText.setText(titleName + "("
								+ mDataSource.size() + ")");
					}
				}
				mActionBar.setIcon(R.drawable.gn_note_actionbar_icon);
				mActionBar.setDisplayHomeAsUpEnabled(false);
				mHomeBack.setVisibility(View.VISIBLE);
				mActionBar.setDisplayShowTitleEnabled(false);
				mActionBar.setDisplayShowHomeEnabled(true);

				// }
			} else {
				// gionee 20121214 jiating modfity for theme begin

				titleName = getResources().getString(R.string.home_title);
				/**
				 * mFolderTitle.setVisibility(View.GONE);
				 * mHomeTitleView.setVisibility(View.VISIBLE);
				 * mHomeTitleIcon.setVisibility(View.VISIBLE);
				 * mHomeFolderTitleIcon.setVisibility(View.GONE);
				 * mTitleDiverView.setVisibility(View.GONE);
				 * mHomeTitleView.setText(titleName + "(" + mDataSource.size() +
				 * ")");
				 */
				if (mHomeTitle != null) {
					mHomeTitle.setVisibility(View.VISIBLE);
					mHomeTitle.setText(titleName + "(" + mDataSource.size()
							+ ")");
				}
				mActionBar.setDisplayShowHomeEnabled(false);
				mActionBar.setDisplayShowTitleEnabled(false);
				mActionBar.setDisplayHomeAsUpEnabled(false);
				mHomeBack.setVisibility(View.GONE);
				if (mFolderTitleText != null) {
					mFolderTitleText.setVisibility(View.GONE);

				}

				// gionee 20121214 jiating modfity for theme begin
			}
		}

	}

	private void setComponetViewState() {
		// gn jiating 20121009 GN_GUEST_MODE begin
		if (NoteApplication.GN_GUEST_MODE) {
			if (isGridView) {
				mGridView.setVisibility(View.INVISIBLE);
			} else {
				mListview.setVisibility(View.INVISIBLE);
//				mListDiverBottom.setVisibility(View.INVISIBLE);
			}
			// gionee 20121025 jiating CR00716071 begin
			mNoNoteImage.setVisibility(View.VISIBLE);
			mNoNoteText.setVisibility(View.VISIBLE);
			// gionee 20121025 jiating CR00716071 end
		} else {
			// gn jiating 20121009 GN_GUEST_MODE end
			if (isInFolder) {

				mNoNoteImage.setVisibility(View.GONE);
				mNoNoteText.setVisibility(View.GONE);

			} else {

				if (mDataSource.size() > 0) {
					mNoNoteImage.setVisibility(View.GONE);
					mNoNoteText.setVisibility(View.GONE);

				} else {
					mNoNoteImage.setVisibility(View.VISIBLE);
					mNoNoteText.setVisibility(View.VISIBLE);

				}

			}

			if (isGridView) {
				mGridView.setVisibility(View.VISIBLE);
//				mListDiverBottom.setVisibility(View.GONE);
			} else {
				if (mDataSource.size() > 0) {
//					mListDiverBottom.setVisibility(View.VISIBLE);
					mListview.setVisibility(View.VISIBLE);
				} else {
//					mListDiverBottom.setVisibility(View.GONE);
				}
			}
		}
	}

	// 确定当前显示的方式，是listview或gridview
	private void setShowViewMode() {

		if (isGridView) {
			mGridView.setVisibility(View.VISIBLE);
			mListview.setVisibility(View.GONE);
			mGridView.setAdapter(adapter);
			mGridView.setDropListener(gridOnDrop);
			mGridView.setRemoveListener(gridRemoveListenerToFolder);
			mGridView.setRemoveListenerTemp(gridOnRemoveTemp);
//			mListDiverBottom.setVisibility(View.GONE);
			// gn jiating 20121009 GN_GUEST_MODE begin
			if (NoteApplication.GN_GUEST_MODE) {
				mGridView.setVisibility(View.INVISIBLE);

			} else {
				// gn jiating 20121009 GN_GUEST_MODE end
				mGridView.setVisibility(View.VISIBLE);
			}
		} else {
			mGridView.setVisibility(View.GONE);
			mListview.setVisibility(View.VISIBLE);
			mListview.setAdapter(adapter);
			mListview.setDropListViewListener(listOnDrop);
			mListview.setRemoveListViewListener(listRemoveListenerToFolder);
			mListview.setRemoveListViewListenerTemp(listOnRemoveTemp);
			// gn jiating 20121009 GN_GUEST_MODE begin
			if (NoteApplication.GN_GUEST_MODE) {
				mListview.setVisibility(View.INVISIBLE);
//				mListDiverBottom.setVisibility(View.GONE);

			} else {
				// gn jiating 20121009 GN_GUEST_MODE end
				mListview.setVisibility(View.VISIBLE);
//				mListDiverBottom.setVisibility(View.VISIBLE);
			}
		}

	}

	private void setViewState() {
		// gn lilg 2012-11-15 add for nullPointerException start
		if (adapter != null) {
			// updateDisplay(folderId);
			adapter.updateView(mDataSource, -1);
		} else {
			Log.e("HomeActivity------adapter: " + adapter);
		}
		// gn lilg 2012-11-15 add for nullPointerException end

	}

	// click gridview or Listview item

	private void clickGridOrListView(int position) {
		Intent intent = new Intent();
		Note note = mDataSource.get(position);
		intent.putExtra(DBOpenHelper.ID, Integer.parseInt(note.getId()));
		String is_File = note.getIsFolder();
		if (Constants.NO_FOLDER.equals(is_File)) {


			if (isInFolder) {
				intent.putExtra(DBOpenHelper.PARENT_FOLDER,
						Integer.parseInt(note.getParentFile()));
				intent.setClass(HomeActivity.this, NoteActivity.class);

			} else {
				intent.setClass(HomeActivity.this, NoteActivity.class);
			}
			startActivity(intent);

		} else if (Constants.IS_FOLDER.equals(is_File)) {
			setIsInFolder(true);
			folderId = Integer.parseInt(note.getId());
			updateDisplay();

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (isInFolder) {
					setIsInFolder(false);
					clearFolderId();
					updateDisplay();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private void setIsInFolder(boolean isInFolder){
        HomeActivity.isInFolder = isInFolder;
    }
    private void clearFolderId(){
        folderId = -1;
    }
	@Override
	protected void onStop() {
		Log.i("HomeActivity------onStop start!");

		// gn lilg 2012-11-19 modify for CR00734123 start
		if (dialog != null) {
			dialog.dismiss();
			Log.i("HomeActivity------delete note dialog.dismiss!");
			if (isGridView) {
				Log.d("HomeActivity------isGridView: " + isGridView);
				DragGridViewReal.moveNote = null;
			} else {
				Log.d("HomeActivity------isGridView: " + isGridView);
				DragListViewReal.moveNote = null;
			}
		}
		// gn lilg 2012-11-19 modify for CR00734123 end

		super.onStop();
		Log.d("HomeActivity------onStop end!");
	}

	@Override
	protected void onDestroy() {
		Log.i("HomeActivity------onDestroy start!");
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			super.onDestroy();
			if (mGusestModeObserver != null) {
				getContentResolver().unregisterContentObserver(
						mGusestModeObserver);
				mGusestModeObserver = null;
			}
			// gionee 20121026 jiating CR00718021 begin
			if (mSearchViewClickNoteReceiver != null) {
				HomeActivity.this
						.unregisterReceiver(mSearchViewClickNoteReceiver);
				mSearchViewClickNoteReceiver = null;
			}
			adapter = null;
			// gionee 20121026 jiating CR00718021 end

		
		// Gionee <lilg><2013-04-02> modify for location with google api begin  
		// gn lilg 2012-01-23 add for location begin
//		GNLocateService.getInstance(getApplicationContext()).destroy();
		GNLocateService2.getInstance(getApplicationContext()).destroy();
		// gn lilg 2012-01-23 add for location end
		// Gionee <lilg><2013-04-02> modify for location with google api end
		
		// gn lilg 2012-12-08 add for optimization begin
		DBOperations.release();
		// gn lilg 2012-12-08 add for optimization end
		
		// gn lilg 2013-01-08 add for net alert begin
		Intent alertIntent = new Intent("gn.android.intent.action.APP_EXIT");
		alertIntent.putExtra("appname", getPackageName());
		sendBroadcast(alertIntent);
		// gn lilg 2013-01-08 add for net alert end
		
		Log.d("HomeActivity------onDestroy end!");
		clean();
		
		// Gionee <lilg><2013-04-26> modify for CR00801445 begin
		// System.exit(0);//add by pengwei
		// Gionee <lilg><2013-04-26> modify for CR00801445 en
	}

	private synchronized static void clean() {
        if(mTempNoteList != null){
			mTempNoteList.clear();
			mTempNoteList = null;
		}
    }

	
//gionee 20121214 jiating modify for theme begin
	
	private OnMenuItemClickListener mFilterOptionsMenuItemClickListener = new OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {

			switch (item.getItemId()) {
//			case R.id.select_mode:
//				// gn pengwei 20121126 add for statistics begin
//				StatisticalValue statis = StatisticalValue.getInstance();
//				if (isInFolder) {
//					int num = statis.getMainAppFolderOperationSwitch();
//					statis.setMainAppFolderOperationSwitch(++num);
//					Statistics.addNum(
//							statis.getKeyMainAppFolderOperationSwitch(), num);
//				} else {
//					int num = statis.getMainAppOerationSwitch();
//					statis.setMainAppOerationSwitch(++num);
//					Statistics
//							.addNum(statis.getKeyMainAppOerationSwitch(), num);
//				}
//				// gn pengwei 20121126 add for statistics end
//				if (isGridView) {
//					isGridView = false;
//
//				} else {
//					isGridView = true;
//				}
//				SharedPreferences.Editor editor = AmigoPreferenceManager
//						.getDefaultSharedPreferences(HomeActivity.this).edit();
//				editor.putBoolean(Constants.HOME_SHOW_VIEW_MODE, isGridView);
//				editor.commit();
//				setShowViewMode();
//				break;
//			case R.id.search_mode:
//				onSearchRequested();
//				break;
			case R.id.new_folder_mode:
				newFolder();
				break;
			case R.id.all_operate_mode:

				Intent intent = new Intent(HomeActivity.this,
						ALLEditActivity.class);

				intent.putExtra(Constants.IS_IN_FOLDER, isInFolder);
				intent.putExtra(DBOpenHelper.ID, folderId);
				startActivity(intent);
				break;
			case R.id.inport_export_mode:
				inportOrExport();
				break;
			// gn pengwei 20121122 add for CR00735355 start
			case R.id.about_mode:
				// gn pengwei 20121126 add for statistics begin
				Statistics.onEvent(HomeActivity.this, Statistics.ABOUT);
				// gn pengwei 20121126 add for statistics end
				Intent aboutIntent = new Intent(HomeActivity.this,
						AboutActivity.class);
				startActivity(aboutIntent);
				break;
			// gn pengwei 20121122 add for CR00735355 end
			}

			return true;
		}
	};
	
	//gionee 20121214 jiating modify for theme end

	@Override
	public boolean onSearchRequested() {
		// gn pengwei 20121126 add for statistics start
		if (isInFolder) {
			Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_FOLDER_SEARCH);
		} else {
			Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_SEARCH);
		}
		// gn pengwei 20121126 add for statistics end
		Intent intent = new Intent(HomeActivity.this, SearchNoteActivity.class);
		startActivity(intent);
		return true;
	}

	/*
	 * create a new note
	 */
	private void newNote() {
		Log.i("HomeActivity------newNote");

		Intent i = new Intent();
		i.putExtra(DBOpenHelper.PARENT_FOLDER, folderId);

		i.setClass(HomeActivity.this, NoteActivity.class);
		startActivity(i);
	}

	/*
	 * create a new folder
	 */
	private void newFolder() {
		Log.i("HomeActivity------newFolder");
		// gn pengwei 20121126 add for statistics begin
		Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_NEW_FOLDER);
		// gn pengwei 20121126 add for statistics end
		newOrEditFolder(true);
	}

	private void newOrEditFolder(boolean isNewFolder) {
		Log.i("HomeActivity------newOrEditFolder!");
		
		// gn pengwei 20121126 add for statistics begin
		Statistics.onEvent(HomeActivity.this, Statistics.FOLDER_INPUT_TITLE);
		// gn pengwei 20121126 add for statistics end
		final boolean newFolder = isNewFolder;
		Context mContext = HomeActivity.this;
		AmigoAlertDialog.Builder builder = new AmigoAlertDialog.Builder(mContext,CommonUtils.getTheme());
//		AmigoAlertDialog.Builder builder = new AmigoAlertDialog.Builder
//				(mContext,AmigoAlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		final Note oldFolder = dbo.queryOneNote(HomeActivity.this, folderId);

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		View layout = null;
		layout = inflater.inflate(R.layout.dialog_layout_new_folder_white,
		        (ViewGroup) findViewById(R.id.dialog_layout_new_folder_root));
		builder.setView(layout);
		final AmigoEditText et_folder_name = (AmigoEditText) layout
				.findViewById(R.id.et_dialog_new_folder);
		if (isNewFolder) {
			builder.setTitle(R.string.new_folder);
			et_folder_name.setText(R.string.folder_title_name);
		//Gionee liuliang 2014-4-16 modify CR01186605 begin	
	     // et_folder_name.setSelection(getResources().getString(
	     // R.string.folder_title_name).length());
	        et_folder_name.selectAll();
       //Gionee liuliang 2014-4-16 modify CR01186605 end	
		} else {
			builder.setTitle(R.string.note_new_title_label);
			et_folder_name.setText(oldFolder.getTitle());
			et_folder_name.setSelection(oldFolder.getTitle().length());
		}

		et_folder_name
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						TITLE_MAX_LENGTH) });

		et_folder_name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s != null && s.length() >= TITLE_MAX_LENGTH) {
					Toast.makeText(HomeActivity.this,
							getString(R.string.folder_title_max_length),
							Toast.LENGTH_SHORT).show();
					return;
				}
				//Gionee <wangpan><2014-03-27> add for CR01147708 begin
				if (s != null && s.toString().trim().length() <= 0){
                    ((AmigoAlertDialog)alertDialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }else {
                    ((AmigoAlertDialog)alertDialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }
                //Gionee <wangpan><2014-03-27> add for CR01147708 end
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		builder.setPositiveButton(R.string.Ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						//gn pengwei 20130125 modify for CR00768042 begin
					    if (CommonUtils.isFastDoubleClick()) {
					        return;
					    }
						//gn pengwei 20130125 modify for CR00768042 end
						String newFolderName = et_folder_name.getText()
								.toString().trim().replace("\n", " ");

						if (newFolder) {

							Note note = new Note();
							// note.setContent(newFolderName);
							note.setAlarmTime(null);
							note.setBgColor(null);
							note.setParentFile(Constants.PARENT_FILE_ROOT);
							note.setIsFolder(Constants.IS_FOLDER);
							note.setUpdateDate(dbo.getDate());
							note.setUpdateTime(dbo.getTime());
							note.setNoteFontSize(null);
							note.setNoteListMode(null);
							if (newFolderName.length() > 0
									&& newFolderName != null) {
								note.setTitle(newFolderName);
							} else {
								note.setTitle(getResources().getString(
										R.string.folder_title_name));
							}
							//Gionee <wangpan><2014-03-11> add for CR01102110 begin
							if(isNoteExist(newFolderName)){
							    Toast.makeText(HomeActivity.this,
			                            getString(R.string.folder_title_duplicate_alert),
			                            Toast.LENGTH_SHORT).show();
							    return;
							}
                            //Gionee <wangpan><2014-03-11> add for CR01102110 end
							long noteID = dbo.createNote(HomeActivity.this, note);
							// titleName = getResources().getString(
							// R.string.home_title);
							note.setId(noteID + "");
							int posInt = UtilsQueryDatas.sortFolder(note,mTempNoteList);
							Log.v("HomeActivity---newOrEditFolder---posInt---" + posInt);
							if(null != mTempNoteList){
							    mTempNoteList.add(posInt,note);
							}
							Log.d("HomeActivity---newOrEditFolder---posInt---" + posInt);
							updateDisplay();
							setShowViewMode();
						} else {
							Log.i("HomeActivity------newFolder: " + newFolder);

							if (newFolderName.length() > 0
									&& newFolderName != null) {
								oldFolder.setTitle(newFolderName);

							} else {
								newFolderName = oldFolder.getTitle();
								// gionee lilg 2013-01-10 add for alert user when the folder title input is empty begin
								Toast.makeText(HomeActivity.this, getResources().getString(R.string.folder_title_empty_alert), Toast.LENGTH_SHORT).show();
								// gionee lilg 2013-01-10 add for alert user when the folder title input is empty end
							}
                            //Gionee <wangpan><2014-03-11> add for CR01102110 begin
							if(isNoteExist(newFolderName)){
                                Toast.makeText(HomeActivity.this,
                                        getString(R.string.folder_title_duplicate_alert),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //Gionee <wangpan><2014-03-11> add for CR01102110 end
							oldFolder.setTitle(newFolderName);
							// titleName = newFolderName;
							//Gionee <pengwei><20130731> modify for CR00839583 begin
							String updateDate = dbo.getDate();
							String updateTime = dbo.getTime();
							oldFolder.setUpdateDate(updateDate);
							oldFolder.setUpdateTime(updateTime);
							dbo.updateNote(HomeActivity.this, oldFolder);
							Note note = UtilsQueryDatas.queryNoteByID(folderId,mTempNoteList);
							
							// Gionee <lilg><2013-04-26> modify for CR00801759 begin
							if(note != null){
								note.setTitle(newFolderName);
								note.setUpdateDate(updateDate);
								note.setUpdateTime(updateTime);
								//Gionee <pengwei><20130731> modify for CR00839583 end
								// Gionee <lilg><2013-04-26> modify for CR00801759 end
								
								mTempNoteList.remove(note);
								int posInt = UtilsQueryDatas.sortFolder(note,mTempNoteList);
								Log.d("HomeActivity---newOrEditFolder---posInt---" + posInt);
								mTempNoteList.add(posInt,note);
								Log.v("HomeActivity---newOrEditFolder---posInt---" + posInt);
							}
							updateDisplay();
						}

					}

				});

		// set cancel button
		builder.setNegativeButton(R.string.Cancel,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setCancelable(true);
		
		// Gionee <lilg><2013-05-11> modify for CR00809745 begin
//		AmigoAlertDialog ad = builder.create();
		alertDialog = builder.create();
		//Gionee liuliang 2014-4-16 modify CR01186605 begin	
		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		//Gionee liuliang 2014-4-16 modify CR01186605 end
		alertDialog.show();
		// Gionee <lilg><2013-05-11> modify for CR00809745 end
//tangzz add for inputmethod begin CR01442825 
		CommonUtils.showInputMethod(et_folder_name);
//tangzz add for inputmethod end CR01442825 
	}

    //Gionee <wangpan><2014-03-11> add for CR01102110 begin
	private boolean isNoteExist(String newFolderName) {
	    Note folder = dbo.queryNoteByFolderTitle(HomeActivity.this, newFolderName);
	    boolean isExist = false;
	    if(folder.getId() == null || "".equals(folder.getId())){
            //the note don't exist in db
	        isExist = false;
	    }else{
            //the note exists in db
	        isExist =  true;
	    }
	    Log.d("HomeActivity-isNoteExist: "+isExist);
	    return isExist;
    }
    //Gionee <wangpan><2014-03-11> add for CR01102110 end

    /*
	 * export to text
	 */
	private void inportOrExport() {
		// gn pengwei 20121126 add for statistics begin
		if (isInFolder) {
			StatisticalName.isFold = 1;
		} else {
			StatisticalName.isFold = 0;
		}
		// gn pengwei 20121126 add for statistics end
		Intent intent = new Intent();
		intent.setClass(this, ImportExportActivity.class);
		startActivity(intent);
	}

	/**
	 * update widget gn lilg 2012-07-11
	 * 
	 * @param note
	 */
	private void updateWidget(String wId, String wType) {

		if ("".equals(wId) || "".equals(wType)) {
			Log.i("HomeActivity------widgetId is \"\" or widgetType is \"\"!");
			return;
		}

		int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		int widgetType = Notes.TYPE_WIDGET_INVALIDE;

		try {
			widgetId = Integer.parseInt(wId);
		} catch (Exception e) {
			Log.e("HomeActivity------e.getMessage(): "+ e.getMessage());
		}

		try {
			widgetType = Integer.parseInt(wType);
		} catch (Exception e) {
			Log.e("HomeActivity------e.getMessage(): "+ e.getMessage());
		}

		if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID
				|| widgetType == Notes.TYPE_WIDGET_INVALIDE) {
			Log.d("HomeActivity------widgetId is: " + widgetId + ", widgetType is: "
					+ widgetType);

			return;
		}

		WidgetUtils.updateWidget(HomeActivity.this, widgetId, widgetType);
	}

	/**
	 * share note gn lilg 20120711
	 * 
	 * @param note
	 */
	private int shareNote(Note note) {

		int result = CommonUtils.RESULT_OK;

		if (note == null) {
			Log.i("HomeActivity------note is null!");
			return CommonUtils.RESULT_ERROR;
		}
		if (CommonUtils.STR_YES.equals(note.getIsFolder())) {
			// is folder
			Log.d("HomeActivity------is folder!");

			int parentId = -1;
			try {
				parentId = Integer.parseInt(note.getId());
			} catch (Exception e) {
				Log.e("HomeActivity------e.getMessage(): "+e.getMessage());

				return CommonUtils.RESULT_ERROR;
			}
			List<Note> noteList = new ArrayList<Note>();
			UtilsQueryDatas.queryNotesIsInFolder(parentId,mTempNoteList,noteList);
			result = CommonUtils.shareNote(HomeActivity.this,
					CommonUtils.DEFAULT_MIMETYPE, noteList);
		} else if (CommonUtils.STR_NO.equals(note.getIsFolder())) {
			// is note
			Log.d("HomeActivity------is note!");
			// Gionee <pengwei><2013-3-15> modify for CR00784912 begin
			String noteContent = dbo.queryOneNoteTitle(this,Integer.valueOf(note.getId()));
			result = CommonUtils.shareNote(HomeActivity.this,
					CommonUtils.DEFAULT_MIMETYPE, noteContent);
			// Gionee <pengwei><2013-3-15> modify for CR00784912 end
		} else {
			Log.d("HomeActivity------note.getIsFolder: " + note.getIsFolder());
		}

		return result;
	}

	// jiating begin add dragItem
	private DragGridViewReal.DropListener gridOnDrop = new DragGridViewReal.DropListener() {

		public void drop(int from, int to, boolean isDelete, boolean isShare,
				ViewGroup moveItemView) {
			Log.d("HomeActivity------gridOnDrop, drop, " + ", isDelete: " + isDelete
					+ ", isShare: " + isShare);

		    //Gionee <wangpan><2014-03-20> modify for CR01126031 begin
			showOptionMenu();
		    //Gionee <wangpan><2014-03-20> modify for CR01126031 end
//			mHomeOperateLayoutAlwayshow.setVisibility(View.VISIBLE);
			//gionee 20121214 jiating modfity for theme end
			Note oneNoteData = DragGridViewReal.moveNote;
			folderId = -1;
			if (isDelete) {
				// move to deleteButton delete Note
				// moveItemPosition = from;
				deleteDialogShown(Constants.DIALOG_DELETE_NOTEPAD_LIST);
				// showDialog(Constants.DIALOG_DELETE_NOTEPAD_LIST);
			} else if (isShare) {

				// gn pengwei 20121126 add for statistics begin
				Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_LONG_SHARE);
				// gn pengwei 20121126 add for statistics end
				// move to ShareButton Share Note
				// gn lilg 20120711 add for share note start.
				Log.d("HomeActivity------the info of the note to be share: "
						+ oneNoteData.toString());

				// Gionee <lilg><2013-04-18> modify for CR00795054 begin
				int noteCount = CommonUtils.getNoteCount(oneNoteData);
				if(noteCount <= Constants.SHARE_NOTE_MAX_COUNT){
					// permit share note
					if (shareNote(oneNoteData) == -1) {
						// Gionee <lilg><2013-05-11> modify for CR00809539 begin
//						CommonUtils.showToast(HomeActivity.this, getString(R.string.share_no_note_infolder_toast));
						CommonUtils.showToast(HomeActivity.this, getString(R.string.share_note_empty_toast));
						// Gionee <lilg><2013-05-11> modify for CR00809539 end
					}
				}else{
					// do not permit share note
					CommonUtils.showToast(HomeActivity.this, getString(R.string.share_note_count_toast));
				}
				// Gionee <lilg><2013-04-18> modify for CR00795054 end

				DragGridViewReal.moveNote = null;
				updateDisplay();
				// setViewState();

				// gn lilg 20120711 add for share note end.

			} else {
				// move other position except folder deleteBurron or shareButton
				String oneNoteDataParentId = oneNoteData.getParentFile();
				if (!("no".equals(oneNoteDataParentId))) {// the note is in folder
					// gn pengwei 20121126 add for statistics begin
					Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_LONG_MOVE_OUT_FOLDER);
					// gn pengwei 20121126 add for statistics end
					dbo.moveOneNote(HomeActivity.this, oneNoteData, null);
					// Note sourceFolder = dbo.queryOneNote(HomeActivity.this,
					// Integer.parseInt(oneNoteDataParentId));
					// sourceFolder.setHaveNoteCount(sourceFolder
					// .getHaveNoteCount() - 1);
					// dbo.updateNote(HomeActivity.this, sourceFolder);
					// oneNoteData.setParentFile("no");
					// dbo.updateNote(HomeActivity.this, oneNoteData);
					UtilsQueryDatas.moveNote(oneNoteData,UtilsQueryDatas.folderIDInt,mTempNoteList);
				}
				DragGridViewReal.moveNote = null;
				updateDisplay();
				// setViewState();

			}

		}
	};

	private DragGridViewReal.RemoveListenerTemp gridOnRemoveTemp = new DragGridViewReal.RemoveListenerTemp() {

		public Note removeTemp(int which) {
			Log.d("HomeActivity------gridOnRemoveTemp, removeTemp");

            //Gionee <wangpan><2014-03-20> modify for CR01126031 begin
//			Note moveNote = mDataSource.remove(which);
            Note moveNote = mDataSource.get(which);
            //Gionee <wangpan><2014-03-20> modify for CR01126031 end
			notifyAdapter();
			if (!moveNote.getParentFile().equals("no")) {
				// 如果移动是文件夹的note
				isInFolder = false;
				folderId = -1;

				updateDisplay();

			} else {
				// 如果移动的是主文件夹下的
				// TODO jiating
				// boolean isHaveFolder = false;
				if (moveNote.getIsFolder().equals(Constants.NO_FOLDER)) {
					for (Note note : mDataSource) {
						if (note.getIsFolder().equals(Constants.IS_FOLDER)) {
							// 如果开始移动时，主界面里有文件夹存在，且移动的不是文件夹
							// isHaveFolder = true;
							setShowViewMode();
							break;
						}
					}
				} else {
					setViewState();
				}

			}
            //Gionee <wangpan><2014-03-20> modify for CR01126031 begin
			// make homeOprateLayout Visiable and have animation
            hiddenOptionMenu();
            startAnimationOfHomeOprateLayout();    
            //Gionee <wangpan><2014-03-20> modify for CR01126031 end
            
			return moveNote;

		}


	};
    //Gionee <wangpan><2014-03-20> modify for CR01126031 begin
    private void startAnimationOfHomeOprateLayout() {
        TranslateAnimation myAnimation_Translate = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_PARENT, 0);

        myAnimation_Translate.setDuration(500);

        myAnimation_Translate.setInterpolator(AnimationUtils
                .loadInterpolator(HomeActivity.this,
                        android.R.anim.accelerate_decelerate_interpolator));
        homeOprateLayout.startAnimation(myAnimation_Translate);
    }
	
	 private void hiddenOptionMenu() {
         homeOprateLayout.setVisibility(View.VISIBLE);
         mSearch.setVisible(false);
         HomeActivity.this.setOptionsMenuHideMode(true);
         mAddNote.setVisible(false);
         mNewFolder.setVisible(false);
         mAllOperator.setVisible(false);
         mInportExport.setVisible(false);
         // Gionee <wanghaiyan><2015-02-04> modify CR01445194 for begin
         //mAboutNote.setVisible(false);
         // Gionee <wanghaiyan><2015-02-04> modify CR01445194 for end
     }
	 
	private void showOptionMenu() {
        homeOprateLayout.setVisibility(View.GONE);
        HomeActivity.this.setOptionsMenuHideMode(false);
        mSearch.setVisible(true);
        mAddNote.setVisible(true);
        mNewFolder.setVisible(true);
        mAllOperator.setVisible(true);
        mInportExport.setVisible(true);
        // Gionee <wanghaiyan><2015-02-04> modify CR01445194 for begin
        // mAboutNote.setVisible(true);
       // Gionee <wanghaiyan><2015-02-04> modify CR01445194 for end
    }
    //Gionee <wangpan><2014-03-20> modify for CR01126031 end
	
	private DragGridViewReal.RemoveListenerToFolder gridRemoveListenerToFolder = new DragGridViewReal.RemoveListenerToFolder() {

		public void removeToFolder(int from, int to) {
			// move into folder
			Log.i("HomeActivity------gridRemoveListenerToFolder, removeToFolder");
			// gn pengwei 20121126 add for statistics begin
			Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_LONG_MOVE_IN_FOLDER);
			// gn pengwei 20121126 add for statistics end
			Note oneNoteData = DragGridViewReal.moveNote;
			// String oneNoteDataParentId = oneNoteData.getParentFile();
			Note destFolder = adapter.getmDataSource().get(to);
			dbo.moveOneNote(HomeActivity.this, oneNoteData, destFolder);
			UtilsQueryDatas.moveNote(oneNoteData,Integer.valueOf(destFolder.getId()),mTempNoteList);            
            // Gionee <wangpan><2014-03-20> modify for CR01126031 begin
            showOptionMenu();
            // Gionee <wangpan><2014-03-20> modify for CR01126031 end
//			mHomeOperateLayoutAlwayshow.setVisibility(View.VISIBLE);
			isInFolder = false;
			folderId = -1;
			DragGridViewReal.moveNote = null;
			updateDisplay();
			// updateTitle(titleName);
			// setViewState();

		}

	};

	// jiating begin add dragItem
	private DragListViewReal.DropListViewListener listOnDrop = new DragListViewReal.DropListViewListener() {

		public void dropListView(int from, int to, boolean isDelete,
				boolean isShare, ViewGroup moveItemView) {
			Log.d("HomeActivity------gridOnDrop, drop, " + ", isDelete: " + isDelete
					+ ", isShare: " + isShare);

            // Gionee <wangpan><2014-03-20> modify for CR01126031 begin
            showOptionMenu();
            // Gionee <wangpan><2014-03-20> modify for CR01126031 end
//			mHomeOperateLayoutAlwayshow.setVisibility(View.VISIBLE);
			Note oneNoteData = DragListViewReal.moveNote;
			folderId = -1;
			if (isDelete) {

				// moveItemPosition = from;
				deleteDialogShown(Constants.DIALOG_DELETE_NOTEPAD_LIST);
				// showDialog(Constants.DIALOG_DELETE_NOTEPAD_LIST);
			} else if (isShare) {
				// gn lilg 20120711 add for share note start.
				Log.d("HomeActivity------the info of the note to be share: "
						+ oneNoteData.toString());

				// Gionee <lilg><2013-04-18> modify for CR00795054 begin
				int noteCount = CommonUtils.getNoteCount(oneNoteData);
				if(noteCount <= Constants.SHARE_NOTE_MAX_COUNT){
					// permit share note
					if (shareNote(oneNoteData) == -1) {
						// Gionee <lilg><2013-05-11> modify for CR00809539 begin
//						CommonUtils.showToast(HomeActivity.this, getString(R.string.share_no_note_infolder_toast));
						CommonUtils.showToast(HomeActivity.this, getString(R.string.share_note_empty_toast));
						// Gionee <lilg><2013-05-11> modify for CR00809539 end
					}
				}else{
					// do not permit share note
					CommonUtils.showToast(HomeActivity.this, getString(R.string.share_note_count_toast));
				}
				// Gionee <lilg><2013-04-18> modify for CR00795054 end

				DragListViewReal.moveNote = null;
				updateDisplay();

				// gn lilg 20120711 add for share note end.

			} else {

				String oneNoteDataParentId = oneNoteData.getParentFile();
				if (!("no".equals(oneNoteDataParentId))) {
					dbo.moveOneNote(HomeActivity.this, oneNoteData, null);
					UtilsQueryDatas.moveNote(oneNoteData,-1,mTempNoteList);
					// Note sourceFolder = dbo.queryOneNote(HomeActivity.this,
					// Integer.parseInt(oneNoteDataParentId));
					// sourceFolder.setHaveNoteCount(sourceFolder
					// .getHaveNoteCount() - 1);
					// dbo.updateNote(HomeActivity.this, sourceFolder);
					// oneNoteData.setParentFile("no");
					// dbo.updateNote(HomeActivity.this, oneNoteData);

				}

				DragListViewReal.moveNote = null;
				updateDisplay();

			}

		}
	};

	private DragListViewReal.RemoveListViewListenerTemp listOnRemoveTemp = new DragListViewReal.RemoveListViewListenerTemp() {

		public Note removeListViewTemp(int which) {

            //Gionee <wangpan><2014-03-20> modify for CR01126031 begin
//			Note moveNote = mDataSource.remove(which);
		    Note moveNote = mDataSource.get(which);
            //Gionee <wangpan><2014-03-20> modify for CR01126031 end
			notifyAdapter();
			// 如果移动时界面没有数据时去掉横线
//			if (mDataSource.size() < 1) {
//				mListDiverBottom.setVisibility(View.GONE);
//			}
			if (!moveNote.getParentFile().equals("no")) {
				// 如果移动的是文件夹下的note
				isInFolder = false;
				folderId = -1;
				updateDisplay();
			} else {

				// boolean isHaveFolder = false;
				if (moveNote.getIsFolder().equals(Constants.NO_FOLDER)) {
					for (Note note : mDataSource) {
						if (note.getIsFolder().equals(Constants.IS_FOLDER)) {
							// isHaveFolder = true;
							setShowViewMode();
							break;
						}
					}
				} else {
					setViewState();
				}

			}

			//Gionee <wangpan><2014-03-20> modify for CR01126031 begin
            // make homeOprateLayout Visiable and have animation
			hiddenOptionMenu();
			startAnimationOfHomeOprateLayout();
            //Gionee <wangpan><2014-03-20> modify for CR01126031 end

			return moveNote;

		}

	};

	private DragListViewReal.RemoveListViewListenerToFolder listRemoveListenerToFolder = new DragListViewReal.RemoveListViewListenerToFolder() {

		public void removeListViewToFolder(int from, int to) {
			// 移动到文件夹下
			Note oneNoteData = DragListViewReal.moveNote;
			// String oneNoteDataParentId = oneNoteData.getParentFile();
			Note destFolder = adapter.getmDataSource().get(to);

			dbo.moveOneNote(HomeActivity.this, oneNoteData, destFolder);
			UtilsQueryDatas.moveNote(oneNoteData,Integer.valueOf(destFolder.getId()),mTempNoteList);
		    //Gionee <wangpan><2014-03-20> modify for CR01126031 begin
			showOptionMenu();
		    //Gionee <wangpan><2014-03-20> modify for CR01126031 end
			isInFolder = false;
			folderId = -1;
			DragListViewReal.moveNote = null;
			updateDisplay();

		}

	};

	private void deleteDialogShown(int id) {
		
		Log.i("HomeActivity------delete dialog shown!");

		// gn lilg 2012-11-19 modify for CR00734123 start
		// Dialog dialog;
		// gn lilg 2012-11-19 modify for CR00734123 end

		Builder builder = new AmigoAlertDialog.Builder(HomeActivity.this,CommonUtils.getTheme());
		switch (id) {
		case Constants.DIALOG_DELETE_NOTEPAD_LIST:
			builder.setTitle(R.string.delete_note_dialog_title);
			builder.setMessage(R.string.delete_note_dialog_body);
			
			// gionee lilg 2013-01-16 modify for new demands begin
//			builder.setIcon(android.R.drawable.ic_dialog_alert);
			// gionee lilg 2013-01-16 modify new demands end
			
			builder.setPositiveButton(R.string.delete_note_dialog_sure,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//gn pengwei 20120118 modify for CR00765638 begin
							// gn pengwei 20121126 add for statistics begin
							Statistics.onEvent(HomeActivity.this, Statistics.MAIN_APP_LONG_DEL);
							// gn pengwei 20121126 add for statistics end
							//gn pengwei 20120118 modify for CR00765638 end

							if (mProgressDialog != null) {
								mProgressDialog.dismiss();
							}
							dialogShow();

							new ApiAsynTask().execute();

						}
					});
			builder.setNegativeButton(R.string.delete_note_dialog_cancle,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							// Note oneNoteData;
							if (isGridView) {

								DragGridViewReal.moveNote = null;
							} else {

								DragListViewReal.moveNote = null;
							}

							updateDisplay();

						}
					});
			builder.setCancelable(true);
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {

					if (isGridView) {
						DragGridViewReal.moveNote = null;
					} else {
						DragListViewReal.moveNote = null;
					}
					updateDisplay();

				}
			});
			dialog = builder.create();
			dialog.show();
		}
	}

	class ApiAsynTask extends AsyncTask<Void, Void, Integer> {

		public ApiAsynTask() {
			super();
			Log.i("HomeActivity------ApiAsynTask");
		}

		protected Integer doInBackground(Void... params) {
			Note oneNoteData;
			if (isGridView) {
				oneNoteData = DragGridViewReal.moveNote;
			} else {
				oneNoteData = DragListViewReal.moveNote;
			}
			String widgetId = "";
			String widgetType = "";

			if (null == oneNoteData) {
			    return 1;
			}

				Map<Integer, Set<Integer>> widgetIdMap = null;
				String isFolder = oneNoteData.getIsFolder();
				if (Constants.IS_FOLDER.equals(isFolder)) {

			    List<Note> notes = dbo.queryFromFolder(HomeActivity.this,
			            Integer.parseInt(oneNoteData.getId()));
			    widgetIdMap = new HashMap<Integer, Set<Integer>>();
			    for (Note tempNote : notes) {
			        Integer widgetIdTemp = Integer.parseInt(tempNote
			                .getWidgetId());
			        Integer widgetTypeTemp = Integer.parseInt(tempNote
			                .getWidgetType());
			        Set<Integer> widgetIdSet = widgetIdMap
			        .get(widgetTypeTemp);
			        if (widgetIdSet == null) {
			            widgetIdSet = new HashSet<Integer>();
			            widgetIdMap.put(widgetTypeTemp, widgetIdSet);
			        }
			        widgetIdSet.add(widgetIdTemp);

			        // gn lilg 2013-03-04 add for delete media files in the sdcard begin
			        List<MediaInfo> mediaInfoList = dbo.queryMeidas(HomeActivity.this, tempNote.getId());
			        if(mediaInfoList != null && mediaInfoList.size() > 0){
			            for(MediaInfo mediaInfo : mediaInfoList){
			                if(mediaInfo != null && mediaInfo.getMediaFileName().contains(getResources().getString(R.string.path_note_media))){
			                    Log.d("HomeActivity------path contains 备份/便签多媒体: " + mediaInfo.getMediaFileName().contains(getResources().getString(R.string.path_note_media)));
			                    //									FileUtils.deleteFile(mediaInfo.getMediaFileName());
			                    String deleteFile = FileUtils.getPathByPathType(mediaInfo.getMediaFileName().substring(0, 1)) + FileUtils.getSubPathAndFileName(mediaInfo.getMediaFileName());
			                    Log.d("NoteActivity------delete file: " + deleteFile);
			                    FileUtils.deleteFile(deleteFile);
			                }
			            }
			        }
			        // gn lilg 2013-03-04 add for delete media files in the sdcard end
			    }
			    notes.add(oneNoteData);
			    UtilsQueryDatas.deleteNotes(notes, mTempNoteList);
			} else if (Constants.NO_FOLDER.equals(isFolder)) {

			    widgetIdMap = new HashMap<Integer, Set<Integer>>();
			    Set<Integer> widgetIdSet = new HashSet<Integer>();

			    widgetIdSet
			    .add(Integer.parseInt(oneNoteData.getWidgetId()));
			    widgetIdMap.put(
			            Integer.parseInt(oneNoteData.getWidgetType()),
			            widgetIdSet);

					// gn lilg 2013-03-04 add for delete media files in the sdcard begin
					List<MediaInfo> mediaInfoList = dbo.queryMeidas(HomeActivity.this, oneNoteData.getId());
					if(mediaInfoList != null && mediaInfoList.size() > 0){
						for(MediaInfo mediaInfo : mediaInfoList){
							if(mediaInfo != null && mediaInfo.getMediaFileName().contains(getResources().getString(R.string.path_note_media))){
								Log.d("HomeActivity------path contains 备份/便签多媒体: " + mediaInfo.getMediaFileName().contains(getResources().getString(R.string.path_note_media)));
//								FileUtils.deleteFile(mediaInfo.getMediaFileName());
								String deleteFile = FileUtils.getPathByPathType(mediaInfo.getMediaFileName().substring(0, 1)) + FileUtils.getSubPathAndFileName(mediaInfo.getMediaFileName());
								Log.d("NoteActivity------delete file: " + deleteFile);
								FileUtils.deleteFile(deleteFile);
							}
						}
					}
					// gn lilg 2013-03-04 add for delete media files in the sdcard end
					UtilsQueryDatas.deleteNote(oneNoteData, mTempNoteList);
				}

			dbo.deleteNote(HomeActivity.this, oneNoteData);
			WidgetUtils.updateWidget(HomeActivity.this, widgetIdMap);
			// gn lilg 2012-11-09 modify for CR00725669 end

			return 1;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			Log.i("HomeActivity------onProgressUpdate");

			mProgressDialog.setMessage(getResources().getString(
					R.string.all_delete_dialog_message));
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.i("HomeActivity------onPostExecute");

			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}

			isInFolder = false;

			if (isGridView) {
				DragGridViewReal.moveNote = null;
			} else {
				DragListViewReal.moveNote = null;
			}
			updateDisplay();

		}
	}

	private void dialogShow() {

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		mProgressDialog = new AmigoProgressDialog(HomeActivity.this,CommonUtils.getTheme());
		// mProgressDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		Log.i("HomeActivity------dialogShow");

		mProgressDialog.setMessage(getResources().getString(
				R.string.all_delete_dialog_message));

		mProgressDialog.setProgressStyle(AmigoProgressDialog.STYLE_SPINNER);
		// mProgressDialog.setMax(MAX_PROGRESS);

		mProgressDialog.setCancelable(false);

		mProgressDialog.setIndeterminate(false);

		mProgressDialog.show();
	}
	
	// gionee 20121214 jiating modfity for theme begin
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    Log.d("HomeActivity-onCreateOptionsMenu");
		if (mOptionsMenu == null) {
			mOptionsMenu = menu;
			getMenuInflater().inflate(R.menu.home_title_bar_menu, menu);
			// gionee 20121218 jiating modfity for theme begin
			mSearch = menu.findItem(R.id.gn_note_search_item);
			mAddNote = menu.findItem(R.id.gn_note_add_note_item);
			mNewFolder = menu.findItem(R.id.gn_note_add_folder_item);
			mAllOperator = menu.findItem(R.id.gn_note_all_operator_item);
			mInportExport = menu.findItem(R.id.gn_note_import_export_item);
			// Gionee <wanghaiyan><2015-02-04> modify CR01445194 for begin
            //mAboutNote = menu.findItem(R.id.gn_note_about_item);
			// Gionee <wanghaiyan><2015-02-04> modify CR01445194 for end
			// gn jiating 20121009 GN_GUEST_MODE begin
		}
	    updateMenuState();
		// gionee 20121218 jiating modfity for theme begin
	    
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// gionee 20121218 jiating modify for theme begin
		switch (item.getItemId()) {
		case android.R.id.home:
			if (isInFolder) {
				// in folder click backIcon
	            setIsInFolder(false);
				clearFolderId();
				updateDisplay();
				Log.v("DayView---onOptionsItemSelected---");
			}
			Log.v("DayView---onOptionsItemSelected---1");
			return super.onOptionsItemSelected(item);


		case R.id.gn_note_search_item:
			onSearchRequested();
			return super.onOptionsItemSelected(item);

		case R.id.gn_note_add_note_item:
			newNote();
			return super.onOptionsItemSelected(item);

		case R.id.gn_note_add_folder_item:
			newFolder();
			return super.onOptionsItemSelected(item);

		case R.id.gn_note_all_operator_item:
			Intent intent = new Intent(HomeActivity.this, ALLEditActivity.class);

			intent.putExtra(Constants.IS_IN_FOLDER, isInFolder);
			intent.putExtra(DBOpenHelper.ID, folderId);
			startActivity(intent);
			return super.onOptionsItemSelected(item);

		case R.id.gn_note_import_export_item:
			inportOrExport();
			return super.onOptionsItemSelected(item);
        //Gionee <wanghaiyan><2015-02-04> modify CR01445194 for begin
		//case R.id.gn_note_about_item:
		//	Statistics.onEvent(HomeActivity.this, Statistics.ABOUT);
			// gn pengwei 20121126 add for statistics end
		//	Intent aboutIntent = new Intent(HomeActivity.this,
		//			AboutActivity.class);
		//	startActivity(aboutIntent);
		//	return super.onOptionsItemSelected(item);

			// gionee 20121218 jiating modify for theme end
		//Gionee <wanghaiyan><2015-02-04> modify CR01445194 for end
		default:
			return super.onOptionsItemSelected(item);

		}

	}

	@Override
	protected void onPause() {
	    Log.d("HomeActivity-onPause");
		// TODO Auto-generated method stub
		obtainViewMode();
		// Gionee <lilg><2013-04-10> add for note upgrade begin
		((NoteApplication) getApplication()).unregisterVersionCallback(this);
		// Gionee <lilg><2013-04-10> add for note upgrade end
		
		super.onPause();
		SharedPreferences.Editor editor = AmigoPreferenceManager
				.getDefaultSharedPreferences(HomeActivity.this).edit();
		editor.putBoolean(Constants.HOME_SHOW_VIEW_MODE, isGridView);
		editor.commit();
		Statistics.onPause(this);
	}
	// gionee 20121214 jiating modfity for theme end      



	class UpdateHomeViewTask extends AsyncTask<Integer, Integer, String> {
		private static final String LOADDATASUCESS = "SUCCESS";
		private static final String LOADDATAFAIL = "FAIL";
		private Context context = null;
		private Message msg;
		public UpdateHomeViewTask(Context context) {
			dbo = DBOperations.getInstances(context);
			mTempNoteList = new Vector<Note>();
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Integer... params) {
			try {
	            msg = Message.obtain();
	            Bundle b = new Bundle();
	            b.putBoolean(SHOW_DAILOG,true);
	            msg.setData(b);
	            homeProgressHandler.sendMessageDelayed(msg,delayTime);
				dbo.queryAllDatas(context, mTempNoteList);
				return LOADDATASUCESS;
			} catch (Exception e) {
			    Log.d("HomeActivity-UpdateHomeViewTask-doInBackground : "+e);
				return LOADDATAFAIL;
			}
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
            Log.d("HomeActivity-UpdateHomeViewTask-onPostExecute : "+result);
			startDialog = false;
			if (mProDialog != null) {
				mProDialog.dismiss();
			}
			if(result.equals(LOADDATASUCESS)){
				updateDisplay();
			}else{
				Toast.makeText(this.context,context.getResources().getString(R.string.home_load_datas_error)
						,Toast.LENGTH_SHORT);
				programExit();
			}
		}

	}
	
		private void programExit(){
		    Log.d("HomeActivity-programExit");
			this.finish();
		}
		
		/*
		 * Get ViewMode
		 * 
		 * @param context Context environment of Activity
		 * 
		 * @return void
		 * 
		 * @since 2012-11-27
		 */
		public void obtainViewMode(){
			boolean isGridView = AmigoPreferenceManager.getDefaultSharedPreferences(
					this).getBoolean(Constants.HOME_SHOW_VIEW_MODE, true);
			if (isGridView) {
				Statistics.onEvent(HomeActivity.this, Statistics.MODE_THUMBNAIL);
			} else {
				Statistics.onEvent(HomeActivity.this, Statistics.MODE_LIST);
			}
		}

}
//Gionee <pengwei><20130805> modify for CR00844807 end
