package it.unitn.disi.fumiprovv.roommates.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Turno;

public class TurniPuliziaAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final Long currentWeek;
    private final Long currentYear;
    private List<Turno> turni;

    public TurniPuliziaAdapter(Context context, List<Turno> turni) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        currentWeek = (long) calendar.get(Calendar.WEEK_OF_YEAR);
        currentYear = (long) calendar.get(Calendar.YEAR);
        this.turni = turni;
        this.context = context;
        if (context != null)
            this.inflater = LayoutInflater.from(context);
        else
            this.inflater = null;
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
        TurniPuliziaAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.turni_list_item, parent, false);

            holder = new TurniPuliziaAdapter.ViewHolder();
            holder.luogoTextField = convertView.findViewById(R.id.luogoTextField);
            holder.turnoUserField = convertView.findViewById(R.id.turnoUserField);
            holder.turnoDeleteButton = convertView.findViewById(R.id.turnoDeleteButton);
            holder.turnoShareButton = convertView.findViewById(R.id.turnoShareBtn);
            holder.turnoInfo = convertView.findViewById(R.id.turnoInfo);

            convertView.setTag(holder);
        } else {
            holder = (TurniPuliziaAdapter.ViewHolder) convertView.getTag();
        }

        holder.turnoInfo.removeAllViews();

        Turno item = turni.get(position);
        String luogo = item.getName();
        holder.luogoTextField.setText(luogo);
        ArrayList<String> userIds = item.getUsers();
        holder.turnoDeleteButton.setOnClickListener(view -> onDeleteButtonClick(item));

        long weekStart = item.getWeekStart();
        long yearStart = item.getYearStart();

        //calcola indice
        long weeksPassed = ((currentYear - yearStart) * 52) + currentWeek - weekStart + 1;

        long userIndex = (weeksPassed - 1) % userIds.size();
        int index = Math.toIntExact(userIndex);

        holder.turnoUserField.setText(item.getUserNameAt(item.getId()));
        String id = userIds.get(index);
        String nome = item.getUserNameAt(id);
        holder.turnoUserField.setText(nome);

        String currentUser = mAuth.getUid();

        if (false) { //se non sono moderatore nascondo il pulsante per eliminare i turni
            holder.turnoShareButton.setVisibility(View.INVISIBLE);
        } else {
            holder.turnoShareButton.setOnClickListener(view -> onShareButtonClick("Nuovo turno: " + item.getName(), item.getId()));
        }

        boolean completato = (item.getWeekLast().equals(currentWeek) && item.getYearLast().equals(currentYear));

        if (Objects.equals(currentUser, userIds.get(index))) { //sono la persona del turno
            if (!completato) {
                //aggiungo pulsante per completare
                Button b = new Button(convertView.getContext());
                b.setText(R.string.complete);
                b.setOnClickListener(view -> onCompleteButtonClick(item));
                holder.turnoInfo.addView(b);
            } else {
                //scritta "completato"
                TextView t = new TextView(convertView.getContext());
                t.setText(R.string.completed);
                t.setTextColor(Color.GREEN);
                holder.turnoInfo.addView(t);
            }
        } else {
            TextView t = new TextView(convertView.getContext());
            if (completato) {
                //scritta "completato"
                t.setText(R.string.completed);
                t.setTextColor(Color.GREEN);

            } else {
                //scritta non completato
                t.setText(R.string.to_complete);
                t.setTextColor(Color.RED);
            }
            holder.turnoInfo.addView(t);
        }
        return convertView;
    }

    private void onCompleteButtonClick(Turno turno) {
        turno.setWeekLast(currentWeek);
        db.collection("turniPulizia").document(turno.getId()).set(turno)
                .addOnCompleteListener(task -> notifyDataSetChanged());
    }

    public void setTurni(List<Turno> turni) {
        this.turni = turni;
    }

    private void onShareButtonClick(String text, String id) { //boh qua sicuramente non ho fatto qualcosa
        text = String.format("%s\nLink: %s", text, "http://roommates.asd/turni?id=" + id);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_turno_title));
        context.startActivity(shareIntent);
    }

    private void onDeleteButtonClick(Turno turno) {
        // Create alert dialog to confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Roommates_AlertDialg);
        builder.setTitle("Elimina il turno " + turno.getName());
        builder.setMessage("Vuoi eliminare il turno?");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            // Delete note from database
            db.collection("turniPulizia").document(turno.getId()).delete();
            turni.remove(turno);
            notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancella", (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    private void retrieveUserName(ArrayList<String> userIds, int index, TextView turnoUserField) {
        if (index < userIds.size()) {
            String userId = userIds.get(index);
            db.collection("utenti").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userName = task.getResult().getString("name");
                    Log.d("nomeUtente", userName);
                    turnoUserField.setText(userName);
                    int nextIndex = index + 1;
                    retrieveUserName(userIds, nextIndex, turnoUserField);
                }
            });
        }
    }

    private static class ViewHolder {
        TextView turnoUserField;
        TextView luogoTextField;
        ImageView turnoShareButton;
        ImageView turnoDeleteButton;
        LinearLayout turnoInfo;
    }
}
