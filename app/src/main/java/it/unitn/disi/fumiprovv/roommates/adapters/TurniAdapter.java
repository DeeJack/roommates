package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Sondaggio;
import it.unitn.disi.fumiprovv.roommates.models.Turno;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;

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

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Long currentWeek = new Long(calendar.get(Calendar.WEEK_OF_YEAR));
        Long currentYear = new Long(calendar.get(Calendar.YEAR));

        Long annoInizio =  item.getAnnoInizio();
        Long settimanaInizio = item.getWeekInizio();

        Long weeksPassed = ((currentYear - annoInizio) * 52) + (currentWeek - settimanaInizio);

        ArrayList<String> userIds = item.getUtenti();
        int index = 0;
        Long userIndex = (weeksPassed - 1) % userIds.size();
        index = Math.toIntExact(userIndex);
        if(thisWeek) {
            holder.luogo.setText("this week");
        } else {
            index +=1;
            holder.luogo.setText("next week");
        }

        String currentUser = userIds.get(index);

        db.collection("utenti").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String nome = (String) task.getResult().get("name");
                holder.utente.setText(nome);
            }
        });

        holder.luogo.setText(item.getLuogo());

        //holder.utente.setText(currentUser);

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
        //Button buttonNewTurno;
    }
}
