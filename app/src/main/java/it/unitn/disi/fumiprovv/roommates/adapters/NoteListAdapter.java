package it.unitn.disi.fumiprovv.roommates.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Note;

public class NoteListAdapter extends BaseAdapter {
    private List<Note> notes;
    private final LayoutInflater inflater;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public NoteListAdapter(Context context, List<Note> notes) {
        this.notes = notes;
        this.context = context;
        if (context != null)
            this.inflater = LayoutInflater.from(context);
        else
            this.inflater = null;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Note getItem(int i) {
        return this.notes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.note_list_item, parent, false);

            holder = new ViewHolder();
            holder.noteNameField = convertView.findViewById(R.id.noteNameField);
            holder.noteTextField = convertView.findViewById(R.id.noteTextField);
            holder.noteShareButton = convertView.findViewById(R.id.noteShareBtn);
            holder.noteDeleteButton= convertView.findViewById(R.id.noteDeleteBtn);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note item = notes.get(position);
        Date creationDate = item.getCreationDate().toDate();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM, Locale.ITALY);
        holder.noteNameField.setText(String.format("%s, %s", item.getUserName(), dateFormat.format(creationDate)));
        holder.noteTextField.setText(item.getText());
        holder.noteShareButton.setOnClickListener(view -> onShareButtonClick(item.getText(), item.getId()));
        holder.noteDeleteButton.setOnClickListener(view -> onDeleteButtonClick(item));

        return convertView;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    private void onShareButtonClick(String text, String id) {
        text = String.format("%s\nLink: %s", text, "http://roommates.asd/notes?id=" + id);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_note_title));
        context.startActivity(shareIntent);
    }

    private void onDeleteButtonClick(Note note) {
        // Create alert dialog to confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_note_title);
        builder.setMessage(R.string.delete_note_message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            // Delete note from database
            db.collection("note").document(note.getId()).delete();
            notes.remove(note);
            notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancella", (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    private static class ViewHolder {
        TextView noteNameField;
        TextView noteTextField;
        ImageView noteShareButton;
        ImageView noteDeleteButton;
    }
}
