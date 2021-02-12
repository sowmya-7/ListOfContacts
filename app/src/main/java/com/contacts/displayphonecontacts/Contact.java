package com.contacts.displayphonecontacts;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.List;



public class Contact {
    private final String mId; // required
    private final String mDisplayNamePrimary;
    private final String mPhoneNumber;
    private final String mEmail;


    private Contact(ContactBuilder builder) {
        mId = builder.mId;
        mDisplayNamePrimary = builder.mDisplayNamePrimary;
        mPhoneNumber = builder.mPhoneNumber;
        mEmail = builder.mEmail;
    }

    public String getId() {
        return mId;
    }


    public String getDisplayName() {
        return mDisplayNamePrimary;
    }


    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getEmailAddress() {
        return mEmail;
    }


    public static class ContactBuilder {
        private final String mId;
        private String mDisplayNamePrimary;
        private String mPhoneNumber;
        private String mEmail;

        public ContactBuilder(String id) {
            mId = id;
        }


        public ContactBuilder displayNamePrimary(String displayNamePrimary) {
            mDisplayNamePrimary = displayNamePrimary;
            return this;
        }

        public ContactBuilder emailAddress(String emailAddress) {
            mEmail = emailAddress;
            return this;
        }

        public ContactBuilder phoneNumber(String phoneNumber) {
            mPhoneNumber = phoneNumber;
            return this;
        }

        public Contact build() {
            Contact contact = new Contact(this);
            return contact;
        }

    }

}