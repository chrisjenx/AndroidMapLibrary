package couk.chrisjenx.androidmaplib;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import couk.chrisjenx.androidmaplib.interfaces.OutOfBoundsCallbacks;
import couk.chrisjenx.androidmaplib.interfaces.StartStopMovingCallbacks;
import couk.chrisjenx.androidmaplib.overlays.OutOfBoundsOverlay;
import couk.chrisjenx.androidmaplib.overlays.OutOfBoundsOverlay.BoundingBox;
import couk.chrisjenx.androidmaplib.overlays.StartStopMovingOverlay;

public abstract class AbstractAMLController<T extends AbstractAMLController<T>>
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

	public AbstractAMLController(final MapActivity mapActivity, String apiKey)
	{
		mMapView = new MapView(mapActivity, apiKey);
		mContext = mapActivity;
		mHandler = new AMLHandler(mapActivity.getMainLooper());

		addMapToView(null);

		init();
	}

	public AbstractAMLController(final MapActivity mapActivity, String apiKey, int mapHolder)
	{
		mMapView = new MapView(mapActivity, apiKey);
		mContext = mapActivity;
		mHandler = new AMLHandler(mapActivity.getMainLooper());

		addMapToView(mapHolder);

		init();
	}

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
	public T debug(int textView)
	{
		if (mContext != null && textView > 0)
		{
			mDTV = (TextView) mContext.findViewById(textView);
		}
		else
		{
			mDTV = null;
		}
		return self();
	}

	/**
	 * Output to the debug text view, safe to use, if you dont set the textView
	 * this does nothing
	 * 
	 * @param output
	 * @return
	 */
	public T debug(String output)
	{
		if (mDTV != null)
		{
			mDTV.setText(output);
		}
		return self();
	}

	public final T animateTo(GeoPoint point)
	{
		if (mMapController != null)
		{
			mHandler.removeMessages(MSG_ANIMATE_TO);
			final Message msg = mHandler.obtainMessage(MSG_ANIMATE_TO);
			msg.obj = point;
			mHandler.sendMessageDelayed(msg, MSG_ANIMATE_TO_DELAY);
		}
		return self();
	}

	public final T snapTo(GeoPoint point)
	{
		if (mMapController != null)
		{
			mMapController.setCenter(point);
		}
		return self();
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
	public T registerStartStopListener(StartStopMovingCallbacks mListener)
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
		return self();
	}

	/**
	 * Very shorthand method for setting bounding on the MapView.
	 * 
	 * @see {@link #setOutOfBoundsBounding(BoundingBox)}
	 *      {@link #setOutOfBoundsAutoBounding(boolean, boolean)}
	 *      {@link BoundingBox}
	 * @param northLat
	 *            top bound
	 * @param eastLon
	 *            right bound
	 * @param southLat
	 *            bottom bound
	 * @param westLon
	 *            left bound
	 * @return self
	 */
	public T bounds(double northLat, double eastLon, double southLat, double westLon)
	{
		setOutOfBoundsBounding(new BoundingBox(northLat, eastLon, southLat, westLon));
		setOutOfBoundsAutoBounding(true, false);
		return self();
	}

	/**
	 * Set the bounding box by passing in a bounding box.
	 * 
	 * @see {@link #setOutOfBoundsBounding(BoundingBox)}
	 *      {@link #setOutOfBoundsAutoBounding(boolean, boolean)}
	 *      {@link BoundingBox}
	 * @param bounds
	 *            a predefined bounding box.
	 * @return self
	 */
	public T bounds(OutOfBoundsOverlay.BoundingBox bounds)
	{
		setOutOfBoundsBounding(bounds);
		setOutOfBoundsAutoBounding(true, false);
		return self();
	}

	/**
	 * Sets the bounding box on the map
	 * 
	 * @param boundingBox
	 * @return
	 */
	public T setOutOfBoundsBounding(OutOfBoundsOverlay.BoundingBox boundingBox)
	{
		createOutOfBoundsOverlay();
		if (oOutOfBounds != null)
		{
			oOutOfBounds.setBounds(boundingBox);
		}
		return self();
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
	public T setOutOfBoundsAutoBounding(boolean autoBound, boolean snap)
	{
		createOutOfBoundsOverlay();
		if (oOutOfBounds != null)
		{
			oOutOfBounds.setAutoBounding(autoBound, snap);
		}
		return self();
	}

	/**
	 * Register and set the bounding box
	 * 
	 * @param boundingBox
	 * @param mListener
	 * @return
	 */
	public T registerOutOfBoundsListener(OutOfBoundsOverlay.BoundingBox boundingBox,
			OutOfBoundsCallbacks mListener)
	{
		registerOutOfBoundsListener(mListener);
		setOutOfBoundsBounding(boundingBox);
		return self();
	}

	/**
	 * Register a OutOfBounds listener, remember you need to set the bounding
	 * box, so if you haven't called
	 * {@link #registerOutOfBoundsListener(OutOfBoundsOverlay.BoundingBox, OutOfBoundsCallbacks)}
	 * or {@link #setOutOfBoundsBounding(OutOfBoundsOverlay.BoundingBox)}
	 * nothing will happen.
	 * 
	 * @param mListener
	 * @return
	 */
	public T registerOutOfBoundsListener(OutOfBoundsCallbacks mListener)
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

		return self();
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
	public T drawOutOfBoundsBox(boolean draw)
	{
		if (oOutOfBounds != null)
		{
			oOutOfBounds.drawBounds(draw);
		}
		return self();
	}

	/*
	 * Internal methods
	 */

	private final void addMapToView(final int viewGroup)
	{
		if (mContext == null) return;
		View v = mContext.findViewById(viewGroup);
		if (v instanceof ViewGroup)
		{
			addMapToView((ViewGroup) v);
		}
	}

	private final void addMapToView(final ViewGroup v)
	{
		if (mMapView == null) return;
		View root = null;
		View content = null;
		if (v != null)
		{
			content = v;
		}
		else
		{
			// Grab the activity content view
			root = mContext.findViewById(android.R.id.content);
		}

		if (root instanceof ViewGroup && content == null)
		{
			// Try to get the inner view group of my
			content = ((ViewGroup) root).getChildAt(0);
		}
		if (content instanceof RelativeLayout)
		{
			// Add to the beginning of the view group
			((ViewGroup) content).addView(mMapView, 0);
		}
		else if (content instanceof LinearLayout)
		{
			if (((LinearLayout) content).getOrientation() == LinearLayout.VERTICAL)
			{
				((LinearLayout) content).addView(mMapView, 0, new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, 0, 1));
			}
			else
			{
				((LinearLayout) content).addView(mMapView, 0, new LinearLayout.LayoutParams(0,
						LayoutParams.FILL_PARENT, 1));
			}
		}
	}

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

	@SuppressWarnings("unchecked")
	private final T self()
	{
		return (T) this;
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