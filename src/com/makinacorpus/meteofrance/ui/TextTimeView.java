package com.makinacorpus.meteofrance.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makinacorpus.meteofrance.R;
import com.makinacorpus.meteofrance.listener.ITextViewListener;

public class TextTimeView extends LinearLayout implements View.OnClickListener {
	
	private ITextViewListener mListener;
	
	private TextView timeTextTitle;
	private TextView timeTextSummary;
	private View mView;
	private View viewSelected;
	private View viewNotSelected;
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
    	viewNotSelected = (View) mView.findViewById(R.id.viewNotSlectedMarker);
		viewSelected = (View) mView.findViewById(R.id.viewSlectedMarker);
	}
	
	public void setListener(ITextViewListener listener, int pos) {
		mListener = listener;
		position = pos;
		
		setTag(new String("pos" + pos));
	}
	
	public void changeColor(boolean isselected) {
		if (isselected) {
			// Change Color
			timeTextTitle.setTextSize(16.0f);
			timeTextTitle.setTextColor(getResources().getColor(android.R.color.white));
			timeTextSummary.setTextSize(14.0f);
			timeTextSummary.setTextColor(getResources().getColor(android.R.color.white));
			viewSelected.setVisibility(View.VISIBLE);
			viewNotSelected.setVisibility(View.GONE);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			timeTextTitle.setGravity(Gravity.CENTER);
			params.setMargins(0, 10, 0, 0);
			timeTextTitle.setLayoutParams(params);
			
			
			
		} else {
			timeTextTitle.setTextColor(getResources().getColor(R.color.bleu_matin));
			timeTextTitle.setTextSize(14.0f);
			timeTextSummary.setTextSize(12.0f);
			timeTextSummary.setTextColor(getResources().getColor(R.color.bleu_matin));
			
			viewSelected.setVisibility(View.GONE);
			viewNotSelected.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			timeTextTitle.setGravity(Gravity.CENTER);
			params.setMargins(0, 0, 0, 0);
			timeTextTitle.setLayoutParams(params);
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
		timeTextSummary.setText(textSummary);
	}
	
}
