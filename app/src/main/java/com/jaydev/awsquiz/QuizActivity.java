package com.jaydev.awsquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.jaydev.awsquiz.data.QuestionBank;
import com.jaydev.awsquiz.models.Question;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuizActivity extends AppCompatActivity {

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int answeredCount = 0;
    // store user selections per question for review
    private List<List<Integer>> userAnswers = new ArrayList<>();

    private TextView questionText;
    private CheckBox option1, option2, option3, option4, option5;
    private Button nextBtn;
    private Button btnFinish;

    // Timer
    private TextView txtTimer;
    private Button btnPause;
    private Handler timerHandler = new Handler();
    private long startTime = 0L;
    private long elapsedBeforePause = 0L; // accumulated before current running
    private boolean isPaused = false;
    private static final long MAX_TIME_MS = 90 * 60 * 1000; // 90 minutos em milissegundos

    // Selection rules
    private int maxSelections = 1;
    private boolean suppressCheckListener = false;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsed = elapsedBeforePause + (SystemClock.uptimeMillis() - startTime);
            
            // Verificar se o tempo acabou
            if (elapsed >= MAX_TIME_MS) {
                timerHandler.removeCallbacks(this);
                finishQuizTimeUp();
                return;
            }
            
            // Mostrar tempo restante ao invés de tempo decorrido
            long remaining = MAX_TIME_MS - elapsed;
            txtTimer.setText(formatElapsed(remaining));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txtTimer = findViewById(R.id.txtTimer);
        btnPause = findViewById(R.id.btnPause);

        questionText = findViewById(R.id.txtQuestion);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        option5 = findViewById(R.id.option5);
        nextBtn = findViewById(R.id.btnNext);
        btnFinish = findViewById(R.id.btnFinish);

        // Setup check listeners that enforce maxSelections
        CompoundButton.OnCheckedChangeListener chListener = (buttonView, isChecked) -> {
            if (suppressCheckListener) return;
            if (!isChecked) return;

            if (maxSelections == 1) {
                // behave like radio: uncheck others
                suppressCheckListener = true;
                if (buttonView != option1) option1.setChecked(false);
                if (buttonView != option2) option2.setChecked(false);
                if (buttonView != option3) option3.setChecked(false);
                if (buttonView != option4) option4.setChecked(false);
                if (buttonView != option5) option5.setChecked(false);
                suppressCheckListener = false;
                return;
            }

            int cnt = getCheckedCount();
            if (cnt > maxSelections) {
                suppressCheckListener = true;
                ((CompoundButton) buttonView).setChecked(false);
                suppressCheckListener = false;
                Toast.makeText(QuizActivity.this, "Selecione apenas " + maxSelections + " opção(ões).", Toast.LENGTH_SHORT).show();
            }
        };

        option1.setOnCheckedChangeListener(chListener);
        option2.setOnCheckedChangeListener(chListener);
        option3.setOnCheckedChangeListener(chListener);
        option4.setOnCheckedChangeListener(chListener);
        option5.setOnCheckedChangeListener(chListener);

        String asset = getIntent().getStringExtra("SIMULADO_ASSET");
        if (asset == null || asset.isEmpty()) asset = "questions.json";
        final String simAsset = asset;
        List<Question> allQuestions = QuestionBank.getQuestions(this, asset);
        
        // Embaralhar questões para apresentar em ordem aleatória
        Collections.shuffle(allQuestions);
        
        // Sempre usar todas as questões (65)
        questionList = allQuestions;

        // initialize userAnswers with empty lists for each question
        for (int i = 0; i < questionList.size(); i++) {
            userAnswers.add(new ArrayList<>());
        }

        loadQuestion();

        nextBtn.setOnClickListener(v -> {
            // record answer for current question (if any) and update score
            checkAnswer();
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                loadQuestion();
            } else {
                // stop timer callbacks before finishing
                timerHandler.removeCallbacks(timerRunnable);
                Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                intent.putExtra("SCORE", score);
                intent.putExtra("TOTAL", questionList.size());
                intent.putExtra("ANSWERED", answeredCount);
                // send elapsed ms
                long elapsed = elapsedBeforePause + (isPaused ? 0L : (SystemClock.uptimeMillis() - startTime));
                intent.putExtra("ELAPSED_MS", elapsed);
                intent.putExtra("SIMULADO_ASSET", simAsset);
                // build JSON of user answers
                JSONArray outer = new JSONArray();
                for (List<Integer> la : userAnswers) {
                    JSONArray inner = new JSONArray();
                    for (Integer idx : la) inner.put(idx);
                    outer.put(inner);
                }
                // also serialize the questions currently in questionList
                JSONArray qArr = new JSONArray();
                try {
                    for (Question qq : questionList) {
                        JSONObject jo = new JSONObject();
                        String full = qq.getQuestion() == null ? "" : qq.getQuestion();
                        String label = null;
                        String body = full;
                        java.util.regex.Matcher m = java.util.regex.Pattern.compile("^\\s*(\\d+\\/\\d+)\\s*-\\s*(.*)").matcher(full);
                        if (m.find()) {
                            label = m.group(1);
                            body = m.group(2);
                        }
                        if (label != null) jo.put("originalLabel", label);
                        jo.put("question", body);
                        JSONArray opts = new JSONArray();
                        for (String s : qq.getOptions()) opts.put(s);
                        jo.put("options", opts);
                        JSONArray ca = new JSONArray();
                        for (Integer c : qq.getCorrectAnswers()) ca.put(c);
                        jo.put("correctAnswers", ca);
                        qArr.put(jo);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
                intent.putExtra("QUESTIONS_JSON", qArr.toString());
                intent.putExtra("USER_ANSWERS_JSON", outer.toString());
                startActivity(intent);
                finish();
            }
        });

        // Finish early button
        btnFinish.setOnClickListener(v -> {
            // include current answer (if any) before finishing
            checkAnswer();
            // stop timer callbacks and go to results with current score
            timerHandler.removeCallbacks(timerRunnable);
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL", questionList.size());
            intent.putExtra("ANSWERED", answeredCount);
            long elapsed = elapsedBeforePause + (isPaused ? 0L : (SystemClock.uptimeMillis() - startTime));
            intent.putExtra("ELAPSED_MS", elapsed);
            intent.putExtra("SIMULADO_ASSET", simAsset);
            JSONArray outer2 = new JSONArray();
            for (List<Integer> la : userAnswers) {
                JSONArray inner = new JSONArray();
                for (Integer idx : la) inner.put(idx);
                outer2.put(inner);
            }
            // also serialize the questions currently in questionList so review shows the exact subset/order
            JSONArray qArr2 = new JSONArray();
            try {
                for (Question qq : questionList) {
                    JSONObject jo = new JSONObject();
                    String full = qq.getQuestion() == null ? "" : qq.getQuestion();
                    String label = null;
                    String body = full;
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("^\\s*(\\d+\\/\\d+)\\s*-\\s*(.*)").matcher(full);
                    if (m.find()) {
                        label = m.group(1);
                        body = m.group(2);
                    }
                    if (label != null) jo.put("originalLabel", label);
                    jo.put("question", body);
                    JSONArray opts = new JSONArray();
                    for (String s : qq.getOptions()) opts.put(s);
                    jo.put("options", opts);
                    JSONArray ca = new JSONArray();
                    for (Integer c : qq.getCorrectAnswers()) ca.put(c);
                    jo.put("correctAnswers", ca);
                    qArr2.put(jo);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            intent.putExtra("QUESTIONS_JSON", qArr2.toString());
            intent.putExtra("USER_ANSWERS_JSON", outer2.toString());
            startActivity(intent);
            finish();
        });

        btnPause.setOnClickListener(v -> {
            if (isPaused) {
                // resume
                startTime = SystemClock.uptimeMillis();
                timerHandler.postDelayed(timerRunnable, 0);
                btnPause.setText("Pausar");
                isPaused = false;
            } else {
                // pause
                elapsedBeforePause += SystemClock.uptimeMillis() - startTime;
                timerHandler.removeCallbacks(timerRunnable);
                btnPause.setText("Retomar");
                isPaused = true;
            }
        });

        // start timer
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void finishQuizTimeUp() {
        // Mostrar mensagem que o tempo acabou
        Toast.makeText(this, "Tempo esgotado! O simulado será finalizado.", Toast.LENGTH_LONG).show();
        
        // Incluir resposta atual antes de finalizar
        checkAnswer();
        
        String simAsset = getIntent().getStringExtra("SIMULADO_ASSET");
        if (simAsset == null || simAsset.isEmpty()) simAsset = "questions.json";
        
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionList.size());
        intent.putExtra("ANSWERED", answeredCount);
        intent.putExtra("ELAPSED_MS", MAX_TIME_MS); // Tempo máximo atingido
        intent.putExtra("SIMULADO_ASSET", simAsset);
        intent.putExtra("TIME_UP", true); // Flag para indicar que o tempo acabou
        
        // Serializar respostas do usuário
        JSONArray outer = new JSONArray();
        for (List<Integer> la : userAnswers) {
            JSONArray inner = new JSONArray();
            for (Integer idx : la) inner.put(idx);
            outer.put(inner);
        }
        
        // Serializar questões
        JSONArray qArr = new JSONArray();
        try {
            for (Question qq : questionList) {
                JSONObject jo = new JSONObject();
                String full = qq.getQuestion() == null ? "" : qq.getQuestion();
                String label = null;
                String body = full;
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("^\\s*(\\d+\\/\\d+)\\s*-\\s*(.*)").matcher(full);
                if (m.find()) {
                    label = m.group(1);
                    body = m.group(2);
                }
                if (label != null) jo.put("originalLabel", label);
                jo.put("question", body);
                JSONArray opts = new JSONArray();
                for (String s : qq.getOptions()) opts.put(s);
                jo.put("options", opts);
                JSONArray ca = new JSONArray();
                for (Integer c : qq.getCorrectAnswers()) ca.put(c);
                jo.put("correctAnswers", ca);
                qArr.put(jo);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        
        intent.putExtra("QUESTIONS_JSON", qArr.toString());
        intent.putExtra("USER_ANSWERS_JSON", outer.toString());
        startActivity(intent);
        finish();
    }

    private String formatElapsed(long ms) {
        long seconds = ms / 1000;
        long hrs = seconds / 3600;
        long mins = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }

    private void loadQuestion() {
        Question q = questionList.get(currentQuestionIndex);
        // Remove any existing numbering like "1/65 - " that may be present in the JSON
        String raw = q.getQuestion() == null ? "" : q.getQuestion();
        String cleaned = raw.replaceFirst("^\\s*\\d+\\/\\d+\\s*-\\s*", "");
        String header = (currentQuestionIndex + 1) + "/" + questionList.size() + " - ";
        questionText.setText(header + cleaned);

        List<String> opts = q.getOptions();

        // determine max selections allowed for this question
        maxSelections = computeMaxSelections(raw);

        // Helper to set or hide option views depending on number of options
        if (opts.size() > 0) { option1.setText(opts.get(0)); option1.setVisibility(View.VISIBLE); } else { option1.setText(""); option1.setVisibility(View.GONE); }
        if (opts.size() > 1) { option2.setText(opts.get(1)); option2.setVisibility(View.VISIBLE); } else { option2.setText(""); option2.setVisibility(View.GONE); }
        if (opts.size() > 2) { option3.setText(opts.get(2)); option3.setVisibility(View.VISIBLE); } else { option3.setText(""); option3.setVisibility(View.GONE); }
        if (opts.size() > 3) { option4.setText(opts.get(3)); option4.setVisibility(View.VISIBLE); } else { option4.setText(""); option4.setVisibility(View.GONE); }
        if (opts.size() > 4) { option5.setText(opts.get(4)); option5.setVisibility(View.VISIBLE); } else { option5.setText(""); option5.setVisibility(View.GONE); }

        option1.setChecked(false);
        option2.setChecked(false);
        option3.setChecked(false);
        option4.setChecked(false);
        option5.setChecked(false);
    }

    /**
     * Evaluate current question. If user selected at least one option, this counts as answered.
     * Increments answeredCount and score (if correct). Returns true if the question was answered.
     */
    private boolean checkAnswer() {
        Question q = questionList.get(currentQuestionIndex);
        // record selected indices for review
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < q.getOptions().size(); i++) {
            boolean checked = false;
            switch (i) {
                case 0: checked = option1.isChecked(); break;
                case 1: checked = option2.isChecked(); break;
                case 2: checked = option3.isChecked(); break;
                case 3: checked = option4.isChecked(); break;
                case 4: checked = option5.isChecked(); break;
            }
            if (checked) selected.add(i);
        }
        userAnswers.set(currentQuestionIndex, selected);
        boolean anyChecked = false;
        boolean correct = true;

        for (int i = 0; i < q.getOptions().size(); i++) {
            boolean checked = false;
            switch (i) {
                case 0: checked = option1.isChecked(); break;
                case 1: checked = option2.isChecked(); break;
                case 2: checked = option3.isChecked(); break;
                case 3: checked = option4.isChecked(); break;
                case 4: checked = option5.isChecked(); break;
            }

            if (checked) anyChecked = true;

            if (q.getCorrectAnswers().contains(i)) {
                if (!checked) correct = false;
            } else {
                if (checked) correct = false;
            }
        }

        if (anyChecked) {
            answeredCount++;
            if (correct) {
                score++;
            }
        }

        return anyChecked;
    }

    private int getCheckedCount() {
        int c = 0;
        if (option1.isChecked()) c++;
        if (option2.isChecked()) c++;
        if (option3.isChecked()) c++;
        if (option4.isChecked()) c++;
        if (option5.isChecked()) c++;
        return c;
    }

    private int computeMaxSelections(String text) {
        if (text == null) return 1;
        String t = text.toUpperCase();
        // If question explicitly instructs selection, try to extract the number or word after 'SELECIONE'
        if (t.contains("SELECIONE")) {
            int idx = t.indexOf("SELECIONE");
            String sub = t.substring(idx, Math.min(t.length(), idx + 60)); // small window after the word

            // check for numeric digits first in the window
            Matcher mNum = Pattern.compile("(\\d+)").matcher(sub);
            if (mNum.find()) {
                try { return Integer.parseInt(mNum.group(1)); } catch (NumberFormatException ex) { }
            }

            // check common Portuguese words for numbers
            if (sub.contains("DUAS") || sub.contains("DOIS")) return 2;
            if (sub.contains("TRÊS") || sub.contains("TRES")) return 3;
            if (sub.contains("QUATRO")) return 4;
            if (sub.contains("CINCO")) return 5;
        }

        return 1;
    }
}

