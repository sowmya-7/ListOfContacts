package com.contacts.displayphonecontacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;


import java.util.ArrayList;
import java.util.List;




public class ContactsLocalDataSource implements ContactsDataSource, ContactsLoader.ContactsLoaderListener {

    private static final String TAG = "ContactsLocalDataSource";
    public static boolean loadContactIsProgress = false;
    private static ContactsLocalDataSource INSTANCE;
    private Context mContext;
    private ContactsLoader contactsLoader = null;
    private LoaderManager loaderManager = null;
    private LoadContactsCallback mLoadContactsCallback = null;
    private List<Contact> contacts = new ArrayList<Contact>();

    protected ContactsLocalDataSource(@NonNull Context context) {
        mContext = context;
    }

    public static ContactsLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ContactsLocalDataSource(context);
        }
        return INSTANCE;
    }


    public void setLoaderManager(LoaderManager loaderManager) {
        this.loaderManager = loaderManager;
    }

    public void initContactsLoader(Context context) {
        contactsLoader = new ContactsLoader(context, this);
    }


    @Override
    public void getContacts(@NonNull LoadContactsCallback callback, String searchVal) {
        contacts.clear();
        if (mLoadContactsCallback != null)
            mLoadContactsCallback = null;
        mLoadContactsCallback = callback;

        if (loaderManager != null && contactsLoader != null)
            contactsLoader.loadDeviceContacts(loaderManager, searchVal);
        else
            Log.e(TAG, "getContacts. Please set LoaderManger!!!");
        if (contacts.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onContactsLoaded(contacts);
        }

    }

    @Override
    public void resetData() {
        if (mLoadContactsCallback != null)
            mLoadContactsCallback = null;

        if (loaderManager != null)
            loaderManager = null;

        contacts.clear();
        loadContactIsProgress = false;
    }


    @Override
    public void onDeviceContactLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mLoadContactsCallback != null && data != null && !loadContactIsProgress) {
            mLoadContactsCallback.onContactsLoaded(ParseCursorData(data));
        }
    }

    @Override
    public void onDeviceContactLoaderReset(Loader<Cursor> loader) {

    }

    private synchronized List<Contact> ParseCursorData(final Cursor cursor) {


        String oldcontactId = ""; //used to check multiple contacts are not added for same Id.
        Contact contact = null;
        List<Contact> contactList = new ArrayList<>();
        if (cursor != null && !cursor.isClosed()) {
            if (cursor.getCount() > 0) {
                contacts.clear();

                if (cursor.isClosed()) {
                    return contactList;
                }

                while (cursor.moveToNext()) {

                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                    String lookUpKey = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.LOOKUP_KEY));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String mimetype = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE));

                    String value = "";

                    if (mimetype.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE) || mimetype.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                        value = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DATA1));
                        if (!TextUtils.isEmpty(value)) {
                            boolean isValueANumber = Utils.isValidPhoneNumber(value);
                            if (!contactId.equalsIgnoreCase(oldcontactId)) {
                                contact = new Contact.ContactBuilder(contactId)
                                        .displayNamePrimary(displayName)
                                        .phoneNumber(isValueANumber ? value : "")
                                        .emailAddress(isValueANumber ? "" : value)
                                        .build();
                                contacts.add(contact);
                                oldcontactId = contactId;
                            } else {
                                if (contact != null && TextUtils.isEmpty(contact.getPhoneNumber()) && Utils.isValidPhoneNumber(value)) {
                                    Contact contactLatest = null;
                                    contactLatest = new Contact.ContactBuilder(contactId)
                                            .displayNamePrimary(displayName)
                                            .phoneNumber(isValueANumber ? value : "")
                                            .emailAddress(isValueANumber ? "" : value).build();
                                    contacts.remove(contact);
                                    contacts.add(contactLatest);
                                    oldcontactId = contactId;
                                }
                            }


                        }

                    }
                }
            }
        }

        loadContactIsProgress = false;

        if (cursor != null) {
            cursor.close();
        }

        return contacts;
    }
}