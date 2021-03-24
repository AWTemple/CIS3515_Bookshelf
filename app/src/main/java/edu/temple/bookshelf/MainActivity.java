package edu.temple.bookshelf;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    BookList myBooks;
    boolean twoPanes;
    public Book currBook;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Determine if only one or two panes are visible - as shown in class example code
        twoPanes = (findViewById(R.id.details_fragment) != null);

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

        myBooks = new BookList(this, bookAL);
        currBook = myBooks.get(0);

        //  Load BookList fragment by default
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.booklist_fragment, BookListFragment.newInstance(myBooks, this), null);

        //Load Details if two panes are present
        if(twoPanes)
        {
            Book currBook = myBooks.get(0);
            fragmentTransaction.add(R.id.details_fragment, BookDetailsFragment.newInstance(currBook));
        }

        fragmentTransaction.commit();
    }

    public void selectBook(int position)
    {
        currBook = myBooks.get(position);

        //If we only have one pane, we replace the list
        if(!twoPanes)
        {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.booklist_fragment, BookDetailsFragment.newInstance(currBook))
                    .addToBackStack(null)
                    .commit();
        }
        else //Otherwise we replace the existing details fragment
        {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment, BookDetailsFragment.newInstance(currBook))
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            twoPanes = true;
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment, BookDetailsFragment.newInstance(currBook))
                    .addToBackStack(null)
                    .commit();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            twoPanes = false;
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.booklist_fragment, BookDetailsFragment.newInstance(currBook))
                    .addToBackStack(null)
                    .commit();
        }

    }

}