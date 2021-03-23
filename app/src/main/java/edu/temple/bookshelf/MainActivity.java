package edu.temple.bookshelf;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.small_portrait);

        //Get Title and Author Resources from XML File
        Resources res = getResources();
        String[] titles = res.getStringArray(R.array.book_titles);
        String[] authors = res.getStringArray(R.array.authors);

        //Instantiate BookList Containing Titles and Authors
        ArrayList<Book> bookAL = new ArrayList<Book>();
        for(int i = 0; i < titles.length; i++)
        {
            bookAL.add(new Book(titles[i], authors[i]));
        }

        BookList myBooks = new BookList(this, bookAL);
        listview = findViewById(R.id.booklist_fragment);

        //  Load BookList fragment by default
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.booklist_fragment, BookListFragment.newInstance(myBooks, listview), null);
        fragmentTransaction.commit();
    }


    private void doTransition()
    {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.booklist_fragment, new BookListFragment())
                .addToBackStack(null)
                .commit();
    }

}