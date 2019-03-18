package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.TypedCursor;



public class ViewPeerActivity extends Activity implements IQueryListener<Message> {

    public static final String PEER_KEY = "peer";

    private SimpleCursorAdapter messageAdapter;

    private MessageManager messageManager;

    private TextView m1;
    private TextView m2;
    private TextView m3;
    private ListView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO init the UI
        m1= (TextView) findViewById(R.id.view_user_name);
        m2= (TextView) findViewById(R.id.view_timestamp);
        m3= (TextView) findViewById(R.id.view_address);
        m1.setText(peer.name);
        m2.setText(peer.timestamp.toString());
        m3.setText(peer.tempaddress);
        messageList = (ListView) findViewById(R.id.view_messages);
        messageAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1
                ,null, new String[]{MessageContract.MESSAGE_TEXT},new int[]{android.R.id.text1},0);
        messageList.setAdapter(messageAdapter);
        messageManager = new MessageManager(this);
        messageManager.getMessagesByPeerAsync(peer,this);

    }

    @Override
    public void handleResults(TypedCursor<Message> results) {
        messageAdapter.swapCursor(results.getCursor());
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        messageAdapter.swapCursor(null);
        messageAdapter.notifyDataSetChanged();
    }
}
