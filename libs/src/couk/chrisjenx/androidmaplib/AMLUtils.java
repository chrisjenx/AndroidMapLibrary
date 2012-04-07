package couk.chrisjenx.androidmaplib;

import android.util.Log;

public class AMLUtils
{

	private static boolean DEBUG = false;

	public static final void debug(boolean debug)
	{
		DEBUG = debug;
	}

	public static void debug(Object output)
	{
		if (DEBUG)
		{
			Log.d("AML", String.valueOf(output));
		}
	}

}
