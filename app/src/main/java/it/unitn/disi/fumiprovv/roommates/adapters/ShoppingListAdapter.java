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
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.ShoppingItem;

public class ShoppingListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ArrayList<ShoppingItem> checkedItems = new ArrayList<>();
    private List<ShoppingItem> items;
    private List<ViewHolder> itemsHolders = new ArrayList<>();

    public ShoppingListAdapter(Context context, ArrayList<ShoppingItem> items) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ShoppingItem getItem(int i) {
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
            convertView = inflater.inflate(R.layout.shopping_list_item, parent, false);

            holder = new ViewHolder();
            holder.itemCheckbox = convertView.findViewById(R.id.itemCheckbox);
            holder.deleteItemButton = convertView.findViewById(R.id.deleteItemButton);
            itemsHolders.add(holder);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShoppingItem item = items.get(position);
        holder.itemCheckbox.setText(item.getName());
        holder.itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkedItems.add(item);
            } else {
                checkedItems.remove(item);
            }
        });
        holder.deleteItemButton.setOnClickListener(view -> onDeleteButtonClick(item));

        return convertView;
    }

    public ArrayList<ShoppingItem> getCheckedItems() {
        return checkedItems;
    }

    public void setItems(List<ShoppingItem> items) {
        this.items = items;
    }

    private void onDeleteButtonClick(ShoppingItem shoppingItem) {
        // Create alert dialog to confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_note_title);
        builder.setMessage(R.string.delete_shopitem_message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            // Delete shoppingItem from database
            db.collection("listaspesa").document(shoppingItem.getId()).delete();
            items.remove(shoppingItem);
            checkedItems.clear();
            itemsHolders.forEach(holder -> holder.itemCheckbox.setChecked(false));
            notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancella", (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    public void addItem(ShoppingItem shoppingItem) {
        items.add(shoppingItem);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        CheckBox itemCheckbox;
        ImageView deleteItemButton;
    }
}
