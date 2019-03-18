package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Persistable;



public class Peer implements Parcelable, Persistable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public InetAddress address;
    public String tempaddress;

    public Peer() {
    }

    public Peer(Cursor cursor) {
        // TODO
        this.name = PeerContract.getName(cursor);
        this.timestamp = new Date(PeerContract.getLastTime(cursor));
        this.tempaddress = PeerContract.getAddress(cursor);
    }

    public Peer(Parcel in) {
        // TODO
        this.name = in.readString();
        this.timestamp=new Date(in.readLong());

        this.tempaddress=in.readString();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        PeerContract.putName(out,this.name);
        PeerContract.putLastTime(out, this.timestamp.getTime());
        PeerContract.putAddress(out,this.tempaddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO
        out.writeString(this.name);
        out.writeLong(this.timestamp.getTime());
        out.writeString(this.tempaddress);
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            // TODO
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            // TODO
            return new Peer[size];
        }

    };
}
