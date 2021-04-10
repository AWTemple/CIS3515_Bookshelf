package edu.temple.bookshelf;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class BookDetailsFragment extends Fragment
{
    private Book book;
    private TextView title, author;
    private ImageView cover;
    private MainActivity parentAct;
    private Button button;

    public BookDetailsFragment()
    {
        //Required empty default constructor
    }

    public static BookDetailsFragment newInstance()
    {
        return new BookDetailsFragment();
    }

    public static BookDetailsFragment newInstance(Book book, MainActivity parent)
    {
        BookDetailsFragment myfrag = new BookDetailsFragment();
        myfrag.book = book;
        myfrag.parentAct = parent;
        myfrag.displayBook(myfrag.book);
        return myfrag;
    }

    public void updateBook(Book book)
    {
        this.book = book;
    }

    public void displayBook(Book book)
    {
        this.book = book;
        if (title != null && author != null && cover != null && book != null)
        {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());

            try
            {
                Picasso.get().load(Uri.parse(book.getCoverURL())).placeholder(R.drawable.placeholder).into(cover);
            }
            catch(Exception e){ e.printStackTrace(); }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.details_fragment, null);
        title = v.findViewById(R.id.bigtitle);
        author = v.findViewById(R.id.bigauthor);
        cover = (ImageView)v.findViewById(R.id.bookCover);
        button = v.findViewById(R.id.playThisBook);

        if(cover != null)
            cover.setImageResource(R.drawable.placeholder);

        if(this.book != null)
            this.displayBook(this.book);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                parentAct.playThisBook();
            }
        });

        return v;
    }
}
