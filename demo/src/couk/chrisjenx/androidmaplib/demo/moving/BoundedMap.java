package couk.chrisjenx.androidmaplib.demo.moving;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import couk.chrisjenx.androidmaplib.AMLController;
import couk.chrisjenx.androidmaplib.demo.R;
import couk.chrisjenx.androidmaplib.interfaces.OutOfBoundsCallbacks;
import couk.chrisjenx.androidmaplib.overlays.OutOfBoundsOverlay;

public class BoundedMap extends MapActivity
{

	private AMLController aml;

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_map);

		aml = new AMLController(this, R.id.map_view);

		aml.debug(R.id.text1)
				.registerOutOfBoundsListener(OutOfBoundsOverlay.BoundingBox.BOUND_UNITED_KINGDOM,
						new OutOfBoundsCallbacks()
						{

							@Override
							public void mapOutOfBounds(GeoPoint mapCentre)
							{
								aml.debug("Currently out of bounds");
							}

							@Override
							public void mapInsideBounds()
							{
								aml.debug("Currently inside bounds");
							}

						}).drawOutOfBoundsBox(true);

	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
