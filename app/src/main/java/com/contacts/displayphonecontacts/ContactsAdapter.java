package com.contacts.displayphonecontacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;




public class ContactsAdapter extends RecyclerView.Adapter<ContactsViewHolder> {

    public List<Contact> contactsList = new ArrayList<>();
    private Context mContext;

    public ContactsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ContactsViewHolder(inflater.inflate(R.layout.item_contact, parent, false), mContext);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {

        if (contactsList.size() > 0) {
            holder.bind( contactsList.get(position));
            holder.itemView.setTag(position);
        }
    }



    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public List<Contact> getItems() {
        return contactsList;
    }


    public synchronized void updateContactList(List<Contact> list) {
        if (list != null) {
            this.contactsList.clear();
            this.contactsList.addAll(list);
            this.notifyDataSetChanged();
        }
    }



    public interface ContactsAdapterListener {


        void onCallContactAudioClicked(Contact contact);

        void onMessageContactClicked(Contact contact);

        void onRemoveContactDetails();
    }

}
