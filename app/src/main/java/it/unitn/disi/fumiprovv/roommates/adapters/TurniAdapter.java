package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Sondaggio;
import it.unitn.disi.fumiprovv.roommates.models.Turno;

public class TurniAdapter extends BaseAdapter {
    private List<Turno> turni;
    private boolean thisWeek;
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public TurniAdapter(Context context, List<Turno> turni, boolean thisWeek){
        this.context = context;
        this.turni = turni;
        this.inflater = LayoutInflater.from(context);
        this.thisWeek = thisWeek;
    }

    @Override
    public int getCount() {
        return turni.size();
    }

    @Override
    public Turno getItem(int i) {
        return this.turni.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TurniAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.turni_item, parent, false);

            holder = new TurniAdapter.ViewHolder();
            holder.utente = convertView.findViewById(R.id.utenteTurno);
            holder.luogo = convertView.findViewById(R.id.luogoTurno);

            convertView.setTag(holder);
        } else {
            holder = (TurniAdapter.ViewHolder) convertView.getTag();
        }

        Turno item = turni.get(position);
        if(thisWeek) {
            //calcola chi dovrebbe svolgere questo turno questa settimana
            holder.luogo.setText("this week");
        } else {
            //calcola chi dovrebbe svolgere questo turno la prossima settimana
            holder.luogo.setText("next week");
        }

        //to do: se devo svolgerlo io e non l'ho ancora svolto, deve comparire il pulsante
        //altrimenti deve comparire completato/non completato

        return convertView;
    }

    public void setTurni(List<Turno> turni) {
        this.turni = turni;
    }

    private static class ViewHolder {
        TextView utente;
        TextView luogo;
        //Button buttonVota;
    }
}
