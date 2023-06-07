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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Note;
import it.unitn.disi.fumiprovv.roommates.models.Sondaggio;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

public class SondaggiAdapter extends BaseAdapter {
    private List<Sondaggio> sondaggi;
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SondaggiAdapter(Context context, List<Sondaggio> sondaggi) {
        this.sondaggi = sondaggi;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sondaggi.size();
    }

    @Override
    public Sondaggio getItem(int i) {
        return this.sondaggi.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SondaggiAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.survey_item, parent, false);

            holder = new ViewHolder();
            holder.buttonVota = convertView.findViewById(R.id.buttonVota);
            holder.domandaTextField = convertView.findViewById(R.id.domanda);
            holder.optionsRadioGroup = convertView.findViewById(R.id.optionsRadioGroup);

            convertView.setTag(holder);
        } else {
            holder = (SondaggiAdapter.ViewHolder) convertView.getTag();
            holder.optionsRadioGroup.removeAllViews();
        }

        Sondaggio item = sondaggi.get(position);
        String domanda = item.getDomanda();
        holder.domandaTextField.setText(domanda);
        List<String> options = item.getOpzioni();

        Boolean isSceltaMultipla = item.getSceltaMultipla();
        String currentUserId = mAuth.getUid();

        ArrayList<String> vincitori = new ArrayList<String>();
        boolean votato = false;
        if(item.getVotiTotali() >= item.getMaxVotanti()) {
            votato = true;
            vincitori = item.getVincitori();
        }

        if(isSceltaMultipla) {
            for(int i=0;i<options.size();i++) {
                CheckBox checkBox = new CheckBox(convertView.getContext());
                checkBox.setText(options.get(i) + " (" + item.getVoto(i) + ")");
                if(item.getVotanti().contains(currentUserId) || votato) {
                    checkBox.setEnabled(false);
                }
                if(votato) {
                    if(vincitori.contains(options.get(i))) {
                        checkBox.setBackgroundColor(Color.GRAY);
                        checkBox.setTextColor(Color.WHITE);
                    }
                }
                holder.optionsRadioGroup.addView(checkBox);
            }
        } else {
            for(int i=0;i<options.size();i++) {
                RadioButton radioButton = new RadioButton(convertView.getContext());
                radioButton.setText(options.get(i) + " (" + item.getVoto(i) + ")");
                if(item.getVotanti().contains(currentUserId) || votato) {
                    radioButton.setEnabled(false);
                    radioButton.setTextColor(Color.WHITE);
                }
                if(votato) {
                    if(vincitori.contains(options.get(i))) { //ci potrebbero essere più vincitori
                        radioButton.setBackgroundColor(Color.GRAY);
                    }
                }
                holder.optionsRadioGroup.addView(radioButton);
            }
        }

        if(item.getVotanti().contains(currentUserId) || votato) {
            holder.buttonVota.setEnabled(false);
        }

        holder.buttonVota.setOnClickListener(view -> {List<String> selectedOptions = getSelectedOptions(holder.optionsRadioGroup); onButtonVotaClick(item, selectedOptions);});

        return convertView;
    }

    private List<String> getSelectedOptions(RadioGroup optionsRadioGroup) {
        List<String> selectedOptions = new ArrayList<>();
        for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
            View view = optionsRadioGroup.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) view;
                if (radioButton.isChecked()) {
                    String selectedOption = radioButton.getText().toString();
                    selectedOptions.add(selectedOption);
                }
            } else {
                CheckBox checkBox = (CheckBox) view;
                if(checkBox.isChecked()) {
                    String selectedOption = checkBox.getText().toString();
                    selectedOptions.add(selectedOption);
                }
            }
        }
        return selectedOptions;
    }

    private void onButtonVotaClick(Sondaggio item, List<String> selectedOptions) {
        Log.d("click", "cliccato bottone vota");
        if(selectedOptions.isEmpty()) {
            Log.d("vuoto", "nessuna opzione selezionata");
        } else {
            Log.d("non vuoto", selectedOptions.toString());
            for(String option : selectedOptions) {
                //aggiungi voto
                int i = item.getOpzioni().indexOf(option);
                item.addVoto(i);
            }
        }

        item.setVotiTotali(item.getVotiTotali()+1);
        item.addVotante(mAuth.getUid());
        db.collection("sondaggi").document(item.getId()).set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("failure", "Error writing document", e);
                    }
                });
        notifyDataSetChanged();
    }

    public void setSondaggi(List<Sondaggio> sondaggi) {
        this.sondaggi = sondaggi;
    }

    /*private void onShareButtonClick(String text, String id) {
        text = String.format("%s\nLink: %s", text, "http://roommates.asd/notes?id=" + id);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_note_title));
        context.startActivity(shareIntent);
    }*/

    private static class ViewHolder {
        TextView domandaTextField;
        RadioGroup optionsRadioGroup;
        Button buttonVota;
    }
}
