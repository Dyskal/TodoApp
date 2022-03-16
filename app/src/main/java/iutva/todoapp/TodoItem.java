package iutva.todoapp;

import java.io.Serializable;
import java.util.UUID;

public class TodoItem implements Serializable {
    private String id;
    private String intitule;
    private String contexte;
    private String description;
    private String date;
    private String url;
    private boolean finished;

    @SuppressWarnings("unused")
    public TodoItem() {}

    public TodoItem(String intitule, String contexte, String description, String date) {
        this.id = UUID.randomUUID().toString();
        this.intitule = intitule;
        this.contexte = contexte;
        this.description = description;
        this.date = date;
        this.url = "";
        this.finished = false;
    }

    public TodoItem update(String intitule, String contexte, String description, String date, String url, boolean finished) {
        this.intitule = intitule;
        this.contexte = contexte;
        this.description = description;
        this.date = date;
        this.url = url;
        this.finished = finished;
        return this;
    }

    public String getIntitule() {
        return intitule;
    }

    public String getContexte() {
        return contexte;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public boolean isFinished() {
        return finished;
    }

    public void invertFinished() {
        this.finished = !this.finished;
    }

    public String getId() {
        return id;
    }
}
