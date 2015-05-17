package com.mridang.reittio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.preference.PreferenceCategory;

/**
 * This class is a custom long-pressable preference for a stop
 */
public class StopPreference extends LongPressPreference implements WidgetSettings.OnLongPressListener {

	/** This is the category that contains the preference */
	private PreferenceCategory pctCategory;
	/** The activity in which this preference is placed */
	private final WidgetSettings widSettings;

	/*
	 * @see android.preference.Preference#Preference(Preference)
	 */
	public StopPreference(WidgetSettings widSettings) {

		super(widSettings);
		this.widSettings = widSettings;

	}

	/**
	 * This method returns the category containing this preference
	 * 
	 * @returns The category containing this preference.
	 */
	public PreferenceCategory getCategory() {

		return pctCategory;

	}

	/*
	 * @see com.example.mridang.reittio.CustomPreferenceActivity.OnLongPressListener #onLongPress(android.preference.Preference)
	 */
	@Override
	public boolean onLongPress(final WidgetSettings cpaPreferences, final Preference prePreference) {

		final AlertDialog.Builder adbBuilder = new AlertDialog.Builder(getContext());
		adbBuilder.setTitle(prePreference.getTitle());

		adbBuilder.setPositiveButton(getContext().getString(R.string.ok_button), new OnClickListener() {

			@Override
			public void onClick(DialogInterface difInterface, int intButton) {

				widSettings.delStop(prePreference.getKey());
				((StopPreference) prePreference).getCategory().removePreference(prePreference);

			}

		});

		adbBuilder.setNegativeButton(getContext().getString(R.string.cancel_button), new OnClickListener() {

			@Override
			public void onClick(DialogInterface difInterface, int intButton) {

				difInterface.dismiss();

			}

		});

		adbBuilder.setMessage(getContext().getString(R.string.remove_asktext));
		adbBuilder.show();
		return true;

	}

	/**
	 * This method sets the category containing this preference
	 * 
	 * @param pctCategory The category containing this preference.
	 */
	public void setCategory(PreferenceCategory pctCategory) {

		this.pctCategory = pctCategory;

	}

}