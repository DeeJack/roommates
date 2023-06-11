package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.PagamentoUtente;

public class StoricoPagamentiUtentiAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<PagamentoUtente> storicList;

    public StoricoPagamentiUtentiAdapter(Context context, List<PagamentoUtente> storicList) {
        this.storicList = storicList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return storicList.size();
    }

    @Override
    public PagamentoUtente getItem(int i) {
        return storicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setItems(List<PagamentoUtente> items) {
        this.storicList = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.situazioni_list_item, parent, false);

            holder = new ViewHolder();
            holder.titolo = convertView.findViewById(R.id.situazioneUserField);
            holder.descrizione = convertView.findViewById(R.id.amountTextField);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PagamentoUtente item = storicList.get(position);

        holder.titolo.setText(item.getUserNameFrom() + " -> " + item.getUserNameTo());

        Timestamp t = item.getDate();
        Date d = t.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String data = day + "/" + month + "/" + year;

        holder.descrizione.setText("Pagati " + item.getAmount().toString() + "€ in data " + data);

        return convertView;
    }

    private static class ViewHolder {
        TextView titolo;
        TextView descrizione;
    }
}
