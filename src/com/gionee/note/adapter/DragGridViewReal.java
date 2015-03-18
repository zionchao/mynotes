package com.gionee.note.adapter;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.util.AttributeSet;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.gionee.note.HomeActivity;
import com.gionee.note.R;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.ResourceParser;
import com.gionee.note.content.Session;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.Log;

public class DragGridViewReal extends GridView {
	private int dragPosition;
	private int dropPosition;
	private int dragPointX;
	private int dragPointY;
	private int dragOffsetX;
	private int dragOffsetY;
	private boolean mIsDelete = false;
	private boolean mIsShare = false;
	private int itemHeight;
	private int numColumns=2;
	public static Note moveNote = null;
	private ImageView dragImageView;
	private DropListener mDropListener;
	private RemoveListenerToFolder mRemoveListenerToFolder;
	private RemoveListenerTemp mRemoveListenerTemp;
	private WindowManager windowManager;
	private WindowManager.LayoutParams windowParams;


	private ViewAdapter adapter;
	// private String isFolder;

	private Context context;

	private ViewGroup moveItemView;

	//CQ679151 jiating 20120921 begin
	private static boolean isStartDrag=false;
	//CQ679151 jiating 20120921 end

	public DragGridViewReal(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;

	}
    
	public static boolean isStartDrag() {
        return isStartDrag;
    }

    public static void setStartDrag(boolean isStartDrag) {
        DragGridViewReal.isStartDrag = isStartDrag;
    }

    public void setOnItemLongClickListener(final MotionEvent ev) {

		final int x = (int) ev.getX();
		final int y = (int) ev.getY();
		this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mIsDelete = false;
				mIsShare = false;
				// isMove = true;
                ((HomeActivity) context).getDelete_button()
                        .setBackgroundResource(R.drawable.gn_note_bottom_bg);
                ((HomeActivity) context).getmShareButton()
                        .setBackgroundResource(R.drawable.gn_note_bottom_bg);
				
				Vibrator mVibrator = (Vibrator) context.getSystemService(
						Service.VIBRATOR_SERVICE);
				
				mVibrator.vibrate(3);
				adapter = (ViewAdapter) arg0.getAdapter();
				// Note note = adapter.getmDataSource().get(arg2);
				// isFolder = (String) note.getIsFolder();
				dragPosition = dropPosition = arg2;
				if (dragPosition == AdapterView.INVALID_POSITION) {
					return false;
				}
				moveItemView = (ViewGroup) getChildAt(dragPosition
						- getFirstVisiblePosition());//Get the current position of the view
				
				dragPointX = 0;
				dragOffsetY = 0;
				dragPointX = x - moveItemView.getLeft();
				dragPointY = y - moveItemView.getTop();//Get the dragPoint is actually in you click on a specified item in height

				dragOffsetX = (int) (ev.getRawX() - x);
				dragOffsetY = (int) (ev.getRawY() - y);
				itemHeight = moveItemView.getHeight();
			
				// if (isFolder.equals(Constants.NO_FOLDER)) {
				if (null != mRemoveListenerTemp) {
					moveNote = mRemoveListenerTemp.removeTemp(dragPosition);

				}
				// if (!HomeActivity.isGridView) {
				// moveItemView.getChildAt(1).setVisibility(View.GONE);
				// }
				if (moveNote!=null && !(moveNote.getIsFolder().equals(Constants.IS_FOLDER))) {
					View childTitleView = moveItemView.getChildAt(0);

					childTitleView.setBackgroundResource(ResourceParser
							.getNoteMoveTitleBg(Integer.parseInt(moveNote
									.getBgColor())));
					View childContentView = moveItemView.getChildAt(1);

					childContentView.setBackgroundResource(ResourceParser
							.getNoteMoveContentBg(Integer.parseInt(moveNote
									.getBgColor())));
					

				}
				moveItemView.destroyDrawingCache();//Release drawing resources used in the cache
				moveItemView.setDrawingCacheEnabled(true);
				Bitmap bm = Bitmap.createBitmap(moveItemView.getDrawingCache());
				// moveItemView.setVisibility(View.INVISIBLE);
				startDrag(bm, x, y);

				return true;
				// } else {
				// return false;
				// }
			};
		});
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mDropListener != null) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				//TODO jiating

				setOnItemLongClickListener(ev);
			}
		}
		return super.onInterceptTouchEvent(ev);
		// return false;
	}

	private void startDrag(Bitmap bm, int x, int y) {
		stopDrag();
		//CQ679151 jiating 20120921 begin
		setStartDrag(true);
		//CQ679151 jiating 20120921 end
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;

		windowParams.x = x - dragPointX + dragOffsetX;
		windowParams.y = y - dragPointY + dragOffsetY;

		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
		| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(bm);
		// iv.setBackgroundColor(R.color.black);
		// // iv.setB
		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);// "window"
		windowParams.alpha = 0.6f;
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}
	
	//Gionee <pengwei><2013-11-15> modify for CR00956703 begin
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
		if (dragImageView != null
				&& dragPosition != AdapterView.INVALID_POSITION
				&& (mDropListener != null)) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:

				adapter = (ViewAdapter) getAdapter();
				onDrag(x, y);
				break;
			case MotionEvent.ACTION_UP:
				stopDrag();
				onDrop(x, y);
				setEnabled(true);
				// isMove = false;
				// adapter.setHaveFolder(true);
				break;
			}
		}
		return super.onTouchEvent(ev);
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	//Gionee <pengwei><2013-11-15> modify for CR00956703 end

	//Gionee <pengwei><2013-11-15> modify for CR00933874 begin
	private void notifyAdapter() {
		requestLayout();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	//Gionee <pengwei><2013-11-15> modify for CR00933874 end
	
	private void onDrag(int x, int y) {
		//gn pengwei 20130121 add for CR00766172 begin
		try {
		//gn pengwei 20130121 add for CR00766172 end
		if (dragImageView != null) {
			//TODO jiating
			//			windowParams.alpha = 0.6f;
			// windowParams.gravity = Gravity.TOP | Gravity.LEFT;
			windowParams.x = x - dragPointX + dragOffsetX;
			windowParams.y = y - dragPointY + dragOffsetY;

			windowManager.updateViewLayout(dragImageView, windowParams);
		}

		// int tempScrollX = x - dragPointX + dragOffsetX;
		int tempScrollY = y - dragPointY + dragOffsetY;

		int rangeY = itemHeight;
		// int maxHeight = getHeight() - rangeY / 6;
		int position = pointToPosition(x, y);

		int mDeleteButtonHeight = ((HomeActivity) context).getDelete_button()
		.getHeight();
		int mDeleteButtonWight = ((HomeActivity) context).getDelete_button()
		.getWidth();
		int maxHeight = getHeight() - mDeleteButtonHeight;
		if (position > -1 && position < adapter.getmDataSource().size()) {
			Note note = adapter.getmDataSource().get(position);
			String is_Folder = (String) note.getIsFolder();
			if (is_Folder.equals(Constants.IS_FOLDER)) {
				if (!moveNote.getIsFolder().equals(Constants.IS_FOLDER)) {

					adapter.updateView(position);
				}

			} else {
				adapter.updateView(-1);
			}
		} else {
			adapter.updateView(-1);
		}

		int screenHeight = Session.getScreenHeight();
		if (y > screenHeight - mDeleteButtonHeight - dragOffsetY) {
			// if (position > -1) {
			// Note note = adapter.getmDataSource().get(position);
			// String is_Folder = (String) note.getIsFolder();
			// if (is_Folder.equals(Constants.IS_FOLDER)) {
			// isOnBottom = true;
			// }
			// }
			if (x < mDeleteButtonWight) {
				mIsDelete = true;
				mIsShare = false;
				((HomeActivity) context).getDelete_button().setBackgroundResource(R.drawable.gn_note_home_delete_or_share_on);
				((HomeActivity) context).getmShareButton().setBackgroundResource(R.drawable.gn_note_bottom_bg);

			} else if(x >mDeleteButtonWight && x<(getWidth()-mDeleteButtonWight)){
				mIsDelete = false;
				mIsShare = false;

				((HomeActivity) context).getDelete_button().setBackgroundResource(R.drawable.gn_note_bottom_bg);
				((HomeActivity) context).getmShareButton().setBackgroundResource(R.drawable.gn_note_bottom_bg);
			}else{
	
				mIsDelete = false;
				mIsShare = true;

				((HomeActivity) context).getmShareButton().setBackgroundResource(R.drawable.gn_note_home_delete_or_share_on);
				((HomeActivity) context).getDelete_button().setBackgroundResource(R.drawable.gn_note_bottom_bg);
			}

		} else {
			mIsDelete = false;
			mIsShare = false;

			((HomeActivity) context).getDelete_button().setBackgroundResource(R.drawable.gn_note_bottom_bg);
			((HomeActivity) context).getmShareButton().setBackgroundResource(R.drawable.gn_note_bottom_bg);
		}

		boolean isFolder = false;
		for (Note note : adapter.getmDataSource()) {
			if (Constants.IS_FOLDER.equals(note.getIsFolder())) {
				isFolder = true;
				break;
			}
		}

		if (isFolder && moveNote.getIsFolder().equals(Constants.NO_FOLDER)) {
			float miniTopScroll = HomeActivity.isGridView ? (float)rangeY / 2
					: (rangeY + (float)rangeY / 3);

			if (tempScrollY < miniTopScroll) {
				// setEnabled(true);
				smoothScrollBy(-itemHeight,
						400);

			} else if (tempScrollY > maxHeight) {

				int position1 = getLastVisiblePosition();
				Log.i("DragGridViewReal------position1: " + position1);

				// int mm = position1%2 == 0? 2:1;

				if (adapter.getmDataSource().size() > position1 + 1
						&& Constants.IS_FOLDER.equals(adapter.getmDataSource()
								.get(position1 + 1).getIsFolder())) {


					smoothScrollBy(itemHeight,
							400);
//					smoothScrollToPosition(position1 + 1);

				}
				else if(position1 -1 > 0 && Constants.IS_FOLDER.equals(adapter.getmDataSource()
						.get(position1 -1).getIsFolder())){
					int lastViewIndex = getChildCount() - 1;
					int listHeight = getHeight();
					View lastView = getChildAt(lastViewIndex);
					int lastViewHeight = lastView.getHeight();
					int lastViewTop = lastView.getTop();
					int lastViewPixelsShowing = listHeight - lastViewTop;

					smoothScrollBy(lastViewHeight - lastViewPixelsShowing,
							400);
				}
				// else if(Constants.IS_FOLDER.equals(adapter
				// .getmDataSource().get(position1 )
				// .getIsFolder())){
				// Log.i("jiating","position1....13......."+position1);
				//
				// // smoothScrollByOffset(1);
				// // smoothScrollToPosition(position1, position1-1);
				// // smoothScrollToPositionFromTop(position1, 0);
				// smoothScrollToPosition(position1);
				//
				// Log.i("jiating","position1....14......."+getLastVisiblePosition());
				// }

				Log.i("DragGridViewReal------position1, 15: "
						+ getLastVisiblePosition());

			}

		}
		//gn pengwei 20130121 add for CR00766172 begin
	} catch (Exception e) {
		// TODO: handle exception
		Log.e("DragGridViewReal------Exception");
	}
	//gn pengwei 20130121 add for CR00766172 end
	}

    private void onDrop(int x, int y) {
        // gionee pengwei 20130310 modify for CR00780549 begin
        try {
            int tempPosition = pointToPosition(x, y);

            dropPosition = tempPosition;

            if ((-1) == dropPosition || dropPosition >= getCount() || mIsDelete
                    || mIsShare) {

                if (mDropListener != null) {
                    mDropListener.drop(dragPosition, dropPosition, mIsDelete,
                            mIsShare, moveItemView);

                }
            } else {
                // TODO jiating
                if (moveNote.getIsFolder().equals(Constants.NO_FOLDER)) {
                    // mIsDelete = false;
                    // mIsShare = false;
                    Note note = adapter.getmDataSource().get(dropPosition);
                    String is_Folder = (String) note.getIsFolder();

                    if (is_Folder.equals(Constants.NO_FOLDER)) {
                        if (mDropListener != null && dropPosition >= 0
                                && dropPosition <= getCount()) {
                            mDropListener.drop(dragPosition, dropPosition,
                                    mIsDelete, mIsShare, moveItemView);

                        }
                    } else if (is_Folder.equals(Constants.IS_FOLDER)) {

                        if (mRemoveListenerToFolder != null) {

                            mRemoveListenerToFolder.removeToFolder(
                                    dragPosition, dropPosition);
                        }

                    }
                } else {
                    if (mDropListener != null) {
                        mDropListener.drop(dragPosition, dropPosition,
                                mIsDelete, mIsShare, moveItemView);

                    }
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("DragListViewReal---onDrop---" + e);
        }
        // gionee pengwei 20130310 modify for CR00780549 end
    }

	public void stopDrag() {
		//CQ679151 jiating 20120921 begin
		setStartDrag(false);
		//CQ679151 jiating 20120921 end
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	public interface DropListener {
		void drop(int from, int to, boolean isDelete, boolean isShare,
				ViewGroup moveItemView);
	}

	public void setDropListener(DropListener onDrop) {
		// TODO Auto-generated method stub
		mDropListener = onDrop;
	}

	public interface RemoveListenerToFolder {
		void removeToFolder(int which, int to);
	}

	public void setRemoveListener(RemoveListenerToFolder onRemoveToFolder) {
		mRemoveListenerToFolder = onRemoveToFolder;
	}

	public interface RemoveListenerTemp {
		Note removeTemp(int which);
	}

	public void setRemoveListenerTemp(RemoveListenerTemp onRemoveTemp) {
		mRemoveListenerTemp = onRemoveTemp;
	}
}
