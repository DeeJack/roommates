package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Expense;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;

public class ExpenseAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ArrayList<Expense> checkedItems = new ArrayList<Expense>();
    private List<Expense> items;

    public ExpenseAdapter(Context context, ArrayList<Expense> items) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Expense getItem(int i) {
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
            convertView = inflater.inflate(R.layout.spese_storico, parent, false);

            holder = new ExpenseAdapter.ViewHolder();
            holder.nome = convertView.findViewById(R.id.nomeSpesa);
            holder.data = convertView.findViewById(R.id.dataSpesa);
            holder.paganti = convertView.findViewById(R.id.divisoTra);
            holder.valore = convertView.findViewById(R.id.valoreSpesa);
            holder.pagatoDa = convertView.findViewById(R.id.pagatoDa);

            convertView.setTag(holder);
        } else {
            holder = (ExpenseAdapter.ViewHolder) convertView.getTag();
        }

        Expense item = items.get(position);
        holder.nome.setText(item.getName());
        holder.pagatoDa.setText(item.getPayerId());
        //prendere nome dell'utente che ha pagato

        holder.data.setText(item.getDataString());
        holder.valore.setText(Double.toString(item.getAmount()) + "€");
        holder.paganti.setText("fhjkwhkj");

        return convertView;
    }

    public void setItems(List<Expense> items) {
        this.items = items;
    }

    private static class ViewHolder {
        TextView nome;
        TextView data;
        TextView valore;
        TextView pagatoDa;
        TextView paganti;
    }

}
