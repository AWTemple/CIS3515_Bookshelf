package edu.temple.bookshelf;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class BookListFragment extends Fragment
{
    private BookList bookList;
    private ListView listview;
    private MainActivity parentAct;

    public BookListFragment()
    {
        //Required empty default constructor
    }

    public static BookListFragment newInstance()
    {
        return new BookListFragment();
    }

    public static BookListFragment newInstance(BookList booklist, MainActivity parent)
    {
        BookListFragment myfrag = new BookListFragment();
        myfrag.bookList = booklist;
        myfrag.parentAct = parent;
        return myfrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.booklist_fragment, null);
        listview = v.findViewById(R.id.booklist_fragment);
        listview.setAdapter(this.bookList);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
               parentAct.selectBook(position);
            }

        });
        return v;
    }
}
