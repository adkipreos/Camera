package me.kipreos.hw2;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;


public class MainActivity extends ActionBarActivity
implements Preview.SurfaceHolderHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Camera mCamera;
    private Preview mPreview;
    private byte[] mImageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume(){
        super.onResume();
        initLayout();
    }

    protected void initLayout() {
        safeCameraOpen();
        // Setting the right parameters in the camera
        Camera.Parameters params = mCamera.getParameters();

        mCamera.setParameters(params);

        mPreview = new Preview(this);
        mPreview.setCamera(mCamera);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeAllViews();

        preview.addView(mPreview);
    }


    private boolean safeCameraOpen() {
        boolean qOpened = false;

        try {
            // releaseCameraAndPreview();
            mCamera = Camera.open();
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        mPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback()	{
        @Override
        public void onPictureTaken(@Nullable byte[] data, Camera mCamera) {
            mImageData = data;
            if (data == null) {
                return;
            }
            Log.d(TAG, "received data with length " + data.length);
        }
    };

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed callback");
        if (this.mCamera != null) {
            Log.d(TAG, "Releasing camera");
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // Esto sirve para ver si la cámara está activa
    }

}
