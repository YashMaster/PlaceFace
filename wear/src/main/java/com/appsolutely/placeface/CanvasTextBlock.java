package com.appsolutely.placeface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;

/**
 * Created by yashar on 3/5/15.
 */
public class CanvasTextBlock
{
	private SpannableStringBuilder mText;
	private DynamicLayout mLayout;
	private TextPaint mPaint;
	private int mWidth;
	private float mX, mY;

	public CanvasTextBlock(String text, Paint paint, float x, float y, int width)
	{
		mText = new SpannableStringBuilder(text);
		mPaint = new TextPaint(paint);
		mWidth = width;
		mX = x;
		mY = y;

		mLayout = new DynamicLayout(mText, mText, mPaint, mWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, true);
	}

	//Sets the text, only if the text has changed.
	public void setText(String text)
	{
		//Don't do anything if the text is unchanged.
		if(text.equals(mText.toString()) == false)
			return;

		//Create another layout.
		mText.clear();
		mText.append(text);
		//mText = new SpannableStringBuilder(text);
		//mLayout = new DynamicLayout(mText, mText, mPaint, mWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, true);
	}

	//Draws the damn thing to the canvas...
	public void draw(Canvas canvas)
	{
		canvas.save();
		canvas.translate(mX, mY);
		mLayout.draw(canvas);
		canvas.restore();
	}

	@Override
	public String toString()
	{
		return "(x,y)w = (" + mX + ", " + mY + ") " + mWidth;
	}
}
