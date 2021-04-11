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

    public static ControlFragment newInstance(MainActivity parent) {
        ControlFragment myfrag = new ControlFragment();
        myfrag.playing = false;
        myfrag.parentAct = parent;
        return myfrag;
    }

    public void setPlayBook(Book book)
    {
        currBook = book;

        if(currBook != null)
            playing = true;
        else
            playing = false;

        buttonTextSwitch();

        updateHeader();
    }

    private void updateHeader()
    {
        String header = getString(R.string.now_playing_header);

        if(currBook != null)
            header += currBook.getTitle();

        if(nowPlaying != null)
            nowPlaying.setText(header);
    }

    private void playPause()
    {
        playing = !playing;
        buttonTextSwitch();

        parentAct.pause(); //Pause handles the logic for if already paused or not
    }

    private void buttonTextSwitch()
    {
        if(playPause == null)
            return;

        if(playing)
            playPause.setText(R.string.pause);
        else
            playPause.setText(R.string.play);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.control_fragment, null);

        nowPlaying = v.findViewById(R.id.nowPlaying);
        updateHeader();

        //Set up Listener for play/pause button
        playPause = v.findViewById(R.id.playPause);
        buttonTextSwitch();
        playPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                playPause();
            }
        });

        stop = v.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                parentAct.stop();
            }
        });

        //Set up Listener for Search
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

