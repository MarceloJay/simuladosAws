package com.jaydev.awsquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jaydev.awsquiz.data.QuestionBank;
import com.jaydev.awsquiz.models.Question;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private Button btnBackToResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = findViewById(R.id.recyclerReview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBackToResult = findViewById(R.id.btnBackToResult);
        
        // Forçar cor azul no botão (funciona em modo dark)
        btnBackToResult.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF2D6CDF")));
        
        btnBackToResult.setOnClickListener(v -> finish());

        try {
            Intent intent = getIntent();
            String questionsJson = intent.getStringExtra("QUESTIONS_JSON");
            List<Question> questions = new ArrayList<>();
            if (questionsJson != null) {
                try {
                    JSONArray qarr = new JSONArray(questionsJson);
                    for (int i = 0; i < qarr.length(); i++) {
                        JSONObject jo = qarr.optJSONObject(i);
                        if (jo == null) continue;
                        String qtext = jo.optString("question", "");
                        String lbl = jo.optString("originalLabel", "");
                        JSONArray opts = jo.optJSONArray("options");
                        List<String> optList = new ArrayList<>();
                        if (opts != null) {
                            for (int j = 0; j < opts.length(); j++) optList.add(opts.optString(j));
                        }
                        JSONArray ca = jo.optJSONArray("correctAnswers");
                        List<Integer> caList = new ArrayList<>();
                        if (ca != null) {
                            for (int j = 0; j < ca.length(); j++) caList.add(ca.optInt(j));
                        }
                        // do NOT prefix with originalLabel — show only the question text in the review
                        questions.add(new Question(qtext, optList, caList));
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
            if (questions.isEmpty()) {
                String asset = intent.getStringExtra("SIMULADO_ASSET");
                if (asset == null || asset.isEmpty()) asset = "questions.json";
                questions = QuestionBank.getQuestions(this, asset);
            }

            if (questions == null || questions.isEmpty()) {
                Toast.makeText(this, "Nenhuma questão disponível para revisão.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // load user answers JSON
            String uaJson = intent.getStringExtra("USER_ANSWERS_JSON");
            List<List<Integer>> userAnswers = new ArrayList<>();
            if (uaJson != null) {
                try {
                    JSONArray outer = new JSONArray(uaJson);
                    for (int i = 0; i < outer.length(); i++) {
                        JSONArray inner = outer.optJSONArray(i);
                        List<Integer> la = new ArrayList<>();
                        if (inner != null) {
                          for (int j = 0; j < inner.length(); j++) la.add(inner.optInt(j));
                        }
                        userAnswers.add(la);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // ensure sizes match; if not, trim or pad
            if (userAnswers.size() > questions.size()) {
                userAnswers = userAnswers.subList(0, questions.size());
            } else {
                while (userAnswers.size() < questions.size()) userAnswers.add(new ArrayList<>());
            }

            // filter to only questions that the user answered
            List<Question> filteredQuestions = new ArrayList<>();
            List<List<Integer>> filteredAnswers = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                List<Integer> ua = userAnswers.get(i);
                if (ua != null && !ua.isEmpty()) {
                    filteredQuestions.add(questions.get(i));
                    filteredAnswers.add(ua);
                }
            }

            if (filteredQuestions.isEmpty()) {
                Toast.makeText(this, "Você não respondeu nenhuma questão para revisar.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            adapter = new ReviewAdapter(filteredQuestions, filteredAnswers);
            recyclerView.setAdapter(adapter);

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Erro ao abrir revisão do simulado.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
