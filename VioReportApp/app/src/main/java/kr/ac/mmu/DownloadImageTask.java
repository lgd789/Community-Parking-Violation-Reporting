package kr.ac.mmu;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... params) {
        String ip ="218.157.77.240:51212";
        String url = "http://" + ip + "//app//uploads//" + params[0];
        Bitmap bitmap = null;
        try {
            Log.e("bit", "1");
            InputStream inputStream = new java.net.URL(url).openStream();
            Log.e("bit", "2");


            // byte [] content = convertInputStreamToByteArray(inputStream);
            // bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 8;

            bitmap = BitmapFactory.decodeStream(inputStream, null, opts);
            Log.e("bit", "3");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        // 서버 응답 결과 처리
        if (result.equals("success")) {

        }
        else if (result != null) {
            Log.d(TAG, "HTTP POST request result : " + result);
        }
        else {

        }
    }
    int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    public static byte[] convertInputStreamToByteArray(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result !=-1) {
            byte b = (byte)result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toByteArray();
    }
}