package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

public class Book
{
    private String title, author, coverURL;
    private int id;

    public Book(){};

    public Book(String title, String author, String coverURL)
    {
        this.title = title;
        this.author = author;
        this.coverURL = coverURL;
    }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }

    public String getCoverURL() { return coverURL; }

}
