package couk.chrisjenx.androidmaplib;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class AMLController extends AbstractAMLController
{

	public AMLController(MapActivity mapActivity, int mapRes)
	{
		super(mapActivity, mapRes);
	}

	public AMLController(MapActivity mapActivity, MapView mapView)
	{
		super(mapActivity, mapView);
	}

}
