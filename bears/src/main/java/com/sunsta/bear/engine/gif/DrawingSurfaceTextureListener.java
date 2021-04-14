package com.sunsta.bear.engine.gif;

import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;

class DrawingSurfaceTextureListener implements TextureView.SurfaceTextureListener {
	private final GifTextureView.PlaceholderDrawListener mDrawer;

	DrawingSurfaceTextureListener(GifTextureView.PlaceholderDrawListener drawer) {
		mDrawer = drawer;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
		final Surface surface = new Surface(surfaceTexture);
		final Canvas canvas = surface.lockCanvas(null);
		mDrawer.onDrawPlaceholder(canvas);
		surface.unlockCanvasAndPost(canvas);
		surface.release();
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
		//no-op
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
		//no-op
	}
}
