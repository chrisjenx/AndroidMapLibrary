package couk.chrisjenx.androidmaplib.demo.easy;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import couk.chrisjenx.androidmaplib.AMLController;
import couk.chrisjenx.androidmaplib.demo.Constants;
import couk.chrisjenx.androidmaplib.demo.R;
import couk.chrisjenx.androidmaplib.tools.ReflectionHelper;

public class QuickStart extends MapActivity
{

	private AMLController aml;

	@Override
	protected void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.main);

		aml = new AMLController(this, Constants.MAP_API_KEY);

		ReflectionHelper.listMethodsUsingReflection(MapView.class);

	}

	@Override
	protected boolean isRouteDisplayed()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
