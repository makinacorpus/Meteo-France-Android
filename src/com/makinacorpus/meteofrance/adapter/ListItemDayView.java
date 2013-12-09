package com.makinacorpus.meteofrance.adapter;
import java.util.ArrayList;

import com.makinacorpus.meteofrance.ui.TextDayView;



public class ListItemDayView {
	
	private ArrayList<TextDayView> daysView = new ArrayList<TextDayView>();
	
	public void addView(TextDayView day) {
		daysView.add(day);
	}
	
	public void removeView(TextDayView day) {
		daysView.remove(day);
	}
	
	public void setSelected(TextDayView day) {
		for (TextDayView dayView : daysView)  {
			boolean value = day.getTag().equals(dayView.getTag());
			dayView.changeColor(value);
		}
	}
	
	
	public int getCount() {
		return daysView.size();
	}
}
