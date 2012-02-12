package couk.chrisjenx.androidmaplib.overlays;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import couk.chrisjenx.androidmaplib.interfaces.StoppedMovingEvents;

public final class StoppedMovingOverlay extends Overlay
{

	/**
	 * The last location the map was at when drawing
	 */
	private GeoPoint lastLatLon = new GeoPoint(0, 0);
	/**
	 * The current lat lon of the map in this pass
	 */
	private GeoPoint currLatLon;

	/**
	 * Is the map currently moving?
	 */
	private boolean isMapMoving = false;

	private boolean isBeingTouched = false;

	/**
	 * The event callback listener
	 */
	private StoppedMovingEvents mListener;

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView)
	{
		switch (e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				// The map is currently being moved, we need to know this if the
				// user is holding the map still it doesn't trigger the stopped
				// moving
				if (mListener != null && !isMapMoving)
					mListener.onMapStartedMoving();
				isBeingTouched = true;
				isMapMoving = true;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				isBeingTouched = false;
				break;
			default:
				break;
		}
		// We don't handle the touch event as other overlays need to use this
		return false;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		// We need to ignore the shadow, as this essentially doubles the work
		// load otherwise!!
		if (!shadow)
		{
			if (isMapMoving)
			{
				currLatLon = mapView.getProjection().fromPixels(0, 0);
				if (currLatLon.equals(lastLatLon) && !isBeingTouched)
				{
					isMapMoving = false;
					if (mListener != null)
						mListener.onMapStoppedMoving(mapView.getMapCenter());
				}
				else
				{
					lastLatLon = currLatLon;
				}
			}
		}
	}

	/**
	 * This should never really need to be called as the callback will provide
	 * more accurate information
	 * 
	 * @return true if the map is moving, false if not
	 */
	public boolean isMapMoving()
	{
		return isMapMoving;
	}

	/**
	 * Set the {@link StoppedMovingEvents} listener, callbacks moving events
	 * will be called through here.
	 * 
	 * @param listener
	 */
	public final void setStoppedMovingListener(StoppedMovingEvents listener)
	{
		mListener = listener;
	}
}
