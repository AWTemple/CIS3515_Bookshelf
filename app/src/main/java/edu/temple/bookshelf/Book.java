package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

//I referred to some posts on stack overflow to implement parcelable for ease of passing
//arguments to fragments. You can see the advice I got here:
//https://stackoverflow.com/questions/35247641/instantiate-a-fragment-with-a-custom-object-array-list/35248184

public class Book implements Parcelable
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(author);
    }

    public static final Creator<Book> CREATOR = new Creator<Book>()
    {

        @Override
        public Book createFromParcel(Parcel in)
        {
            String title = in.readString();
            String author = in.readString();

            return new Book(title, author);
        }

        @Override
        public Book[] newArray(int size)
        {
            return new Book[size];
        }
    };
}
