package com.example.myemailapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * Get the file path from a Uri
     */
    public static String getPath(Context context, Uri uri) {
        try {
            // Try to get path from MediaStore first
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                cursor.close();

                if (path != null && new File(path).exists()) {
                    return path;
                }
            }

            // If MediaStore approach fails, create a temporary file
            return createTempFileFromUri(context, uri);

        } catch (Exception e) {
            Log.e(TAG, "Error getting path from URI", e);
            try {
                return createTempFileFromUri(context, uri);
            } catch (IOException ioException) {
                Log.e(TAG, "Error creating temp file", ioException);
                return null;
            }
        }
    }

    /**
     * Create a temporary file from Uri
     */
    private static String createTempFileFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Cannot open input stream from URI");
        }

        String fileName = getFileName(context, uri);
        if (fileName == null) {
            fileName = "temp_image_" + System.currentTimeMillis() + ".jpg";
        }

        File tempFile = new File(context.getCacheDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();

        return tempFile.getAbsolutePath();
    }

    /**
     * Get the file name from a Uri
     */
    public static String getFileName(Context context, Uri uri) {
        String fileName = null;

        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name", e);
            }
        }

        if (fileName == null) {
            fileName = uri.getPath();
            if (fileName != null) {
                int cut = fileName.lastIndexOf('/');
                if (cut != -1) {
                    fileName = fileName.substring(cut + 1);
                }
            }
        }

        return fileName;
    }
}