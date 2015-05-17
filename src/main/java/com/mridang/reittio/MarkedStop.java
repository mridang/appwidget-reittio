package com.mridang.reittio;

import java.io.Serializable;

/**
 * This class is a structure that is used to store the details of a particular stop.
 */
public class MarkedStop implements Serializable {

	/** A serialization identifier */
	private static final long serialVersionUID = -2977539441063094354L;
	/** The name of the stop */
	private final String strName;
	/** The identifier of the stop */
	private final String strId;
	/** The number of the stop */
	private final String strNumber;

	/**
	 * Simple constructor to initialize the stop and set the id, number and name
	 * 
	 * @param strId The identifier of the stop
	 * @param strName The name of the stop
	 * @param strNumber The number of the stop
	 */
	public MarkedStop(String strId, String strName, String strNumber) {

		this.strId = strId;
		this.strName = strName;
		this.strNumber = strNumber;

	}

	/**
	 * This returns the identifier.
	 * 
	 * @return the identifier of the stop
	 */
	public String getId() {

		return strId;

	}

	/**
	 * This returns the name of the stop
	 * 
	 * @return the name of the stop
	 */
	public String getName() {

		return strName;

	}

	/**
	 * This returns the number of the stop.
	 * 
	 * @return the number of the stop
	 */
	public String getNumber() {

		return strNumber;

	}

}