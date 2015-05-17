package com.mridang.reittio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;

import com.mridang.widgets.SavedSettings;

/**
 * This class is a to helps with saving the stop information to the storage.
 */
public class StopStorage {

	/** The set of marked stops saved to disk */
	private Set<MarkedStop> setStops;
	/** The file object of the serialized stops */
	private final File filStops;
	/** The instance of the preference helper */
	private final SavedSettings objSettings;

	/**
	 * Simple constructor that initializes the storage
	 * 
	 * @param ctxContext The instance of the application context
	 * @param integer
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public StopStorage(Context ctxContext, Integer intInstance, SavedSettings objSettings) {

		this.objSettings = objSettings;
		setStops = new HashSet<MarkedStop>();
		filStops = new File(ctxContext.getDir("data", Context.MODE_PRIVATE), "stops.dat");

		if (filStops.exists()) {

			try {

				final ObjectInputStream oisStream = new ObjectInputStream(new FileInputStream(filStops));
				setStops = (Set<MarkedStop>) oisStream.readObject();
				oisStream.close();

			} catch (final Exception e) {
				filStops.delete();
			}

		}

	}

	/**
	 * This method creates and saves a stop to the storage
	 * 
	 * @param strId The identifier of the stop
	 * @param strName The name of the stop
	 * @param strNumber The number of the stop
	 */
	public void addStop(String strId, String strName, String strNumber) {

		final MarkedStop objStop = new MarkedStop(strId, strName, strNumber);
		setStops.add(objStop);
		save();

		Set<String> setIds = new HashSet<String>();
		setIds = objSettings.spePreferences.getStringSet("monitored_stops", setIds);
		setIds.add(strId);
		objSettings.spePreferences.edit().putStringSet("monitored_stops", setIds).commit();

	}

	/**
	 * This method deletes a stop from the storage.
	 * 
	 * @param strId The identifier of the stop
	 */
	public void delStop(String strId) {

		Set<String> setIds = new HashSet<String>();
		setIds = objSettings.spePreferences.getStringSet("monitored_stops", setIds);
		setIds.remove(strId);
		objSettings.spePreferences.edit().putStringSet("monitored_stops", setIds).commit();

		final Iterator<MarkedStop> iteStops = setStops.iterator();
		while (iteStops.hasNext()) {

			final MarkedStop objStop = iteStops.next();
			if (objStop.getId().equalsIgnoreCase(strId)) {
				iteStops.remove();
			}

		}

		save();

	}

	/**
	 * This method fetches the set of stops from the storage.
	 * 
	 * @returns The stop for that identifier from the storage
	 */
	public List<MarkedStop> getStops() {

		Set<String> setIds = new HashSet<String>();
		setIds = objSettings.spePreferences.getStringSet("monitored_stops", setIds);

		final List<MarkedStop> lstStops = new ArrayList<MarkedStop>();
		final Iterator<MarkedStop> iteStops = setStops.iterator();
		while (iteStops.hasNext()) {

			final MarkedStop objStop = iteStops.next();
			for (final String strId : setIds) {

				if (objStop.getId().equalsIgnoreCase(strId)) {
					lstStops.add(objStop);
				}

			}

		}

		return lstStops;

	}

	/**
	 * This method serializes and saves the stops to the storage
	 */
	private void save() {

		try {

			final FileOutputStream fosStream = new FileOutputStream(filStops);
			final ObjectOutputStream oosStream = new ObjectOutputStream(fosStream);
			oosStream.writeObject(setStops);
			oosStream.flush();
			oosStream.close();

		} catch (final Exception e) {
			return;
		}

	}

}