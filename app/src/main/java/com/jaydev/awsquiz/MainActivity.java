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

        // Configurar action bar customizada
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            
            // Inflar layout customizado para a action bar
            android.view.LayoutInflater inflater = (android.view.LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.custom_action_bar, null);
            getSupportActionBar().setCustomView(customView);
        }

        // Configurar dados dos simulados
        String[] simuladoAssets = {"simulado_1.json", "simulado_2.json", "simulado_3.json", "simulado_4.json", 
                                   "simulado_5.json", "simulado_6.json", "simulado_7.json", "simulado_8.json", 
                                   "simulado_9.json", "simulado_10.json"};
        android.content.SharedPreferences prefs = getSharedPreferences("SimuladoResults", MODE_PRIVATE);
        int completedCount = 0;
        int totalPercentage = 0;
        int bestPercentage = 0;
        long totalTimeSeconds = 0;
        
        for (String asset : simuladoAssets) {
            int percentage = prefs.getInt(asset, -1);
            if (percentage >= 0) {
                completedCount++;
                totalPercentage += percentage;
                if (percentage > bestPercentage) {
                    bestPercentage = percentage;
                }
                // Obter tempo gasto
                long timeSeconds = prefs.getLong(asset + "_time", 0);
                totalTimeSeconds += timeSeconds;
            }
        }
        
        int averagePercentage = completedCount > 0 ? totalPercentage / completedCount : 0;
        int progressPercent = (completedCount * 100) / simuladoAssets.length;
        long averageTimeMinutes = completedCount > 0 ? (totalTimeSeconds / completedCount) / 60 : 0;
        
        // Atualizar header de progresso
        View headerProgress = findViewById(R.id.headerProgress);
        if (headerProgress != null) {
            TextView txtProgressCount = headerProgress.findViewById(R.id.txtProgressCount);
            TextView txtProgressPercent = headerProgress.findViewById(R.id.txtProgressPercent);
            android.widget.ProgressBar progressBar = headerProgress.findViewById(R.id.progressBar);
            
            if (txtProgressCount != null) {
                txtProgressCount.setText(completedCount + "/10 simulados concluídos");
            }
            if (txtProgressPercent != null) {
                txtProgressPercent.setText(progressPercent + "%");
            }
            if (progressBar != null) {
                progressBar.setProgress(progressPercent);
            }
        }
        
        // Atualizar header de estatísticas
        View headerStats = findViewById(R.id.headerStatistics);
        if (headerStats != null) {
            TextView txtStatsCompleted = headerStats.findViewById(R.id.txtStatsCompleted);
            TextView txtStatsAverage = headerStats.findViewById(R.id.txtStatsAverage);
            TextView txtStatsBest = headerStats.findViewById(R.id.txtStatsBest);
            TextView txtStatsAvgTime = headerStats.findViewById(R.id.txtStatsAvgTime);
            
            if (txtStatsCompleted != null) {
                txtStatsCompleted.setText(String.valueOf(completedCount));
            }
            if (txtStatsAverage != null) {
                txtStatsAverage.setText(averagePercentage + "%");
            }
            if (txtStatsBest != null) {
                txtStatsBest.setText(bestPercentage + "%");
            }
            if (txtStatsAvgTime != null) {
                txtStatsAvgTime.setText(averageTimeMinutes + "min");
            }
        }

        
        // Calcular estatísticas
        String[] cardTitles = {"Simulado 01", "Simulado 02", "Simulado 03", "Simulado 04", "Simulado 05", 
                               "Simulado 06", "Simulado 07", "Simulado 08", "Simulado 09", "Simulado 10"};
        int[] cardIds = {R.id.cardSimulado1, R.id.cardSimulado2, R.id.cardSimulado3, R.id.cardSimulado4, 
                         R.id.cardSimulado5, R.id.cardSimulado6, R.id.cardSimulado7, R.id.cardSimulado8, 
                         R.id.cardSimulado9, R.id.cardSimulado10};

        for (int i = 0; i < cardIds.length; i++) {
            View card = findViewById(cardIds[i]);
            if (card != null) {
                TextView title = card.findViewById(R.id.txtSimuladoTitle);
                if (title != null) {
                    title.setText(cardTitles[i]);
                }
                
                // Load and display percentage if available
                TextView txtStatusBadge = card.findViewById(R.id.txtStatusBadge);
                Button btnStart = card.findViewById(R.id.btnStartSim);
                LinearLayout layoutProgress = card.findViewById(R.id.layoutProgress);
                android.widget.ProgressBar progressBarSimulado = card.findViewById(R.id.progressBarSimulado);
                TextView txtPercentage = card.findViewById(R.id.txtPercentage);
                
                // Forçar cor azul do botão programaticamente
                if (btnStart != null) {
                    btnStart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2563EB));
                }
                
                int percentage = prefs.getInt(simuladoAssets[i], -1);
                
                if (txtStatusBadge != null && btnStart != null) {
                    if (percentage >= 0) {
                        // Simulado concluído
                        txtStatusBadge.setText("Concluído");
                        txtStatusBadge.setTextColor(0xFFFFFFFF);
                        txtStatusBadge.setBackgroundResource(R.drawable.badge_completed);
                        btnStart.setText("▶ Continuar");
                        
                        // Atualizar barra de progresso e porcentagem
                        if (progressBarSimulado != null) {
                            progressBarSimulado.setProgress(percentage);
                            
                            // Definir cor baseado na performance
                            int progressColor;
                            if (percentage >= 70) {
                                progressColor = 0xFF27AE60; // Verde
                            } else if (percentage >= 50) {
                                progressColor = 0xFFF39C12; // Laranja
                            } else {
                                progressColor = 0xFFE74C3C; // Vermelho
                            }
                            progressBarSimulado.setProgressTintList(android.content.res.ColorStateList.valueOf(progressColor));
                        }
                        
                        if (txtPercentage != null) {
                            txtPercentage.setText(percentage + "%");
                            
                            // Definir cor do texto baseado na performance
                            int textColor;
                            if (percentage >= 70) {
                                textColor = 0xFF27AE60; // Verde
                            } else if (percentage >= 50) {
                                textColor = 0xFFF39C12; // Laranja
                            } else {
                                textColor = 0xFFE74C3C; // Vermelho
                            }
                            txtPercentage.setTextColor(textColor);
                        }
                    } else {
                        // Não iniciado
                        txtStatusBadge.setText("Não iniciado");
                        txtStatusBadge.setTextColor(0xFF6B7280);
                        txtStatusBadge.setBackgroundResource(R.drawable.badge_not_started);
                        btnStart.setText("▶ Iniciar");
                        
                        // Progress bar zerado
                        if (progressBarSimulado != null) {
                            progressBarSimulado.setProgress(0);
                            progressBarSimulado.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFE5E7EB));
                        }
                        
                        if (txtPercentage != null) {
                            txtPercentage.setText("0%");
                            txtPercentage.setTextColor(0xFF9CA3AF);
                        }
                    }
                }
            }
        }

        // Configurar botões dos simulados (sempre 65 questões)
        View card1 = findViewById(R.id.cardSimulado1);
        View card2 = findViewById(R.id.cardSimulado2);
        View card3 = findViewById(R.id.cardSimulado3);
        View card4 = findViewById(R.id.cardSimulado4);
        View card5 = findViewById(R.id.cardSimulado5);
        View card6 = findViewById(R.id.cardSimulado6);
        View card7 = findViewById(R.id.cardSimulado7);
        View card8 = findViewById(R.id.cardSimulado8);
        View card9 = findViewById(R.id.cardSimulado9);
        View card10 = findViewById(R.id.cardSimulado10);

        View btnStartSim1 = card1 != null ? card1.findViewById(R.id.btnStartSim) : null;
        View btnStartSim2 = card2 != null ? card2.findViewById(R.id.btnStartSim) : null;
        View btnStartSim3 = card3 != null ? card3.findViewById(R.id.btnStartSim) : null;
        View btnStartSim4 = card4 != null ? card4.findViewById(R.id.btnStartSim) : null;
        View btnStartSim5 = card5 != null ? card5.findViewById(R.id.btnStartSim) : null;
        View btnStartSim6 = card6 != null ? card6.findViewById(R.id.btnStartSim) : null;
        View btnStartSim7 = card7 != null ? card7.findViewById(R.id.btnStartSim) : null;
        View btnStartSim8 = card8 != null ? card8.findViewById(R.id.btnStartSim) : null;
        View btnStartSim9 = card9 != null ? card9.findViewById(R.id.btnStartSim) : null;
        View btnStartSim10 = card10 != null ? card10.findViewById(R.id.btnStartSim) : null;

        if (btnStartSim1 != null) {
            btnStartSim1.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_1.json");
                startActivity(intent);
            });
        }

        if (btnStartSim2 != null) {
            btnStartSim2.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_2.json");
                startActivity(intent);
            });
        }

        if (btnStartSim3 != null) {
            btnStartSim3.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_3.json");
                startActivity(intent);
            });
        }

        if (btnStartSim4 != null) {
            btnStartSim4.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_4.json");
                startActivity(intent);
            });
        }

        if (btnStartSim5 != null) {
            btnStartSim5.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_5.json");
                startActivity(intent);
            });
        }

        if (btnStartSim6 != null) {
            btnStartSim6.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_6.json");
                startActivity(intent);
            });
        }

        if (btnStartSim7 != null) {
            btnStartSim7.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_7.json");
                startActivity(intent);
            });
        }

        if (btnStartSim8 != null) {
            btnStartSim8.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_8.json");
                startActivity(intent);
            });
        }

        if (btnStartSim9 != null) {
            btnStartSim9.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_9.json");
                startActivity(intent);
            });
        }

        if (btnStartSim10 != null) {
            btnStartSim10.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("SIMULADO_ASSET", "simulado_10.json");
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
