package com.example.firebase;

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.app.ProgressDialog;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // initialize Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // initialize UI components
        recyclerView = findViewById(R.id.rcvLibrary);
        floatingActionButton = findViewById(R.id.floatAddLibrary);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");

        // set up RecyclerView
        itemList = new ArrayList<>();
        myAdapter = new MyAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        // floating action button listener
        floatingActionButton.setOnClickListener(v -> {
            Intent toAddPage = new Intent(MainActivity.this, LibraryAdd.class);
            startActivity(toAddPage);
        });

        myAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(MainActivity.this, LibraryDetail.class);
            intent.putExtra("id", item.getId()); // Send the id
            intent.putExtra("judul", item.getJudul());
            intent.putExtra("kategori", item.getKategori());
            intent.putExtra("keterangan", item.getKeterangan());
            intent.putExtra("imageUrl", item.getImageUrl());
            startActivity(intent);
        });

        // apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // load data
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
}

