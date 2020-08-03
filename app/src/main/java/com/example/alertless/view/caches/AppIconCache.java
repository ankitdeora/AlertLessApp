package com.example.alertless.view.caches;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

public class AppIconCache {
    private static volatile AppIconCache INSTANCE;

    private LruCache<String, Drawable> memoryCache;

    private AppIconCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Drawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, Drawable icon) {
                // The cache size will be measured in kilobytes rather than
                // number of items.

                return getBitmapFromDrawable(icon).getByteCount() / 1024;
            }
        };
    }

    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public static AppIconCache getInstance() {
        if (INSTANCE == null) {
            synchronized (AppIconCache.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppIconCache();
                }
            }
        }

        return INSTANCE;
    }


    public void addDrawableToMemoryCache(String key, Drawable drawable) {
        if (getDrawableFromMemCache(key) == null) {
            memoryCache.put(key, drawable);
        }
    }

    public Drawable getDrawableFromMemCache(String key) {
        return memoryCache.get(key);
    }
}
