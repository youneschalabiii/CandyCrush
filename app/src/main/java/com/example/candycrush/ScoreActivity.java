package com.example.candycrush;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ScoreActivity extends AppCompatActivity {
    DatabaseHelper db;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        listView = findViewById(R.id.score_list);
        db = new DatabaseHelper(this);

        Cursor cursor = db.getAllScores();
        String[] from = {DatabaseHelper.NAME, DatabaseHelper.THEME, DatabaseHelper.TIME, DatabaseHelper.SCORE};
        int[] to = {R.id.name, R.id.theme, R.id.time, R.id.score};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.score_item, cursor, from, to, 0);
        listView.setAdapter(adapter);
    }
}
