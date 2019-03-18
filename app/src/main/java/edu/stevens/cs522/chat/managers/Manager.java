package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.ISimpleQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.async.SimpleQueryBuilder;




public abstract class Manager<T> {

    protected final Context context;

    private final IEntityCreator<T> creator;

    protected final int loaderID;

    protected final String tag;

    protected Manager(Context context,
                      IEntityCreator<T> creator,
                      int loaderID) {
        this.context = context;
        this.creator = creator;
        this.loaderID = loaderID;
        this.tag = this.getClass().getCanonicalName();
    }

    private ContentResolver syncResolver;

    private AsyncContentResolver asyncResolver;

    protected ContentResolver getSyncResolver() {
        if (syncResolver == null)
            syncResolver = context.getContentResolver();
        return syncResolver;
    }

    protected AsyncContentResolver getAsyncResolver() {
        if (asyncResolver == null)
            asyncResolver = new AsyncContentResolver(context.getContentResolver());
        return asyncResolver;
    }


    protected void executeSimpleQuery(Uri uri,
                                      ISimpleQueryListener<T> listener) {
        // TODO
        SimpleQueryBuilder.executeQuery(this.tag, (Activity) this.context, uri,this.creator,listener);
    }

    protected void executeSimpleQuery(Uri uri,
                                      String[] projection,
                                      String selection,
                                      String[] selectionArgs,
                                      ISimpleQueryListener<T> listener) {
        // TODO
        SimpleQueryBuilder.executeQuery(this.tag,(Activity)this.context, uri, projection,selection,selectionArgs,this.creator,listener);
    }

    protected void executeQuery(Uri uri,
                                IQueryListener<T> listener) {
        // TODO
        QueryBuilder.executeQuery(this.tag,(Activity)this.context,uri,this.loaderID,this.creator, listener);
    }

    protected void executeQuery(Uri uri,
                                String[] projection,
                                String selection,
                                String[] selectionArgs,
                                String order,
                                IQueryListener<T> listener) {
        QueryBuilder.executeQuery(this.tag,(Activity) this.context,uri,projection,selection,selectionArgs,order,this.loaderID, this.creator,listener);
    }

    protected void reexecuteQuery(Uri uri,
                                  IQueryListener<T> listener) {
        QueryBuilder.reexecuteQuery(this.tag,(Activity)this.context,uri,this.loaderID,this.creator,listener);
    }

    protected void reexecuteQuery(Uri uri,
                                  String[] projection,
                                  String selection,
                                  String[] selectionArgs,
                                  String order,
                                  IQueryListener<T> listener) {
        // TODO
        QueryBuilder.reexecuteQuery(this.tag,(Activity)this.context,uri,projection,selection,selectionArgs,order,this.loaderID,this.creator,listener);
    }

}
