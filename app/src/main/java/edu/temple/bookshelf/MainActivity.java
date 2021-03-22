package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Title and Author Resources from XML File
        Resources res = getResources();
        String[] titles = res.getStringArray(R.array.book_titles);
        String[] authors = res.getStringArray(R.array.authors);

        //Instantiate BookList Containing Titles and Authors
        BookList myBooks = new BookList();
        for(int i = 0; i < titles.length; i++)
        {
            myBooks.add(new Book(titles[i], authors[i]);
        }
    }
}