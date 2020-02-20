package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;


public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    RecyclerView recyclerView;

    private String post_key;
    private String name;
    private String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Task Manager");

        //Firebase..
         mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String uid = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("All Data").child(uid);

        //Recyclerview..

        recyclerView = findViewById(R.id.recyclerid);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        FloatingActionButton fabbtn = findViewById(R.id.fabadd);

        fabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddData();
            }
        });
    }

    private void AddData() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View myview = inflater.inflate(R.layout.inputlayout, null);

        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        final EditText name = myview.findViewById(R.id.name);
        final EditText description = myview.findViewById(R.id.description);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mName = name.getText().toString().trim();
                String mDescription = description.getText().toString().trim();

                if (TextUtils.isEmpty(mName)) {
                    name.setError("Required Field..");

                    return;
                }
                if (TextUtils.isEmpty(mDescription)) {

                    description.setError("Required Field..");

                    return;
                }

                String id = mDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(mName, mDescription, id, mDate);
                assert id != null;
                mDatabase.child(id).setValue(data);
                Toast.makeText(getApplicationContext(), "Data Uploaded", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.itemlayoutdesign, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final Data model) {

                holder.setName(model.getName());
                holder.sedDescription(model.getDescription());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(position).getKey();
                        name = model.getName();
                        description = model.getDescription();

                        updateData();
                    }
                });
            }


        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView Mname = mView.findViewById(R.id.name_item);
            Mname.setText(name);
        }

        void sedDescription(String description) {
            TextView Mdescription = mView.findViewById(R.id.description_item);
            Mdescription.setText(description);
        }

        void setDate(String date) {
            TextView Mdate = mView.findViewById(R.id.date_item);
            Mdate.setText(date);
        }

    }

    public void updateData() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View myview = inflater.inflate(R.layout.update_data, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();

        final EditText mName = myview.findViewById(R.id.name);
        final EditText mDescription = myview.findViewById(R.id.description);


        mName.setText(name);
        mName.setSelection(name.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        Button btnDelete =myview.findViewById(R.id.btnDelete);
        Button btnUpdate =myview.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mName.getText().toString().trim();
                description = mDescription.getText().toString().trim();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(name, description, post_key, mDate);
                mDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}