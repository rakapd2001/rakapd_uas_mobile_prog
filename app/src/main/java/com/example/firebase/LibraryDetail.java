package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class LibraryDetail extends AppCompatActivity {

    TextView judul, kategori, keterangan;
    ImageView libraryImage;
    Button edit, delete;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_library_detail);

        // Initialize UI Components
        judul = findViewById(R.id.detail_judul);
        kategori = findViewById(R.id.detail_kategori);
        keterangan = findViewById(R.id.detail_keterangan);
        libraryImage = findViewById(R.id.detail_image);
        edit = findViewById(R.id.btn_edit);
        delete = findViewById(R.id.btn_delete);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String judul_value = intent.getStringExtra("judul");
        String kategori_value = intent.getStringExtra("kategori");
        String keterangan_value = intent.getStringExtra("keterangan");
        String imageUrl_value = intent.getStringExtra("imageUrl");

        judul.setText(judul_value);
        kategori.setText(kategori_value);
        keterangan.setText(keterangan_value);
        Glide.with(this).load(imageUrl_value).into(libraryImage);

        edit.setOnClickListener(v -> {
            Intent intent1 = new Intent(LibraryDetail.this, LibraryAdd.class);
            intent1.putExtra("id", id);
            intent1.putExtra("judul", judul_value);
            intent1.putExtra("kategori", kategori_value);
            intent1.putExtra("keterangan", keterangan_value);
            intent1.putExtra("imageUrl", imageUrl_value);
            startActivity(intent1);
        });

        delete.setOnClickListener(v -> {
            if (id != null && !id.isEmpty()) {
                db.collection("books").document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(LibraryDetail.this, "Library deleted successfully",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent12 = new Intent(LibraryDetail.this, MainActivity.class);
                            intent12.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent12);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(LibraryDetail.this, "Error deleting: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.w("Library", "Error deleting", e);
                        });
            } else {
                Toast.makeText(LibraryDetail.this, "Document ID is null or empty",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

