package couk.chrisjenx.androidmaplib.interfaces;

import com.google.android.maps.GeoPoint;

public interface OutOfBoundsCallbacks
{

	/**
	 * Will fire when out of bounds. This is called before any auto
	 * rebounding/moving is done.
	 * 
	 * @param mapCentre
	 *            the map centre before moving back
	 */
	public void mapOutOfBounds(GeoPoint mapCentre);

	/**
	 * Its worth noting that this will only be fired once until we move out of
	 * bounds again.
	 */
	public void mapInsideBounds();
}
