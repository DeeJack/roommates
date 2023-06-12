package it.unitn.disi.fumiprovv.roommates.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Debt;

public class SituazioniAdapter extends BaseAdapter {
    private List<Debt> debts;
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SituazioniAdapter(Context context, List<Debt> debts) {
        this.debts = debts;
        this.context = context;
        if (context != null)
            this.inflater = LayoutInflater.from(context);
        else
            this.inflater = null;
    }

    public int getCount() {
        return debts.size();
    }

    @Override
    public Debt getItem(int i) {
        return this.debts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    public View getView(int position, View convertView, ViewGroup parent) {
        SituazioniAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.situazioni_list_item, parent, false);

            holder = new SituazioniAdapter.ViewHolder();
            holder.titolo = convertView.findViewById(R.id.situazioneUserField);
            holder.descrizione = convertView.findViewById(R.id.amountTextField);
            holder.viewButton = convertView.findViewById(R.id.viewButtonPaga);

            convertView.setTag(holder);
        } else {
            holder = (SituazioniAdapter.ViewHolder) convertView.getTag();
        }

        holder.viewButton.removeAllViews();

        Debt item = debts.get(position);

        //se sono in idTo sono in debito
        double amount = item.getAmount();
        if(item.getIdFrom().equals(mAuth.getUid())) { //debito
            String user = item.getUserNameTo();
            holder.titolo.setText(user);
            if(amount == 0.0) {
                holder.descrizione.setText("Sei in pari con " + user);
            } else if (amount > 0.0) {
                holder.descrizione.setText(String.format(Locale.getDefault(), "Devi %.2f a %s", amount, user));
            } else {
                holder.descrizione.setText(String.format(Locale.getDefault(), "Devi %.2f a %s", -amount, user));
            }
        } else {//credito
            String user = item.getUserNameFrom();
            holder.titolo.setText(user);
            if(amount == 0.0) {
                holder.descrizione.setText("Sei in pari con " + user);
            } else {
                holder.descrizione.setText(String.format(Locale.getDefault(), "%s ti deve %.2f€", user, Math.abs(amount)));
                Button b = new Button(convertView.getContext());
                b.setText("Paga");
                b.setBackgroundColor(R.color.purple_200);
                b.setOnClickListener(view -> onSegnaPagatoButtonClick(item));
                holder.viewButton.addView(b);
            }
        }

        return convertView;
    }

    private void onSegnaPagatoButtonClick(Debt debt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pagamento Utente");
        builder.setMessage("Inserisci la cifra che ti è stata pagata da " + debt.getUserNameFrom());

        // Create EditText programmatically and set it as the dialog's view
        final EditText input = new EditText(context);
        input.setTextColor(Color.WHITE);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        builder.setView(input);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            String inputText = input.getText().toString();

            if (!TextUtils.isEmpty(inputText)) {
                try {
                    Double number = Double.parseDouble(inputText);

                    if(number>Math.abs(debt.getAmount())) {
                        Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    } else {
                        //update db
                        Double newAmount = 0.0;
                        if (debt.getAmount() > 0)
                            newAmount = debt.getAmount() - number;
                        else
                            newAmount = debt.getAmount() + number;
                        DocumentReference debtRef = db.collection("debiti").document(debt.getId());
                        WriteBatch batch = db.batch();
                        batch.update(debtRef, "amount", newAmount);
                        CollectionReference collectionRef = db.collection("storicoPagamentiUtenti");
                        DocumentReference newDocumentRef = collectionRef.document();
                        Map<String, Object> newData = new HashMap<>();
                        newData.put("amount", number);
                        newData.put("house", debt.getHouse());
                        newData.put("date", FieldValue.serverTimestamp());
                        newData.put("from", debt.getIdTo());
                        newData.put("to", debt.getIdFrom());
                        batch.set(newDocumentRef, newData);

                        Double finalNewAmount = newAmount;
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                debt.setAmount(finalNewAmount);
                                notifyDataSetChanged();
                            }
                        });
                    }
                } catch (NumberFormatException e) {
                    // Input is not a valid number
                    Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancella", (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    public void setDebts(List<Debt> debiti) {
        this.debts = debiti;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView titolo;
        TextView descrizione;
        LinearLayout viewButton;
    }

}
