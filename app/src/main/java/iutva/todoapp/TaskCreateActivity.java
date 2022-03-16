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
 * Activité de création de tâche.
 */
public class TaskCreateActivity extends AppCompatActivity {
    private String date;

    /**
     * Initialise la vue de création d'une tâche et les différents listeners associés.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_create);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.show();

        TextInputEditText intitule = findViewById(R.id.intituleText);
        TextInputEditText contexte = findViewById(R.id.contexteText);
        TextInputEditText description = findViewById(R.id.descriptionText);
        MaterialButton dateBtn = findViewById(R.id.dateText);
        MaterialButton valider = findViewById(R.id.valider);
        MaterialButton annuler = findViewById(R.id.annuler);

        intitule.setText("");
        contexte.setText("");
        description.setText("");

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
                TodoItem todoItem = new TodoItem(intitule.getText().toString(), contexte.getText().toString(), description.getText().toString(), date);
                Intent intent = new Intent();
                intent.putExtra("item", todoItem);
                setResult(RESULT_OK, intent);
                super.finish();
            } else {
                Toast.makeText(this, "Entrez un intitulé", Toast.LENGTH_SHORT).show();
            }
        });

        annuler.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            super.finish();
        });
    }
}