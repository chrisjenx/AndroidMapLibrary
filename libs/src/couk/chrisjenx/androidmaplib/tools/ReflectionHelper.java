package couk.chrisjenx.androidmaplib.tools;

import java.lang.reflect.Method;

import android.util.Log;
import couk.chrisjenx.androidmaplib.AMLConsts;

/**
 * This class has been carefully designed to provide reflection utilities on the
 * internal google map plugin which has many hidden internal methods.
 * 
 * @author Chris
 * 
 */
public final class ReflectionHelper implements AMLConsts
{

	/**
	 * Lists the methods of a class using the Reflection api.
	 */
	@SuppressWarnings("rawtypes")
	public static final void listMethodsUsingReflection(Class obj)
	{

		// Get the methods
		Method[] methods = obj.getDeclaredMethods();

		// Loop through the methods and print out their names
		for (Method method : methods)
		{
			Log.d(LTAG, obj.getSimpleName() + " -  Method Name : " + method.getName());
			Class<?>[] params = method.getParameterTypes();
			for (Class<?> param : params)
			{
				Log.d(LTAG, "/t Method Name : " + method.getName() + " : " + param.getSimpleName());
			}
		}
	}

}
