/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.Calendar;

import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.services.ChatService;
import edu.stevens.cs522.chat.services.IChatService;
import edu.stevens.cs522.chat.services.ResultReceiverWrapper;
import edu.stevens.cs522.chat.settings.Settings;

public class ChatActivity extends Activity implements OnClickListener, IQueryListener<Message>, ServiceConnection, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText destinationPort;

    private EditText messageText;

    private Button sendButton;


    /*
     * Reference to the service, for sending a message
     */
    private IChatService chatService;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Initialize settings to default values.
         */
        Settings.init(this);

        setContentView(R.layout.messages);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
        messageList = (ListView) findViewById(R.id.message_list);
        destinationHost = (EditText) findViewById(R.id.destination_host);
        destinationPort = (EditText) findViewById(R.id.destination_port);
        messageText = (EditText) findViewById(R.id.message_text);
        Log.i(TAG, "UI initialized ");
        // TODO use SimpleCursorAdapter to display the messages received.
        messagesAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1
                ,null, new String[]{MessageContract.MESSAGE_TEXT},new int[]{android.R.id.text1},0);
        messageList.setAdapter(messagesAdapter);
        Log.i(TAG, "Set messages adapter ");
        // TODO create the message and peer managers, and initiate a query for all messages
        messageManager = new MessageManager(this);
        peerManager =  new PeerManager(this);
        Log.i(TAG, "Initialzed message manager ");
        messageManager.getAllMessagesAsync(this);
        // TODO initiate binding to the service
        Log.i(TAG, "Pre service intent , post async query");
        Intent bindIntent = new Intent(this,ChatService.class);
        Log.i(TAG, "Post service intent ");
        Boolean bool = bindService(bindIntent,this, Context.BIND_AUTO_CREATE);
        if(bool){
            Log.i(TAG, "bool True");
        }else{
            Log.i(TAG, "Bool False");
        }
        Log.i(TAG, "Post service bound");
        // TODO initialize sendResultReceiver (for receiving notification of message sent)
        sendResultReceiver = new ResultReceiverWrapper(new Handler());


 /*       Message test = new Message();
        test.sender="Alison";
        test.timestamp = Calendar.getInstance().getTime();
        test.id=6;
        test.messageText="It is working now alison";
        Peer temp = new Peer();
        temp.name="Alison";
        temp.timestamp=Calendar.getInstance().getTime();
        temp.tempaddress = "10.0.2.3";
//        messageManager.persistAsync(test);
        peerManager.persistAsync(temp,new IContinue<Long>() {
            @Override
           public void kontinue(Long value) {
            }
        });*/


        Log.i(TAG, "On create khatam");
    }

	public void onResume() {
        super.onResume();
        // TODO register result receiver
        Log.i(TAG, "onResume result reciever");
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        // TODO unregister result receiver
        Log.i(TAG, "onPause result reciever");
        sendResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODO PEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent intent= new Intent(this, ViewPeersActivity.class);
                startActivity(intent);
                break;

            // TODO SETTINGS provide the UI for settings
            case R.id.settings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        Log.i(TAG, "Implementing Onclick ");
        if (chatService != null) {
            /*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

            InetAddress destAddr;

            int destPort;

            String username;

            String message;

            // Get destination and message from UI, and username from preferences.
            destAddr = InetAddressUtils.fromString(destinationHost.getText().toString());
            destPort = Integer.parseInt(destinationPort.getText().toString());
            message = messageText.getText().toString();

            // Get username from default shared preferences (see PreferenceManager, SettingsActivity)
            username = Settings.getChatName(this);

            // TODO use chatService to send the message
            if(chatService!=null){
                chatService.send(destAddr,destPort,username,message,sendResultReceiver);
            }


            Log.i(TAG, "Sent message: " + message);

            messageText.setText("");
        }
    }

    @Override
    /**
     * Show a text message when notified that sending a message succeeded or failed
     */
    public void onReceiveResult(int resultCode, Bundle data) {
        Log.i(TAG, "On receive result activity ");
        switch (resultCode) {
            case RESULT_OK:
                // TODO show a success toast message
                Toast toast = Toast.makeText(getApplicationContext(), "Message sent successfully.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                break;
            default:
                // TODO show a failure toast message
                Toast toast2 = Toast.makeText(getApplicationContext(), "Message failed to send.", Toast.LENGTH_SHORT);
                toast2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast2.show();
                break;
        }
    }

    @Override
    public void handleResults(TypedCursor<Message> results) {
        // TODO
        Log.i(TAG, "message query has come ");
        messagesAdapter.swapCursor(results.getCursor());
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        // TODO
        messagesAdapter.swapCursor(null);
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // TODO initialize chatService
        Log.i(TAG, "Service Connected ");
        chatService = ((ChatService.ChatBinder)service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, "Service Disconnected ");
        chatService = null;
    }
}