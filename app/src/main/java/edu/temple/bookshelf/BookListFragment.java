package edu.temple.bookshelf;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookListFragment extends Fragment
{
    public BookListFragment()
    {
        //Required empty default constructor
    }

    public static BookListFragment newInstance()
    {
        return new BookListFragment();
    }

    public static BookListFragment newInstance(BookList booklist)
    {
        BookListFragment blf = new BookListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("booklist", booklist.getBookArrayList());
        blf.setArguments(args);
        return blf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View myfrag = inflater.inflate(R.layout.booklist_fragment, container, false);

        TextView title = myfrag.findViewById(R.id.title);
        TextView author = myfrag.findViewById(R.id.author);

        title.setText("title");
        author.setText("author");

        return myfrag;
    }
}
