package com.mridang.reittio;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.mridang.widgets.WidgetHelpers;

/**
 * This class is used to provide the stacks for the widget
 */
public class SlideFactory implements RemoteViewsFactory {

	/* This is the array containing the the list of stops */
	private JSONArray jsoTimings = new JSONArray();
	/* The context of the calling activity */
	private final Context ctxContext;
	/* The view that is used for each of the slides */
	private RemoteViews remView;

	/*
	 * 
	 */
	public SlideFactory(Context ctxContext, Intent ittIntent) {

		remView = new RemoteViews(ctxContext.getPackageName(), R.layout.slide);
		this.ctxContext = ctxContext;
		try {
			jsoTimings = new JSONArray(ittIntent.getStringExtra("data"));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getCount()
	 */
	@Override
	public int getCount() {

		return jsoTimings != null ? jsoTimings.length() : 0;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getItemId(int)
	 */
	@Override
	public long getItemId(int intPosition) {

		return intPosition;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getLoadingView()
	 */
	@Override
	public RemoteViews getLoadingView() {

		return new RemoteViews(ctxContext.getPackageName(), R.layout.loading);

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getViewAt(int)
	 */
	@Override
	public RemoteViews getViewAt(int intPosition) {

		try {

			final JSONObject jsoStop = jsoTimings.getJSONObject(intPosition);

			if (jsoStop.has("departures")) {

				JSONArray jsoDepartures = null; 
				if (!jsoStop.get("departures").toString().equals("null")) {
					jsoDepartures = jsoTimings.getJSONObject(intPosition).getJSONArray("departures");
				} else {
					jsoDepartures = new JSONArray();
				}

				remView.setTextViewText(R.id.stop_name, jsoTimings.getJSONObject(intPosition).getString("name_fi"));
				remView.setTextViewText(R.id.stop_number, jsoTimings.getJSONObject(intPosition).getString("code_short"));

				for (Integer intId = 0; intId < jsoDepartures.length(); intId++) {

					final RemoteViews remViewlet = new RemoteViews(ctxContext.getPackageName(), R.layout.arrival);

					final String strRide = jsoDepartures.getJSONObject(intId).getString("code");

					final String strCode = jsoStop.getJSONObject("routes").getJSONObject(strRide).getString("code_short");
					remViewlet.setTextViewText(R.id.ride_number, strCode);

					String strTime = jsoDepartures.getJSONObject(intId).getString("time");
					strTime = strTime.substring(0, strTime.length() == 3 ? 1 : 2) + ":" + strTime.substring(strTime.length() == 3 ? 1 : 2, strTime.length());
					remViewlet.setTextViewText(R.id.arrival_time, strTime);

					final String strName = jsoStop.getJSONObject("routes").getJSONObject(strRide).getString("line_end");
					remViewlet.setTextViewText(R.id.ride_name, strName);

					remView.addView(R.id.stop_arrivals, remViewlet);

				}

				remView.setOnClickPendingIntent(R.id.reitti_logo, WidgetHelpers.getIntent(ctxContext, jsoTimings.getJSONObject(intPosition).getString("timetable_link")));

				return remView;

			}

		} catch (final JSONException e) {
			Log.e("SlideFactory", "Unknown error encountered", e);
			ACRA.getErrorReporter().handleSilentException(e);
		} catch (final Exception e) {
			Log.e("SlideFactory", "Unknown error encountered", e);
			ACRA.getErrorReporter().handleSilentException(e);
		}

		return null;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {

		return 1;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {

		return true;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#onCreate()
	 */
	@Override
	public void onCreate() {

		return;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#onDataSetChanged()
	 */
	@Override
	public void onDataSetChanged() {

		return;

	}

	/*
	 * @see android.widget.RemoteViewsService.RemoteViewsFactory#onDestroy()
	 */
	@Override
	public void onDestroy() {

		jsoTimings = null;

	}

}