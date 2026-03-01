package com.jaydev.awsquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.play.core.install.model.AppUpdateType;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerGlobal;
    private UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // enable edge-to-edge handling so we get proper system insets on Android 15+
        EdgeToEdge.enable(this);
        
        // Inicializar UpdateManager e verificar atualizações
        updateManager = new UpdateManager(this);
        updateManager.checkForUpdate(AppUpdateType.FLEXIBLE);

        // Configurar action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        String[] options = new String[]{"5 questões", "10 questões", "20 questões", "30 questões", "65 questões"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(0xFF111111); // Preto
                    tv.setTextSize(13);
                }
                return view;
            }
            
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(0xFF111111); // Preto
                    tv.setBackgroundColor(0xFFFFFFFF); // Branco
                    tv.setPadding(14, 14, 14, 14);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Configure card titles
        String[] cardTitles = {"Simulado 01", "Simulado 02", "Simulado 03", "Simulado 04", "Simulado 05", 
                               "Simulado 06", "Simulado 07", "Simulado 08", "Simulado 09", "Simulado 10"};
        String[] simuladoAssets = {"simulado_1.json", "simulado_2.json", "simulado_3.json", "simulado_4.json", 
                                   "simulado_5.json", "simulado_6.json", "simulado_7.json", "simulado_8.json", 
                                   "simulado_9.json", "simulado_10.json"};
        int[] cardIds = {R.id.cardSimulado1, R.id.cardSimulado2, R.id.cardSimulado3, R.id.cardSimulado4, 
                         R.id.cardSimulado5, R.id.cardSimulado6, R.id.cardSimulado7, R.id.cardSimulado8, 
                         R.id.cardSimulado9, R.id.cardSimulado10};

        android.content.SharedPreferences prefs = getSharedPreferences("SimuladoResults", MODE_PRIVATE);

        for (int i = 0; i < cardIds.length; i++) {
            View card = findViewById(cardIds[i]);
            if (card != null) {
                TextView title = card.findViewById(R.id.txtSimuladoTitle);
                if (title != null) {
                    title.setText(cardTitles[i]);
                }
                
                // Load and display percentage if available
                TextView percentageView = card.findViewById(R.id.txtPercentage);
                if (percentageView != null) {
                    int percentage = prefs.getInt(simuladoAssets[i], -1);
                    if (percentage >= 0) {
                        percentageView.setText(percentage + "%");
                        percentageView.setVisibility(View.VISIBLE);
                        // Color based on performance
                        if (percentage >= 70) {
                            percentageView.setTextColor(0xFF27AE60); // Green
                        } else if (percentage >= 50) {
                            percentageView.setTextColor(0xFFF39C12); // Orange
                        } else {
                            percentageView.setTextColor(0xFFE74C3C); // Red
                        }
                    } else {
                        percentageView.setVisibility(View.GONE);
                    }
                }
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Each card include contains a Spinner with id spinnerCount; find them inside the included layouts
        View card1 = findViewById(R.id.cardSimulado1);
        Spinner spinnerCard1 = null;
        if (card1 != null) spinnerCard1 = card1.findViewById(R.id.spinnerCount);
        if (spinnerCard1 != null) {
            spinnerCard1.setAdapter(adapter);
            spinnerCard1.setSelection(4); // Seleciona "65 questões" por padrão (índice 4)
        }

        View card2 = findViewById(R.id.cardSimulado2);
        Spinner spinnerCard2 = null;
        if (card2 != null) spinnerCard2 = card2.findViewById(R.id.spinnerCount);
        if (spinnerCard2 != null) {
            spinnerCard2.setAdapter(adapter);
            spinnerCard2.setSelection(4); // Seleciona "65 questões" por padrão
        }

        View card3 = findViewById(R.id.cardSimulado3);
        Spinner spinnerCard3 = null;
        if (card3 != null) spinnerCard3 = card3.findViewById(R.id.spinnerCount);
        if (spinnerCard3 != null) {
            spinnerCard3.setAdapter(adapter);
            spinnerCard3.setSelection(4); // Seleciona "65 questões" por padrão
        }

        View card4 = findViewById(R.id.cardSimulado4);
        Spinner spinnerCard4 = null;
        if (card4 != null) spinnerCard4 = card4.findViewById(R.id.spinnerCount);
        if (spinnerCard4 != null) {
            spinnerCard4.setAdapter(adapter);
            spinnerCard4.setSelection(4);
        }

        View card5 = findViewById(R.id.cardSimulado5);
        Spinner spinnerCard5 = null;
        if (card5 != null) spinnerCard5 = card5.findViewById(R.id.spinnerCount);
        if (spinnerCard5 != null) {
            spinnerCard5.setAdapter(adapter);
            spinnerCard5.setSelection(4);
        }

        View card6 = findViewById(R.id.cardSimulado6);
        Spinner spinnerCard6 = null;
        if (card6 != null) spinnerCard6 = card6.findViewById(R.id.spinnerCount);
        if (spinnerCard6 != null) {
            spinnerCard6.setAdapter(adapter);
            spinnerCard6.setSelection(4);
        }

        View card7 = findViewById(R.id.cardSimulado7);
        Spinner spinnerCard7 = null;
        if (card7 != null) spinnerCard7 = card7.findViewById(R.id.spinnerCount);
        if (spinnerCard7 != null) {
            spinnerCard7.setAdapter(adapter);
            spinnerCard7.setSelection(4);
        }

        View card8 = findViewById(R.id.cardSimulado8);
        Spinner spinnerCard8 = null;
        if (card8 != null) spinnerCard8 = card8.findViewById(R.id.spinnerCount);
        if (spinnerCard8 != null) {
            spinnerCard8.setAdapter(adapter);
            spinnerCard8.setSelection(4);
        }

        View card9 = findViewById(R.id.cardSimulado9);
        Spinner spinnerCard9 = null;
        if (card9 != null) spinnerCard9 = card9.findViewById(R.id.spinnerCount);
        if (spinnerCard9 != null) {
            spinnerCard9.setAdapter(adapter);
            spinnerCard9.setSelection(4);
        }

        View card10 = findViewById(R.id.cardSimulado10);
        Spinner spinnerCard10 = null;
        if (card10 != null) spinnerCard10 = card10.findViewById(R.id.spinnerCount);
        if (spinnerCard10 != null) {
            spinnerCard10.setAdapter(adapter);
            spinnerCard10.setSelection(4);
        }

        // Buttons on each simulado card
        View btnStartSim1 = findViewById(R.id.btnStartSim);
        View btnStartSim2 = findViewById(R.id.btnStartSim2);
        View btnStartSim3 = findViewById(R.id.btnStartSim3);
        View btnStartSim4 = card4 != null ? card4.findViewById(R.id.btnStartSim) : null;
        View btnStartSim5 = card5 != null ? card5.findViewById(R.id.btnStartSim) : null;
        View btnStartSim6 = card6 != null ? card6.findViewById(R.id.btnStartSim) : null;
        View btnStartSim7 = card7 != null ? card7.findViewById(R.id.btnStartSim) : null;
        View btnStartSim8 = card8 != null ? card8.findViewById(R.id.btnStartSim) : null;
        View btnStartSim9 = card9 != null ? card9.findViewById(R.id.btnStartSim) : null;
        View btnStartSim10 = card10 != null ? card10.findViewById(R.id.btnStartSim) : null;

        if (btnStartSim1 != null) {
            Spinner finalSpinnerCard1 = spinnerCard1;
            btnStartSim1.setOnClickListener(v -> {
                int count = 65; // default
                if (finalSpinnerCard1 != null) {
                    String selected = (String) finalSpinnerCard1.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_1.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim2 != null) {
            Spinner finalSpinnerCard2 = spinnerCard2;
            btnStartSim2.setOnClickListener(v -> {
                int count = 65; // default
                if (finalSpinnerCard2 != null) {
                    String selected = (String) finalSpinnerCard2.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
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
                if (finalSpinnerCard3 != null) {
                    String selected = (String) finalSpinnerCard3.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_3.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim4 != null) {
            Spinner finalSpinnerCard4 = spinnerCard4;
            btnStartSim4.setOnClickListener(v -> {
                int count = 65;
                if (finalSpinnerCard4 != null) {
                    String selected = (String) finalSpinnerCard4.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_4.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim5 != null) {
            Spinner finalSpinnerCard5 = spinnerCard5;
            btnStartSim5.setOnClickListener(v -> {
                int count = 65;
                if (finalSpinnerCard5 != null) {
                    String selected = (String) finalSpinnerCard5.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_5.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim6 != null) {
            Spinner finalSpinnerCard6 = spinnerCard6;
            btnStartSim6.setOnClickListener(v -> {
                int count = 65;
                if (finalSpinnerCard6 != null) {
                    String selected = (String) finalSpinnerCard6.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_6.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim7 != null) {
            Spinner finalSpinnerCard7 = spinnerCard7;
            btnStartSim7.setOnClickListener(v -> {
                int count = 65;
                if (finalSpinnerCard7 != null) {
                    String selected = (String) finalSpinnerCard7.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_7.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim8 != null) {
            Spinner finalSpinnerCard8 = spinnerCard8;
            btnStartSim8.setOnClickListener(v -> {
                int count = 65;
                if (finalSpinnerCard8 != null) {
                    String selected = (String) finalSpinnerCard8.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_8.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim9 != null) {
            Spinner finalSpinnerCard9 = spinnerCard9;
            btnStartSim9.setOnClickListener(v -> {
                int count = 65;
                if (finalSpinnerCard9 != null) {
                    String selected = (String) finalSpinnerCard9.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_9.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }

        if (btnStartSim10 != null) {
            Spinner finalSpinnerCard10 = spinnerCard10;
            btnStartSim10.setOnClickListener(v -> {
                int count = 65;
                if (finalSpinnerCard10 != null) {
                    String selected = (String) finalSpinnerCard10.getSelectedItem();
                    count = Integer.parseInt(selected.split(" ")[0]);
                }
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_10.json");
                intent.putExtra("QUESTION_COUNT", count);
                startActivity(intent);
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retomar atualização se necessário
        if (updateManager != null) {
            updateManager.resumeUpdateIfNeeded();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpar recursos
        if (updateManager != null) {
            updateManager.cleanup();
        }
    }
}
