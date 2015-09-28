/*
Useful commands:
	Connect to Emulator:
		adb -d forward tcp:5601 tcp:5601

	Connect to Moto360:
		Good Link: http://melix.github.io/blog/2014/10/android-moto360.html
		adb forward tcp:4444 localabstract:/adb-hub;
		adb connect localhost:4444
*/
package com.appsolutely.placeface;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.wearable.watchface.WatchFaceStyle;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.text.format.DateUtils;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.ustwo.clockwise.WatchFace;
import com.ustwo.clockwise.WatchFaceTime;
import com.ustwo.clockwise.WatchMode;
import com.ustwo.clockwise.WatchShape;
import com.ustwo.clockwise.data.calendar.CalendarEvent;
import com.ustwo.clockwise.data.calendar.CalendarWatchFaceHelper;
import com.ustwo.clockwise.data.calendar.OnCalendarDataChangedListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PlaceFaceWatchFaceService extends WatchFace
{
	//You're it!
	private static final String TAG = "PlaceFaceService";

	//For Time
	private final Date mDate = new Date();
	private final SimpleDateFormat mTimeFormat12 = new SimpleDateFormat("h:mm");
	private final SimpleDateFormat mTimeFormat24 = new SimpleDateFormat("HH:mm");

	//For Paint
	private Paint mTimePaint = new Paint();
	private Paint mEventNamePaint = new Paint();
	private Paint mEventLocationPaint = new Paint();
	private Paint mEventCountDownPaint = new Paint();

	//TextBlocks
	private CanvasTextBlock mEventNameBlock;

	//For Calendar Events
	private List<CalendarEvent> mCalendarEvents;
	private CalendarWatchFaceHelper mCalendarHelper;
	private OnCalendarDataChangedListener mCalendarListener;

	//For background
	private int mBackgroundColor = Color.BLACK;
	private float[] mHSV = new float[3];

	@Override
	public void onCreate()
	{
		super.onCreate();

		//Make sure we can get calendar data and listen for changes
		mCalendarHelper = new CalendarWatchFaceHelper(1000 * 60 * 60 * 24);
		mCalendarListener = new OnCalendarDataChangedListener()
		{
			public void onCalendarDataChanged(List<CalendarEvent> events)
			{
				CalendarEvent event;
				for (int i = 0; i < events.size(); i++)
				{
					event = events.get(i);
					Log.d(TAG, i + " " + event.getTitle() + " @ " + event.getStart());
					Log.d(TAG, i + " " + event.getTitle() + " @ " + event.getLocation());
					Log.d(TAG, i + " " + event.getTitle() + " @ " + event.getDisplayColor());
					Log.d(TAG, i + " " + event.getTitle() + " @ " + event.isAllDay());
				}
				mCalendarEvents = events;
			}
		};
		mCalendarHelper.onCreate(getApplicationContext(), mCalendarListener);

	}

	@Override
	protected WatchFaceStyle getWatchFaceStyle()
	{
		return new WatchFaceStyle.Builder(this)
				.setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
				.setShowSystemUiTime(false)
				.setHotwordIndicatorGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL)
				.setStatusBarGravity(Gravity.END | Gravity.TOP)
				.build();
	}

	@Override
	protected long getInteractiveModeUpdateRate()
	{
		return DateUtils.SECOND_IN_MILLIS;
		//return 33;
	}

	@Override
	protected void onLayout(WatchShape shape, Rect screenBounds, WindowInsets screenInsets)
	{
		super.onLayout(shape, screenBounds, screenInsets);
		Log.d(TAG, "onLayout():");
		createDrawingPaints();
	}

	@Override
	protected void onWatchModeChanged(WatchMode watchMode)
	{
		//Log.d(TAG, "onWatchModeChanged():");

		switch (watchMode)
		{
			case INTERACTIVE:
				//timePaint.setAntiAlias(true);
				//timePaint.setStyle(Paint.Style.FILL);

				WindowInsets wi = getWindowInsets();
				int insetBottom = wi.getStableInsetBottom();
				Log.d(TAG, "Is there a chin? insetBottom =  " + insetBottom);


				break;

			case AMBIENT:
				mBackgroundColor = Color.BLACK;
				//timePaint.setStyle(Paint.Style.FILL);
				break;

			case LOW_BIT:
				mBackgroundColor = Color.BLACK;
				//timePaint.setStyle(Paint.Style.FILL);
				//timePaint.setAntiAlias(false);
				break;

			case BURN_IN:
			case LOW_BIT_BURN_IN:
				mBackgroundColor = Color.BLACK;
				//timePaint.setStyle(Paint.Style.STROKE);
				//timePaint.setStrokeWidth(1.0f);
				//timePaint.setAntiAlias(true);
				break;
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		//Log.d(TAG, "onDraw():");
		mBackgroundColor = Color.BLACK;
		if(getCurrentWatchMode() == WatchMode.INTERACTIVE)
		{
			WatchFaceTime wft = getTime();
			mHSV[0] = 360f * wft.percentage12;
			mHSV[1] = 1f;
			mHSV[2] = 1f;
			mBackgroundColor = Color.HSVToColor(mHSV);
		}
		canvas.drawColor(mBackgroundColor);

		//Time
		String time = is24HourFormat() ? mTimeFormat24.format(mDate) : mTimeFormat12.format(mDate);
		if(time != null)
			canvas.drawText(time, getWidth() / 2 - measureWidth(mTimePaint, time)/2, (((float)getHeight()) * 3f) / 12f, mTimePaint);

		CalendarEvent event = CalendarWatchFaceHelper.getNextEvent(mCalendarEvents);
		if(event != null)
		{
			//Event Name
			String name = CalendarWatchFaceHelper.getNextEvent(mCalendarEvents).getTitle();
			if (name != null)
				canvas.drawText(name, getWidth() / 2 - measureWidth(mEventNamePaint, name) / 2, (((float)getHeight()) * 5f) / 12f, mEventNamePaint);

			//Event Location
			String loc = CalendarWatchFaceHelper.getNextEvent(mCalendarEvents).getLocation();
			if (loc != null)
				canvas.drawText(loc, getWidth() / 2 - measureWidth(mEventLocationPaint, loc) / 2, (((float)getHeight()) * 7f) / 12f, mEventLocationPaint);

			//Event CountDown
			String countDown = CalendarWatchFaceHelper.getNextEvent(mCalendarEvents).getStringDurationUntilStart();
			if (countDown != null &&
					getPeekCardRect().isEmpty())
				canvas.drawText(countDown, getWidth() / 2 - measureWidth(mEventCountDownPaint, countDown) / 2, (((float)getHeight()) * 9f) / 12f, mEventCountDownPaint);
		}

		mEventNameBlock.draw(canvas);


	}

	@Override
	//This happens a lot. I think once every getInteractiveModeUpdateRate()
	protected void onTimeChanged(WatchFaceTime oldTime, WatchFaceTime newTime)
	{
		//Log.d(TAG, "onTimeChanged():");
		super.onTimeChanged(oldTime, newTime);
		mDate.setTime(newTime.toMillis(true));
		//timeText = is24HourFormat() ? timeFormat24.format(date) : timeFormat12.format(date);
	}

	@Override
	protected void onCardPeek(Rect rect)
	{
		super.onCardPeek(rect);
		Log.d(TAG, "onCardPeek(): rect = " + rect.toString());
	}


	private void createDrawingPaints()
	{
		int fontSize = 30;

		mTimePaint.setStyle(Paint.Style.FILL);
		mTimePaint.setColor(Color.WHITE);
		mTimePaint.setAntiAlias(true);
		mTimePaint.setTypeface(Typeface.createFromAsset(getAssets(), "segoeui.ttf"));
		mTimePaint.setTextAlign(Paint.Align.LEFT);
		mTimePaint.setTextSize(52);

		mEventNamePaint.setStyle(Paint.Style.FILL);
		mEventNamePaint.setColor(Color.WHITE);
		mEventNamePaint.setAntiAlias(true);
		mEventNamePaint.setTypeface(Typeface.createFromAsset(getAssets(), "segoeui.ttf"));
		mEventNamePaint.setTextAlign(Paint.Align.LEFT);
		mEventNamePaint.setTextSize(22);

		String longString = "1-2-3-4-5-6-7-8-9-10-11-12-13-14-15-16-17-18-19-20";
		float textY = (((float)getHeight()) * 5f)/12f;
		float textX = getLeftEdgeAt((int)textY);
		int width = getWidthAt((int)textY);
		mEventNameBlock = new CanvasTextBlock(longString, mEventNamePaint, textX, textY, width);


		mEventLocationPaint.setStyle(Paint.Style.FILL);
		mEventLocationPaint.setColor(Color.WHITE);
		mEventLocationPaint.setAntiAlias(true);
		mEventLocationPaint.setTypeface(Typeface.createFromAsset(getAssets(), "segoeui.ttf"));
		mEventLocationPaint.setTextAlign(Paint.Align.LEFT);
		mEventLocationPaint.setTextSize(22);

		mEventCountDownPaint.setStyle(Paint.Style.FILL);
		mEventCountDownPaint.setColor(Color.WHITE);
		mEventCountDownPaint.setAntiAlias(true);
		mEventCountDownPaint.setTypeface(Typeface.createFromAsset(getAssets(), "segoeui.ttf"));
		mEventCountDownPaint.setTextAlign(Paint.Align.LEFT);
		mEventCountDownPaint.setTextSize(52);
	}

	public float measureHeight(Paint paint, String text)
	{
		Rect result = new Rect();
		paint.getTextBounds(text, 0, text.length(), result);
		return (float)result.height();
	}

	public float measureWidth(Paint paint, String text)
	{
		return paint.measureText(text);
	}



	/*
	void drawRainbow(Canvas canvas)
	{
		float[] hsv = new float[3];
		Paint p = new Paint();

		for(int i = 0; i < getHeight(); i++)
		{
			hsv[0] = ((float)i)/((float)getHeight()) * 360f;
			hsv[1] = 1f;
			hsv[2] = 1f;

			p.setStyle(Paint.Style.FILL);
			p.setColor(Color.HSVToColor(hsv));

			canvas.drawLine(0f, (float)i, (float)getWidth(), (float)i, p);
		}
	}
	*/


	//Prints out all calendar events for the next 24 hours to the logcat
	/*public void printCalendarEvents()
	{
		Log.d(TAG, "Starting calendar event stream... ");

		CalendarWatchFaceHelper calHelp = new CalendarWatchFaceHelper(1000 * 60 * 60 * 24);
		OnCalendarDataChangedListener listener = new OnCalendarDataChangedListener()
		{
			public void onCalendarDataChanged(List<CalendarEvent> events)
			{
				CalendarEvent event;
				//for(CalendarEvent event: events)
				for (int i = 0; i < events.size(); i++)
				{
					event = events.get(i);
					Log.d(TAG, i + " " + event.getTitle() + " @ " + event.getStart());
					Log.d(TAG, i + " " + event.getTitle() + " @ " + event.getLocation());
					Log.d(TAG, i + " " + event.getTitle() + " @ " + event.getDisplayColor());
					Log.d(TAG, i + " " + event.getTitle() + " @ " + event.isAllDay());
				}
				mCalendarEvents = events;
			}
		};
		calHelp.onCreate(getApplicationContext(), listener);

		Log.d(TAG, "Done with stream!");
	}
	*/
}