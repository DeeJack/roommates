package it.unitn.disi.fumiprovv.roommates.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ItemViewHolder> {
    private final List<Event> eventList;

    // Constructor
    public EventAdapter() {
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
        private final TextView itemDataEvento;
        private final TextView itemDescrizioneEvento;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemDataEvento = itemView.findViewById(R.id.itemDataEvento);
            itemDescrizioneEvento = itemView.findViewById(R.id.itemDescrizioneEvento);
        }

        public void bind(Event event) {
            itemDataEvento.setText(String.format(Locale.getDefault(),
                    "%d/%d/%d", event.getGiorno(), event.getMese(), event.getAnno()));
            itemDescrizioneEvento.setText(event.getNome());
        }
    }
}
