package com.jaydev.awsquiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);
        int answered = getIntent().getIntExtra("ANSWERED", -1);
        long elapsed = getIntent().getLongExtra("ELAPSED_MS", 0L);
        String simuladoAsset = getIntent().getStringExtra("SIMULADO_ASSET");

        TextView resultText = findViewById(R.id.txtResult);
        TextView pctText = findViewById(R.id.txtPercentage);
        TextView elapsedText = findViewById(R.id.txtElapsed);

        int percent = total > 0 ? (score * 100 / total) : 0;
        resultText.setText("Você acertou " + score + " de " + total + " questões.");
        pctText.setText("Sua pontuação: " + percent + "%");

        // Save percentage to SharedPreferences
        if (simuladoAsset != null && !simuladoAsset.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("SimuladoResults", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(simuladoAsset, percent);
            editor.apply();
        }

        if (answered >= 0) {
            elapsedText.setText("Respondidas: " + answered + "  •  Tempo: " + formatElapsed(elapsed));
        } else {
            elapsedText.setText("Tempo: " + formatElapsed(elapsed));
        }

        Button restart = findViewById(R.id.btnRestart);
        restart.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            // clear activity stack so MainActivity becomes root
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Button review = findViewById(R.id.btnReview);
        review.setOnClickListener(v -> {
            Intent r = new Intent(ResultActivity.this, ReviewActivity.class);
            // pass asset and user's answers JSON if available
            String asset = getIntent().getStringExtra("SIMULADO_ASSET");
            String ua = getIntent().getStringExtra("USER_ANSWERS_JSON");
            String qjson = getIntent().getStringExtra("QUESTIONS_JSON");
            if (asset != null) r.putExtra("SIMULADO_ASSET", asset);
            if (ua != null) r.putExtra("USER_ANSWERS_JSON", ua);
            if (qjson != null) r.putExtra("QUESTIONS_JSON", qjson);
            startActivity(r);
        });
    }

    @SuppressLint("DefaultLocale")
    private String formatElapsed(long ms) {
        long seconds = ms / 1000;
        long hrs = seconds / 3600;
        long mins = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }
}
