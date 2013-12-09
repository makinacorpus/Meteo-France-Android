package com.makinacorpus.meteofrance.ui;

import com.makinacorpus.meteofrance.R;
import com.makinacorpus.meteofrance.listener.ITextViewListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;

public class TextDayView extends LinearLayout implements View.OnClickListener {
	
	private ITextViewListener mListener;
	
	private TextView dayText;
	private int position;
	
	public TextDayView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
		
	}

	public TextDayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public TextDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void init(Context context) {
		mListener = null;
		this.setOnClickListener(this);
		LayoutInflater  mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.text_day, this, true);
        dayText = (TextView) view.findViewById(R.id.text_day);
	}
	
	public void setListener(ITextViewListener listener, int pos) {
		mListener = listener;
		position = pos;
		
		setTag(new String("pos" + pos));
	}
	
	public void changeColor(boolean isselected) {
		if (isselected) {
			// Change Color
			dayText.setTextSize(15.0f);
			dayText.setTextColor(getResources().getColor(android.R.color.black));
			dayText.setBackgroundResource(R.drawable.backrgound_selected);
		} else {
			dayText.setTextColor(getResources().getColor(android.R.color.darker_gray));
			dayText.setTextSize(12.0f);
			dayText.setBackgroundResource(R.drawable.backrgound_noselected);
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
	
	public void setText(String text) {
		dayText.setText(text);
	}
	
	
}
