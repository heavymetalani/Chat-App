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
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;




public class MessageManager extends Manager<Message> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<Message> creator = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };


    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllMessagesAsync(IQueryListener<Message> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder.executeQuery(this.tag,(Activity)this.context, MessageContract.CONTENT_URI,MessageContract.PROJECTION,null,null,null,this.loaderID, this.creator, listener);
    }

    public void getAllMessagesAgainASync (IQueryListener<Message> listener){
        QueryBuilder.reexecuteQuery(this.tag,(Activity)this.context,MessageContract.CONTENT_URI,MessageContract.PROJECTION,null,null,null,this.loaderID, this.creator, listener);
    }

    public void getMessagesByPeerAsync(Peer peer, IQueryListener<Message> listener) {
        // TODO use QueryBuilder to complete this
        // Remember to reset the loader!
        QueryBuilder.reexecuteQuery(this.tag,(Activity)this.context,MessageContract.CONTENT_URI, MessageContract.PROJECTION," sender = ? ",new String[]{peer.name}, null, this.loaderID, this.creator, listener);
    }

    public void persistAsync(final Message Message) {
        // TODO
        ContentValues values = new ContentValues();
        Message.writeToProvider(values);
        AsyncContentResolver cr = getAsyncResolver();
        cr.insertAsync(MessageContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {
                Message.senderId = MessageContract.getId(value);
            }
        });
    }

    public long persist(Message message) {
        // Synchronous version, executed on background thread
        ContentValues messageData = new ContentValues();
        message.writeToProvider(messageData);
        Uri res = getSyncResolver().insert(MessageContract.CONTENT_URI, messageData);
        if(res == null){
            throw new UnsupportedOperationException("persist not implemented");
        }
        long i=1;
        return i;
    }


}
