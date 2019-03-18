package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Persistable;


public class Message implements Parcelable, Persistable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message(Cursor cursor) {
        // TODO
        this.sender = MessageContract.getSender(cursor);
        this.messageText= MessageContract.getMessageText(cursor);
        this.timestamp = new Date(MessageContract.getTimestamp(cursor));
    }

    public Message(Parcel in) {
        // TODO
        this.sender=in.readString();
        this.timestamp=new Date(in.readLong());
        this.messageText=in.readString();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        MessageContract.putMessageText(out, this.messageText);
        MessageContract.putTimestamp(out,this.timestamp.getTime());
        MessageContract.putSender(out,this.sender);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeString(sender);
        dest.writeLong(timestamp.getTime());
        dest.writeString(messageText);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            // TODO
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            // TODO
            return new Message[size];
        }

    };

}

