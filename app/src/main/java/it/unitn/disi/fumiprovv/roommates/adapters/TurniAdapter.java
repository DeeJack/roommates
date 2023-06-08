package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Turno;

public class TurniAdapter extends BaseAdapter {
    private List<Turno> turni;
    private boolean thisWeek;
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Map<String, String> userNames = new HashMap<>(); // Store retrieved user names
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
            holder.utente = convertView.findViewById(R.id.userTurno);
            holder.luogo = convertView.findViewById(R.id.placeTurno);
            holder.info = convertView.findViewById(R.id.infoTurno);

            convertView.setTag(holder);
        } else {

            holder = (TurniAdapter.ViewHolder) convertView.getTag();
        }

        holder.info.removeAllViews();

        Turno item = turni.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Long currentWeek = new Long(calendar.get(Calendar.WEEK_OF_YEAR));
        Long currentYear = new Long(calendar.get(Calendar.YEAR));

        Long annoInizio =  item.getYearStart();
        Long settimanaInizio = item.getWeekStart();

        Long weeksPassed = ((currentYear - annoInizio) * 52) + currentWeek - settimanaInizio + 1;

        ArrayList<String> userIds = item.getUsers();
        int index = 0;
        Long userIndex = (weeksPassed - 1) % userIds.size();
        index = Math.toIntExact(userIndex);
        Log.d("index", index + "");
        if(thisWeek) {
            holder.luogo.setText("this week");
        } else {
            index = index+1;
            if(index == userIds.size())
                index = 0;
            holder.luogo.setText("next week");
            Log.d("index2", index + "");
        }

        String currentUser = userIds.get(index);
        Log.d("currentUser", currentUser);
        holder.utente.setText(currentUser);

        if (!userNames.containsKey(currentUser)) {
            db.collection("utenti").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String nome = (String) task.getResult().get("name");
                        userNames.put(currentUser, nome);
                        holder.utente.setText(nome);
                    }
                }
            });
        } else {
            String nome = userNames.get(currentUser);
            holder.utente.setText(nome);
        }

        /*db.collection("utenti").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String nome = (String) task.getResult().get("name");
                Log.d("currentUserName", nome);
                holder.utente.setText(nome);
                //notifyDataSetChanged();
            }
        });*/

        holder.luogo.setText(item.getName());

        String user = mAuth.getUid();
        Log.d("utenti", user + " " + currentUser);

        if(user.equals(currentUser) && thisWeek) {
            Log.d("week", item.getWeekLast() + " " + currentWeek);
            if(item.getWeekLast().equals(currentWeek) && item.getYearLast().equals(currentYear)) { //se ho completato il turno
                TextView t = new TextView(convertView.getContext());
                t.setText("Completato");
                t.setTextColor(Color.GREEN);
                holder.info.addView(t);
            } else {
                Button b = new Button(convertView.getContext());
                b.setText("completa");
                b.setOnClickListener(v -> onCompletaButtonClick(item, currentWeek, holder));
                holder.info.addView(b);
            }
        } else {
            Log.d("currentWeek", currentWeek + "");
            if(thisWeek) {
                if(item.getWeekLast().equals(currentWeek) && item.getYearLast().equals(currentYear)) {
                    TextView t = new TextView(convertView.getContext());
                    t.setText("Completato");
                    t.setTextColor(Color.GREEN);
                    holder.info.addView(t);
                } else {
                    TextView t = new TextView(convertView.getContext());
                    t.setText("Da completare");
                    t.setTextColor(Color.RED);
                    holder.info.addView(t);
                }
            }
        }

        //holder.utente.setText(currentUser);

        //to do: se devo svolgerlo io e non l'ho ancora svolto, deve comparire il pulsante
        //altrimenti deve comparire completato/non completato


        return convertView;
    }

    private void onCompletaButtonClick(Turno turno, Long newWeekLast, ViewHolder holder) {
        Log.d("completa", "completato");

        DocumentReference documentRef = db.collection("turniPulizia").document(turno.getId());
        documentRef.update("weekLast", newWeekLast)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    holder.info.removeAllViews();

                    TextView t = new TextView(context);
                    t.setText("Completato");
                    t.setTextColor(Color.GREEN);
                    holder.info.addView(t);

                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Error occurred
                    Log.d("error", "Failed to update the 'weekLast' field: " + e.getMessage());
                });

    }


    public void setTurni(List<Turno> turni) {
        this.turni = turni;
    }

    private static class ViewHolder {
        TextView utente;
        TextView luogo;
        LinearLayout info;
    }
}
