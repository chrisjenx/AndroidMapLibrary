package couk.chrisjenx.androidmaplib.demo.easy;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

import couk.chrisjenx.androidmaplib.AMLController;
import couk.chrisjenx.androidmaplib.demo.R;
import couk.chrisjenx.androidmaplib.overlays.OutOfBoundsOverlay.BoundingBox;

public class QuickBoundedMap extends MapActivity
{

	private AMLController aml;

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_map);

		aml = new AMLController(this, R.id.map_view);
		aml.bounds(BoundingBox.BOUND_UNITED_KINGDOM);
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
