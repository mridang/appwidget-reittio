package com.mridang.reittio;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * This class is the service that provides the factory for the list
 */
public class SlideService extends RemoteViewsService {

	/*
	 * @see android.widget.RemoteViewsService#onGetViewFactory(android.content.Intent )
	 */
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent ittIntent) {

		return new SlideFactory(getApplicationContext(), ittIntent);

	}

}