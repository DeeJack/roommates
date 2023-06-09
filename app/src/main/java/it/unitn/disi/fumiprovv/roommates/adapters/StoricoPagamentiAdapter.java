package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Pagamento;

public class StoricoPagamentiAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Pagamento> storicList;

    public StoricoPagamentiAdapter(Context context, List<Pagamento> storicList) {
        this.storicList = storicList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return storicList.size();
    }

    @Override
    public Pagamento getItem(int i) {
        return storicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setItems(List<Pagamento> items) {
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

        Pagamento item = storicList.get(position);

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
