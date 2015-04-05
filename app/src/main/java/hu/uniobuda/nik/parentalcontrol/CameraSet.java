package hu.uniobuda.nik.parentalcontrol;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;

public class CameraSet {

    public static int getFrontCameraIndex() {
        int cameraNumber = Camera.getNumberOfCameras();
        int frontCameraIndex = -1;
        CameraInfo info = new CameraInfo();
        for (int i = 0; i < cameraNumber; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                frontCameraIndex = i;
                // Log.d("FCI", Integer.toString(frontCameraIndex));
                break;
            }
        }
        return frontCameraIndex;
    }

    public static int setCameraRotation(int windowRotation, int cameraId) {
        int degrees = 0;
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        Log.d("window rotation", Integer.toString(windowRotation));

        switch (windowRotation) {

            case Surface.ROTATION_0:
                degrees = 0;
                break; //Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; //Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;//Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;//Landscape right
        }
        Log.d("camera rotation", Integer.toString(info.orientation));
        int rotate = (info.orientation - degrees + 360) % 360;
        Log.d("return", Integer.toString(rotate));
        return rotate;
    }
}
