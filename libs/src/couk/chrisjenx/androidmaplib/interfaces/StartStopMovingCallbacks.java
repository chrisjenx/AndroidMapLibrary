package couk.chrisjenx.androidmaplib.interfaces;

import com.google.android.maps.GeoPoint;

public interface StartStopMovingCallbacks
{

	/**
	 * This is fired as soon as the map starts moving, it doesn't matter how it
	 * starts moving, as soon as it moves this is called. Also if the map is
	 * already moving and is flung again this wont be called until its stopped
	 * and stated again.
	 */
	public void onMapStartedMoving();

	/**
	 * This is fired when the map stops moving.
	 * 
	 * @param mapCenter
	 *            This is the current map centre after it finishes moving.
	 */
	public void onMapStoppedMoving(GeoPoint mapCenter);

}
