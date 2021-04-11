package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

public class Book
{
    private String title, author, coverURL;
    private int id, duration;

    public Book(){};

    public Book(int id, String title, String author, String coverURL, int duration)
    {
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverURL = coverURL;
        this.duration = duration;
    }

    public int getId() { return id; }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }

    public String getCoverURL() { return coverURL; }

    public int getDuration() { return duration; }

}
