package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;

public class SituazioniAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> utenti;

    public SituazioniAdapter(Context context, List<String> utenti) {
        this.utenti = utenti;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return utenti.size()-1;
    }

    @Override
    public Object getItem(int i) {
        return utenti.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spesa_comune, parent, false);

            holder = new ViewHolder();
            holder.titolo = convertView.findViewById(R.id.titoloSituazione);
            holder.descrizione = convertView.findViewById(R.id.descrizioneSituazione);
            holder.buttonPaga = convertView.findViewById(R.id.pagaSituazione);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String item = utenti.get(position);
        holder.titolo.setText(item);

        //holder.buttonPaga.setOnClickListener(view -> onPagaButtonClick(item));

        return convertView;
    }

    private static class ViewHolder {
        TextView titolo;
        TextView descrizione;
        Button buttonPaga;
    }
}
