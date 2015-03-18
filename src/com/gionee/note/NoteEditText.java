/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gionee.note;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import amigo.app.AmigoAlertDialog;
import amigo.app.AmigoAlertDialog.Builder;
import amigo.widget.AmigoEditText;

import com.gionee.note.noteMedia.record.NoteMediaManager;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;


public class NoteEditText extends AmigoEditText {
	private int mIndex;
	private int mSelectionStartBeforeDelete;

	private static final String SCHEME_TEL = "tel:" ;
	private static final String SCHEME_HTTP = "http:" ;
	private static final String SCHEME_EMAIL = "mailto:" ;

	private static final Map<String, Integer> sSchemaActionResMap = new HashMap<String, Integer>();
	static {
		sSchemaActionResMap.put(SCHEME_TEL, R.string.note_link_tel);
		sSchemaActionResMap.put(SCHEME_HTTP, R.string.note_link_web);
		sSchemaActionResMap.put(SCHEME_EMAIL, R.string.note_link_email);
	}
    // Gionee <wangpan><2014-04-17> add for CR01045885 begin
	private int mLastMotionX, mLastMotionY;  
	//是否移动了  
	private boolean isMoved;  
	//是否释放了  
	private boolean isReleased;  
	//计数器，防止多次点击导致最后一次形成longpress的时间变短  
	private int mCounter;  
	//长按的runnable  
	private Runnable mLongPressRunnable;  
	//移动的阈值  
	private static final int TOUCH_SLOP = 20;
	private boolean isContextMenuVisiable = false; 
    // Gionee <wangpan><2014-04-17> add for CR01045885 end
	
	/**
	 * Call by the {@link NoteEditActivity} to delete or add edit text
	 */
	public interface OnTextViewChangeListener {
		/**
		 * Delete current edit text when {@link KeyEvent#KEYCODE_DEL} happens
		 * and the text is null
		 */
		void onEditTextDelete(int index, String text);

		/**
		 * Add edit text after current edit text when {@link KeyEvent#KEYCODE_ENTER}
		 * happen
		 */
		void onEditTextEnter(int index, String text);

		/**
		 * Hide or show item option when text change
		 */
		void onTextChange(int index, boolean hasText);
	}

	private OnTextViewChangeListener mOnTextViewChangeListener;

	public NoteEditText(Context context) {
		super(context, null);
		mIndex = 0;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

	public void setOnTextViewChangeListener(OnTextViewChangeListener listener) {
		mOnTextViewChangeListener = listener;
	}

	public NoteEditText(final Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.editTextStyle);
		// Gionee <wangpan><2014-04-17> add for CR01045885 begin
        mLongPressRunnable = new Runnable() {

            @Override
            public void run() {
                mCounter--;
                // 计数器大于0，说明当前执行的Runnable不是最后一次down产生的。
                if (mCounter > 0 || isReleased || isMoved)
                    return;
                int selStart = getSelectionStart();
                int selEnd = getSelectionEnd();

                int min = Math.min(selStart, selEnd);
                int max = Math.max(selStart, selEnd);

                final URLSpan[] urls = getText().getSpans(min, max, URLSpan.class);
                if (urls.length == 1) {
                    isContextMenuVisiable = true;
                    int defaultResId = getDialogTitle(urls);
                    showDialog(context, urls, defaultResId);
                }
            }
        }; 
	}
	private int getDialogTitle(final URLSpan[] urls) {
        int defaultResId = 0;
        for(String schema: sSchemaActionResMap.keySet()) {
            if(urls[0].getURL().indexOf(schema) >= 0) {
                defaultResId = sSchemaActionResMap.get(schema);
                break;
            }
        }

        if (defaultResId == 0) {
            defaultResId = R.string.note_link_other;
        }
        return defaultResId;
    }
	private void showDialog(final Context context,
            final URLSpan[] urls, int defaultResId) {
        Builder builder = new AmigoAlertDialog.Builder(context,CommonUtils.getTheme());
        String[] menu = {context.getResources().getString(defaultResId)};
        builder.setItems(menu, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
             // goto a new intent
                urls[0].onClick(NoteEditText.this);
            }
        });
        builder.show();
    }
    // Gionee <wangpan><2014-04-17> add for CR01045885 end
	public NoteEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
	        // Gionee <wangpan><2014-04-17> add for CR01045885 begin
		    isContextMenuVisiable = false;
	        // Gionee <wangpan><2014-04-17> add for CR01045885 end
			//
			mLastMotionX = x;
            mLastMotionY = y;
            
			Log.i("NoteEditText-onTouchEvent x0="+ x + " y0="+y);
			x -= getTotalPaddingLeft();
			y -= getTotalPaddingTop();
			Log.i("NoteEditText-onTouchEvent x1="+ x + " y1="+y);
			x += getScrollX();
			y += getScrollY();
			Log.i("NoteEditText-onTouchEvent x2="+ x + " y2="+y);
			
			Layout layout = getLayout();
			int line = layout.getLineForVertical(y);
			int off = layout.getOffsetForHorizontal(line, x);
			Selection.setSelection(getText(), off);

	        // Gionee <wangpan><2014-04-17> add for CR01045885 begin
            mCounter++;
            isReleased = false;
            isMoved = false;
            postDelayed(mLongPressRunnable,
                    ViewConfiguration.getLongPressTimeout());
            // Gionee <wangpan><2014-04-17> add for CR01045885 end
			break;
	    // Gionee <wangpan><2014-04-17> add for CR01045885 begin
		case MotionEvent.ACTION_MOVE:
		    if (isMoved)
                break;
            if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
                    || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
                // 移动超过阈值，则表示移动了
                isMoved = true;
            }
		    break;
		case MotionEvent.ACTION_UP:
		 // 释放了
            isReleased = true;
		    break;
		}
		if(isContextMenuVisiable){
		    return true;
		}
        // Gionee <wangpan><2014-04-17> add for CR01045885 end
		return super.onTouchEvent(event);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_ENTER:
			if (mOnTextViewChangeListener != null) {
				return false;
			}
			break;
		case KeyEvent.KEYCODE_DEL:
			mSelectionStartBeforeDelete = getSelectionStart();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch(keyCode) {
		case KeyEvent.KEYCODE_DEL:
			if (mOnTextViewChangeListener != null) {
				if (0 == mSelectionStartBeforeDelete && mIndex != 0) {
					mOnTextViewChangeListener.onEditTextDelete(mIndex, getText().toString());
					return true;
				}
			} else {
				Log.i("NoteEditText------OnTextViewChangeListener was not seted 1");
			}
			break;
		case KeyEvent.KEYCODE_ENTER:
			if (mOnTextViewChangeListener != null) {
				int selectionStart = getSelectionStart();
				String text = getText().subSequence(selectionStart, length()).toString();
				setText(getText().subSequence(0, selectionStart));
				mOnTextViewChangeListener.onEditTextEnter(mIndex + 1, text);
			} else {
				Log.i("NoteEditText------OnTextViewChangeListener was not seted 2");
			}
			break;
		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		if (mOnTextViewChangeListener != null) {
			if (!focused && TextUtils.isEmpty(getText())) {
				mOnTextViewChangeListener.onTextChange(mIndex, false);
			} else {
				mOnTextViewChangeListener.onTextChange(mIndex, true);
			}
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}
	
    // Gionee <wangpan><2014-04-17> delete for CR01045885 begin
	/*@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		if (getText() instanceof Spanned) {
			int selStart = getSelectionStart();
			int selEnd = getSelectionEnd();

			int min = Math.min(selStart, selEnd);
			int max = Math.max(selStart, selEnd);

			final URLSpan[] urls = ((Spanned) getText()).getSpans(min, max, URLSpan.class);
			if (urls.length == 1) {
				int defaultResId = 0;
				for(String schema: sSchemaActionResMap.keySet()) {
					if(urls[0].getURL().indexOf(schema) >= 0) {
						defaultResId = sSchemaActionResMap.get(schema);
						break;
					}
				}

				if (defaultResId == 0) {
					defaultResId = R.string.note_link_other;
				}

				menu.add(0, 0, 0, defaultResId).setOnMenuItemClickListener(
						new OnMenuItemClickListener() {
							public boolean onMenuItemClick(MenuItem item) {
								// goto a new intent
								urls[0].onClick(NoteEditText.this);
								return true;
							}
						});
			}
		}
		super.onCreateContextMenu(menu);
	}*/
    // Gionee <wangpan><2014-04-17> delete for CR01045885 end
	
    public static final byte KEYBOARD_STATE_SHOW = -3;  
    public static final byte KEYBOARD_STATE_HIDE = -2;  
    public static final byte KEYBOARD_STATE_INIT = -1;  
    
    private IOnKeyboardStateChangedListener onKeyboardStateChangedListener;  
    private boolean mHasInit = false;  
    private boolean mHasKeyboard = false;  
    private int mHeight;  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        super.onLayout(changed, l, t, r, b);  
        if(!mHasInit) {  
            mHasInit = true;  
            mHeight = b;  
            if(onKeyboardStateChangedListener != null) {  
                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_INIT);  
            }  
        } else {  
            mHeight = mHeight < b ? b : mHeight;  
        }  
          
        if(mHasInit && mHeight > b) {  
            mHasKeyboard = true;  
            if(onKeyboardStateChangedListener != null) {  
                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_SHOW);  
            }  
        }  
        if(mHasInit && mHasKeyboard && mHeight == b) {  
            mHasKeyboard = false;  
            if(onKeyboardStateChangedListener != null) {  
                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_HIDE);  
            }  
        }  
    }  
    
    public void setOnKeyboardStateChangedListener(IOnKeyboardStateChangedListener iOnKeyboardStateChangedListener) {  
        this.onKeyboardStateChangedListener = iOnKeyboardStateChangedListener;  
    }  
    
    public interface IOnKeyboardStateChangedListener {
        public void onKeyboardStateChanged(int state);  
    }    
    
    
	
	public void insertMediaRecord(Context context, final String recordNameTag, Bitmap mediaRecordBitmap) {
		final SpannableString spannableString = new SpannableString(recordNameTag);
		
		ImageSpan imageSpan = new ImageSpan(context, mediaRecordBitmap);
		spannableString.setSpan(imageSpan, 0, recordNameTag.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		
		append(spannableString);
	}
	
	public void insertMediaButton(String btnNameTag, int drawableId, final Handler handler){
		final SpannableString spannableString = new SpannableString(btnNameTag);
		
		Drawable drawable = getResources().getDrawable(drawableId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		spannableString.setSpan(imageSpan, 0, btnNameTag.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				Log.d("NoteEditText------on click media record button delete drawable!");
				if(handler != null){
					handler.sendEmptyMessage(NoteActivity.FLAG_DELETE_MEDIA_RECORD);
				}
			}
		};
		
		spannableString.setSpan(clickableSpan, 0, btnNameTag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		append(spannableString);
	}
	
	// gn lilg 2013-02-25 add for insert media record info into edittext end

}
