package com.contacts.displayphonecontacts;



import androidx.annotation.NonNull;

import java.util.List;

/**
 * Main entry point for accessing contacts data.
 * <p>
 * For simplicity, only getTasks() and getTask() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface ContactsDataSource {

    void getContacts(@NonNull LoadContactsCallback callback, String searchVal);

    void resetData();

    interface LoadContactsCallback {

        void onContactsLoaded(List<Contact> contacts);

        void onDataNotAvailable();
    }
}
