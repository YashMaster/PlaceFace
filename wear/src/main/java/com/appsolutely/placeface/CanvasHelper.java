package com.appsolutely.placeface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

/**
 * Created by yashar on 3/2/15.
 */
public class CanvasHelper
{
	public static void drawMultiLineText(Canvas canvas, Paint paint, int width, float x, float y, String text)
	{
		TextPaint tp = new TextPaint(paint);
		StaticLayout mTextLayout = new StaticLayout(text, tp, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

		canvas.save();
		canvas.translate(x, y);
		mTextLayout.draw(canvas);
		canvas.restore();
	}
}
