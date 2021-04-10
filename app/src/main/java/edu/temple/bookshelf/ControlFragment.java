package edu.temple.bookshelf;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

public class ControlFragment extends Fragment
{
    private boolean playing;
    private Book currBook;
    private MainActivity parentAct;
    private Button playPause, search, stop;
    private TextView nowPlaying;

    public ControlFragment()
    {
        //Required empty default constructor
    }

    public static ControlFragment newInstance()
    {
        ControlFragment myfrag = new ControlFragment();
        myfrag.playing = false;
        return myfrag;
    }

    public static ControlFragment newInstance(Book book, MainActivity parent) {
        ControlFragment myfrag = new ControlFragment();
        myfrag.currBook = book;
        myfrag.playing = true;
        myfrag.parentAct = parent;
        return myfrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.control_fragment, null);

        nowPlaying = v.findViewById(R.id.nowPlaying);
        String header = getString(R.string.now_playing_header);
        if(currBook != null)
            header += currBook.getTitle();
        nowPlaying.setText(header);

        playPause = v.findViewById(R.id.playPause);
        if(playing)
            playPause.setText(R.string.play);
        else
            playPause.setText(R.string.pause);

        search = v.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                parentAct.StartBookSearch();
            }
        });

        return v;
    }
}

