package com.sunsta.bear.engine.gif;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;


/**
 * Helper used to work around native libraries loading on some systems.
 * See <a href="https://medium.com/keepsafe-engineering/the-perils-of-loading-native-libraries-on-android-befa49dce2db">ReLinker</a> for more details.
 */
public class LibraryLoader {
	//	Java_com_sunsta_bear_engine_gif_GifInfoHandle_extractNativeFileDescriptor
	public static final String SURFACE_LIBRARY_NAME = "com_sunsta_bear_engine_gif_surface";
	public static final String BASE_LIBRARY_NAME = "com_sunsta_bear_engine_gif";
	@SuppressLint("StaticFieldLeak") //workaround for Android bug
	private static Context sAppContext;

	private LibraryLoader() {
	}

	/**
	 * Initializes loader with given `Context`. Subsequent calls should have no effect since application Context is retrieved.
	 * Libraries will not be loaded immediately but only when needed.
	 *
	 * @param context any Context except null
	 */
	public static void initialize(@NonNull final Context context) {
		sAppContext = context.getApplicationContext();
	}

	private static Context getContext() {
		if (sAppContext == null) {
			try {
				@SuppressLint("PrivateApi")
				final Class<?> activityThread = Class.forName("android.app.ActivityThread");
				final Method currentApplicationMethod = activityThread.getDeclaredMethod("currentApplication");
				sAppContext = (Context) currentApplicationMethod.invoke(null);
			} catch (Exception e) {
				throw new IllegalStateException("LibraryLoader not initialized. Call LibraryLoader.initialize() before using library classes.", e);
			}
		}
		return sAppContext;
	}

	static void loadLibrary() {
		try {
			System.loadLibrary(BASE_LIBRARY_NAME);
		} catch (final UnsatisfiedLinkError e) {
			ReLinker.loadLibrary(getContext());//todo 20191207
		}
	}
}
