package edu.temple.bookshelf;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class BookListFragment extends Fragment
{
    private ListView listView;
    private BookList bookList;

    public BookListFragment()
    {
        //Required empty default constructor
    }

    public static BookListFragment newInstance()
    {
        return new BookListFragment();
    }

    public static BookListFragment newInstance(BookList booklist, ListView listView )
    {
        BookListFragment myfrag = new BookListFragment();
        myfrag.bookList = booklist;
        myfrag.listView = listView;
        myfrag.listView.setAdapter(booklist);
        return myfrag;
    }
}
