package com.example.notesnest.createnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesnest.R;
import com.example.notesnest.dashboard.DashboardActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Random;

public class DisplayNotesActivity extends AppCompatActivity {

    LinearLayout image;
    ImageView new_note,back_button;
    RecyclerView notesList;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Note,ViewHolder> adapter;
    FirestoreRecyclerOptions<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_notes);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        Random random = new Random();
        image = findViewById(R.id.empty_img);
        new_note = findViewById(R.id.add_note);
        notesList = findViewById(R.id.notes);
        back_button = findViewById(R.id.arrow_back);

        new_note.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),NewNoteActivity.class);
            intent.putExtra("activity","new note");
            intent.putExtra("title","New Note");
            intent.putExtra("color", Color.argb(85,random.nextInt(255),random.nextInt(255),random.nextInt(255)));
            startActivity(intent);
            finish();
        });

        back_button.setOnClickListener(v -> {
            startActivity(new Intent(DisplayNotesActivity.this, DashboardActivity.class));
            onBackPressed();
        });

        Query query = firestore.collection("notes-nest").document(user.getUid()).collection("notes");
        notes = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query,Note.class).build();
        adapter = new FirestoreRecyclerAdapter<Note, ViewHolder>(notes) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int i, @NonNull Note note) {
                holder.note_title.setText(note.getTitle());
                holder.view.getBackground().setColorFilter(note.getColor(), PorterDuff.Mode.SRC_ATOP);

                if(notes.getSnapshots().size()==0){
                    image.setVisibility(View.VISIBLE);
                    notesList.setVisibility(View.INVISIBLE);
                }else{
                    image.setVisibility(View.INVISIBLE);
                    notesList.setVisibility(View.VISIBLE);
                }

                holder.view.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), NewNoteActivity.class);
                    intent.putExtra("title",note.getTitle());
                    intent.putExtra("content",note.getContent());
                    intent.putExtra("color",note.getColor());
                    intent.putExtra("id",adapter.getSnapshots().getSnapshot(holder.getBindingAdapterPosition()).getId());
                    v.getContext().startActivity(intent);
                    finish();
                });

                holder.delete_icon.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete the note?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            DocumentReference reference = firestore.collection("notes-nest").document(user.getUid()).collection("notes").document(adapter.getSnapshots().getSnapshot(holder.getAdapterPosition()).getId());
                            reference.delete().addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "Note Deleted Successfully!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Could Not Delete Note! Try Again...", Toast.LENGTH_SHORT).show());
                            if(notes.getSnapshots().size()==0 || holder.getBindingAdapterPosition()==0){
                                image.setVisibility(View.VISIBLE);
                                notesList.setVisibility(View.INVISIBLE);
                            }else{
                                image.setVisibility(View.INVISIBLE);
                                notesList.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("No",null)
                        .setIcon(R.drawable.alert_icon)
                        .show());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card,parent,false);
                return new ViewHolder(view);
            }
        };
        notesList.setLayoutManager(new LinearLayoutManager(this));
        notesList.setAdapter(adapter);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView note_title;
        ImageView delete_icon;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            note_title = itemView.findViewById(R.id.note_title);
            delete_icon = itemView.findViewById(R.id.delete_icon);
            view = itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}