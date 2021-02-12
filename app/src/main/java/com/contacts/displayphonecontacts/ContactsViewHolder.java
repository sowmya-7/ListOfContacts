package com.contacts.displayphonecontacts;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;





public class ContactsViewHolder extends RecyclerView.ViewHolder{

    TextView txtContactName;
    TextView txtContactNumber;

    private Context mContext;


    public ContactsViewHolder(View itemView,  Context mContext) {
        super(itemView);
        txtContactName = itemView.findViewById(R.id.txtContactName);
        txtContactNumber = itemView.findViewById(R.id.txtContactNumber);
        this.mContext = mContext;
    }

    public void bind(final Contact model) {
        if (model == null)
            return;

        if (!TextUtils.isEmpty(model.getDisplayName())) {
            txtContactName.setText(model.getDisplayName());
        }else {
            if (!TextUtils.isEmpty(model.getPhoneNumber())) {
                txtContactName.setText(model.getPhoneNumber());
            }
        }
        if (!TextUtils.isEmpty(model.getPhoneNumber())) {
                txtContactNumber.setText(model.getPhoneNumber());
            }
        else if(!TextUtils.isEmpty(model.getEmailAddress())){
            txtContactNumber.setText(model.getEmailAddress());
        }
        }
}
