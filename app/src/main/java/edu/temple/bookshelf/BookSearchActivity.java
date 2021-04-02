package edu.temple.bookshelf;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BookSearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_search_activity);

        EditText searchBox = findViewById(R.id.searchBox);
        Button searchButton = findViewById(R.id.commenceSearch);

        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String response = searchBox.getText().toString();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("response", response);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}