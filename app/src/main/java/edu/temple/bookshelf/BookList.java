package edu.temple.bookshelf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//BookList is more or less a wrapper class for an ArrayList of book objects, that I then
//turned into an adapter class

public class BookList extends BaseAdapter
{
    private ArrayList<Book> bookArray;
    private Context context;
    private LayoutInflater inflater;

    public BookList(Context context, ArrayList<Book> bookArray)
    {
        this.context = context;
        this.bookArray = bookArray;
        inflater = LayoutInflater.from(context);
    }

    public void add(Book book)
    {
        bookArray.add(book);
    }

    public void remove(Book book)
    {
        bookArray.remove(book);
    }

    public Book get(int index)
    {
        return bookArray.get(index);
    }

    public int size()
    {
        return bookArray.size();
    }

    public ArrayList<Book> getBookArrayList()
    {
        return bookArray;
    }

    @Override
    public int getCount()
    {
        return bookArray.size();
    }

    @Override
    public Object getItem(int position)
    {
        return bookArray.get(position);
    }

    //Must be implemented, though we don't use it
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Inflate the convertView from the booklist fragment file
        convertView = inflater.inflate(R.layout.booklist_fragment, parent, false);

        //Prep the Title Text
        TextView title = convertView.findViewById(R.id.title);
        title.setText(bookArray.get(position).getTitle());

        //Prep the Author text
        TextView author = convertView.findViewById(R.id.author);
        author.setText(bookArray.get(position).getAuthor());

        return convertView;
    }
}
