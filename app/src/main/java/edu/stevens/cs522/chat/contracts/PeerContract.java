package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;



public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));

    public static final String ID = _ID;

    public static final String NAME = "sender";

    public static final String LAST_TIME = "timestamp";

    public static final String  ADDRESS = "address";


    // TODO define column names, getters for cursors, setters for contentvalues

    public static int name = -1;
    private static int lastTime = -1;
    private static int address = -1;

    public static String getName(Cursor cursor) {
        if (name < 0) {
            name = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(name);
    }

    public static void putName(ContentValues out, String sender) {
        out.put(NAME, sender);
    }
    public static Long getLastTime(Cursor cursor) {
        if (lastTime < 0) {
            lastTime = cursor.getColumnIndexOrThrow(LAST_TIME);
        }
        return cursor.getLong(lastTime);
    }

    public static void putLastTime(ContentValues out, Long lastTime) {
        out.put(LAST_TIME, lastTime);
    }
    public static String getAddress(Cursor cursor) {
        if (address < 0) {
            address = cursor.getColumnIndexOrThrow(ADDRESS);
        }
        return cursor.getString(address);
    }

    public static void putAddress(ContentValues out, String address) {
        out.put(ADDRESS, address);
    }

}
