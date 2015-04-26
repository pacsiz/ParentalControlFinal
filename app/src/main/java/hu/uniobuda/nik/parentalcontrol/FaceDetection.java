package hu.uniobuda.nik.parentalcontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FaceDetection {

    private static final int NUMBER_OF_PHOTOS = 5;

    public static void learnJPG(int personId, Context context) {
        File root = context.getCacheDir();

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg");
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);

        MatVector images = new MatVector(imageFiles.length);
        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.getIntBuffer();

        int counter = 0;

        for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            int label = Integer.parseInt(image.getName().split("\\-")[0]);
            images.put(counter, img);
            labelsBuf.put(counter, label);
            counter++;
        }


        FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
        File f = new File(context.getFilesDir(), context.getString(R.string.xmlName));

        if (f.exists()) {

            faceRecognizer.load(f.getAbsolutePath());
            faceRecognizer.update(images, labels);
        } else {
            faceRecognizer.train(images, labels);
        }

        faceRecognizer.save(f.getAbsolutePath());
        deleteJPGs(context);
    }


    public static void learn(Context context, ArrayList<Bitmap> faces, int label) {

        Mat labels = new Mat(NUMBER_OF_PHOTOS, 1, CV_32SC1);
        MatVector matVector = new MatVector(NUMBER_OF_PHOTOS);
        for (Bitmap face : faces) {
            matVector.put(matForLBPH(face));
        }
        faces.clear();
        IntBuffer intBuff = labels.getIntBuffer();
        FaceRecognizer fr = createLBPHFaceRecognizer();
        for (int i = 0; i < NUMBER_OF_PHOTOS; i++) {
            intBuff.put(i, label);
        }

        File file = new File(context.getFilesDir(), context.getString(R.string.xmlName));
        if (file.exists()) {
            fr.load(file.getAbsolutePath());
            fr.update(matVector, labels);
        } else {
            fr.train(matVector, labels);
            fr.save(file.getAbsolutePath());
        }
    }

    public static void deleteJPGs(Context context) {
        File root = context.getCacheDir();

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg");
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);
        for (File image : imageFiles) {
            image.delete();
        }
    }

    public static Mat matForLBPH(Bitmap face) {
        Bitmap out = face.copy(Config.ARGB_8888, true);
        int height = out.getHeight();
        int width = out.getWidth();

        IplImage iplTemp = IplImage.create(width, height, IPL_DEPTH_8U, 4);
        IplImage grayImg = IplImage.create(width, height, IPL_DEPTH_8U, 1);
        out.copyPixelsToBuffer(iplTemp.createBuffer());
        out.recycle();
        cvCvtColor(iplTemp, grayImg, CV_BGR2GRAY);

        return new Mat(grayImg);
    }

    public static int predict(Context context, Bitmap bitmap, int threshold) {
        FaceRecognizer fr = createLBPHFaceRecognizer();
        Log.d("FaceDetection", "Threshold: "+threshold);
        File file = new File(context.getFilesDir(), context.getString(R.string.xmlName));
        if (file.exists()) {
            fr.load(file.getAbsolutePath());
            fr.set("threshold", threshold);
            Mat mat = matForLBPH(bitmap);
            return fr.predict(mat);
        } else {
            return -1;
        }
    }

    public static boolean numberOfFaces(byte[] rawData, Context context) {
        Bitmap bitmap = getBitmapFromBytes(rawData);
        FaceDetector fd = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 2);
        Face[] faces = new Face[2];
        int numOfFaces = fd.findFaces(bitmap, faces);
        bitmap.recycle();

        if (numOfFaces == 1) {
            return true;
        } else if (numOfFaces == 0) {
            Toast.makeText(context, R.string.faceNotFound, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, R.string.moreFaces, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean saveCroppedFace(Context context, Bitmap croppedFace, String person, int personId) {
        try {
            File jpg = new File(context.getCacheDir(), personId
                    + "-"
                    + person
                    + "-"
                    + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(jpg);

            croppedFace.compress(CompressFormat.JPEG, 90, fos);

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Bitmap cropFace(byte[] data) {
        Bitmap bitmap = getBitmapFromBytes(data);
        FaceDetector fd = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 2);
        Face[] faces = new Face[2];
        fd.findFaces(bitmap, faces);
        int faceSize = (int) (faces[0].eyesDistance() * 2);
        PointF centerFace = new PointF();
        faces[0].getMidPoint(centerFace);

        int initX = bitmap.getWidth();
        int initY = bitmap.getHeight();
        int endX = 0;
        int endY = 0;

        int tInitX = (int) (centerFace.x - faceSize / 2);
        int tInitY = (int) (centerFace.y - faceSize / 2);
        tInitX = Math.max(0, tInitX);
        tInitY = Math.max(0, tInitY);

        int tEndX = tInitX + faceSize;
        int tEndY = (int) (tInitY + faceSize * 1.5);
        tEndX = Math.min(tEndX, bitmap.getWidth());
        tEndY = Math.min(tEndY, bitmap.getHeight());

        initX = Math.min(initX, tInitX);
        initY = Math.min(initY, tInitY);
        endX = Math.max(endX, tEndX);
        endY = Math.max(endY, tEndY);

        int sizeX = endX - initX;
        int sizeY = endY - initY;

        if (sizeX + initX > bitmap.getWidth()) {
            sizeX = bitmap.getWidth() - initX;
        }
        if (sizeY + initY > bitmap.getHeight()) {
            sizeY = bitmap.getHeight() - initY;
        }

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, initX, initY,
                sizeX, sizeY);
        bitmap.recycle();

        Bitmap b = Bitmap
                .createScaledBitmap(croppedBitmap, 100, 150, false);
        return b;
    }

    public static Bitmap getBitmapFromBytes(byte[] imageContent) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageContent, 0,
                    imageContent.length);

            return (bitmap.copy(Config.RGB_565, true));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
