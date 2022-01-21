package com.example.notesnest.uploadnotes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesnest.R;
import com.example.notesnest.dashboard.DashboardActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DisplayUploadsActivity extends AppCompatActivity {

    LinearLayout image;
    RelativeLayout action_bar;
    RecyclerView uploadsList;
    TextView title;
    ImageView back_button,add_button;
    ProgressBar progressBar;
    ActivityResultLauncher<Intent> uploadActivityResultLauncher;
    FirebaseRecyclerAdapter<PDF,ViewHolder> adapter;
    AlertDialog.Builder builder;

    String pdfName;

    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_uploads);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("notes-nest/"+user.getUid()+"/uploads");

        title = findViewById(R.id.page_title);
        action_bar = findViewById(R.id.notes_action_bar);
        back_button = findViewById(R.id.arrow_back);
        add_button = findViewById(R.id.add_note);
        progressBar = findViewById(R.id.progressBar2);
        uploadsList = findViewById(R.id.uploads);
        image = findViewById(R.id.empty_pdf);

        progressBar.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);
        title.setText("Your Uploads");
        action_bar.setBackgroundColor(Color.RED);
        add_button.setOnClickListener(v -> {
            builder = new AlertDialog.Builder(DisplayUploadsActivity.this);
            builder.setTitle("Upload a PDF");

            View upload_dialog = getLayoutInflater().inflate(R.layout.upload_dialog,null);
            builder.setView(upload_dialog);
            builder.setPositiveButton("Choose", (dialog, which) -> {
                final EditText pdf_name = upload_dialog.findViewById(R.id.pdf_name);
                String pdfNameInDialog = pdf_name.getText().toString();
                sendDialogDataToActivity(pdfNameInDialog);
                requestUpload();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
            });
            builder.create().show();
        });
        back_button.setOnClickListener(v -> {
            startActivity(new Intent(DisplayUploadsActivity.this, DashboardActivity.class));
            onBackPressed();
        });

        FirebaseRecyclerOptions<PDF> uploads = new FirebaseRecyclerOptions.Builder<PDF>().setQuery(databaseReference,PDF.class).build();

        new Handler().postDelayed(() -> {
            if(uploads.getSnapshots().size()==0) {
                image.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        },3000);

        adapter = new FirebaseRecyclerAdapter<PDF, ViewHolder>(uploads) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull PDF pdf) {
                viewHolder.pdf_title.setText(pdf.getName());
                if(uploads.getSnapshots().size()==0){
                    image.setVisibility(View.VISIBLE);
                    uploadsList.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.GONE);
                }else{
                    image.setVisibility(View.INVISIBLE);
                    uploadsList.setVisibility(View.VISIBLE);
                }

                viewHolder.view.setOnClickListener(v -> {
                    Intent intent = new Intent(DisplayUploadsActivity.this,PDFViewActivity.class);
                    intent.putExtra("url",Uri.parse(pdf.getUrl()));
                    startActivity(intent);
                });

                viewHolder.delete_icon.setOnClickListener(v -> new android.app.AlertDialog.Builder(v.getContext())
                        .setTitle("Delete PDF")
                        .setMessage("Are you sure you want to delete the PDF?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            progressBar.setVisibility(View.VISIBLE);
                            String key = adapter.getSnapshots().getSnapshot(viewHolder.getBindingAdapterPosition()).getKey();
                            databaseReference.child(key).removeValue().addOnSuccessListener(unused -> storageReference.child("notes-nest/"+user.getUid()+"/uploads").child(pdf.getName()+".pdf").delete().addOnSuccessListener(unused1 -> {
                                if(uploads.getSnapshots().size()==0 || viewHolder.getBindingAdapterPosition()==0){
                                    image.setVisibility(View.VISIBLE);
                                    uploadsList.setVisibility(View.INVISIBLE);
                                }else{
                                    image.setVisibility(View.INVISIBLE);
                                    uploadsList.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(DisplayUploadsActivity.this, "PDF Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(DisplayUploadsActivity.this, "Error Deleting File! Try Again...", Toast.LENGTH_SHORT).show();
                                Log.d("failure",String.valueOf(e));
                                progressBar.setVisibility(View.GONE);
                            })).addOnFailureListener(e -> {
                                Toast.makeText(DisplayUploadsActivity.this, "Error Deleting File! Try Again...", Toast.LENGTH_SHORT).show();
                                Log.d("failure",String.valueOf(e));
                                progressBar.setVisibility(View.GONE);
                            });
                        })
                        .setNegativeButton("No",null)
                        .setIcon(R.drawable.alert_icon)
                        .show());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                System.out.println("PDFS = "+uploads.getSnapshots().size());
                progressBar.setVisibility(View.GONE);
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_card,parent,false);
                return new ViewHolder(view);
            }
        };
        uploadsList.setLayoutManager(new LinearLayoutManager(this));
        uploadsList.setAdapter(adapter);
        uploadActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                progressBar.setVisibility(View.VISIBLE);
                Intent data = result.getData();
                Uri pdfUri = data.getData();
                final String timestamp = "" + System.currentTimeMillis();
                StorageReference sReference = storageReference.child("notes-nest/"+user.getUid()+"/uploads").child(pdfName+".pdf");
                sReference.putFile(pdfUri).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uri.isComplete());
                    Uri url = uri.getResult();
                    PDF pdf = new PDF(pdfName,url.toString());
                    databaseReference.child(databaseReference.push().getKey()).setValue(pdf).addOnFailureListener(e -> {
                        Toast.makeText(DisplayUploadsActivity.this,"Error Uploading File! Try Again..." , Toast.LENGTH_SHORT).show();
                        Log.d("failure",String.valueOf(e));
                        progressBar.setVisibility(View.GONE);
                    }).addOnSuccessListener(unused -> {
                        Toast.makeText(DisplayUploadsActivity.this, "File Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(DisplayUploadsActivity.this, "Error Uploading File! Try Again...", Toast.LENGTH_SHORT).show();
                    Log.d("failure",String.valueOf(e));
                    progressBar.setVisibility(View.GONE);
                }).addOnProgressListener(snapshot -> {
                });
            }else{
                Toast.makeText(DisplayUploadsActivity.this, "PDF not selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDialogDataToActivity(String pdfNameInDialog) {
        pdfName = pdfNameInDialog;
    }

    private void requestUpload() {
        if(ContextCompat.checkSelfPermission(DisplayUploadsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            uploadActivity();
        else {
            ActivityCompat.requestPermissions(DisplayUploadsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            uploadActivity();
        }else{
            Toast.makeText(DisplayUploadsActivity.this, "Permissions Not Provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadActivity(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/pdf");
        uploadActivityResultLauncher.launch(galleryIntent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView pdf_title;
        ImageView delete_icon;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pdf_title = itemView.findViewById(R.id.pdf_title);
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
        adapter.stopListening();
    }
}