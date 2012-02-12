package couk.chrisjenx.androidmaplib.demo.moving;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import couk.chrisjenx.androidmaplib.AMLController;
import couk.chrisjenx.androidmaplib.demo.R;
import couk.chrisjenx.androidmaplib.interfaces.StartStopMovingCallbacks;

public class StartedAndStoppedMoving extends MapActivity
{

	private AMLController aml;

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_map);

		aml = new AMLController(this, R.id.map_view);

		aml.debug(R.id.text1).registerStartStopListener(
				new StartStopMovingCallbacks()
				{

					@Override
					public void onMapStoppedMoving(GeoPoint mapCentre)
					{
						// Toast.makeText(StartedAndStoppedMoving.this,
						// "Map Stopped Moving", Toast.LENGTH_SHORT).show();
						aml.debug("Map Stopped Moving");
					}

					@Override
					public void onMapStartedMoving()
					{
						// Toast.makeText(StartedAndStoppedMoving.this,
						// "Map Started Moving", Toast.LENGTH_SHORT).show();
						aml.debug("Map Started Moving");
					}
				});

	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
