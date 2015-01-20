package me.kipreos.hw2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by andreskipreos on 1/16/15.
*/
class Preview extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mHolder;
    Camera.Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;
    SurfaceHolderHandler mHandler;
    private final String TAG = "Preview";


    Preview(SurfaceHolderHandler handler) {
        super(handler.getContext());

        // mCamera = camera;
        mHandler = handler;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        if (mHolder != null) {
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    public interface SurfaceHolderHandler {
        public void surfaceDestroyed(SurfaceHolder holder);
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height);
        public Context getContext();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mHandler != null) {
            this.mHandler.surfaceDestroyed(holder);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        requestLayout();

        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters params = mCamera.getParameters();
        // Aquí hay que hacer una mejor lógica
        Camera.Size s = params.getSupportedPreviewSizes().get(0);
        params.setPictureSize(s.width, s.height);
        params.setPreviewSize(s.width, s.height);
        params.setJpegQuality(85);

        if (this.mHandler.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        try {
            mCamera.setParameters(params);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mHandler.surfaceChanged(holder, format, w, h);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) { return; }

        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
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