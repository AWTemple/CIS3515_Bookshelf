package edu.temple.bookshelf;

import java.util.ArrayList;

//BookList is more or less a wrapper class for an ArrayList of book objects

public class BookList
{
    private ArrayList<Book> bookArray;

    public BookList()
    {
        bookArray = new ArrayList<Book>();
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
}
