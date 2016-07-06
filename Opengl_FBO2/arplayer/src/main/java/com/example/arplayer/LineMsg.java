package com.example.arplayer;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LineMsg extends LinearLayout  {
	private TextView mText;
	private TextView mSize;
	public LineMsg(Context context,String Name,long size) {
		super(context);
		String SizeStr="";
		
		mText = new TextView(context);
		mText.setText(Name);
		mText.setTextSize(12);
		mText.setGravity(Gravity.CENTER_VERTICAL);
		mText.setPadding(5, 0, 0, 0);
		mText.setMaxLines(1);
		mText.setTextColor(0xff000000);
		addView(mText, new LayoutParams(-2,54));
		
		if(size>1024*1024)
		{
			SizeStr=""+size/1024/1024+"MB";
		}else if(size>1024)
		{
			SizeStr=""+size/1024+"KB";
		}else 
		{
			SizeStr=""+size+"B";
		}
			
		mSize=new TextView(context);
		mSize.setText(SizeStr);
		mSize.setTextSize(9);
		mSize.setGravity(Gravity.CENTER_VERTICAL);
		mSize.setPadding(20, 0, 0, 0);
		mSize.setMaxLines(1);
		mSize.setTextColor(0xff000000);
		addView(mSize, new LayoutParams(-2,54));
		
		
	}
	
	public void setText(String Name)
	{
		mText.setText(Name);
	}
	
	public void setSize(long size)
	{
		String SizeStr="";
		if(size>1024*1024)
		{
			SizeStr=""+size/1024/1024+"MB";
		}else if(size>1024)
		{
			SizeStr=""+size/1024+"KB";
		}else 
		{
			SizeStr=""+size+"B";
		}
		mSize.setText(SizeStr);
	}
}
