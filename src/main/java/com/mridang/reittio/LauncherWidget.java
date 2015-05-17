package com.mridang.reittio;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.acra.ACRA;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import com.mridang.widgets.BaseWidget;
import com.mridang.widgets.SavedSettings;
import com.mridang.widgets.WidgetHelpers;
import com.mridang.widgets.utils.GzippedClient;

/**
 * This class is the provider for the widget and updates the data
 */
public class LauncherWidget extends BaseWidget {

	/*
	 * @see com.mridang.widgets.BaseWidget#fetchContent(android.content.Context, java.lang.Integer,
	 * com.mridang.widgets.SavedSettings)
	 */
	@Override
	public String fetchContent(Context ctxContext, Integer intInstance, SavedSettings objSettings)
			throws Exception {

		final DefaultHttpClient dhcClient = GzippedClient.createClient();
		JSONArray jsoData = new JSONArray();

		for (final String strId : objSettings.spePreferences.getStringSet("monitored_stops", new HashSet<String>())) {

			Log.d("SlideFactory", "Fetching the stop timings for stop #" + strId);
			final HttpGet getStop = new HttpGet("http://api.reittiopas.fi/hsl/prod/?request=stop&user=mridang&pass=as23a6350&code=" + strId);
			final HttpResponse resStop = dhcClient.execute(getStop);

			final Integer intStop = resStop.getStatusLine().getStatusCode();
			if (intStop != HttpStatus.SC_OK) {
				throw new HttpResponseException(intStop, "Server responded with code " + intStop);
			}

			Log.d("LauncherWidget", "Fetched the stop timings");
			final String strLines = EntityUtils.toString(resStop.getEntity(), "UTF-8");
			final JSONObject jsoStop = new JSONArray(strLines).getJSONObject(0);
			jsoStop.put("routes", new JSONObject());
			final JSONArray jsoLines = jsoStop.getJSONArray("lines");
			final ExecutorService exeService = Executors.newFixedThreadPool(3);

			for (Integer x = 0; x < jsoLines.length(); x++) {

				final String strRoute = jsoLines.getString(x).split("\\:")[0];

				final Runnable runProject = new Runnable() {

					@Override
					public void run() {

						try {

							Log.d("SlideFactory", "Fetching the line information for " + strRoute);
							final DefaultHttpClient dhcLine = GzippedClient.createClient();
							final HttpGet getLine = new HttpGet("http://api.reittiopas.fi/hsl/prod/?request=lines&user=mridang&pass=as23a6350&query=" + URLEncoder.encode(strRoute, "UTF-8"));
							final HttpResponse resLine = dhcLine.execute(getLine);

							final Integer intLine = resLine.getStatusLine().getStatusCode();
							if (intLine != HttpStatus.SC_OK) {
								throw new HttpResponseException(intLine, "Server responded with code " + intLine);
							}

							Log.d("LauncherWidget", "Fetched the line information");
							final String strLine = EntityUtils.toString(resLine.getEntity(), "UTF-8");
							resLine.getEntity().consumeContent();
							jsoStop.getJSONObject("routes").put(strRoute, new JSONArray(strLine).get(0));

						} catch (final Exception e) {
							Log.e("SlideFactory", "Unknown error encountered", e);
							ACRA.getErrorReporter().handleSilentException(e);
						}

					}

				};

				exeService.execute(runProject);

			}

			exeService.shutdown();
			exeService.awaitTermination(60, TimeUnit.SECONDS);
			jsoData.put(jsoData.length(), jsoStop);

		}

		return jsoData.toString(2);

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#getIcon()
	 */
	@Override
	public Integer getIcon() {

		return R.drawable.ic_notification;

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#getKlass()
	 */
	@Override
	protected Class<?> getKlass() {

		return getClass();

	}

	/*
	 * @see com.mridang.BaseWidget#getToken()
	 */
	@Override
	public String getToken() {

		return "a1b2c3d4";

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#updateWidget(android.content.Context, java.lang.Integer,
	 * com.mridang.widgets.SavedSettings, java.lang.String)
	 */
	@Override
	public void updateWidget(Context ctxContext, Integer intInstance, SavedSettings objSettings, String strContent)
			throws Exception {

		final RemoteViews remView = new RemoteViews(ctxContext.getPackageName(), R.layout.widget);
		final Intent ittSlides = new Intent(ctxContext, SlideService.class);
		ittSlides.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, intInstance);
		ittSlides.setData(Uri.fromParts("content", String.valueOf(new Random().nextInt()), null));
		ittSlides.putExtra("data", strContent);

        final PendingIntent pitOptions = WidgetHelpers.getIntent(ctxContext, WidgetSettings.class, intInstance);
		remView.setTextViewText(R.id.last_update, DateFormat.format("kk:mm", new Date()));
		remView.setOnClickPendingIntent(R.id.settings_button, pitOptions);
		remView.setRemoteAdapter(R.id.widget_cards, ittSlides);

		AppWidgetManager.getInstance(ctxContext).updateAppWidget(intInstance, remView);

	}

}