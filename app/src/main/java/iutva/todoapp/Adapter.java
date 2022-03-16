package iutva.todoapp;

import android.view.View;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.textview.MaterialTextView;

/**
 * Implémentation d'un BaseAdapter spécifiquement pour Firebase.
 */
public class Adapter extends FirebaseListAdapter<TodoItem> {

    /**
     * Initialise l'Adapter avec les paramètres du contenu de la ListView.
     * Le paramètre principal de ces options est la requête qui contient les tâches.
     *
     * @param options liste d'options
     */
    public Adapter(@NonNull FirebaseListOptions<TodoItem> options) {
        super(options);
    }

    /**
     * Remplis la vue de la liste avec les données de la requête Firebase.
     *
     * @param view     La vue à remplir
     * @param item     La tâche à ajouter
     * @param position La position de la tâche qui est ajoutée
     */
    @Override
    protected void populateView(@NonNull View view, @NonNull TodoItem item, int position) {
        if (item.isFinished()) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.selected, view.getContext().getApplicationContext().getTheme()));
        } else {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.transparent, view.getContext().getApplicationContext().getTheme()));
        }

        MaterialTextView intitule = view.findViewById(R.id.intitule);
        MaterialTextView date = view.findViewById(R.id.date);
        MaterialTextView contexte = view.findViewById(R.id.contexte);
        MaterialTextView description = view.findViewById(R.id.description);

        intitule.setText(item.getIntitule());
        date.setText(item.getDate());
        contexte.setText(item.getContexte());
        description.setText(item.getDescription());
    }
}
