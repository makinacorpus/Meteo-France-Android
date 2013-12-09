package com.makinacorpus.meteofrance.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.makinacorpus.meteofrance.listener.ITextViewListener;
import com.makinacorpus.meteofrance.ui.TextDayView;
import com.makinacorpus.meteofrance.ui.TextTimeView;

//Nothing special about this adapter, just throwing up colored views for demo
public class MyPagerAdapterTime extends PagerAdapter {
	
	private TextTimeView mCurrentView;
	public ListItemTimeView timesViewPager;
	private Context mContext;
	private ITextViewListener mListener;
	

    public MyPagerAdapterTime(Context context, ITextViewListener listener) {
		super();
		mContext = context;
		mListener = listener;
		timesViewPager = new ListItemTimeView();
	}

	@Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextTimeView view = new TextTimeView(mContext);
        //view.setText("Item "+position);
        view.setGravity(Gravity.CENTER);
        view.setListener(mListener, position);
        
        switch(position) {
        case 1:
        	view.setText("Afternoon", "12 - 17");
        	break;
        case 2:
        	view.setText("Evening", "18 - 22");
        	break;
        default:
        	view.setText("Morning", "8 - 11");
        	break;
        }
        //view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50));
        timesViewPager.addView(view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
        timesViewPager.removeView((TextDayView) object);
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
        mCurrentView = (TextTimeView) object;
        timesViewPager.setSelected(mCurrentView);
	}
}
