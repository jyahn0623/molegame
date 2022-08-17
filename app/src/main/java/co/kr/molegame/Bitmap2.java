package co.kr.molegame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class Bitmap2 {

    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // Rotate around the place
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public static byte[] bitmapToByteArray(Bitmap $bitmap) {
        final int COMPRESSION_QUALITY = 100;
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    public  static Bitmap base64EncodedImageToBitmap(String base64EncodedImage){
        byte[] decodedString = Base64.decode(base64EncodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String byteArrayToBase64(byte[] byteArray){
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
