package com.example.firebase;

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private List<ItemList> itemList;
    private MyAdapter myAdapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // inisialisasi Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // inisialisasi komponen UI
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rcvLibrary);
        floatingActionButton = findViewById(R.id.floatAddLibrary);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");

        // atur RecyclerView
        itemList = new ArrayList<>();
        myAdapter = new MyAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        // listener tombol floating action
        floatingActionButton.setOnClickListener(v -> {
            Intent toAddPage = new Intent(MainActivity.this, LibraryAdd.class);
            startActivity(toAddPage);
        });

        myAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(MainActivity.this, LibraryDetail.class);
            intent.putExtra("id", item.getId()); // Kirim id
            intent.putExtra("judul", item.getJudul());
            intent.putExtra("kategori", item.getKategori());
            intent.putExtra("keterangan", item.getKeterangan());
            intent.putExtra("imageUrl", item.getImageUrl());
            startActivity(intent);
        });

        // terapkan window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // memuat data awal
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // memuat data setiap kali aktivitas menjadi aktif kembali
        getData();
    }

    private void getData() {
        progressDialog.show();
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ItemList item = new ItemList(
                                    document.getString("judul"),
                                    document.getString("kategori"),
                                    document.getString("keterangan"),
                                    document.getString("imageUrl")
                            );
                            item.setId(document.getId());
                            itemList.add(item);
                            Log.d("data", document.getId() + " => " + document.getData());
                        }
                        myAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("data", "Error getting documents.", task.getException());
                    }
                    progressDialog.dismiss();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, DefaultActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
