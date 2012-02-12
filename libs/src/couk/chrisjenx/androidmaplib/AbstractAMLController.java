package couk.chrisjenx.androidmaplib;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import couk.chrisjenx.androidmaplib.interfaces.OutOfBoundsCallbacks;
import couk.chrisjenx.androidmaplib.interfaces.StartStopMovingCallbacks;
import couk.chrisjenx.androidmaplib.overlays.OutOfBoundsOverlay;
import couk.chrisjenx.androidmaplib.overlays.StartStopMovingOverlay;

public abstract class AbstractAMLController
{

	private final MapView mMapView;
	private final MapActivity mContext;
	private TextView mDTV = null;

	/*
	 * Overlays
	 */
	private List<Overlay> mMapOverlays;
	// StartStop
	private StartStopMovingOverlay oStoppedMoving = null;
	private final List<SoftReference<StartStopMovingCallbacks>> cStartStopCallbacks = new ArrayList<SoftReference<StartStopMovingCallbacks>>(
			2);
	// OutOfBounds
	private OutOfBoundsOverlay oOutOfBounds;
	private final List<SoftReference<OutOfBoundsCallbacks>> cOutOfBoundsCallbacks = new ArrayList<SoftReference<OutOfBoundsCallbacks>>(
			2);

	/*
	 * Map controls
	 */
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

	/*
	 * Testing and Util methods
	 */

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

	public final MapView getMapView()
	{
		return mMapView;
	}

	/**
	 * Register a start stop listener, this is a fairly dumb overlay, it will
	 * notify when the map is moving and when its stopped.
	 * 
	 * @param mListener
	 * @return
	 */
	public AbstractAMLController registerStartStopListener(
			StartStopMovingCallbacks mListener)
	{
		// Register a soft reference to the callBack
		cStartStopCallbacks.add(new SoftReference<StartStopMovingCallbacks>(
				mListener));
		if (mMapOverlays != null)
		{
			if (oStoppedMoving == null)
			{
				oStoppedMoving = new StartStopMovingOverlay();
				oStoppedMoving
						.setStoppedMovingListener(new StartStopMovingCallbacks()
						{

							@Override
							public void onMapStoppedMoving(GeoPoint mapCentre)
							{
								StartStopMovingCallbacks listener;
								for (final SoftReference<StartStopMovingCallbacks> softListener : cStartStopCallbacks)
								{
									listener = softListener.get();
									if (listener == null) cStartStopCallbacks
											.remove(softListener);
									else listener.onMapStoppedMoving(mapCentre);
								}
							}

							@Override
							public void onMapStartedMoving()
							{
								StartStopMovingCallbacks listener;
								for (final SoftReference<StartStopMovingCallbacks> softListener : cStartStopCallbacks)
								{
									listener = softListener.get();
									if (listener == null) cStartStopCallbacks
											.remove(softListener);
									else listener.onMapStartedMoving();
								}
							}
						});
				// Add mapOverlay to the end
				mMapOverlays.add(oStoppedMoving);
			}
		}
		return this;
	}

	/**
	 * Sets the bounding box on the map
	 * 
	 * @param boundingBox
	 * @return
	 */
	public AbstractAMLController setOutOfBoundsBounding(
			OutOfBoundsOverlay.BoundingBox boundingBox)
	{
		createOutOfBoundsOverlay();
		if (oOutOfBounds != null)
		{
			oOutOfBounds.setBounds(boundingBox);
		}
		return this;
	}

	/**
	 * Register and set the bounding box
	 * 
	 * @param boundingBox
	 * @param mListener
	 * @return
	 */
	public AbstractAMLController registerOutOfBoundsListener(
			OutOfBoundsOverlay.BoundingBox boundingBox,
			OutOfBoundsCallbacks mListener)
	{
		registerOutOfBoundsListener(mListener);
		setOutOfBoundsBounding(boundingBox);
		return this;
	}

	/**
	 * Register a OutOfBounds listener, remember you need to set the bounding
	 * box, so if you havent called
	 * {@link #registerOutOfBoundsListener(OutOfBoundsOverlay.BoundingBox, OutOfBoundsCallbacks)}
	 * or {@link #setOutOfBoundsBounding(OutOfBoundsOverlay.BoundingBox)}
	 * nothing will happen
	 * 
	 * @param mListener
	 * @return
	 */
	public AbstractAMLController registerOutOfBoundsListener(
			OutOfBoundsCallbacks mListener)
	{
		// Register a soft reference to the callBack
		cOutOfBoundsCallbacks.add(new SoftReference<OutOfBoundsCallbacks>(
				mListener));
		createOutOfBoundsOverlay();
		if (oOutOfBounds != null
				&& oOutOfBounds.getOutOfBoundsListener() == null)
		{
			oOutOfBounds.setOutOfBoundsListener(new OutOfBoundsCallbacks()
			{

				@Override
				public void mapOutOfBounds(GeoPoint mapCentre)
				{
					OutOfBoundsCallbacks listener;
					for (final SoftReference<OutOfBoundsCallbacks> softListener : cOutOfBoundsCallbacks)
					{
						listener = softListener.get();
						if (listener == null) cOutOfBoundsCallbacks
								.remove(softListener);
						else listener.mapOutOfBounds(mapCentre);
					}
				}

				@Override
				public void mapInsideBounds()
				{
					OutOfBoundsCallbacks listener;
					for (final SoftReference<OutOfBoundsCallbacks> softListener : cOutOfBoundsCallbacks)
					{
						listener = softListener.get();
						if (listener == null) cOutOfBoundsCallbacks
								.remove(softListener);
						else listener.mapInsideBounds();
					}
				}
			});
		}

		return this;
	}

	/**
	 * This will draw your currently set bounding box on the
	 * {@link OutOfBoundsOverlay}. It will appear as a red box for the area you
	 * want to bound.
	 * 
	 * @param draw
	 *            true to draw it, false will hide it.
	 * @return
	 */
	public AbstractAMLController drawOutOfBoundsBox(boolean draw)
	{
		if (oOutOfBounds != null)
		{
			oOutOfBounds.drawBounds(draw);
		}
		return this;
	}

	/*
	 * Internal methods
	 */

	/**
	 * Will create the outofbounds overlay if it doesn't already exist
	 */
	private void createOutOfBoundsOverlay()
	{
		if (mMapOverlays != null && oOutOfBounds == null)
		{
			oOutOfBounds = new OutOfBoundsOverlay(this);
			// Add mapOverlay to the end
			mMapOverlays.add(oOutOfBounds);
		}
	}
}