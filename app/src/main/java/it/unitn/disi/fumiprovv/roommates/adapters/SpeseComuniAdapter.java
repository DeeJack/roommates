package it.unitn.disi.fumiprovv.roommates.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;
import it.unitn.disi.fumiprovv.roommates.models.User;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

public class SpeseComuniAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final ArrayList<SpesaComune> checkedItems = new ArrayList<>();
    private List<SpesaComune> items;

    public SpeseComuniAdapter(Context context, ArrayList<SpesaComune> items) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SpesaComune getItem(int i) {
        return this.items.get(i);
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
            holder.nome = convertView.findViewById(R.id.nomeSpesaComune);
            holder.responsabile = convertView.findViewById(R.id.responsabileSpesaComune);
            holder.scadenza = convertView.findViewById(R.id.scadenzaSpesaComune);
            holder.valore = convertView.findViewById(R.id.valoreSpesaComune);
            holder.buttonPaga = convertView.findViewById(R.id.pagaSpesaComune);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.buttonPaga.setVisibility(View.INVISIBLE);

        SpesaComune item = items.get(position);
        holder.nome.setText(item.getNome());
        holder.responsabile.setText(item.getUserName());
        holder.valore.setText(item.getValore().toString());

        String ripetizione = item.getRipetizione();

        boolean pagata = false;

        if(ripetizione.equals(context.getResources().getString(R.string.mensile))) {
            Long currentMonth = (long) Calendar.MONTH;
            Long currentYear = (long) Calendar.YEAR;
            Long month = item.getLastMonth();
            Long year = item.getLastYear();
            if (currentMonth.equals(month) && currentYear.equals(year)) {
                pagata = true;
            }
        } else if(ripetizione.equals(context.getString(R.string.settimanale))) {
            Long currentWeek = (long) Calendar.WEEK_OF_YEAR;
            Long currentYear = (long) Calendar.YEAR;
            Long week = item.getLastWeek();
            Long year = item.getLastYear();
            if (currentWeek.equals(week) && currentYear.equals(year)) {
                pagata = true;
            }
        }

        if(pagata) {
            holder.scadenza.setText(context.getString(R.string.spesa_gia_pagata));
            if(ripetizione.equals(context.getResources().getString(R.string.mensile))) {
                holder.scadenza.append(context.getString(R.string.questo_mese));
            } else if(ripetizione.equals(context.getResources().getString(R.string.settimanale))){
                holder.scadenza.append(context.getString(R.string.questa_settimana));
            }
        } else {
            holder.buttonPaga.setVisibility(View.VISIBLE);
            holder.scadenza.setText(context.getString(R.string.da_pagare_entro));
            if(ripetizione.equals(context.getResources().getString(R.string.mensile))) {
                holder.scadenza.append(context.getString(R.string.questo_mese));
            } else if(ripetizione.equals(context.getResources().getString(R.string.settimanale))){
                holder.scadenza.append(context.getString(R.string.questa_settimana));
            } else {
                holder.scadenza.setText(context.getString(R.string.da_pagare));
            }
        }

        if(item.getResponsabile().equals(mAuth.getUid()) && !pagata) {
            holder.buttonPaga.setVisibility(View.VISIBLE);
        }

        if (item.getResponsabile().equals(mAuth.getUid()) && !pagata) {
            holder.buttonPaga.setVisibility(View.VISIBLE);
            holder.buttonPaga.setOnClickListener(view -> onPagaButtonClick(item));
        } else {
            holder.buttonPaga.setVisibility(View.GONE);
        }
        //aggiungi pulsante per eliminare spesa se sono il moderatore

        return convertView;
    }

    public ArrayList<SpesaComune> getCheckedItems() {
        return checkedItems;
    }

    public void setItems(List<SpesaComune> items) {
        this.items = items;
    }

    private void onPagaButtonClick(SpesaComune spesa) {
        // Create alert dialog to confirm pagamento
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.pagamento));
        builder.setMessage(context.getString(R.string.conferma_pagamento));
        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
            //aggiorna data ultimo pagamento nel db
            db.collection("utenti")
                    .whereEqualTo("houseId", spesa.getCasa())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<DocumentReference> userRefs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference userRef = document.getReference();
                                userRefs.add(userRef);
                            }

                            WriteBatch batch = db.batch();
                            String spesaId = spesa.getId();
                            DocumentReference spesaRef = db.collection("speseComuni").document(spesaId);
                            if(spesa.getRipetizione().equals(context.getResources().getString(R.string.mensile))) {
                                Long mese = (long) Calendar.MONTH;
                                Long anno = (long) Calendar.YEAR;
                                spesa.setLastMonth(mese);
                                spesa.setLastYear(anno);
                                batch.update(spesaRef, "lastMonth", mese);
                                batch.update(spesaRef, "lastYear", anno);
                            } else if(spesa.getRipetizione().equals(context.getResources().getString(R.string.settimanale))) {
                                Long settimana = (long) Calendar.WEEK_OF_YEAR;
                                Long anno = (long) Calendar.YEAR;
                                spesa.setLastWeek(settimana);
                                spesa.setLastYear(anno);
                                batch.update(spesaRef, "lastWeek", settimana);
                                batch.update(spesaRef, "lastYear", anno);
                            }

                            //aggiungi a storico spese
                            Map<String, Object> newSpesaStorico = new HashMap<>();
                            newSpesaStorico.put("houseId", spesa.getCasa());
                            newSpesaStorico.put("name", spesa.getNome());
                            newSpesaStorico.put("amount", spesa.getValore());
                            newSpesaStorico.put("payer", spesa.getResponsabile());
                            Timestamp t = Timestamp.now();
                            newSpesaStorico.put("date", t);
                            newSpesaStorico.put("usersPaying", userRefs);
                            batch.set(db.collection("listaspesaeffettuata").document(), newSpesaStorico);

                            //aggiorna tutti i debiti
                            /*String debitiId = boh;
                            DocumentReference debitiRef = db.collection("debiti").document(debitiId);
                            batch.update(debitiRef, bohData);*/

                            String casa = spesa.getCasa();

                            Double amountToAdd = spesa.getValore();

                            CollectionReference debitiCollectionRef = db.collection("debiti");
                            String currentUser = mAuth.getUid();

                            Query query = debitiCollectionRef
                                    .whereEqualTo("houseId", casa)
                                    .where(Filter.or(Filter.equalTo("idFrom", spesa.getResponsabile()),
                                            Filter.equalTo("idTo", spesa.getResponsabile())));

                            query.get().addOnSuccessListener(querySnapshot -> {
                                WriteBatch batch2 = db.batch();
                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                    double currentAmount = documentSnapshot.getDouble("amount");
                                    String userFrom = documentSnapshot.getString("idFrom");
                                    String userTo = documentSnapshot.getString("idTo");
                                    Double newAmount = new Double(0.0);
                                    if(userFrom.equals(currentUser)) {
                                        newAmount = currentAmount - amountToAdd;
                                    } else if(userTo.equals(currentUser)) {
                                        newAmount = currentAmount + amountToAdd;
                                    }

                                    DocumentReference debtRef = debitiCollectionRef.document(documentSnapshot.getId());
                                    batch2.update(debtRef, "amount", newAmount);
                                }
                                batch2.commit().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        // Update successful
                                        // Commit the batch
                                        batch.commit()
                                                .addOnSuccessListener(aVoid -> {
                                                    // Batch operation successful
                                                    // Handle success case
                                                    notifyDataSetChanged();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Batch operation failed
                                                    // Handle failure case
                                                });
                                    } else {
                                        // Handle update failure
                                    }
                                });
                            }).addOnFailureListener(e -> {
                                // Handle query failure
                            });



                        } else {
                            // Handle any errors
                        }
                    });


        });
        builder.setNegativeButton(context.getString(R.string.cancella), (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    public void addItem(SpesaComune spesa) {
        items.add(spesa);
        notifyDataSetChanged();
    }

    //funzione per eliminare spesa comune

    private static class ViewHolder {
        TextView nome;
        TextView scadenza;
        TextView responsabile;
        TextView valore;
        Button buttonPaga;
    }
}

