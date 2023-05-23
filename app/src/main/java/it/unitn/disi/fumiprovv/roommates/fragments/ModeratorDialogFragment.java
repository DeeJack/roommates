package it.unitn.disi.fumiprovv.roommates.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.List;

import it.unitn.disi.fumiprovv.roommates.models.Roommate;

public class ModeratorDialogFragment extends DialogFragment {
    private final List<Roommate> roommates;

    public ModeratorDialogFragment(List<Roommate> roommates) {
        this.roommates = roommates;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("test")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setItems((CharSequence[]) roommates.stream().map(Roommate::getUserId).toArray(String[]::new), (dialog, which) -> {
                            /*
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                selectedItems.add(which);
                            } else if (selectedItems.contains(which)) {
                                // Else, if the item is already in the array, remove it
                                selectedItems.remove(which);
                            }
                            */
                })
                // Set the action buttons
                .setPositiveButton("ok", (dialog, id) -> {
                    // User clicked OK, so save the selectedItems results somewhere
                    // or return them to the component that opened the dialog
                })
                .setNegativeButton("Cancella", (dialog, id) -> {
                });

        return builder.create();
    }

}
