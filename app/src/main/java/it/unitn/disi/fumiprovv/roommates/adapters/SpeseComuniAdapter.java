package it.unitn.disi.fumiprovv.roommates.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.fragments.SpeseSpeseComuni;
import it.unitn.disi.fumiprovv.roommates.models.Event;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;
import it.unitn.disi.fumiprovv.roommates.utils.ItemAdapter;

public class SpeseComuniAdapter extends RecyclerView.Adapter<SpeseComuniAdapter.SpeseComuniViewHolder> {
    private List<SpesaComune> speseList;

    // Constructor
    public SpeseComuniAdapter() {
        this.speseList = new ArrayList<>();
    }

    // Method to set the data
    public void setData(List<SpesaComune> speseList) {
        this.speseList.clear();
        this.speseList.addAll(speseList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpeseComuniAdapter.SpeseComuniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spesa_comune, parent, false);
        return new SpeseComuniAdapter.SpeseComuniViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SpeseComuniAdapter.SpeseComuniViewHolder holder, int position) {
        SpesaComune spesa = speseList.get(position);
        holder.bind(spesa);
    }

    @Override
    public int getItemCount() {
        return speseList.size();
    }

    // Inner ViewHolder class
    public static class SpeseComuniViewHolder extends RecyclerView.ViewHolder {
        private TextView nome;
        private TextView valore;
        private TextView responsabile;
        private TextView scadenza;

        public SpeseComuniViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nomeSpesaComune);
            valore = itemView.findViewById(R.id.valoreSpesaComune);
            responsabile = itemView.findViewById(R.id.responsabileSpesaComune);
            scadenza = itemView.findViewById(R.id.scadenzaSpesaComune);
        }

        public void bind(SpesaComune spesa) {
            nome.setText(spesa.getNome());
            valore.setText(spesa.getValore().toString());
            responsabile.setText("Responsabile:" + spesa.getResponsabile());
            scadenza.setText("boh");
            /*itemDataEvento.setText(event.getGiorno() + "/" + event.getMese() + "/" + event.getAnno());
            itemDescrizioneEvento.setText(event.getNome());*/
        }
    }
}
