package com.jaydev.awsquiz.data;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jaydev.awsquiz.models.Question;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class QuestionBank {

    public static List<Question> getQuestions(Context context, String asset) {
        List<Question> questions = new ArrayList<>();

        // Try to load from assets/questions.json
        if (context != null) {
            try (InputStream is = context.getAssets().open("questions.json")) {
                int size = is.available();
                byte[] buffer = new byte[size];
                int read = is.read(buffer);
                String json = new String(buffer, 0, Math.max(0, read), "UTF-8");

                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String qText = obj.optString("question", "");

                    List<String> options = new ArrayList<>();
                    JSONArray opts = obj.optJSONArray("options");
                    if (opts != null) {
                        for (int j = 0; j < opts.length(); j++) {
                            options.add(opts.optString(j, ""));
                        }
                    }

                    List<Integer> correct = new ArrayList<>();
                    JSONArray cor = obj.optJSONArray("correctAnswers");
                    if (cor != null) {
                        for (int j = 0; j < cor.length(); j++) {
                            correct.add(cor.optInt(j, -1));
                        }
                    }

                    questions.add(new Question(qText, options, correct));
                }

                if (!questions.isEmpty()) {
                    return questions;
                }

            } catch (IOException | JSONException e) {
                // Failed to read/parse the JSON file; fall back to hardcoded questions below
                e.printStackTrace();
            }
        }

        // Fallback: hardcoded examples (previous behavior)
        questions.add(new Question(
                "Which of the following policies grant the necessary permissions required to access your Amazon S3 resources? (Select TWO)",
                java.util.Arrays.asList(
                        "Routing policies",
                        "Network access control policies",
                        "User policies",
                        "Bucket policies",
                        "Object policies"
                ),
                java.util.Arrays.asList(2, 3) // User policies, Bucket policies
        ));

        questions.add(new Question(
                "Which AWS Cost Management tool enables you to forecast future costs and usage of AWS resources based on past consumption?",
                java.util.Arrays.asList(
                        "AWS Pricing Calculator",
                        "Amazon Forecast",
                        "Cost Explorer",
                        "AWS Cost and Usage report"
                ),
                java.util.Arrays.asList(2) // Cost Explorer
        ));

        questions.add(new Question(
                "Which service does AWS use to notify you when AWS is experiencing events that may impact you?",
                java.util.Arrays.asList(
                        "Amazon SNS",
                        "AWS Health",
                        "AWS Support Center",
                        "Amazon CloudWatch"
                ),
                java.util.Arrays.asList(1) // AWS Health
        ));

        return questions;
    }
}

