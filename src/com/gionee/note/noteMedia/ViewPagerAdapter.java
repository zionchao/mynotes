package com.gionee.note.noteMedia;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {

	private ArrayList<View> views = new ArrayList<View>();

	public ViewPagerAdapter(ArrayList<View> v) {
		views = v;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
	    if(container instanceof ViewPager){
	        ((ViewPager) container).removeView(views.get(position));
	    }
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public Object instantiateItem(View container, int position) {
	    if(container instanceof ViewPager){
	        ((ViewPager) container).addView(views.get(position));
	    }
		return views.get(position);
	}

}
