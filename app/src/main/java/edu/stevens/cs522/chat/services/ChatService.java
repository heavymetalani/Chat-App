package edu.stevens.cs522.chat.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.Process;
import android.os.ResultReceiver;
import android.renderscript.RenderScript;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.activities.ChatActivity;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;

import static android.app.Activity.RESULT_OK;


public class ChatService extends Service implements IChatService {

    protected static final String TAG = ChatService.class.getCanonicalName();

    protected static final String SEND_TAG = "ChatSendThread";

    protected static final String RECEIVE_TAG = "ChatReceiveThread";

    protected IBinder binder = new ChatBinder();

    protected SendHandler sendHandler;

    protected Thread receiveThread;

    protected HandlerThread handler;

    protected DatagramSendReceive chatSocket;

    protected boolean socketOK = true;

    protected boolean finished = false;

    protected PeerManager peerManager;

    protected MessageManager messageManager;

    protected int chatPort;

    @Override
    public void onCreate() {

        chatPort = this.getResources().getInteger(R.integer.app_port);
        Log.i(ChatActivity.TAG, "Service on Create called (service gets created) ");
        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);

        try {
            chatSocket = new DatagramSendReceive(chatPort);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to init client socket.", e);
        }

        // TODO initialize the thread that sends messages
        handler = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        handler.start();
        Looper handlerLooper = handler.getLooper();
        sendHandler = new SendHandler(handlerLooper);

        // end TODO

        receiveThread = new Thread(new ReceiverThread());
        receiveThread.start();
    }

    @Override
    public void onDestroy() {
        finished = true;
        sendHandler.getLooper().getThread().interrupt();  // No-op?
        sendHandler.getLooper().quit();
        receiveThread.interrupt();
        chatSocket.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(ChatActivity.TAG, "onBind called ");
        return binder;
    }

    public final class ChatBinder extends Binder {

        public IChatService getService() {
            Log.i(ChatActivity.TAG, "get service called");
            return ChatService.this;
        }

    }

    @Override
    public void send(InetAddress destAddress, int destPort, String sender, String messageText, ResultReceiver receiver) {
        android.os.Message message = sendHandler.obtainMessage();
        // TODO send the message to the sending thread (add a bundle with params)
        Bundle data = new Bundle();
        data.putString("destAddress",destAddress.toString());
        data.putInt("destPort",destPort);
        data.putString("sender", sender);
        data.putString("messageText", messageText);
        data.putParcelable("resultReciever", receiver);
        message.setData(data);
        sendHandler.sendMessage(message);
    }


    private final class SendHandler extends Handler {

        public static final String CHAT_NAME = "edu.stevens.cs522.chat.services.extra.CHAT_NAME";
        public static final String CHAT_MESSAGE = "edu.stevens.cs522.chat.services.extra.CHAT_MESSAGE";
        public static final String DEST_ADDRESS = "edu.stevens.cs522.chat.services.extra.DEST_ADDRESS";
        public static final String DEST_PORT = "edu.stevens.cs522.chat.services.extra.DEST_PORT";
        public static final String RECEIVER = "edu.stevens.cs522.chat.services.extra.RECEIVER";

        public SendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message message) {

            try {
                InetAddress destAddr;

                int destPort;

                byte[] sendData;  // Combine sender and message text; default encoding is UTF-8

                ResultReceiver receiver;

                Bundle data = message.getData();

                // TODO get data from message (including result receiver)

                destAddr = InetAddressUtils.fromString((data.getString("destAddress")).substring(1));
                destPort = data.getInt("destPort");
                String messageText = data.getString("messageText");
                String sender = data.getString("sender");
                receiver = data.getParcelable("resultReciever");
                String dataString = sender +":"+messageText;
                sendData = dataString.getBytes();
                // End todo

                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, destAddr, destPort);

                chatSocket.send(sendPacket);

                Log.i(TAG, "Sent packet: " + messageText);

                receiver.send(RESULT_OK, null);


            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host exception", e);
            } catch (IOException e) {
                Log.e(TAG, "IO exception", e);
            }

        }
    }

    private final class ReceiverThread implements Runnable {

        public void run() {

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (!finished && socketOK) {

                try {

                    chatSocket.receive(receivePacket);
                    Log.i(TAG, "Received a packet");

                    InetAddress sourceIPAddress = receivePacket.getAddress();
                    Log.i(TAG, "Source IP Address: " + sourceIPAddress);

                    String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

                    final Message message = new Message();
                    message.sender = msgContents[0];
                    message.timestamp = Calendar.getInstance().getTime();
                    message.messageText = msgContents[1];

                    Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

                    Peer sender = new Peer();
                    sender.name = message.sender;
                    sender.timestamp = message.timestamp;
                    sender.address = receivePacket.getAddress();
                    sender.tempaddress = sender.address.toString();

                    // TODO upsert the peer and message into the content provider.
                    // For this assignment, must use synchronous manager operations
                    // (no callbacks) because now we are on a background thread
                    messageManager.persist(message);
                    peerManager.persist(sender);



                } catch (Exception e) {

                    Log.e(TAG, "Problems receiving packet.", e);
                    socketOK = false;
                }

            }

        }

    }

}
