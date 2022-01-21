package com.example.notesnest.createnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesnest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewNoteActivity extends AppCompatActivity {

    LinearLayout notes_view,color_view;
    RelativeLayout title_view;
    TextView title;
    EditText note_title,note_content;
    ImageView back_button,save_button;
    String noteTitle,noteContent;
    int color;

    ImageView bold,italic,underline,highlight_text,color_text,no_color;
    Button black,red,yellow,green,blue;
    ProgressBar progressBar;

    FirebaseUser user;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        notes_view = findViewById(R.id.notes_view);
        color_view = findViewById(R.id.color_view);
        title_view = findViewById(R.id.note_view);
        title = findViewById(R.id.title);
        note_title = findViewById(R.id.note_title);
        note_content = findViewById(R.id.note_content);
        note_content.setBackgroundResource(android.R.color.transparent);
        back_button = findViewById(R.id.back_button);
        save_button = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progressBar);

        bold = findViewById(R.id.bold);
        italic = findViewById(R.id.italic);
        underline = findViewById(R.id.underline);
        highlight_text = findViewById(R.id.highlight_text);
        color_text = findViewById(R.id.color_text);
        no_color = findViewById(R.id.remove_color);
        black = findViewById(R.id.black);
        red = findViewById(R.id.red);
        yellow = findViewById(R.id.yellow);
        green = findViewById(R.id.green);
        blue = findViewById(R.id.blue);

        highlight_text.setTag("0");
        color_text.setTag("0");

        color_text.setOnClickListener(v -> {
            if(color_text.getTag()=="0"){
                color_text.setBackgroundColor(Color.parseColor("#FF979797"));
                color_text.setTag("1");
                highlight_text.setTag("0");
                highlight_text.setBackgroundColor(Color.WHITE);
                color_view.setVisibility(View.VISIBLE);
            }else if(color_text.getTag()=="1"){
                color_text.setBackgroundColor(Color.WHITE);
                color_text.setTag("0");
                color_view.setVisibility(View.GONE);
            }

        });

        highlight_text.setOnClickListener(v -> {
            if(highlight_text.getTag()=="0"){
                highlight_text.setBackgroundColor(Color.parseColor("#FF979797"));
                highlight_text.setTag("1");
                color_text.setTag("0");
                color_text.setBackgroundColor(Color.WHITE);
                color_view.setVisibility(View.VISIBLE);
            }else if(highlight_text.getTag()=="1"){
                highlight_text.setBackgroundColor(Color.WHITE);
                highlight_text.setTag("0");
                color_view.setVisibility(View.GONE);
            }

        });

        no_color.setOnClickListener(v->{
            if(color_text.getTag()=="1"){
                Spannable spannableString = new SpannableStringBuilder(note_content.getText());
                spannableString.setSpan(new ForegroundColorSpan(Color.argb(0,Color.red(color),Color.green(color),Color.blue(color))),
                        note_content.getSelectionStart(),
                        note_content.getSelectionEnd(),
                        0);
            }
            if(highlight_text.getTag()=="1"){
                Spannable spannableString = new SpannableStringBuilder(note_content.getText());
                spannableString.setSpan(new BackgroundColorSpan(Color.argb(0,Color.red(color),Color.green(color),Color.blue(color))),
                        note_content.getSelectionStart(),
                        note_content.getSelectionEnd(),
                        0);
            }
        });

        black.setOnClickListener(v->{
            if(color_text.getTag()=="1"){
                changeTextColor(black);
            }
            if(highlight_text.getTag()=="1"){
                changeTextHighlight(black);
            }
        });

        red.setOnClickListener(v->{
            if(color_text.getTag()=="1"){
                changeTextColor(red);
            }
            if(highlight_text.getTag()=="1"){
                changeTextHighlight(red);
            }
        });

        yellow.setOnClickListener(v->{
            if(color_text.getTag()=="1"){
                changeTextColor(yellow);
            }
            if(highlight_text.getTag()=="1"){
                changeTextHighlight(yellow);
            }
        });

        green.setOnClickListener(v->{
            if(color_text.getTag()=="1"){
                changeTextColor(green);
            }
            if(highlight_text.getTag()=="1"){
                changeTextHighlight(green);
            }
        });

        blue.setOnClickListener(v->{
            if(color_text.getTag()=="1"){
                changeTextColor(blue);
            }
            if(highlight_text.getTag()=="1"){
                changeTextHighlight(blue);
            }
        });

        bold.setOnClickListener(v -> textBold());

        italic.setOnClickListener(v -> textItalics());

        underline.setOnClickListener(v -> textUnderline());



        if(getIntent().getStringExtra("activity")!=null) {
            title.setText(getIntent().getStringExtra("title"));
            color = getIntent().getIntExtra("color",0);
            noteTitle = getIntent().getStringExtra("title");
        }
        else {
            color = getIntent().getIntExtra("color",0);
            noteTitle = getIntent().getStringExtra("title");
            noteContent = getIntent().getStringExtra("content");
            title.setText(noteTitle);
            note_title.setText(noteTitle);
            note_content.setText(Html.fromHtml(noteContent,0));
        }
        notes_view.setBackgroundColor(color);

        title_view.setBackgroundColor(Color.argb(250,Color.red(color),Color.green(color),Color.blue(color)));

        save_button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            saveText();
        });

        back_button.setOnClickListener(v -> {
            if(note_title.getText().toString().isEmpty()){
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Unsaved Changes")
                        .setMessage("Save changes to your note!")
                        .setPositiveButton("Ok", (dialog, which) -> {

                        })
                        .setIcon(R.drawable.alert_icon)
                        .show();
            } else if(Html.toHtml(note_content.getText(), 0).equals(noteContent) && note_title.getText().toString().equals(noteTitle)){
                onBackPressed();
            }else{
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Unsaved Changes")
                        .setMessage("Do you want to save the changes made to your note?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            progressBar.setVisibility(View.VISIBLE);
                            saveText();
                        })
                        .setNegativeButton("No", (dialog, which) -> onBackPressed())
                        .setIcon(R.drawable.alert_icon)
                        .show();
            }
        });
    }

    public void saveText(){
        String title = note_title.getText().toString().isEmpty()?"Untitled":note_title.getText().toString();
        String content = Html.toHtml(note_content.getText(),0);
        DocumentReference reference;

        if(getIntent().getStringExtra("activity")!=null) {
            reference = firestore.collection("notes-nest").document(user.getUid()).collection("notes").document();
            Map<String,Object> note = new HashMap<>();
            note.put("title",title);
            note.put("content",content);
            note.put("color",color);
            reference.set(note).addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(), "Note Saved Successfully!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), DisplayNotesActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Error while saving note! Try Again...", Toast.LENGTH_SHORT).show();
                Log.d("upload error", String.valueOf(e));
                progressBar.setVisibility(View.INVISIBLE);
            });
        }
        else {
            String id = getIntent().getStringExtra("id");
            reference = firestore.collection("notes-nest").document(user.getUid()).collection("notes").document(id);
            Map<String,Object> note = new HashMap<>();
            note.put("title",title);
            note.put("content",content);
            note.put("color",color);
            reference.update(note).addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(), "Note Saved Successfully!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), DisplayNotesActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Error while saving note! Try Again...", Toast.LENGTH_SHORT).show();
                Log.d("upload error", String.valueOf(e));
                progressBar.setVisibility(View.INVISIBLE);
            });
        }

    }

    public void textBold(){
        Spannable spannableString = new SpannableStringBuilder(note_content.getText());
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                note_content.getSelectionStart(),
                note_content.getSelectionEnd(),
                0);

        int pos = note_content.getSelectionEnd();
        note_content.setText(spannableString);
        note_content.setSelection(pos);
    }
    public void textItalics(){
        Spannable spannableString = new SpannableStringBuilder(note_content.getText());
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC),
                note_content.getSelectionStart(),
                note_content.getSelectionEnd(),
                0);

        int pos = note_content.getSelectionEnd();
        note_content.setText(spannableString);
        note_content.setSelection(pos);

    }
    public void textUnderline(){
        Spannable spannableString = new SpannableStringBuilder(note_content.getText());
        spannableString.setSpan(new UnderlineSpan(), note_content.getSelectionStart(), note_content.getSelectionEnd(), 0);

        int pos = note_content.getSelectionEnd();
        note_content.setText(spannableString);
        note_content.setSelection(pos);
    }

    public void changeTextColor(Button btn){
        Spannable spannableString = new SpannableStringBuilder(note_content.getText());
        if(btn.getId()==R.id.black)
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF000000")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);
        else if(btn.getId()==R.id.red)
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);
        else if(btn.getId()==R.id.yellow)
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F8E11B")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);
        else if(btn.getId()==R.id.green)
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#4AB64F")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);
        else if(btn.getId()==R.id.blue)
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#008EFF")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);

        int pos = note_content.getSelectionEnd();
        note_content.setText(spannableString);
        note_content.setSelection(pos);
    }

    public void changeTextHighlight(Button btn){
        Spannable spannableString = new SpannableStringBuilder(note_content.getText());
        if(btn.getId()==R.id.black)
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#FF000000")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);
        else if(btn.getId()==R.id.red)
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#FF0000")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);
        else if(btn.getId()==R.id.yellow)
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#F8E11B")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);
        else if(btn.getId()==R.id.green)
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#4AB64F")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);
        else if(btn.getId()==R.id.blue)
            spannableString.setSpan(new BackgroundColorSpan(Color.parseColor("#008EFF")),
                    note_content.getSelectionStart(),
                    note_content.getSelectionEnd(),
                    0);

        int pos = note_content.getSelectionEnd();
        note_content.setText(spannableString);
        note_content.setSelection(pos);
    }
}