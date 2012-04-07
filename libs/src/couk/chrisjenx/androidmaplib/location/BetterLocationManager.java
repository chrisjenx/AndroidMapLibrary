package couk.chrisjenx.androidmaplib.location;

import java.util.Collection;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import couk.chrisjenx.androidmaplib.AbstractAMLController;

public class BetterLocationManager
{

	/**
	 * We define the providers manually as if your targeting a api 4 device some
	 * of values are missing :( Just making it backwards compatible!
	 */
	private static final String PASSIVE = "passive";
	private static final String GPS = "gps";
	private static final String NETWORK = "network";
	private static final String UNKNOWN = "unknown";

	private LocationManager lm;
	private final AbstractAMLController<?> aml;
	private Collection<String> mAvalibleProviders;

	private LocationChangeListener mListener;
	private Location mLastLocation = new Location(UNKNOWN);

	// Callbacks
	private LocationListener mLocationLis = new LocationListener()
	{

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}

		@Override
		public void onProviderEnabled(String provider)
		{
		}

		@Override
		public void onProviderDisabled(String provider)
		{
		}

		@Override
		public void onLocationChanged(Location location)
		{
			final String provider = location.getProvider();
			if (PASSIVE.equalsIgnoreCase(provider))
			{

			}
			mLastLocation = selectMoreAccurateOrChanged(location);
			if (!mLastLocation.equals(location))
			{
				fireLocationChanged(location);
			}
		}
	};

	public BetterLocationManager(AbstractAMLController<?> aml)
	{
		this.aml = aml;
		final Context ctx = aml.getContext();
		lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		init();
	}

	/**
	 * Start the passive listener if there is one..<br>
	 * Get Location Manager values.
	 */
	private void init()
	{
		if (lm.isProviderEnabled(PASSIVE))
		{
			lm.requestLocationUpdates(PASSIVE, 0, 0, mLocationLis);
		}
		mAvalibleProviders = lm.getAllProviders();
	}

	public void registerForLocation(LocationChangeListener listener)
	{

	}

	private Location selectMoreAccurateOrChanged(Location incomingLocation)
	{

		float distance = mLastLocation.distanceTo(incomingLocation);
		aml.debug("Distance diff : " + distance);
		// if distance is quite far probably moved..
		if (distance > 22.5f)
		{
			return incomingLocation;
		}
		float a1 = mLastLocation.getAccuracy();
		float a2 = incomingLocation.getAccuracy();
		if (a2 <= a1)
		{
			return incomingLocation;
		}

		return mLastLocation;
	}

	/**
	 * Will return the last location we have received
	 * 
	 * @return last location, there is no waiting but might not be accurate..
	 */
	public Location lastLocation()
	{
		return mLastLocation;
	}

	private void requestLocation()
	{

		if (mAvalibleProviders.size() < 0)
		{
			// TODO handle better?
			return;
		}

	}

	/**
	 * The location changed listener, handles null stuff etc..
	 * 
	 * @param location
	 *            the location you want to pass to the listener, avoid null if
	 *            possible..
	 */
	private void fireLocationChanged(Location location)
	{
		if (null != location && null != mListener)
		{
			mListener.locationChanged(location);
		}

	}

	public static interface LocationChangeListener
	{

		/**
		 * Location changed callback's, the most relevant location is returned
		 * back to you.
		 * 
		 * @param location
		 */
		public void locationChanged(Location location);
	}

}
