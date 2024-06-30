package com.example.firebase;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class LibraryAdd extends AppCompatActivity {

    String id = "", judul_param, kategori_param, keterangan_param, image_param;
    private static final int PICK_IMG_REQUEST = 1;

    private EditText judul, kategori, keterangan;
    private ImageView imageView;
    private Button saveLibrary, chooseImage;
    private Uri imageUri;
    private FirebaseFirestore dbLibrary;
    private FirebaseStorage storage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        dbLibrary = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        setContentView(R.layout.activity_library_add);

        // Initialize UI Components
        judul = findViewById(R.id.judul);
        kategori = findViewById(R.id.kategori);
        keterangan = findViewById(R.id.keterangan);
        imageView = findViewById(R.id.imageView);
        saveLibrary = findViewById(R.id.btnAdd);
        chooseImage = findViewById(R.id.btnChooseImage);

        progressDialog = new ProgressDialog(LibraryAdd.this);
        progressDialog.setTitle("Loading...");

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        saveLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String libJudul = judul.getText().toString().trim();
                String libKategori = kategori.getText().toString().trim();
                String libKeterangan = keterangan.getText().toString().trim();

                if (libJudul.isEmpty() || libKategori.isEmpty() || libKeterangan.isEmpty()) {
                    Toast.makeText(LibraryAdd.this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();

                if (imageUri != null) {
                    uploadImageToStorage(libJudul, libKategori, libKeterangan);
                } else {
                    saveData(libJudul, libKategori, libKeterangan, image_param);
                }
            }
        });

        Intent updateOption = getIntent();
        if (updateOption != null && updateOption.hasExtra("id")) {
            id = updateOption.getStringExtra("id");
            judul_param = updateOption.getStringExtra("judul");
            kategori_param = updateOption.getStringExtra("kategori");
            keterangan_param = updateOption.getStringExtra("keterangan");
            image_param = updateOption.getStringExtra("imageUrl");

            judul.setText(judul_param);
            kategori.setText(kategori_param);
            keterangan.setText(keterangan_param);
            Glide.with(this).load(image_param).into(imageView);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EdgeToEdge.enable(this);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMG_REQUEST);
    }

    private void uploadImageToStorage(String judul, String kategori, String keterangan) {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReference().child("library_image/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveData(judul, kategori, keterangan, imageUrl);
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(LibraryAdd.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveData(String judulValue, String kategoriValue, String keteranganValue, String imageUrlValue) {
        Map<String, Object> library = new HashMap<>();
        library.put("judul", judulValue);
        library.put("kategori", kategoriValue);
        library.put("keterangan", keteranganValue);
        library.put("imageUrl", imageUrlValue);

        if (id != null && !id.isEmpty()) {
            dbLibrary.collection("books").document(id)
                    .update(library)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(LibraryAdd.this, "Library updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(LibraryAdd.this, "Error updating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("Library", "Error updating", e);
                    });
        } else {
            dbLibrary.collection("books")
                    .add(library)
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();
                        Toast.makeText(LibraryAdd.this, "Library successfully inserted", Toast.LENGTH_SHORT).show();
                        judul.setText("");
                        keterangan.setText("");
                        kategori.setText("");
                        imageView.setImageResource(0);
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(LibraryAdd.this, "Error adding library: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("Library Add", "Error adding library", e);
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
}

