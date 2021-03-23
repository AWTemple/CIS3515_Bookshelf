package edu.temple.bookshelf;

import android.app.Fragment;
import android.widget.ListView;
import android.widget.TextView;

public class BookDetailsFragment extends Fragment
{
    private Book book;

    public BookDetailsFragment()
    {
        //Required empty default constructor
    }

    public static BookDetailsFragment newInstance()
    {
        return new BookDetailsFragment();
    }

    public static BookDetailsFragment newInstance(Book book)
    {
        BookDetailsFragment myfrag = new BookDetailsFragment();
        myfrag.book = book;
        myfrag.displayBook(myfrag.book);
        return myfrag;
    }

    public void displayBook(Book book)
    {
        TextView bigAuthor = getActivity().findViewById(R.id.bigauthor);
        TextView bigTitle = getActivity().findViewById(R.id.bigtitle);

        bigTitle.setText(book.getTitle());
        bigAuthor.setText(book.getAuthor());
    }
}
