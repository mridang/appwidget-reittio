package com.mridang.reittio;

import android.content.Context;
import android.preference.Preference;
import android.view.View;

/**
 * This class is a custom preference type that supports long presses
 */
public class LongPressPreference extends Preference implements View.OnLongClickListener {

	/*
	 * @see android.preference.Preference#Preference(Preference)
	 */
	public LongPressPreference(Context ctxContext) {

		super(ctxContext);

	}

	/*
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	public boolean onLongClick(Preference prePreference) {

		return false;

	}

	/*
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	@Override
	public boolean onLongClick(View v) {

		return false;

	}

}