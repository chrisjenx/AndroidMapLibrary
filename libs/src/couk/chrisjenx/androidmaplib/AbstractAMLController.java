package couk.chrisjenx.androidmaplib;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

	private final static int MSG_ANIMATE_TO = 0;
	private final static long MSG_ANIMATE_TO_DELAY = 500;

	private final MapView mMapView;
	private final MapActivity mContext;
	private final AMLHandler mHandler;
	private TextView mDTV = null;
	/*
	 * Overlays
	 */
	private List<Overlay> mMapOverlays;
	// StartStop
	private StartStopMovingOverlay oStoppedMoving = null;
	private final List<StartStopMovingCallbacks> cStartStopCallbacks = new ArrayList<StartStopMovingCallbacks>(
			2);
	// OutOfBounds
	private OutOfBoundsOverlay oOutOfBounds;
	private final List<OutOfBoundsCallbacks> cOutOfBoundsCallbacks = new ArrayList<OutOfBoundsCallbacks>(
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
	public AbstractAMLController(final MapActivity mapActivity, final MapView mapView)
	{
		mMapView = mapView;
		mContext = mapActivity;
		mHandler = new AMLHandler(mapActivity.getMainLooper());

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
			mHandler = new AMLHandler(mapActivity.getMainLooper());
		}
		else
		{
			mContext = null;
			mMapView = null;
			mHandler = null;
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

	public final AbstractAMLController animateTo(GeoPoint point)
	{
		if (mMapController != null)
		{
			mHandler.removeMessages(MSG_ANIMATE_TO);
			final Message msg = mHandler.obtainMessage(MSG_ANIMATE_TO);
			msg.obj = point;
			mHandler.sendMessageDelayed(msg, MSG_ANIMATE_TO_DELAY);
		}
		return this;
	}

	public final AbstractAMLController snapTo(GeoPoint point)
	{
		if (mMapController != null)
		{
			mMapController.setCenter(point);
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
	public AbstractAMLController registerStartStopListener(StartStopMovingCallbacks mListener)
	{
		// Register a soft reference to the callBack
		cStartStopCallbacks.add(mListener);
		if (mMapOverlays != null)
		{
			if (oStoppedMoving == null)
			{
				oStoppedMoving = new StartStopMovingOverlay();
				oStoppedMoving.setStoppedMovingListener(new StartStopMovingCallbacks()
				{

					@Override
					public void onMapStoppedMoving(GeoPoint mapCentre)
					{
						for (final StartStopMovingCallbacks listener : cStartStopCallbacks)
						{
							listener.onMapStoppedMoving(mapCentre);
						}
					}

					@Override
					public void onMapStartedMoving()
					{
						for (final StartStopMovingCallbacks listener : cStartStopCallbacks)
						{
							listener.onMapStartedMoving();
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
	public AbstractAMLController setOutOfBoundsBounding(OutOfBoundsOverlay.BoundingBox boundingBox)
	{
		createOutOfBoundsOverlay();
		if (oOutOfBounds != null)
		{
			oOutOfBounds.setBounds(boundingBox);
		}
		return this;
	}

	/**
	 * @param autoBound
	 *            should auto bind, requires
	 *            {@link #setOutOfBoundsBounding(OutOfBoundsOverlay.BoundingBox)}
	 *            to be set to work.
	 * @param snap
	 *            if true the view will snap back to the bound box, false will
	 *            animate
	 * @return
	 */
	public AbstractAMLController setOutOfBoundsAutoBounding(boolean autoBound, boolean snap)
	{
		createOutOfBoundsOverlay();
		if (oOutOfBounds != null)
		{
			oOutOfBounds.setAutoBounding(autoBound, snap);
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
			OutOfBoundsOverlay.BoundingBox boundingBox, OutOfBoundsCallbacks mListener)
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
	public AbstractAMLController registerOutOfBoundsListener(OutOfBoundsCallbacks mListener)
	{
		// Register a soft reference to the callBack
		cOutOfBoundsCallbacks.add(mListener);
		createOutOfBoundsOverlay();
		if (oOutOfBounds != null && oOutOfBounds.getOutOfBoundsListener() == null)
		{
			oOutOfBounds.setOutOfBoundsListener(new OutOfBoundsCallbacks()
			{

				@Override
				public void mapOutOfBounds(GeoPoint mapCentre)
				{
					for (final OutOfBoundsCallbacks listener : cOutOfBoundsCallbacks)
					{
						listener.mapOutOfBounds(mapCentre);
					}
				}

				@Override
				public void mapInsideBounds()
				{
					for (final OutOfBoundsCallbacks listener : cOutOfBoundsCallbacks)
					{
						listener.mapInsideBounds();
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
			mMapOverlays.add(0, oOutOfBounds);
		}
	}

	/*
	 * Internal Classes
	 */

	private class AMLHandler extends Handler
	{

		/**
		 * Pass in the map looper!
		 * 
		 * @param looper
		 */
		public AMLHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case MSG_ANIMATE_TO:
					GeoPoint point = (GeoPoint) msg.obj;
					mMapController.animateTo(point);
					break;

				default:
					break;
			}
		}
	}
}