package com.jaydev.awsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerGlobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // enable edge-to-edge handling so we get proper system insets on Android 15+
        EdgeToEdge.enable(this);

        // create a custom action bar view with icon + centered title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            RelativeLayout container = new RelativeLayout(this);
            container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView iv = new ImageView(this);
            iv.setId(View.generateViewId());
            iv.setImageResource(R.mipmap.ic_launcher_round);
            int size = (int) (36 * getResources().getDisplayMetrics().density);
            RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(size, size);
            ivParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            ivParams.addRule(RelativeLayout.CENTER_VERTICAL);
            ivParams.setMarginStart((int) (12 * getResources().getDisplayMetrics().density));
            iv.setLayoutParams(ivParams);

            // invisible spacer on the right to balance the left icon so the title centers correctly
            View spacer = new View(this);
            spacer.setId(View.generateViewId());
            RelativeLayout.LayoutParams spacerParams = new RelativeLayout.LayoutParams(size, size);
            spacerParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            spacerParams.addRule(RelativeLayout.CENTER_VERTICAL);
            spacer.setLayoutParams(spacerParams);
            spacer.setVisibility(View.INVISIBLE);

            TextView title = new TextView(this);
            title.setText(getString(R.string.app_name));
            title.setTextSize(18);
            title.setTextColor(getResources().getColor(android.R.color.white));
            title.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            title.setLayoutParams(titleParams);

            container.addView(iv);
            container.addView(spacer);
            container.addView(title);

            getSupportActionBar().setCustomView(container);
        }

        Integer[] options = new Integer[]{5, 10, 20, 30, 65};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, options);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Each card include contains a Spinner with id spinnerCount; find them inside the included layouts
        View card1 = findViewById(R.id.cardSimulado1);
        Spinner spinnerCard1 = null;
        if (card1 != null) spinnerCard1 = card1.findViewById(R.id.spinnerCount);
        if (spinnerCard1 != null) spinnerCard1.setAdapter(adapter);

        View card2 = findViewById(R.id.cardSimulado2);
        Spinner spinnerCard2 = null;
        if (card2 != null) spinnerCard2 = card2.findViewById(R.id.spinnerCount);
        if (spinnerCard2 != null) spinnerCard2.setAdapter(adapter);

        View card3 = findViewById(R.id.cardSimulado3);
        Spinner spinnerCard3 = null;
        if (card3 != null) spinnerCard3 = card3.findViewById(R.id.spinnerCount);
        if (spinnerCard3 != null) spinnerCard3.setAdapter(adapter);

        // Buttons on each simulado card
        Button btnStartSim1 = findViewById(R.id.btnStartSim);
        Button btnStartSim2 = findViewById(R.id.btnStartSim2);
        Button btnStartSim3 = findViewById(R.id.btnStartSim3);

        if (btnStartSim1 != null) {
            Spinner finalSpinnerCard1 = spinnerCard1;
            btnStartSim1.setOnClickListener(v -> {
                int count = 65; // default
                if (finalSpinnerCard1 != null) count = (Integer) finalSpinnerCard1.getSelectedItem();
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_1.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim2 != null) {
            Spinner finalSpinnerCard2 = spinnerCard2;
            btnStartSim2.setOnClickListener(v -> {
                int count = 20; // default for simulado 2
                if (finalSpinnerCard2 != null) count = (Integer) finalSpinnerCard2.getSelectedItem();
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_2.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim3 != null) {
            Spinner finalSpinnerCard3 = spinnerCard3;
            btnStartSim3.setOnClickListener(v -> {
                int count = 65;
                if (finalSpinnerCard3 != null) count = (Integer) finalSpinnerCard3.getSelectedItem();
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_3.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }
    }
}
