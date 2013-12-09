package com.makinacorpus.meteofrance.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.makinacorpus.meteofrance.listener.ITextViewListener;
import com.makinacorpus.meteofrance.ui.TextDayView;

//Nothing special about this adapter, just throwing up colored views for demo
public class MyPagerAdapterDay extends PagerAdapter {
	
	private TextDayView mCurrentView;
	public ListItemDayView daysViewPager;
	private Context mContext;
	private ITextViewListener mListener;
	

    public MyPagerAdapterDay(Context context, ITextViewListener listener) {
		super();
		mContext = context;
		mListener = listener;
		daysViewPager = new ListItemDayView();
	}

	@Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextDayView view = new TextDayView(mContext);
        //view.setText("Item "+position);
        view.setGravity(Gravity.CENTER);
        view.setListener(mListener, position);
        
        switch(position) {
        case 1:
        	view.setText("Tomorrow");
        	break;
        case 2:
        	view.setText("Next ...");
        	break;
        default:
        	view.setText("Today");
        	break;
        }
        //view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50));
        daysViewPager.addView(view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
        daysViewPager.removeView((TextDayView) object);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
    
    @Override
    public void setPrimaryItem(ViewGroup container, int pos, Object object) {
        mCurrentView = (TextDayView) object;
        daysViewPager.setSelected(mCurrentView);
	}
}
