package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Peer;




public class PeerManager extends Manager<Peer> {

    private static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder.executeQuery(this.tag,(Activity)this.context, PeerContract.CONTENT_URI,new String[]{PeerContract._ID, PeerContract.NAME, PeerContract.LAST_TIME, PeerContract.ADDRESS},null,null,null,this.loaderID, this.creator, listener);
    }

    public void getPeerAsync(long id, IContinue<Peer> callback) {
        // TODO need to check that peer is not null (not in database)
    }

    public void persistAsync(Peer peer, final IContinue<Long> callback) {
        // TODO need to ensure the peer is not already in the database
        AsyncContentResolver cr = getAsyncResolver();
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        cr.insertAsync(PeerContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {
                callback.kontinue(PeerContract.getId(value));
            }
        });
    }

    public long persist(Peer peer) {
        // TODO synchronous version that executes on background thread (in service)
        ContentValues peerData = new ContentValues();
        peer.writeToProvider(peerData);
        Uri res = getSyncResolver().insert(PeerContract.CONTENT_URI, peerData);
        if (res == null){
            throw new UnsupportedOperationException("persist not implemented");
        }
        long i=1;
        return i;
    }

}
