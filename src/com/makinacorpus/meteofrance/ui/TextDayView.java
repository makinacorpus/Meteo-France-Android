package com.makinacorpus.meteofrance.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makinacorpus.meteofrance.R;
import com.makinacorpus.meteofrance.listener.ITextViewListener;

public class TextDayView extends LinearLayout implements View.OnClickListener {

	private ITextViewListener mListener;

	private TextView dayText;
	private View viewSelected;
	private View viewNotSelected;
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
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.text_day, this, true);
		dayText = (TextView) view.findViewById(R.id.text_day);
		viewNotSelected = (View) view.findViewById(R.id.viewNotSlectedMarker);
		viewSelected = (View) view.findViewById(R.id.viewSlectedMarker);
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
			dayText.setTextColor(getResources().getColor(R.color.text_blue));
			viewSelected.setVisibility(View.VISIBLE);
			viewNotSelected.setVisibility(View.GONE);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dayText.getLayoutParams().width,
					dayText.getLayoutParams().height);
			params.setMargins(0, 0, 0, 0);
			dayText.setLayoutParams(params);

		} else {
			dayText.setTextColor(getResources().getColor(R.color.text_gris));
			dayText.setTextSize(11.0f);
			viewSelected.setVisibility(View.GONE);
			viewNotSelected.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dayText.getLayoutParams().width,
					dayText.getLayoutParams().height);
			params.setMargins(0, 0, 0, 20);
			dayText.setLayoutParams(params);

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
