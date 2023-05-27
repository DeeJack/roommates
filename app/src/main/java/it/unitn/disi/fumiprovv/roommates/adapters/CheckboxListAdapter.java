package it.unitn.disi.fumiprovv.roommates.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.ShoppingItem;
import it.unitn.disi.fumiprovv.roommates.models.User;

public class CheckboxListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Set<User> checkedItems = new HashSet<>();
    private List<User> items;

    public CheckboxListAdapter(Context context, ArrayList<User> items) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public User getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.checkbox_list_item, parent, false);

            holder = new ViewHolder();
            holder.itemCheckbox = convertView.findViewById(R.id.listItemCheckboxx);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User item = items.get(position);
        holder.itemCheckbox.setText(item.getName());

        holder.itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedItems.add(item);
            } else {
                checkedItems.remove(item);
            }
        });
        holder.itemCheckbox.setChecked(true);

        return convertView;
    }

    public Set<User> getCheckedItems() {
        return checkedItems;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }

    public void addItem(User shoppingItem) {
        items.add(shoppingItem);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        CheckBox itemCheckbox;
    }
}
