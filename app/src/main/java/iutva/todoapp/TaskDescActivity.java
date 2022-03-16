package iutva.todoapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;

/**
 * Activité d'affichage d'une tâche.
 */
public class TaskDescActivity extends AppCompatActivity {
    private String date;

    /**
     * Initialise la vue d'affichage d'une tâche et les différents listeners associés.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_desc);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.show();

        Bundle bundle = getIntent().getExtras();
        TodoItem item = (TodoItem) bundle.getSerializable("item");

        TextInputEditText intitule = findViewById(R.id.intituleText);
        TextInputEditText contexte = findViewById(R.id.contexteText);
        TextInputEditText description = findViewById(R.id.descriptionText);
        TextInputEditText url = findViewById(R.id.urlText);
        MaterialButton dateBtn = findViewById(R.id.dateText);
        MaterialButton valider = findViewById(R.id.valider);
        MaterialButton supprimer = findViewById(R.id.supprimer);

        intitule.setText(item.getIntitule());
        contexte.setText(item.getContexte());
        description.setText(item.getDescription());
        url.setText(item.getUrl());
        date = item.getDate();

        // Clic long sur le texte de l'url ouvre l'intent de la webview
        url.setOnLongClickListener(v -> {
            if (!TextUtils.isEmpty(url.getText())) {
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", url.getText().toString());
                startActivity(intent);
                return true;
            }
            return false;
        });

        dateBtn.setOnClickListener(v -> {
            // Création d'un calendrier qui récupère la date choisie sous format court : 05/03/2022
            DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
                Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.set(year, month, dayOfMonth);
                date = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault()).toLocalDate());
            };

            LocalDate localDate;
            // Si date est null, ouverture du calendrier au jour actuel, sinon au jour sélectionné
            if (date == null) {
                localDate = LocalDateTime.ofInstant(Calendar.getInstance().toInstant(), ZoneId.systemDefault()).toLocalDate();
            } else {
                localDate = LocalDate.parse(date, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            }
            DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener, localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
            dialog.show();
        });

        valider.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(intitule.getText())) {
                TodoItem todoItem = item.update(intitule.getText().toString(), contexte.getText().toString(), description.getText().toString(), date, url.getText().toString(), item.isFinished());
                Intent intent = new Intent();
                intent.putExtra("item", todoItem);
                setResult(RESULT_OK, intent);
                super.finish();
            } else {
                Toast.makeText(this, "Entrez un intitulé", Toast.LENGTH_SHORT).show();
            }
        });

        supprimer.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("id", item.getId());
            setResult(RESULT_CANCELED, intent);
            super.finish();
        });
    }
}