package ro.sapientia.ms.sapiadvertiser.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.util.Objects;

public class PathParser {

    /**
     *
     * @param context caller is context where method is called
     * @param uri uri of file which contains data about file
     * @return a string containing the path to the file
     *
     * This function parses an URI object to get its file path from content provider
     */
    public static String getPathFromUri(Context context, Uri uri){

        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);
        int columnIndex = Objects.requireNonNull(cursor).getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}
