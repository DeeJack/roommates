package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.util.Log;
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
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;
import it.unitn.disi.fumiprovv.roommates.models.StoricoSituazioni;

public class StoricoSituazioniAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<StoricoSituazioni> storicList;

    public StoricoSituazioniAdapter(Context context, ArrayList<StoricoSituazioni> storicList) {
        this.storicList = storicList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return storicList.size();
    }

    @Override
    public StoricoSituazioni getItem(int i) {
        return storicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setItems(ArrayList<StoricoSituazioni> items) {
        this.storicList = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.generic_item, parent, false);

            holder = new ViewHolder();
            holder.titolo = convertView.findViewById(R.id.titoloGenericItem);
            holder.descrizione = convertView.findViewById(R.id.descrizioneGenericItem);
            holder.buttonPaga = convertView.findViewById(R.id.buttonGenericItem);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        StoricoSituazioni item = storicList.get(position);
        db.collection("utenti").document(item.getFrom()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                holder.titolo.setText(task.getResult().get("name").toString() + " -> ");
            }
        });

        db.collection("utenti").document(item.getTo()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                holder.titolo.append(task.getResult().get("name").toString());
            }
        });

        Timestamp t = item.getDate();
        Date d = t.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String data = day + "/" + month + "/" + year;

        holder.descrizione.setText("Pagati " + item.getAmount().toString() + "€ in data " + data);

        //holder.buttonPaga.setOnClickListener(view -> onPagaButtonClick(item));

        return convertView;
    }

    private static class ViewHolder {
        TextView titolo;
        TextView descrizione;
        Button buttonPaga;
    }
}
