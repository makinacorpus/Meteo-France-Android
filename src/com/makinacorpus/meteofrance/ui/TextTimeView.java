package com.makinacorpus.meteofrance.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makinacorpus.meteofrance.R;
import com.makinacorpus.meteofrance.listener.ITextViewListener;

public class TextTimeView extends LinearLayout implements View.OnClickListener {
	
	private ITextViewListener mListener;
	
	private TextView timeTextTitle;
	private TextView timeTextSummary;
	private View mView;
	private int position;

	public TextTimeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
		
	}

	public TextTimeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public TextTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void init(Context context) {
		LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.text_time, this, true);
        
        timeTextTitle = (TextView) mView.findViewById(R.id.text_time_title);
        timeTextSummary = (TextView) mView.findViewById(R.id.text_time_summary);
	}
	
	public void setListener(ITextViewListener listener, int pos) {
		mListener = listener;
		position = pos;
		
		setTag(new String("pos" + pos));
	}
	
	public void changeColor(boolean isselected) {
		if (isselected) {
			// Change Color
			timeTextTitle.setTextSize(12.0f);
			timeTextTitle.setTextColor(getResources().getColor(android.R.color.black));
			timeTextSummary.setTextSize(10.0f);
			timeTextSummary.setTextColor(getResources().getColor(android.R.color.black));
			
			mView.setBackgroundResource(R.drawable.backrgound_selected);
			
		} else {
			timeTextTitle.setTextColor(getResources().getColor(android.R.color.darker_gray));
			timeTextTitle.setTextSize(10.0f);
			timeTextSummary.setTextSize(8.0f);
			timeTextSummary.setTextColor(getResources().getColor(android.R.color.darker_gray));
			
			mView.setBackgroundResource(R.drawable.backrgound_noselected);
			
		}
	}

	@Override
	public void onClick(View v) {
		// Call Listener
		setSelected();
	}
	
	public void setSelected() {
		if (mListener != null) {
			mListener.hasSelectedText(this, position);
		}
	}
	
	public void setText(String text, String textSummary) {
		timeTextTitle.setText(text);
		timeTextSummary.setText(text);
	}
	
}
