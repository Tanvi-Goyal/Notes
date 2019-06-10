package com.example.tanvi.notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerAdapter extends RecyclerView.Adapter<GridHolder> {

    ArrayList<Note> notes = new ArrayList<>();
    Context context;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    private DatabaseHelper db;
    private LayoutInflater mInflater;

    public RecyclerAdapter(View view, ArrayList<Note> notes) {
        this.mInflater = LayoutInflater.from(view.getContext());
        this.context = view.getContext();
        this.notes = notes;

        db = new DatabaseHelper(view.getContext());

    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_rv, viewGroup, false);
        return new GridHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder gridHolder, final int i) {

        Note note = notes.get(i);
        gridHolder.textView.setText(note.getNote());
        gridHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, i);
            }
        });
        // Formatting and displaying timestamp
        gridHolder.timestamp.setText(formatDate(note.getTimestamp()));

        gridHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v, R.layout.expanded_griv_rv, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    private void showDialog(View v, int layout, final int i) {
        dialogBuilder = new AlertDialog.Builder(v.getContext());
        View layoutView = LayoutInflater.from(v.getContext()).inflate(layout, null);
        ImageButton imageButton = layoutView.findViewById(R.id.expanded_delete_menu);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, i);
            }
        });

        final EditText editText = layoutView.findViewById(R.id.expaned_notes_text);
        editText.setText(notes.get(i).getNote());
        dialogBuilder.setView(layoutView);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Button cancel = layoutView.findViewById(R.id.update_cancel);
        Button update = layoutView.findViewById(R.id.update_note);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString() != "") {
                    updateNote(editText.getText().toString(), i);
                    alertDialog.dismiss();
                } else {
                    deleteNote(i);
                    alertDialog.dismiss();
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

    public void showPopup(final View v, final int i) {
        final PopupMenu popup = new PopupMenu(v.getContext(), v);
        final MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                delete(v, item, i);
                return false;
            }
        });


    }

    private void delete(View v, MenuItem item, int i) {

        switch (item.getItemId()) {
            case R.id.delete_option:
                showConfirmDialog(v, i);
            default:
        }
    }

    private void showConfirmDialog(View v, final int i) {

        new android.support.v7.app.AlertDialog.Builder(v.getContext())
                .setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNote(i);

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MMM-YYYY hh:mm");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    private void updateNote(String note, int position) {
        Note n = notes.get(position);
        // updating note text
        n.setNote(note);

        // updating note in db
        db.updateNote(n);

        // refreshing the list
        notes.set(position, n);
        Notes.recyclerAdapter.notifyItemChanged(position);

    }

    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notes.get(position));

        // removing the note from the list
        notes.remove(position);
        Notes.recyclerAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();

    }

    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            Notes.noNotes.setVisibility(View.GONE);
        } else {
            Notes.noNotes.setVisibility(View.VISIBLE);
        }
    }


}


class GridHolder extends RecyclerView.ViewHolder {

    TextView textView;
    ImageButton imageButton;
    CardView cardView;
    TextView timestamp;

    public GridHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.notes_text);
        imageButton = itemView.findViewById(R.id.delete_menu);
        cardView = itemView.findViewById(R.id.card);
        timestamp = itemView.findViewById(R.id.timestamp);

    }
}
