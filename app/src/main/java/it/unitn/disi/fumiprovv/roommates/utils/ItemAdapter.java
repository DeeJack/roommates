package it.unitn.disi.fumiprovv.roommates.utils;

import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Event;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Event> eventList;

    // Constructor
    public ItemAdapter() {
        this.eventList = new ArrayList<>();
    }

    // Method to set the data
    public void setData(List<Event> eventList) {
        this.eventList.clear();
        this.eventList.addAll(eventList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // Inner ViewHolder class
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemDataEvento;
        private TextView itemDescrizioneEvento;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemDataEvento = itemView.findViewById(R.id.itemDataEvento);
            itemDescrizioneEvento = itemView.findViewById(R.id.itemDescrizioneEvento);
        }

        public void bind(Event event) {
            itemDataEvento.setText(event.getGiorno() + "/" + event.getMese() + "/" + event.getAnno());
            itemDescrizioneEvento.setText(event.getNome());
        }
    }
}
