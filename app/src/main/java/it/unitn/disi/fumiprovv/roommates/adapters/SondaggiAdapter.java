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
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Sondaggio;

public class SondaggiAdapter extends BaseAdapter {
    private List<Sondaggio> sondaggi;
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
            holder.deleteImage = convertView.findViewById(R.id.deleteSurvey);
            holder.shareImage = convertView.findViewById(R.id.shareSurvey);

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

        ArrayList<String> vincitori = new ArrayList<>();
        boolean votato = item.getVotanti().contains(currentUserId);
        if (votato) {
            vincitori = item.getVincitori();
        }

        createSurvey(convertView, holder, item, options, isSceltaMultipla, vincitori, votato);

        holder.buttonVota.setEnabled(!votato);

        holder.buttonVota.setOnClickListener(view -> {
            List<Integer> selectedOptions = getSelectedOptions(holder.optionsRadioGroup);
            onButtonVotaClick(item, selectedOptions);
        });

        holder.deleteImage.setOnClickListener(view -> deleteSurvey(item));
        holder.shareImage.setOnClickListener(view -> shareSurvey(item));

        return convertView;
    }

    private void createSurvey(View convertView, ViewHolder holder, Sondaggio item, List<String> options, Boolean isSceltaMultipla, ArrayList<String> vincitori, boolean votato) {
        if (isSceltaMultipla) {
            for (int i = 0; i < options.size(); i++) {
                CheckBox checkBox = new CheckBox(convertView.getContext());
                checkBox.setText(String.format(Locale.getDefault(), "%s (%d)", options.get(i), item.getVoto(i)));
                checkBox.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (votato) {
                    checkBox.setEnabled(false);
                    if (vincitori.contains(options.get(i))) {
                        checkBox.setBackgroundColor(Color.parseColor("#BB86FC"));
                        checkBox.setTextColor(context.getColor(R.color.primaryTextColor));
                    }
                }
                holder.optionsRadioGroup.addView(checkBox);
            }
        } else {
            for (int i = 0; i < options.size(); i++) {
                RadioButton radioButton = new RadioButton(convertView.getContext());
                radioButton.setText(String.format(Locale.getDefault(), "%s (%d)", options.get(i), item.getVoto(i)));
                radioButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (votato) {
                    radioButton.setEnabled(false);
                    radioButton.setTextColor(context.getColor(R.color.primaryTextColor));
                    if (vincitori.contains(options.get(i))) { //ci potrebbero essere più vincitori
                        radioButton.setBackgroundColor(Color.parseColor("#BB86FC"));
                    }
                }
                holder.optionsRadioGroup.addView(radioButton);
            }
        }
    }

    private void deleteSurvey(Sondaggio sondaggio) {
        // Create alert dialog to confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Roommates_AlertDialg);
        builder.setTitle(R.string.delete_note_title);
        builder.setMessage(R.string.delete_survey_message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            // Delete note from database
            db.collection("sondaggi").document(sondaggio.getId()).delete();
            sondaggi.remove(sondaggio);
            notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancella", (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    private void shareSurvey(Sondaggio item) {
        String text = String.format("%s\nLink: %s", context.getString(R.string.share_survey),
                "http://roommates.asd/survey?id=" + item.getId());

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_survey_title));
        context.startActivity(shareIntent);
    }

    private List<Integer> getSelectedOptions(RadioGroup optionsRadioGroup) {
        List<Integer> selectedOptions = new ArrayList<>();
        for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
            View view = optionsRadioGroup.getChildAt(i);
            if (view instanceof Checkable) {
                Checkable checkable = (Checkable) view;
                if (checkable.isChecked()) {
                    selectedOptions.add(i);
                }
            }
        }
        return selectedOptions;
    }

    private void onButtonVotaClick(Sondaggio item, List<Integer> selectedOptions) {
        Log.d("click", "cliccato bottone vota");
        if (selectedOptions.isEmpty()) {
            Log.d("vuoto", "nessuna opzione selezionata");
        } else {
            Log.d("non vuoto", selectedOptions.toString());
            for (int option : selectedOptions) {
                //aggiungi voto
                item.addVoto(option);
            }
        }

        item.setVotiTotali(item.getVotiTotali() + 1);
        item.addVotante(mAuth.getUid());
        db.collection("sondaggi")
                .document(item.getId()).set(item)
                .addOnSuccessListener(aVoid -> Log.d("success", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("failure", "Error writing document", e));
        notifyDataSetChanged();
    }

    public void setSondaggi(List<Sondaggio> sondaggi) {
        this.sondaggi = sondaggi;
    }

    private static class ViewHolder {
        TextView domandaTextField;
        RadioGroup optionsRadioGroup;
        Button buttonVota;
        ImageView shareImage;
        ImageView deleteImage;
    }
}
