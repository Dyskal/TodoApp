package iutva.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Activité principale d'affichage des tâches dans une liste.
 * La base de données est séparée grace à des ID uniques avec la méthode {@link #getId getId}.
 */
public class TaskListActivity extends AppCompatActivity {
    private Adapter adapter;
    private DatabaseReference ref;

    /**
     * Initialise la vue avec une requête vers la base de données Firebase et l'adapter et initialise les listeners des différents composants.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.show();

        ListView listView = findViewById(R.id.todoList);

        String usrId = getId();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://todoapp-iut-default-rtdb.europe-west1.firebasedatabase.app/");
        db.setPersistenceEnabled(true);
        ref = db.getReference().child(usrId);
        ref.keepSynced(true);

        FirebaseListOptions<TodoItem> options = new FirebaseListOptions.Builder<TodoItem>()
                .setLayout(R.layout.todo_item)
                .setQuery(ref, TodoItem.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new Adapter(options);
        FloatingActionButton fab = findViewById(R.id.fab);

        listView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskCreateActivity.class);
            startActivityForResult(intent, 1);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            TodoItem selected = (TodoItem) parent.getItemAtPosition(position);
            Intent intent = new Intent(TaskListActivity.this, TaskDescActivity.class);
            intent.putExtra("item", selected);
            startActivityForResult(intent, 2);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            TodoItem item = (TodoItem) parent.getItemAtPosition(position);
            item.invertFinished();

            ref.child(item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().child("finished").setValue(item.isFinished());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", error.getDetails());
                }
            });
            return true;
        });
    }

    /**
     * Génère un ID unique dans un thread séparé (API asynchrone)
     * @return l'ID unique
     */
    public String getId() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> Tasks.await(FirebaseInstallations.getInstance().getId()));
        executor.shutdown();
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lance l'adapter et la connexion à la base de données au démarrage.
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    /**
     * Stoppe l'adapter et la connexion à la base de données à l'arrêt.
     */
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /**
     * Récupère les données au retour d'une activité.
     *
     * @param requestCode Code donnée a l'appel d'une activité
     * @param resultCode  Code renvoyé de l'activité appelée
     * @param data        Données renvoyées de l'activité appelée
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if ((requestCode == 1 || requestCode == 2) && data != null) {
                // Si ajout ou changement de la tache, ajout direct de la node
                TodoItem item = (TodoItem) data.getExtras().getSerializable("item");
                ref.child(item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().setValue(item);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", error.getDetails());
                    }
                });
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == 2 && data != null) {
                String id = data.getExtras().getString("id");
                ref.child(id).removeValue();
            }
        }
    }
}