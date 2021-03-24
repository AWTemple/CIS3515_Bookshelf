package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

//I referred to some posts on stack overflow to implement parcelable for ease of passing
//arguments to fragments. You can see the advice I got here:
//https://stackoverflow.com/questions/35247641/instantiate-a-fragment-with-a-custom-object-array-list/35248184

public class Book
{
    private String title, author;

    public Book(){};

    public Book(String title, String author)
    {
        this.title = title;
        this.author = author;
    }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }


}
