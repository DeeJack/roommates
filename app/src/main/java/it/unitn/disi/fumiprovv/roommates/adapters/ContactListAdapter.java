package it.unitn.disi.fumiprovv.roommates.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Contact;

public class ContactListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Contact> contacts;

    public ContactListAdapter(Context context, List<Contact> contacts) {
        this.contacts = contacts;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Contact getItem(int i) {
        return this.contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contact_list_item, parent, false);

            holder = new ViewHolder();
            holder.contactNameField = convertView.findViewById(R.id.contactNameField);
            holder.contactNumField = convertView.findViewById(R.id.contactNumField);
            holder.callButton = convertView.findViewById(R.id.callButton);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact item = contacts.get(position);
        holder.contactNameField.setText(item.getName());
        holder.contactNumField.setText(item.getNumber());
        holder.callButton.setOnClickListener(view -> onCallButtonClick(item.getNumber()));
        holder.deleteButton.setOnClickListener(view -> onDeleteButtonClick(item));

        return convertView;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    private void onCallButtonClick(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    private void onDeleteButtonClick(Contact contact) {
        // Create alert dialog to confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_note_title);
        builder.setMessage(R.string.delete_note_message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            // Delete note from database
            db.collection("contatti").document(contact.getId()).delete();
            contacts.remove(contact);
            notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancella", (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    private static class ViewHolder {
        TextView contactNameField;
        TextView contactNumField;
        ImageButton callButton;
        ImageView deleteButton;
    }
}
