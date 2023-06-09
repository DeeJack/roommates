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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;

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

            /*holder.itemCheckbox = convertView.findViewById(R.id.itemCheckbox);
            holder.deleteItemButton = convertView.findViewById(R.id.deleteItemButton);*/

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SpesaComune item = items.get(position);
        holder.nome.setText(item.getNome());
        holder.responsabile.setText(item.getResponsabile());

        if(item.getResponsabile().equals(mAuth.getUid())) {
            holder.buttonPaga.setVisibility(View.VISIBLE);
        }

        holder.buttonPaga.setOnClickListener(view -> onPagaButtonClick(item));

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
        builder.setTitle("PagamentoUtente");
        builder.setMessage("Confermi il pagamento?");
        builder.setPositiveButton("Ok", (dialog, which) -> {
            //Setta la spesa come pagata
            /*db.collection("listaspesa").document(spesa.getId()).delete();
            items.remove(spesa);*/
            Log.d("Pago spesa", "pagata");
            notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancella", (dialog, which) -> {
            // Do nothing
        });
        builder.show();
    }

    public void addItem(SpesaComune spesa) {
        items.add(spesa);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView nome;
        TextView scadenza;
        TextView responsabile;
        TextView valore;
        Button buttonPaga;
        Button buttonNuovaSpesaComune;
    }
}

