package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;

public class UserAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<String> users;

    public UserAdapter(Context context, ArrayList<String> users) {
        this.users = users;
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

    public void addUser(String user) {
        this.users.add(user);
    }

    private static class ViewHolder {
        TextView userNameField;
    }
}
