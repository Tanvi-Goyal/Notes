package com.example.tanvi.notes;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class Notes extends Fragment {

    static LinearLayout noNotes;
    static RecyclerAdapter recyclerAdapter;
    ArrayList<Note> notes = new ArrayList<>();
    RecyclerView recyclerView;
    FloatingActionButton fb;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    private DatabaseHelper db;

    public Notes() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        noNotes = view.findViewById(R.id.noNotesView);
        recyclerView = view.findViewById(R.id.rvMain);

        db = new DatabaseHelper(view.getContext());

        notes.addAll(db.getAllNotes());
//        notes = inflateArrayList(notes);

        recyclerAdapter = new RecyclerAdapter(view, notes);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerAdapter);
        toggleEmptyNotes();

        fb = view.findViewById(R.id.add_btn);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v, R.layout.add_note);
            }
        });

        return view;
    }

    private void showDialog(View v, int layout) {
        dialogBuilder = new AlertDialog.Builder(v.getContext());
        View layoutView = LayoutInflater.from(v.getContext()).inflate(layout, null);

        final EditText editText = layoutView.findViewById(R.id.expaned_notes_text);
        Button cancel = layoutView.findViewById(R.id.cancel_btn);
        Button create = layoutView.findViewById(R.id.create_btn);

        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString() != "") {
                    Log.wtf("TAG", editText.getText().toString());
                    createNote(editText.getText().toString());
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(v.getContext(), "Cannot add empty note.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            noNotes.setVisibility(View.GONE);
        } else {
            noNotes.setVisibility(View.VISIBLE);
        }
    }


    private void createNote(String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note);

        // get the newly inserted note from db
        Note n = db.getNote(id);

        if (n != null) {
            // adding new note to array list at 0 position
            notes.add(0, n);

            // refreshing the list
            recyclerAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    private ArrayList<String> inflateArrayList(ArrayList<String> notes) {

        notes.add("Talk is cheap. Show me the code.");
        notes.add("when you don't create things, you become defined by your tastes rather than ability. your tastes only narrow & exclude people. so create.");
        notes.add("Always code as if the guy who ends up maintaining your code will be a violent psychopath who knows where you live.");
        notes.add("Programs must be written for people to read, and only incidentally for machines to execute.");
        notes.add("Programming today is a race between software engineers striving to build bigger and better idiot-proof programs, and the Universe trying to produce bigger and better idiots. So far, the Universe is winning.");
        notes.add("Punishments include such things as flashbacks, flooding of unbearable emotions, painful body memories, flooding of memories in which the survivor perpetrated against others, self-harm, and suicide attempts.");
        notes.add("The best programs are written so that computing machines can perform them quickly and so that human beings can understand them clearly. A programmer is ideally an essayist who works with traditional aesthetic and literary forms as well as mathematical concepts, to communicate the way that an algorithm works and to convince a reader that the results will be correct.");
        notes.add("That's the thing about people who think they hate computers. What they really hate is lousy programmers.");
        notes.add("Any fool can write code that a computer can understand. Good programmers write code that humans can understand.");
        notes.add("Give a man a program, frustrate him for a day.\n" +
                "Teach a man to program, frustrate him for a lifetime.");
        return notes;
    }


}
