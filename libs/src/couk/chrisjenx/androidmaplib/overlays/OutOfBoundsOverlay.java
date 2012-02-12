package couk.chrisjenx.androidmaplib.overlays;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import couk.chrisjenx.androidmaplib.AbstractAMLController;
import couk.chrisjenx.androidmaplib.interfaces.OutOfBoundsCallbacks;
import couk.chrisjenx.androidmaplib.interfaces.StartStopMovingCallbacks;

/**
 * This object is designed to listen to other events coming from the map
 * overlays and tell you if out of bounds. It's also configurable to move you
 * back to the bounding box.
 * 
 * @author Chris Jenkins
 * @version 1.0
 * 
 */
public class OutOfBoundsOverlay extends Overlay
{

	private final AbstractAMLController aml;
	private final MapView mv;
	// The reusable Rect for getting the current bounding box
	private final Rect mPxBounds = new Rect(0, 0, 0, 0);
	private final Paint mDrawBoundsPaint = new Paint();
	private final Rect mPxView = new Rect(0, 0, 0, 0);
	{
		mDrawBoundsPaint.setColor(Color.RED);
		mDrawBoundsPaint.setAlpha(155);
	}

	/**
	 * The current outofbounds listener for this overlay
	 */
	private OutOfBoundsCallbacks mListener;

	/**
	 * Current bounding box
	 */
	private BoundingBox mBounds = null;
	private boolean drawBounds = false;

	public OutOfBoundsOverlay(AbstractAMLController aml)
	{
		this.aml = aml;
		mv = aml.getMapView();
		// We need the start stop listener to make sure we can do any requested
		// work when finished moving
		aml.registerStartStopListener(mStartStop);

	}

	public void setOutOfBoundsListener(final OutOfBoundsCallbacks listener)
	{
		mListener = listener;
	}

	public OutOfBoundsCallbacks getOutOfBoundsListener()
	{
		return mListener;
	}

	public void setBounds(final BoundingBox bb)
	{
		mBounds = bb;
	}

	public final void drawBounds(final boolean draw)
	{
		drawBounds = draw;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if (drawBounds && !shadow && mBounds != null)
		{
			mBounds.toCurrentScreenPx(mapView.getProjection(), mPxBounds);
			canvas.drawRect(mPxBounds, mDrawBoundsPaint);
			// Log.d("OOB", "Drawing - " + mPxBounds.top + " " + mPxBounds.left
			// + " " + mPxBounds.bottom + " " + mPxBounds.right);
		}
		super.draw(canvas, mapView, shadow);
	}

	/**
	 * Will do the heavy lifting to check if the current view port is outside
	 * the bounding box.
	 */
	private final void doOutSideOfBounds()
	{
		if (mBounds == null) return;
		// Create a rect of the view port
		// TODO: this probably shouldn't change wondering if this could be
		// negated or done once on object creation
		mPxView.left = mv.getLeft();
		mPxView.top = mv.getTop();
		mPxView.right = mv.getRight();
		mPxView.bottom = mv.getBottom();
		// Fill the bounding box current pos
		mBounds.toCurrentScreenPx(mv.getProjection(), mPxBounds);

		if (mPxView.contains(mPxBounds))
		{
			// Means were zoomed outside of the bounds...
			fireISB();
		}
		else if (mPxBounds.contains(mPxView))
		{
			// The view is inside of the bounds.. meaning that we well within
			// the bounds. good!
			fireISB();
		}
		else
		{
			// So we can't see the whole bounds and were not inside them.
			// We can fire out of bounds for a start
			fireOOB();
		}
	}

	/**
	 * OutOfBounds
	 */
	private final void fireOOB()
	{
		if (mListener != null) mListener.mapOutOfBounds(mv.getMapCenter());
	}

	/**
	 * InSideBounds
	 */
	private final void fireISB()
	{
		if (mListener != null) mListener.mapInsideBounds();
	}

	/*
	 * Internal classes/anon
	 */

	private final StartStopMovingCallbacks mStartStop = new StartStopMovingCallbacks()
	{

		@Override
		public void onMapStoppedMoving(GeoPoint mapCentre)
		{
			doOutSideOfBounds();
		}

		@Override
		public void onMapStartedMoving()
		{
		}
	};

	/**
	 * This is a non-mutable bounding box.
	 * 
	 * @author Chris Jenkins
	 * 
	 */
	public static class BoundingBox
	{

		/**
		 * UK bounding box, uses the following values: 60.854691, 1.768960,
		 * 49.162090, -13.413930
		 */
		public static final BoundingBox BOUND_UNITED_KINGDOM = new BoundingBox(60.854691, 1.768960,
				49.162090, -13.413930);

		// Float versions of latlon
		// Top Left
		final double northLat;
		final double westLon;
		final GeoPoint northWest;
		// Bottom Right
		final double southLat;
		final double eastLon;
		final GeoPoint southEast;
		// E6 versions of latlon
		// Top Left
		final int northLatE6;
		final int westLonE6;
		// Bottom Right
		final int southLatE6;
		final int eastLonE6;

		/**
		 * Create a new bounding box for the map, remember that these must
		 * represent NW/TopLeft and SE/BottomRight.<br>
		 * <br>
		 * For example the UK is
		 * <code>(60.854691, 1.768960, 49.162090, -13.413930)</code>
		 * 
		 * @param northLat
		 * @param eastLon
		 * @param southLat
		 * @param westLon
		 */
		public BoundingBox(double northLat, double eastLon, double southLat, double westLon)
		{
			super();
			this.northLat = northLat;
			this.eastLon = eastLon;
			this.southLat = southLat;
			this.westLon = westLon;

			northLatE6 = (int) (northLat * 1E6);
			westLonE6 = (int) (westLon * 1E6);
			this.northWest = new GeoPoint(northLatE6, westLonE6);
			southLatE6 = (int) (southLat * 1E6);
			eastLonE6 = (int) (eastLon * 1E6);
			this.southEast = new GeoPoint(southLatE6, eastLonE6);
		}

		/**
		 * Takes the current mapview projection and will return where the
		 * bounding box is in pixel relationship to the current view.
		 * 
		 * @param proj
		 *            current mapview projection
		 * @param outputs
		 *            pixel position to this Rect
		 * @return
		 */
		void toCurrentScreenPx(final Projection proj, final Rect out)
		{
			if (proj != null && out != null)
			{
				final Point p = new Point();
				proj.toPixels(northWest, p);
				out.top = p.y;
				out.left = p.x;
				proj.toPixels(southEast, p);
				out.bottom = p.y;
				out.right = p.x;
			}
		}
	}

}
