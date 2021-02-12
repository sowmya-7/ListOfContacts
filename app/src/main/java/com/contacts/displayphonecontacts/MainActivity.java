package com.contacts.displayphonecontacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ContactsDataSource.LoadContactsCallback{

    public static final String TAG = "DigitsContactsListFragm";

    RecyclerView mContactsRecyclerView;

    ContactsLocalDataSource contactsLocalDataSource;
    List<Contact> contacts = new ArrayList<Contact>();
    private int PERMISSIONS_REQUEST_READ_CONTACTS = 2;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "Refresh off");
            contactsAadapter.updateContactList(contacts);

        }
    };
    private ContactsAdapter contactsAadapter;
    private int LOCAL_CONTACTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        contactsLocalDataSource = ContactsLocalDataSource.getInstance(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mContactsRecyclerView.setLayoutManager(linearLayoutManager);
        contactsAadapter = new ContactsAdapter(this);
        mContactsRecyclerView.setAdapter(contactsAadapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            if (contactsLocalDataSource != null && !contactsLocalDataSource.loadContactIsProgress) {

                contactsLocalDataSource.resetData();

                contactsLocalDataSource.setLoaderManager(LoaderManager.getInstance(this));
                contactsLocalDataSource.initContactsLoader(this);

                contactsLocalDataSource.getContacts(this, "");
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                init();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactsLocalDataSource != null)
            contactsLocalDataSource.resetData();
        contacts.clear();
    }



    @Override
    public void onContactsLoaded(final List<Contact> contacts) {
        this.contacts = contacts;
        if (contacts != null) {
            Log.d(TAG, "contacts loaded " + contacts.size());
        } else {
            Log.e(TAG, "contacts null");
        }
        handler.sendEmptyMessage(LOCAL_CONTACTS);
    }


    @Override
    public void onDataNotAvailable() {
        Log.i(TAG, "datanotavailable");
        contacts.clear();
        contactsAadapter.updateContactList(contacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
