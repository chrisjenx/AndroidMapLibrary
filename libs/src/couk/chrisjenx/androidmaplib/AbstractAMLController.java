package couk.chrisjenx.androidmaplib;

import java.util.List;

import android.content.Context;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import couk.chrisjenx.androidmaplib.interfaces.StoppedMovingEvents;
import couk.chrisjenx.androidmaplib.overlays.StoppedMovingOverlay;

public abstract class AbstractAMLController
{

	private final MapView mMapView;
	private final MapActivity mContext;
	private TextView mDTV = null;

	private List<Overlay> mMapOverlays;
	private MapController mMapController;

	/*
	 * Constructor stuff
	 */

	/**
	 * Create controller from the MapView
	 * 
	 * @param mapView
	 */
	public AbstractAMLController(final MapActivity mapActivity,
			final MapView mapView)
	{
		mMapView = mapView;
		mContext = mapActivity;

		init();
	}

	/**
	 * Create the controller from the resource id and activity
	 * 
	 * @param mapActivity
	 * @param mapRes
	 */
	public AbstractAMLController(MapActivity mapActivity, int mapRes)
	{
		if (mapActivity != null)
		{
			mContext = mapActivity;
			mMapView = (MapView) mapActivity.findViewById(mapRes);
		}
		else
		{
			mContext = null;
			mMapView = null;
		}

		init();
	}

	protected void init()
	{
		if (mMapView != null)
		{
			mMapView.setClickable(true);
			mMapController = mMapView.getController();
			mMapOverlays = mMapView.getOverlays();
		}
	}

	/**
	 * Send debug messages out to a text view for you too see. Only really
	 * useful for demo.
	 * 
	 * @param textView
	 *            valid resource to output, 0 to disable
	 * @return
	 */
	public AbstractAMLController debug(int textView)
	{
		if (mContext != null && textView > 0)
		{
			mDTV = (TextView) mContext.findViewById(textView);
		}
		else
		{
			mDTV = null;
		}
		return this;
	}

	/**
	 * Output to the debug text view, safe to use, if you dont set the textView
	 * this does nothing
	 * 
	 * @param output
	 * @return
	 */
	public AbstractAMLController debug(String output)
	{
		if (mDTV != null)
		{
			mDTV.setText(output);
		}
		return this;
	}

	/*
	 * Methods for user
	 */

	private StoppedMovingOverlay oStoppedMoving = null;

	public AbstractAMLController setStoppedMovingListener(
			StoppedMovingEvents mListener)
	{
		if (mMapOverlays != null)
		{
			if (oStoppedMoving == null)
			{
				oStoppedMoving = new StoppedMovingOverlay();
				// Add mapOverlay to the end
				mMapOverlays.add(oStoppedMoving);
			}
			oStoppedMoving.setStoppedMovingListener(mListener);
		}
		return this;
	}
}