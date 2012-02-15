package couk.chrisjenx.androidmaplib;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class AMLController extends AbstractAMLController<AMLController>
{

	public AMLController(MapActivity mapActivity, String apiKey)
	{
		super(mapActivity, apiKey);
	}

	public AMLController(MapActivity mapActivity, String apiKey, int mapHolder)
	{
		super(mapActivity, apiKey, mapHolder);
	}

	public AMLController(MapActivity mapActivity, int mapRes)
	{
		super(mapActivity, mapRes);
	}

	public AMLController(MapActivity mapActivity, MapView mapView)
	{
		super(mapActivity, mapView);
	}

}
