package com.appsolutely.placeface;

import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;

/**
 * Created by yashar on 3/5/15.
 */
public class DynamicLayoutEx extends DynamicLayout
{
	int mMaxLines;

	public DynamicLayoutEx(CharSequence base, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, int maxlines)
	{
		super(base, paint, width, align, spacingmult, spacingadd, includepad);
		mMaxLines = maxlines;
	}


	@Override
	public int getLineCount()
	{
		if (super.getLineCount() - 1 > mMaxLines)
		{
			return mMaxLines;
		}
		return super.getLineCount() - 1;
	}

	@Override
	public int getEllipsisCount(int line)
	{
		if (line == mMaxLines - 1 && super.getLineCount() - 2 > line)
		{
			return 1;
		}
		return 0;
	}

	@Override
	public int getEllipsisStart(int line)
	{
		if (line == mMaxLines - 1 && super.getLineCount() - 2 > line)
		{
			return getLineEnd(line) - getLineStart(line) - 1;
		}
		return 0;
	}
}
