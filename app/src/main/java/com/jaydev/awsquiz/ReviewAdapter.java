package com.jaydev.awsquiz;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jaydev.awsquiz.models.Question;
import java.util.List;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.Spanned;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Question> questions;
    private List<List<Integer>> userAnswers;

    public ReviewAdapter(List<Question> questions, List<List<Integer>> userAnswers) {
        this.questions = questions;
        this.userAnswers = userAnswers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = questions.get(position);
        List<Integer> ua = userAnswers.get(position);

        holder.title.setText((position+1) + ") " + q.getQuestion());

        // build a Spannable summary and highlight correct options with a green background
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (int i = 0; i < q.getOptions().size(); i++) {
            String opt = q.getOptions().get(i);
            boolean correct = q.getCorrectAnswers().contains(i);
            boolean selected = ua.contains(i);

            int start = ssb.length();
            ssb.append((i+1) + ". " + opt);
            int end = ssb.length();

            if (correct) {
                // light green background for correct option
                ssb.append("  (Correta)");
                ssb.setSpan(new BackgroundColorSpan(Color.argb(0x40, 0x00, 0xC8, 0x00)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (selected) {
                ssb.append("  [Sua resposta]");
                if (!correct) {
                    // red translucent background for user's selected answer
                    ssb.setSpan(new BackgroundColorSpan(Color.argb(0x40, 0xFF, 0x00, 0x00)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            if (i < q.getOptions().size() - 1) ssb.append('\n');
        }

        holder.subtitle.setText(ssb);
        // ensure text is readable: black on white and allow wrapping
        holder.title.setTextColor(Color.BLACK);
        holder.title.setTypeface(holder.title.getTypeface(), Typeface.BOLD);
        holder.subtitle.setTextColor(Color.BLACK);
        holder.subtitle.setSingleLine(false);
        holder.subtitle.setMaxLines(50);
        // color item background if user was wrong
        boolean correctOverall = true;
        for (int i = 0; i < q.getOptions().size(); i++) {
            boolean selected = ua.contains(i);
            if (selected != q.getCorrectAnswers().contains(i)) { correctOverall = false; break; }
        }
        holder.itemView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() { return questions.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}
