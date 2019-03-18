package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;



public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "sender";

    // TODO remaining columns in Messages table

    public static final String[] PROJECTION = {ID, MESSAGE_TEXT, TIMESTAMP, SENDER };

    private static int messageTextColumn = -1;
    private static int timeStamp = -1;
    private static int sender = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    // TODO remaining getter and putter operations for other columns
    public static Long getTimestamp(Cursor cursor){
        if (timeStamp < 0) {
            timeStamp = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getLong(timeStamp);
    }
    public static void putTimestamp(ContentValues out, Long timestamp) {
        out.put(TIMESTAMP, timestamp);
    }

    public static String getSender(Cursor cursor){
        if (sender < 0) {
            sender = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(sender);
    }
    public static void putSender(ContentValues out, String sender) {
        out.put(SENDER, sender);
    }
}
