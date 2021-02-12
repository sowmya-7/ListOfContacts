package com.contacts.displayphonecontacts;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class ContactsLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int DEVICE_CONTACTS_LOADER_ID = 0;
    public static final int FAV_CONTACTS_LOADER_ID = 1;
    public static final int CLOUD_CONTACTS_LOADER_ID = 2;
    public static final int CLOUD_CONTACTS_INIT_LOADER_ID = 3;
    private ContactsLoaderListener listener;
    private Context context;
    private String searchTerm;

    public ContactsLoader(Context context, ContactsLoaderListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void loadDeviceContacts(LoaderManager loaderManager, String searchTerm) {
        load(loaderManager, DEVICE_CONTACTS_LOADER_ID, searchTerm);
    }

    public void loadFavoriteContacts(LoaderManager loaderManager, String searchTerm) {
        load(loaderManager, FAV_CONTACTS_LOADER_ID, searchTerm);
    }

    public void loadCloudContacts(LoaderManager loaderManager, String searchTerm) {
        load(loaderManager, CLOUD_CONTACTS_LOADER_ID, searchTerm);
    }

    public void loadInitialCloudContacts(LoaderManager loaderManager, String searchTerm) {
        load(loaderManager, CLOUD_CONTACTS_INIT_LOADER_ID, searchTerm);
    }

    private void load(LoaderManager loaderManager, int loaderId, String searchTerm) {
        this.searchTerm = searchTerm;
//        if (!PermissionsUtil.getInstance().hasReadWriteContactPermission()) {
//            FileLogUtils.i("ContactsLoader", " Does not have read/write contact permission");
//        } else {
            loaderManager.restartLoader(loaderId, null, this);
//        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String accountType = "";

        if (id == DEVICE_CONTACTS_LOADER_ID) {
            StringBuilder selection = new StringBuilder("");
            if (!TextUtils.isEmpty(searchTerm)) {
                if ((searchTerm.matches("\\d+(?:\\.\\d+)?"))) {
                    selection = selection.append("( " + Contacts.DEVICE_CONTACTS_SELECTION_SEARCH_PHONE + " )");
                    String[] phoneSelectionArgs = new String[]{accountType, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, "%" + searchTerm + "%", "%" + searchTerm + "%",
                            "%" + searchTerm + "%"};
                    return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, selection.toString(), phoneSelectionArgs, Contacts.SORT_ORDER);

                } else {
                    String[] searchSelectionArgs = new String[]{accountType, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                    selection = selection.append("( " + getContactNameSearchSelection(searchTerm) + " )");
                    return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, selection.toString(), searchSelectionArgs, Contacts.SORT_ORDER);
                }
            } else {
                selection = selection.append("( " + Contacts.DEVICE_CONTACTS_SELECTION + " )");
                String[] selectionArgs = new String[]{accountType, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
                return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, selection.toString(), selectionArgs, Contacts.SORT_ORDER);
            }
        } else if (id == FAV_CONTACTS_LOADER_ID) {
            StringBuilder selection = new StringBuilder("");
            selection = selection.append(ContactsContract.Contacts.STARRED + " = 1 AND ");
            if (!TextUtils.isEmpty(searchTerm)) {
                if ((searchTerm.matches("\\d+(?:\\.\\d+)?"))) {
                    selection = selection.append("( " + Contacts.DEVICE_CONTACTS_SELECTION_SEARCH_PHONE + " )");
                    String[] phoneSelectionArgs = new String[]{"", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, "%" + searchTerm + "%", "%" + searchTerm + "%",
                            "%" + searchTerm + "%"};
                    return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, selection.toString(), phoneSelectionArgs, Contacts.SORT_ORDER);

                } else {
                    String[] searchSelectionArgs = new String[]{"", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                    selection = selection.append("( " + getContactNameSearchSelection(searchTerm) + " )");
                    return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, selection.toString(), searchSelectionArgs, Contacts.SORT_ORDER);
                }
            } else {
                selection = selection.append("( " + Contacts.DEVICE_CONTACTS_SELECTION + " )");
                String[] selectionArgs = new String[]{"", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, selection.toString(), selectionArgs, Contacts.SORT_ORDER);
            }
        } else if (id == CLOUD_CONTACTS_LOADER_ID || id == CLOUD_CONTACTS_INIT_LOADER_ID) {
            String[] searchSelectionArgs = new String[]{accountType, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
            if (!TextUtils.isEmpty(searchTerm)) {
                if ((searchTerm.matches("\\d+(?:\\.\\d+)?"))) {
                    String[] phoneSelectionArgs = new String[]{accountType, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, "%" + searchTerm + "%", "%" + searchTerm + "%"};
                    return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, Contacts.CLOUD_CONTACTS_SELECTION_SEARCH_PHONE, phoneSelectionArgs, Contacts.SORT_ORDER);
                } else
                    return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, getCloudContactNameSearchSelection(searchTerm), searchSelectionArgs, Contacts.SORT_ORDER);
            } else {
                String[] selectionArgs = new String[]{accountType, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE , ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
                return new CursorLoader(context, ContactsContract.Data.CONTENT_URI, Contacts.CONTACTS_PROJECTION, Contacts.CLOUD_CONTACTS_SELECTION, selectionArgs, Contacts.SORT_ORDER);
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DEVICE_CONTACTS_LOADER_ID:
                listener.onDeviceContactLoadFinished(loader, data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DEVICE_CONTACTS_LOADER_ID:
                listener.onDeviceContactLoaderReset(loader);
                break;
            default:
                break;
        }
    }

    public String getContactNameSearchSelection(String searchTerm) {
        String selection = "";
        String commonSelection = /*ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND " +*/ ContactsContract.RawContacts.ACCOUNT_TYPE +
                " IS NOT ? AND ( " + ContactsContract.Data.MIMETYPE + " =? OR "+ ContactsContract.Data.MIMETYPE +"=? ) AND ";
        if (searchTerm.contains(" ")) {

            String[] searchKeys = searchTerm.split(" ");
            for (int i = 0; i < searchKeys.length; i++) {
                selection += "(" + ContactsContract.Contacts.DISPLAY_NAME
                        + " LIKE \"%" + searchKeys[i] + "%\""
                        + " OR " + "REPLACE(" + ContactsContract.Contacts.DISPLAY_NAME + ", ' ','')"
                        + " LIKE \"%" + searchKeys[i] + "%\")";
                if (i != searchKeys.length - 1) {
                    selection += " AND ";
                }
            }
        } else {
            selection = "("
                    + ContactsContract.Contacts.DISPLAY_NAME + " LIKE \"%" + searchTerm + "%\""
                    + " OR " + "REPLACE(" + ContactsContract.Contacts.DISPLAY_NAME + ", ' ','')" + " LIKE \"%" +
                    searchTerm + "%\"" + " OR " + ContactsContract.CommonDataKinds.Email.ADDRESS + " LIKE \"%" + searchTerm + "%\")";
        }


        return commonSelection + selection;
    }

    public String getCloudContactNameSearchSelection(String searchTerm) {
        String selection = "";
        String commonSelection = /*ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND " +*/ ContactsContract.RawContacts.ACCOUNT_TYPE +
                " =? AND ( " + ContactsContract.Data.MIMETYPE + " =? OR "+ ContactsContract.Data.MIMETYPE +"=? ) AND";
        if (searchTerm.contains(" ")) {

            String[] searchKeys = searchTerm.split(" ");
            for (int i = 0; i < searchKeys.length; i++) {
                selection += "(" + ContactsContract.Contacts.DISPLAY_NAME
                        + " LIKE \"%" + searchKeys[i] + "%\""
                        + " OR " + "REPLACE(" + ContactsContract.Contacts.DISPLAY_NAME + ", ' ','')"
                        + " LIKE \"%" + searchKeys[i] + "%\")";
                if (i != searchKeys.length - 1) {
                    selection += " AND ";
                }
            }
        } else {
            selection = "("
                    + ContactsContract.Contacts.DISPLAY_NAME + " LIKE \"%" + searchTerm + "%\""
                    + " OR " + "REPLACE(" + ContactsContract.Contacts.DISPLAY_NAME + ", ' ','')" + " LIKE \"%" +
                    searchTerm + "%\"" + " OR " + ContactsContract.CommonDataKinds.Email.ADDRESS + " LIKE \"%" + searchTerm + "%\")";
        }


        return commonSelection + selection;
    }

    public interface ContactsLoaderListener {
        void onDeviceContactLoadFinished(Loader<Cursor> loader, Cursor data);

        void onDeviceContactLoaderReset(Loader<Cursor> loader);
    }


    private interface Contacts {

        public static final String[] CONTACTS_PROJECTION = {
                ContactsContract.Data.CONTACT_ID, ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1, ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI, ContactsContract.Contacts.STARRED, ContactsContract.Data.LOOKUP_KEY
        };

        public static final String DEVICE_CONTACTS_SELECTION =
                /*ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND " +*/ ContactsContract.RawContacts.ACCOUNT_TYPE +
                " IS NOT ? AND ( " + ContactsContract.Data.MIMETYPE + " =? OR "+ ContactsContract.Data.MIMETYPE +"=? )";

        public static final String DEVICE_CONTACTS_SELECTION_SEARCH =
                ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " IS NOT ? AND "
                        + ContactsContract.Data.MIMETYPE + " =? AND ("
                        + ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? "
                        + " OR " + "REPLACE(" + ContactsContract.Contacts.DISPLAY_NAME + ", ' ','')" + " LIKE ? )";

        public static final String DEVICE_CONTACTS_SELECTION_SEARCH_PHONE =
               /* ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND " +*/ ContactsContract.RawContacts.ACCOUNT_TYPE +
                " IS NOT ? AND ( " + ContactsContract.Data.MIMETYPE + " =? OR "+ ContactsContract.Data.MIMETYPE +"=? ) AND "
                        + "(" + ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " LIKE ? OR " + ContactsContract.Data.DATA1 + " LIKE ? OR " + ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? )";


        public static final String CLOUD_CONTACTS_SELECTION =
                /*ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND " +*/ ContactsContract.RawContacts.ACCOUNT_TYPE +
                " =? AND ( " + ContactsContract.Data.MIMETYPE + " =? OR "+ ContactsContract.Data.MIMETYPE +"=? )";

        public static final String CLOUD_CONTACTS_SELECTION_SEARCH =
                ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " =? AND " + ContactsContract.Data.MIMETYPE + " =? AND ("
                        + ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? "
                        + " OR " + "REPLACE(" + ContactsContract.Contacts.DISPLAY_NAME + ", ' ','')" + " LIKE ? )";

        public static final String CLOUD_CONTACTS_SELECTION_SEARCH_PHONE =
               /* ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND " +*/ ContactsContract.RawContacts.ACCOUNT_TYPE +
                "  =? AND ( " + ContactsContract.Data.MIMETYPE + " =? OR "+ ContactsContract.Data.MIMETYPE +" =? ) AND "
                        + "(" + ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER + " LIKE ? OR " + ContactsContract.Data.DATA1 + " LIKE ? )";

        //public static final String SORT_ORDER = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        public static final String SORT_ORDER = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
    }
}
