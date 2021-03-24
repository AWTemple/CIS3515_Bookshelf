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
        Fragment bookListFragment = BookListFragment.newInstance(myBooks, this);
        loadFragment(R.id.booklist_fragment, bookListFragment, true);

        //If two panes, load details fragment as well
        currDetails = BookDetailsFragment.newInstance(currBook);
        if (twoPanes)
            loadFragment(R.id.details_fragment, currDetails, true);

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

        //Switch to Landscape
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            twoPanes = true;
            currDetails.displayBook(currBook);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            twoPanes = false;
            if(currBook != null)
                loadFragment(R.id.booklist_fragment, currDetails, true);

        }
    }

}