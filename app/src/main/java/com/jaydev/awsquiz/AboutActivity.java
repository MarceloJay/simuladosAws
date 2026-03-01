package com.jaydev.awsquiz;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Configurar action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sobre");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obter versão do app
        TextView txtVersion = findViewById(R.id.txtVersion);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersion.setText("Versão " + version);
        } catch (PackageManager.NameNotFoundException e) {
            txtVersion.setText("Versão 1.2.0");
        }

        // Botão copiar chave PIX
        Button btnCopyPix = findViewById(R.id.btnCopyPix);
        TextView txtPixKey = findViewById(R.id.txtPixKey);
        
        // Forçar cor azul no botão copiar (funciona em modo dark)
        btnCopyPix.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF2D6CDF")));
        
        btnCopyPix.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Chave PIX", txtPixKey.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Chave PIX copiada!", Toast.LENGTH_SHORT).show();
        });

        // Botão voltar
        Button btnBack = findViewById(R.id.btnBack);
        
        // Forçar cor azul no botão voltar (funciona em modo dark)
        btnBack.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF2D6CDF")));
        
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
