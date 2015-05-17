package com.mridang.reittio;

import java.util.ArrayList;

import org.acra.ACRA;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mridang.widgets.utils.GzippedClient;

/**
 * This class is an asynchronous task that searches for stops
 */
public class StopFinder extends AsyncTask<String, Integer, JSONArray> {

	/** The instance of the calling class */
	private WidgetSettings objActivity = null;

	/**
	 * Initializes this task
	 * 
	 * @param objActivity The instance of the settings activity
	 */
	public StopFinder(WidgetSettings objActivity) {

		this.objActivity = objActivity;

	}

	/*
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected JSONArray doInBackground(String... strStop) {

		final DefaultHttpClient dhcClient = GzippedClient.createClient();

		try {

			Log.d("SlideFactory", "Searching for stops matching " + strStop[0]);
			final HttpGet getResults = new HttpGet("http://api.reittiopas.fi/hsl/prod/?request=stop&user=mridang&pass=as23a6350&code=" + strStop[0].trim());
			final HttpResponse resResults = dhcClient.execute(getResults);

			final Integer intResults = resResults.getStatusLine().getStatusCode();
			if (intResults != HttpStatus.SC_OK) {
				throw new HttpResponseException(intResults, "Server responded with code " + intResults);
			}

			Log.d("StopFinder", "Fetched the search results");
			final String strResults = EntityUtils.toString(resResults.getEntity(), "UTF-8");
			resResults.getEntity().consumeContent();
			return new JSONArray(strResults);

		} catch (final Exception e) {
			Log.w("StopFinder", "Error fetching and parsing page", e);
		}

		return null;

	}

	/*
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(JSONArray jsoResponse) {

		objActivity.hideProgress();

		if (jsoResponse == null) {

			Toast.makeText(objActivity, R.string.api_error, Toast.LENGTH_LONG).show();

		} else {

			try {

				if (jsoResponse.length() == 0) {

					Toast.makeText(objActivity, R.string.no_results, Toast.LENGTH_LONG).show();
					return;

				}

				final ArrayList<String> lstNames = new ArrayList<String>();
				final ArrayList<String> lstIds = new ArrayList<String>();
				final ArrayList<String> lstStops = new ArrayList<String>();
				final ArrayList<String> lstNumbers = new ArrayList<String>();

				for (Integer intId = 0; intId < jsoResponse.length(); intId++) {

					String strName = "";
					strName = jsoResponse.getJSONObject(intId).getString("name_fi");
					lstStops.add(strName);

					String strNumber = "";

					strNumber = jsoResponse.getJSONObject(intId).getString("code_short");
					lstNames.add(strName + " (" + strNumber + ")");
					lstNumbers.add(strNumber);

					String strId = "";
					strId = jsoResponse.getJSONObject(intId).getString("code");
					lstIds.add(strId);

					Log.v("StopFinder", strName + " (" + strNumber + ")" );

				}

				final AlertDialog.Builder builder = new AlertDialog.Builder(objActivity);
				builder.setTitle(R.string.select_stop);
				builder.setItems(lstNames.toArray(new CharSequence[lstNames.size()]), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int intItem) {

						objActivity.addStop(lstIds.get(intItem), lstStops.get(intItem), lstNumbers.get(intItem));
						objActivity.addMarker(lstIds.get(intItem), lstStops.get(intItem), lstNumbers.get(intItem));

						dialog.dismiss();

					}

				});
				final AlertDialog alert = builder.create();
				alert.show();

				Log.d("StopFinder", String.format("Found %d stops", lstStops.size()));

			} catch (final Exception e) {
				Log.e("StopFinder", "Error fetching stops", e);
				ACRA.getErrorReporter().handleSilentException(e);
			}

		}

	}

	/*
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {

		objActivity.showProgress();

	}

}