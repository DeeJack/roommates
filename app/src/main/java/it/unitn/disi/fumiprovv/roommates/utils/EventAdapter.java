package it.unitn.disi.fumiprovv.roommates.utils;

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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ItemViewHolder> {
    private List<Event> eventList;

    // Constructor
    public EventAdapter() {
        this.eventList = new ArrayList<>();
    }

    // Method to set the data
    public void setData(List<Event> eventList) {
        this.eventList = eventList;
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
        private TextView itemText;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.itemText);
        }

        public void bind(Event event) {
            itemText.setText(event.getNome());
        }
    }
}
