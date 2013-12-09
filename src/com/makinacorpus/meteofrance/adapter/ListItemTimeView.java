package com.makinacorpus.meteofrance.adapter;
import java.util.ArrayList;

import com.makinacorpus.meteofrance.ui.TextDayView;
import com.makinacorpus.meteofrance.ui.TextTimeView;



public class ListItemTimeView {
	
	private ArrayList<TextTimeView> timesView = new ArrayList<TextTimeView>();
	
	public void addView(TextTimeView day) {
		timesView.add(day);
	}
	
	public void removeView(TextDayView day) {
		timesView.remove(day);
	}
	
	public void setSelected(TextTimeView time) {
		for (TextTimeView timeView : timesView)  {
			boolean value = timeView.getTag().equals(time.getTag());
			timeView.changeColor(value);
		}
	}
	
	public int getCount() {
		return timesView.size();
	}
}
