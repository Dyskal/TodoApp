package iutva.todoapp;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activité de visualisation d'une page web.
 */
public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    /**
     * Initialise la vue de visualisation d'une page web et passage de l'url par bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("url");

        webView = findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            super.finish();
        });
    }

    /**
     * Implémente le retour sur la page web avec l'appui sur la touche retour.
     * @param keyCode touche appuyée
     * @param event   valeur de la touche
     * @return true comme l'event s'est bien passé
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}