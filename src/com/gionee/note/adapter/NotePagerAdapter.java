package com.gionee.note.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gionee.note.R;
import com.gionee.note.NoteEditText.IOnKeyboardStateChangedListener;
import com.gionee.note.content.Constants;
import com.gionee.note.content.NoteApplication;
import com.gionee.note.content.ResourceParser;
import com.gionee.note.content.ResourceParser.NoteBgResources;
import com.gionee.note.domain.Note;
import com.gionee.note.utils.CommonUtils;
import com.gionee.note.utils.Log;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NotePagerAdapter extends PagerAdapter {

	private List<Note> noteList;
	private Method method;
	private View[] pageViews;
	private Context context;
	public static final int defaultColor = 0;
	public NotePagerAdapter(List<Note> noteList,Context context) {
		this.noteList = noteList;
		this.context = context;
		pageViews = new View[noteList.size()];
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public int getCount() {
		if(noteList == null){
			return 0;
		}
		return noteList.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		if(pageViews[position] == null){
			return;
		}
		if(container instanceof ViewPager){
		    ((ViewPager) container).removeView(pageViews[position]);
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public Object instantiateItem(View container, final int position) {
		View detailBrowseView = null;
			if(pageViews[position] == null){
				ViewItem  viewItem = null; 
				viewItem = new ViewItem();    
		         detailBrowseView = initLook(noteList.get(position),viewItem);
		         detailBrowseView.setTag(viewItem);
		         if(container instanceof ViewPager){
		             ((ViewPager) container).addView(detailBrowseView);
		         }

			}else{
				detailBrowseView = pageViews[position];
			}
			((ViewItem)(detailBrowseView.getTag())).showContext.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Note note = noteList.get(position);
					onDownListener.onDown(note);
				}
			});
		return detailBrowseView;
	}
	
	private View initLook(Note note,ViewItem viewItem){
		View detailBrowseView = LayoutInflater.from(context).inflate(
				R.layout.note_detail_view, null); 
		viewItem.noteTabRelView = (RelativeLayout) detailBrowseView.findViewById(R.id.note_tab_view);
		viewItem.contentLinearView = (RelativeLayout) detailBrowseView.findViewById(R.id.content_linear_view);
		viewItem.noteBottomView = (LinearLayout) detailBrowseView.findViewById(R.id.note_detail_bottom_view);
		
		viewItem.labelDivisionView = (View) detailBrowseView.findViewById(R.id.label_division_view);
		viewItem.labelAlarmView = (RelativeLayout) detailBrowseView.findViewById(R.id.label_alarm_view);
		viewItem.alarmContextView = (LinearLayout) detailBrowseView.findViewById(R.id.label_alarm_context_view);
		viewItem.alarmTextView = (TextView) detailBrowseView.findViewById(R.id.alarm_text_view);
		viewItem.alarmBtnView = (ImageView) detailBrowseView.findViewById(R.id.alarm_btn_view);
		
		viewItem.labelAddressView = (RelativeLayout) detailBrowseView.findViewById(R.id.label_address_view);
		viewItem.addressTextView = (TextView) detailBrowseView.findViewById(R.id.address_text_view);
		viewItem.addressBtnView = (ImageView) detailBrowseView.findViewById(R.id.address_btn_view);
		viewItem.addressContextView = (LinearLayout) detailBrowseView.findViewById(R.id.label_address_context_view);
		viewItem.showTime = (TextView) detailBrowseView.findViewById(R.id.note_time_show_view);
		viewItem.showContext = (TextView) detailBrowseView.findViewById(R.id.note_show_content_view);
		viewItem.showContext
		.setMovementMethod(ScrollingMovementMethod.getInstance());
		viewItem.createNewNoteBtn = (ImageView) detailBrowseView.findViewById(R.id.create_new_note_view);
		CommonUtils.isAbledAdd(context, viewItem.createNewNoteBtn, false);
		String timeShowStr = CommonUtils.getNoteData(context,
				note.getUpdateDate(), note.getUpdateTime());
		viewItem.showTime.setText(timeShowStr);
		CommonUtils.setTextForTextView(context,note.getContent(),viewItem.showContext,note);
		initLookNoteScreen(viewItem,note);
		changeLookTextColor(note,viewItem);
		if (note.getAlarmTime() == null
				|| Constants.INIT_ALARM_TIME.equals(note
						.getAlarmTime())) {
			hideLookAlarm(viewItem);
		} else {
			showLookAlarm(note,viewItem);
		}

		if ((note.getAddressName() != null && !"".equals(note
				.getAddressName()))) {
			showLookAdress(note.getAddressName(),viewItem);
		} else {
			hideLookAddress(viewItem);
		}
		return detailBrowseView;
	}
	
	private void hideLookAddress(ViewItem viewItem) {
		if (viewItem.labelAlarmView.getVisibility() == View.GONE) {
			viewItem.labelAddressView.setVisibility(View.INVISIBLE);
		} else if (viewItem.labelAlarmView.getVisibility() == View.VISIBLE) {
			viewItem.labelAddressView.setVisibility(View.GONE);
		}
		viewItem.labelDivisionView.setVisibility(View.GONE);
	}
	
	private void showLookAlarm(Note note,ViewItem viewItem) {
		// mBtnAlarm.setImageResource(R.drawable.gn_alarm_orange);
		if (viewItem.labelAddressView.getVisibility() == View.INVISIBLE) {
			viewItem.labelAddressView.setVisibility(View.GONE);
		}
		String alarmTime = com.gionee.note.utils.DateUtils.format(
				new Date(Long.valueOf(note.getAlarmTime())),
				"yyyy-MM-dd HH:mm:ss");
		viewItem.alarmTextView.setText(alarmTime);
		viewItem.labelAlarmView.setVisibility(View.VISIBLE);
	}
	
	private void showLookAdress(String address,ViewItem viewItem) {
		if (viewItem.labelAlarmView.getVisibility() == View.INVISIBLE) {
			viewItem.labelAlarmView.setVisibility(View.GONE);
		}
		// Gionee <pengwei><2013-3-18> modify for CR00785551 begin
		int paddingLeft = CommonUtils.dip2px(context,
				viewItem.showContext.getPaddingLeft());
		int paddingRight = CommonUtils.dip2px(context,
				viewItem.showContext.getPaddingRight());
		int marginLeft = CommonUtils.dip2px(context,8);
		int marginRight = CommonUtils.dip2px(context,72);
		int picWid = com.gionee.note.content.Session.getScreenWight() - paddingLeft
				- paddingRight - marginLeft - marginRight;
		viewItem.addressTextView.setMaxWidth(picWid * 4 / 5);
		// Gionee <pengwei><2013-3-18> modify for CR00785551 end
		viewItem.labelDivisionView.setVisibility(View.VISIBLE);
		viewItem.labelAddressView.setVisibility(View.VISIBLE);
		viewItem.addressTextView.setText(address);
	}
	
	private void changeLookTextColor(Note note,ViewItem viewItem) {
		if(note == null || note.getBgColor() == null){
			viewItem.showContext.setTextColor(ResourceParser
					.getNoteGridContentColor(0));
			viewItem.showTime.setTextColor(ResourceParser.getNoteGridContentColor(0));
			return;
		}
		viewItem.showContext.setTextColor(ResourceParser
				.getNoteGridContentColor(Integer.parseInt(note.getBgColor())));
		viewItem.showTime.setTextColor(ResourceParser.getNoteGridContentColor(Integer
				.parseInt(note.getBgColor())));
	}
	
	@Override  
	public int getItemPosition(Object object) {  
	    return POSITION_NONE;  
	}  

	private IOnDownListener onDownListener;
    public void setOnDownListener(IOnDownListener onDownListener) {  
        this.onDownListener = onDownListener;  
    }  
    
    public interface IOnDownListener {
        public void onDown(Note note);  
    }    
    
	public final class ViewItem {
		private TextView showTime;
		private TextView showContext;
		private RelativeLayout noteTabRelView;
		private RelativeLayout contentLinearView;
		private LinearLayout noteBottomView;
		private RelativeLayout labelAddressView;
		private TextView addressTextView;
		private ImageView addressBtnView;
		private LinearLayout addressContextView;
		private View labelDivisionView;
		private RelativeLayout labelAlarmView;
		private LinearLayout alarmContextView;
		private TextView alarmTextView;
		private ImageView alarmBtnView;
		private ImageView createNewNoteBtn;
	}

	private void initLookNoteScreen(ViewItem viewItem,Note note) {
		try {
		    if (note.getBgColor() != null) {
		        viewItem.noteTabRelView.setBackgroundResource(NoteBgResources
		                .getNoteBgTopWhite(Integer.parseInt(note
		                        .getBgColor())));
		        viewItem.contentLinearView.setBackgroundResource(NoteBgResources
		                .getNoteBgResourceWhite(Integer.parseInt(note
		                        .getBgColor())));
		        viewItem.noteBottomView.setBackgroundResource(NoteBgResources
		                .getNoteBgBottomResourceWhite(Integer.parseInt(note
		                        .getBgColor())));
		    } else {
		        viewItem.noteTabRelView.setBackgroundResource(NoteBgResources
		                .getNoteBgTopWhite(defaultColor));
		        viewItem.contentLinearView.setBackgroundResource(NoteBgResources
		                .getNoteBgResourceWhite(defaultColor));
		        viewItem.noteBottomView.setBackgroundResource(NoteBgResources
		                .getNoteBgBottomResourceWhite(defaultColor));
		    }
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("NoteActivity---initNoteScreen---" + e);
		}
		// mHeadViewPanel.setBackgroundResource(NoteBgResources
		// .getNoteTitleBgResource(Integer.parseInt(note.getBgColor())));

	}

	private void hideLookAlarm(ViewItem viewItem) {
		if (viewItem.labelAddressView.getVisibility() == View.GONE) {
			viewItem.labelAlarmView.setVisibility(View.INVISIBLE);
		} else {
			viewItem.labelAlarmView.setVisibility(View.GONE);
		}
	}
	
	
	
}
