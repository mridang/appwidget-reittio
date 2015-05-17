package com.mridang.reittio;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mridang.widgets.SettingsActivity;

/**
 * This class is the activity which contains the preferences
 */
public class WidgetSettings extends SettingsActivity {

	/**
	 * This is a custom interface that long-pressable widgets need to implement
	 */
	public interface OnLongPressListener {

		/**
		 * This method handled the long press events
		 */
		public boolean onLongPress(WidgetSettings cpaPreferences, Preference prePreference);

	}
	/** The instance of the pdgProgress dialog */
	ProgressDialog pdgProgress;

	/** This identifier of the widget instance */
	Integer intInstance;

	/**
	 * Method to add a stop the preference category containing the stops
	 * 
	 * @param strId The identifier of the stop
	 * @param strName The name of the stop
	 * @param strNumber The number of the stop
	 */
	@SuppressWarnings("deprecation")
	public void addMarker(String strId, String strName, String strNumber) {

		final PreferenceCategory pctCategory = (PreferenceCategory) findPreference("monitored_stops");
		final StopPreference objPreference = new StopPreference(this);
		objPreference.setKey(strId);
		objPreference.setTitle(strName);
		objPreference.setSummary(strNumber);
		objPreference.setCategory(pctCategory);
		pctCategory.addPreference(objPreference);

	}

	/**
	 * Method to save a stop the preferences and the storage
	 * 
	 * @param strId The identifier of the stop
	 * @param strName The name of the stop
	 * @param strNumber The number of the stop
	 */
	public void addStop(String strId, String strName, String strNumber) {

		final StopStorage objStorage = new StopStorage(getApplicationContext(), getInstance(), getSettings());
		objStorage.addStop(strId, strName, strNumber);

	}

	/**
	 * Method to remove a stop from the preferences
	 * 
	 * @param strId The identifier of the stop
	 */
	public void delStop(String strId) {

		final StopStorage objStorage = new StopStorage(getApplicationContext(), getInstance(), getSettings());
		objStorage.delStop(strId);

	}

	/*
	 * @see com.mridang.widgets.SettingsActivity#getIcon()
	 */
	@Override
	public Drawable getIcon() {

		return getApplicationContext().getResources().getDrawable(R.drawable.ic_launcher);

	}

	/*
	 * @see com.mridang.widgets.BaseWidget#getKlass()
	 */
	@Override
	protected Class<?> getKlass() {

		return LauncherWidget.class;

	}

	/*
	 * @see com.mridang.widgets.SettingsActivity#getPreferences()
	 */
	@Override
	public Integer getPreferences() {

		return R.xml.preferences;

	}

	/**
	 * Method to hide the searching-for stops progress dialog
	 */
	public void hideProgress() {

		pdgProgress.dismiss();

	}

	/*
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle bndBundle) {

		super.onCreate(bndBundle);

		final ListView listView = getListView();
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> lvwListview, View vewView, int intPosition, long lngId) {

				final ListView lvwPreferences = (ListView) lvwListview;
				final ListAdapter ladPreferences = lvwPreferences.getAdapter();
				final Object objObject = ladPreferences.getItem(intPosition);
				if (objObject != null && objObject instanceof OnLongPressListener) {
					final OnLongPressListener vlcListener = (OnLongPressListener) objObject;
					final Preference prePreference = (Preference) objObject;
					return vlcListener.onLongPress(WidgetSettings.this, prePreference);
				}

				return false;

			}

		});

	}

	/*
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu mnuMenu) {

		getMenuInflater().inflate(R.xml.menu, mnuMenu);
		return true;

	}

	/**
	 * Method to handle the add-stop menu-button click to show the search dialog
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem itmSearch) {

		final EditText edtSearch = new EditText(this);
		final InputMethodManager immManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		final AlertDialog.Builder adbDialog = new AlertDialog.Builder(WidgetSettings.this);

		adbDialog.setTitle(R.string.search_stops);
		adbDialog.setMessage(R.string.search_helptext);
		adbDialog.setView(edtSearch);
		adbDialog.setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface difInterface, int intButton) {

				immManager.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
				final StopFinder objFinder = new StopFinder(WidgetSettings.this);
				objFinder.execute(edtSearch.getText().toString());

			}

		}).setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface difInterface, int intButton) {

				difInterface.dismiss();

			}

		});

		final AlertDialog diaDialog = adbDialog.create();
		diaDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		diaDialog.show();

		return true;

	}

	/**
	 * Method that loads the marked stops and adds them to the preference category
	 * 
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle bndBundle) {

		super.onPostCreate(bndBundle);

		final StopStorage objStorage = new StopStorage(getApplicationContext(), getInstance(), getSettings());
		for (final MarkedStop hslStop : objStorage.getStops()) {

			addMarker(hslStop.getId(), hslStop.getName(), hslStop.getNumber());

		}

	}

	/**
	 * Method to show the searching-for stops progress dialog
	 */
	public void showProgress() {

		pdgProgress = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.searching_stops), true);

	}

}