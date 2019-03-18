package edu.stevens.cs522.chat.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import edu.stevens.cs522.chat.activities.ChatActivity;



public class ResultReceiverWrapper extends ResultReceiver {

    public ResultReceiverWrapper(Handler handler) {
        super(handler);
    }

    public interface IReceive {
        public void onReceiveResult(int resultCode, Bundle data);
    }

    protected IReceive receiver;

    public void setReceiver(IReceive receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        Log.i(ChatActivity.TAG, "on Recieve result on wrapper ");
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, data);
        }
    }
}
