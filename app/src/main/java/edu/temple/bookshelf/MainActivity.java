package edu.temple.bookshelf;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    BookList myBooks;
    boolean twoPanes;
    Book currBook;
    BookDetailsFragment currDetails;

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
        currBook = null;

        //Load booklist fragment by default
        loadFragment(R.id.booklist_fragment, BookListFragment.newInstance(myBooks, this), false);

        //If two panes, load details fragment as well
        currDetails = BookDetailsFragment.newInstance(currBook);
        if (twoPanes)
            loadFragment(R.id.details_fragment, currDetails, false);

    }

    public void selectBook(int position)
    {
        currBook = myBooks.get(position);

        //Again Determine if only one or two panes are visible - as shown in class example code
        twoPanes = (findViewById(R.id.details_fragment) != null);

        //If details fragment is not visible, we replace the booklist fragment
        if(!twoPanes)
        {
            Fragment newFrag = BookDetailsFragment.newInstance(currBook);
            currDetails = (BookDetailsFragment)newFrag;
            loadFragment(R.id.booklist_fragment, newFrag, true);
        }
        else //Otherwise we simply ask details fragment to update its book
        {
            currDetails.displayBook(currBook);
        }
    }

    // Load fragment in a specified frame -- Adapted from Class Sample Code
    private void loadFragment(int paneId, Fragment fragment, boolean placeOnBackstack)
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction()
                .replace(paneId, fragment);
        if (placeOnBackstack)
            ft.addToBackStack(null);
        ft.commit();

        //  Ensure fragment is attached before attempting to call its public methods
        fm.executePendingTransactions();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
        twoPanes = (findViewById(R.id.details_fragment) != null);

        //Declare new instances because orientation changes cause massive glitches for some reason
        currDetails = BookDetailsFragment.newInstance(currBook);
        Fragment list = BookListFragment.newInstance(myBooks, this);

        //Switch to Landscape
        if(twoPanes)
        {
            loadFragment(R.id.booklist_fragment, list, false);
            loadFragment(R.id.details_fragment, currDetails, false);
        }
        else
        {
            if(currBook != null)
                loadFragment(R.id.booklist_fragment, currDetails, true);
            else
                loadFragment(R.id.booklist_fragment, list, false);
        }
    }

}