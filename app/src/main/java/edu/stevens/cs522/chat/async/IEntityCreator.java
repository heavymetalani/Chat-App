package edu.stevens.cs522.chat.async;

import android.database.Cursor;



public interface IEntityCreator<T> {

    public T create(Cursor cursor);

}

