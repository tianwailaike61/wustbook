package com.edu.wustbook.Tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

public class IMGUtils {
    public static Drawable zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        Drawable drawable = new BitmapDrawable(newbmp);
        matrix = null;
        newbmp = null;
        return drawable;
    }

    public static Drawable getPicture(Context context, int resource) {
        int width = ScreenUtils.getScreenWidth(context) / 4;
        int height = ScreenUtils.getScreenHeight(context) / 6;
        Bitmap bitmap = getBitmap(context, resource);
        return zoomBitmap(bitmap, width, height);
    }

    public static Bitmap getBitmap(Context context, int resource) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                resource);
        return bitmap;
    }

    public static Bitmap bytesToBimap(InputStream is) {
        if (is!=null) {
            return BitmapFactory.decodeStream(is);
        } else {
            return null;
        }
    }

    public static Drawable bytesToDrawable(InputStream is) {
        if (is!=null) {
            return zoomBitmap(bytesToBimap(is));
        } else {
            return null;
        }
    }

    public static Bitmap zoomDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;
    }

    public static Drawable zoomBitmap(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }
}