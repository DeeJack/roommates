package it.unitn.disi.fumiprovv.roommates.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Note;

public class UserAdapter extends BaseAdapter {
    private List<String> users;
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserAdapter(Context context, ArrayList<String> users) {
        this.users = users;
        this.context = context;
        if (context != null)
            this.inflater = LayoutInflater.from(context);
        else
            this.inflater = null;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public String getItem(int i) {
        return this.users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_item, parent, false);

            holder = new UserAdapter.ViewHolder();
            holder.userNameField = convertView.findViewById(R.id.userNameField);

            convertView.setTag(holder);
        } else {
            holder = (UserAdapter.ViewHolder) convertView.getTag();
        }

        String item = users.get(position);

        holder.userNameField.setText(item);

        return convertView;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public void addUser(String user) {
        this.users.add(user);
    }

    private static class ViewHolder {
        TextView userNameField;
    }
}
