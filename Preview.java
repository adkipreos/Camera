package me.kipreos.hw2;

import android.content.Context;
import android.hardware.Camera;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by andreskipreos on 1/16/15.
*/
class Preview extends ViewGroup implements SurfaceHolder.Callback {

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    SurfaceHolderHandler mHandler;
    Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;
    Camera mCamera;

    Preview(SurfaceHolderHandler handler, Camera camera) {
        super(handler.getContext());

		this.mCamera = camera;
		this.mHandler = handler;

        mSurfaceView = new SurfaceView(handler.getContext());
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (Throwable e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
    }

    public interface SurfaceHolderHandler {
        public void surfaceDestroyed(SurfaceHolder holder);
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height);
        public Context getContext();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
		if (this.mHandler != null) {
			mHandler.surfaceDestroyed(holder);
		}
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (this.mHandler != null) {
			mHandler.surfaceChanged(holder, format, w, h);
		}
    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) { return; }

        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = localSizes;
            requestLayout();

            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Important: Call startPreview() to start updating the preview
            // surface. Preview must be started before you can take a picture.
            mCamera.startPreview();
        }
    }
    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }







}
