package edu.temple.bookshelf;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;
import android.os.IBinder;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity {

    //Classwide Declarations
    private ArrayList<Book> bookAL;
    private BookList myBooks;
    private boolean twoPanes;
    private Book currBook, playBook;
    private BookDetailsFragment currDetails;
    private ControlFragment controls;
    private String baseURL;
    private RequestQueue requestQueue;
    private SharedPreferences prefs;
    private AudiobookService.MediaControlBinder abService;
    private SeekBar seekbar;
    private int progress;
    private boolean playing;
    private TimerTask timertask;
    private Timer timer;
    public ServiceConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Determine if only one or two panes are visible - as shown in class example code
        twoPanes = (findViewById(R.id.details_fragment) != null);

        //Get Our Base URL for Queries from our string resources
        Resources res = getResources();
        baseURL = res.getString(R.string.base_url);

        //Try to set up necessary methods to bind to the audiobook service
        //I referred to some help on stack overflow for this:
        //https://stackoverflow.com/questions/8341667/bind-unbind-service-example-android

        myConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder binder)
            {
                abService = (AudiobookService.MediaControlBinder)binder;
            }

            public void onServiceDisconnected(ComponentName className) {
                abService = null;
            }
        };

        //Bind the service
        Intent intent = null;
        intent = new Intent(this, AudiobookService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        //Set up our Volley Request Queue
        requestQueue = Volley.newRequestQueue(this);

        //Instantiate BookList Containing Titles and Authors
        bookAL = new ArrayList<Book>();
        myBooks = new BookList(this, bookAL);
        currBook = null;

        //We'll use the SharedPreferences API to store our search term in case of restart
        prefs = this.getSharedPreferences("edu.temple.bookshelf", Context.MODE_PRIVATE);
        String searchTerm = prefs.getString("searchTerm", "");

        //Use our updateBookList Method to set the BookList to make an initial query
        updateBookList(searchTerm);

        //Load booklist fragment and control by default
        controls = ControlFragment.newInstance(this);
        loadFragment(R.id.control_fragment, controls, false);
        loadFragment(R.id.booklist_fragment, BookListFragment.newInstance(myBooks, this), false);

        //If two panes, load details fragment as well
        currDetails = BookDetailsFragment.newInstance(currBook, this);
        if (twoPanes)
            loadFragment(R.id.details_fragment, currDetails, false);

        //Define our Timer Task and Timer
        createTimerTask();
        timer = new Timer("Timer");

        //Shared prefs stored the necessary data to resume our audiobook on restart
        String bookTitle = prefs.getString("bookTitle", null);

        //If bookTitle is null, we'll assume we weren't playing anything
        //Otherwise, we'll create a new book object to represent what we were playing
        if(bookTitle != null)
        {
            int bookID = prefs.getInt("bookID", 0);
            progress = prefs.getInt("progress", 0);
            int bookDuration = prefs.getInt("bookDuration", 0);
            String bookAuthor = prefs.getString("bookAuthor", "");
            String bookURL = prefs.getString("bookURL", "");
            currBook = new Book(bookID, bookTitle, bookAuthor, bookURL, bookDuration);
            playBook = currBook;
            controls.setPlayBook(playBook); //Let our Control Fragment Know

            //Set up a delay to start playing so we can dodge that null reference
            TimerTask task = new TimerTask()
            {
                public void run()
                {
                    playing = true;
                    setupSeekBar();
                    abService.play(playBook.getId(), progress);
                }
            };

            long initialDelay = 5000;
            timer.schedule(task, initialDelay);
        }

    } // End of onCreate()

    //----------------------------------------------------------------------------------------------
    // Helper Methods for Fragment and Activity Management
    //----------------------------------------------------------------------------------------------

    //In Java we can't reuse a timer task for some unfathomable reason, so we have to redefine
    //it every time its called
    private void createTimerTask()
    {
        //ReDefine our TimerTask
        timertask = new TimerTask()
        {
            public void run()
            {
                seekTimeUpdate();
            }
        };
    }

    // Load fragment in a specified frame -- Adapted from Class Sample Code
    private void loadFragment(int paneId, Fragment fragment, boolean placeOnBackstack)
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction()
                .replace(paneId, fragment);
        if (placeOnBackstack)
            ft.addToBackStack(null);
        ft.commit();

        //  Ensure fragment is attached before attempting to call its public methods
        fm.executePendingTransactions();
    }

    //onActivityResult is called when BookSearchActivity returns a searchTerm

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String searchTerm = data.getStringExtra("response");

                //Store the new term using shared preferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("searchTerm", searchTerm);
                editor.commit();

                //Call the updateBookList method to make an API Query with the new term
                updateBookList(searchTerm);
            }
        }
    }

    //onConfigurationChanged handles when the screen orientation changes, preventing a restart

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);

        //Reinitialize the Control Fragment
        controls = ControlFragment.newInstance(this);
        loadFragment(R.id.control_fragment, controls, false);
        controls.setPlayBook(playBook);
        setupSeekBar();

        twoPanes = (findViewById(R.id.details_fragment) != null);

        //Declare new instances because orientation changes cause massive glitches for some reason
        currDetails = BookDetailsFragment.newInstance(currBook, this);
        Fragment list = BookListFragment.newInstance(myBooks, this);

        //Switch to Landscape
        if (twoPanes) {
            loadFragment(R.id.booklist_fragment, list, false);
            loadFragment(R.id.details_fragment, currDetails, false);
        } else {
            if (currBook != null)
                loadFragment(R.id.booklist_fragment, currDetails, true);
            else
                loadFragment(R.id.booklist_fragment, list, false);
        }
    }

    //selectBook() is called by BookListFragment, changing which books details are displayed

    public void selectBook(int position)
    {
        currBook = myBooks.get(position);

        //Again Determine if only one or two panes are visible - as shown in class example code
        twoPanes = (findViewById(R.id.details_fragment) != null);

        //If details fragment is not visible, we replace the booklist fragment
        if (!twoPanes)
        {
            Fragment newFrag = BookDetailsFragment.newInstance(currBook, this);
            currDetails = (BookDetailsFragment) newFrag;
            loadFragment(R.id.booklist_fragment, newFrag, true);
        }
        else //Otherwise we simply ask details fragment to update its book
        {
            currDetails.displayBook(currBook);
        }
    }

    //StartBookSearch is called when the search button is pressed, launching the search activity

    public void StartBookSearch()
    {
        Intent intent = new Intent(this, BookSearchActivity.class);
        startActivityForResult(intent, 1);
    }

    //UpdateBookList is called after a searchTerm is entered. It filters the displayed list

    private void updateBookList(String searchTerm)
    {
        //Set up a URL based on our search term and make a query
        String urlString = baseURL + searchTerm;

        //This code was directly adapted from the class sample code
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                try
                {
                    ArrayList<Book> newList = new ArrayList<Book>();

                    for (int i = 0; i < response.length(); i++)
                    {
                        JSONObject currObj = (JSONObject) response.getJSONObject(i);
                        int id = currObj.getInt("id");
                        int duration = currObj.getInt("duration");
                        String title = currObj.getString("title");
                        String author = currObj.getString("author");
                        String coverURL = currObj.getString("cover_url");
                        newList.add(new Book(id, title, author, coverURL, duration));
                    }

                    //Reset myBooks with updated results, Load New Fragment
                    myBooks = new BookList(MainActivity.this, newList);
                    Fragment list = BookListFragment.newInstance(myBooks, MainActivity.this);
                    loadFragment(R.id.booklist_fragment, list, false);

                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) { error.printStackTrace(); }
            });

        requestQueue.add(jsonArrayRequest);

    }

    //----------------------------------------------------------------------------------------------
    // Helper Methods for AudioBook Playing / Pausing / Stopping
    //----------------------------------------------------------------------------------------------

    //Pauses pauses the current playback, or plays it if already paused.
    public void pause()
    {
        abService.pause();

        if(playBook != null)
            playing = !playing;

        if(!playing)
        {
            timer.cancel();
            timer.purge();
        }
    }

    //playThisBook() is called BookDetailsFragment. It changes which book is currently loaded
    //and playing by the audiobookservice
    public void playThisBook()
    {
        //Button is visible before book is selected, deal with null reference
        if(currBook == null)
            return;

        if(abService == null)
            return;

        abService.stop(); //Stop a currently playing audiobook
        progress = 0; //Reset our Progress Tracking
        playBook = currBook; //Track What Book is Playing
        controls.setPlayBook(playBook); //Let our Control Fragment Know
        setupSeekBar(); //Reset our Seekbar
        playing = true; //Track that we're playing a book

        //Write the ID of our Current Book
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bookID", playBook.getId());
        editor.putString("bookTitle", playBook.getTitle());
        editor.putString("bookAuthor", playBook.getAuthor());
        editor.putString("bookURL", playBook.getCoverURL());
        editor.putInt("bookDuration", playBook.getDuration());
        editor.putInt("progress", 0);
        editor.commit();

        //Schedule a timer for our seekbar
        createTimerTask();
        long delay = 2000;
        timer.schedule(timertask, delay);

        abService.play(playBook.getId()); //Actually Call the Service
    }

    //seekTimeUpdate() handles passive updates to the seekbar progress when playing
    public void seekTimeUpdate()
    {
        //Update Our Local Progress
        progress += 2;
        seekbar.setProgress(progress);

        //Save our Local Progress
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("progress", progress);
        editor.commit();

        //Schedule another timer
        createTimerTask();
        long delay = 2000;
        timer.schedule(timertask, delay);
    }

    //SetupSeekBar sets up the listener for the seek bar, its called in its own method due to the
    //frequency that the ControlFragment is reinitialized
    public void setupSeekBar()
    {
        seekbar = findViewById(R.id.seekBar);
        seekbar.setMax(playBook.getDuration());
        seekbar.setProgress(progress);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int newProgress, boolean fromUser)
            {
                if (fromUser) //We check this because the seekbar will update periodically
                {
                    abService.seekTo(newProgress);
                    progress = newProgress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                //onProgressChanged will handle this. This can do nothing.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                //onProgressChanged will handle this. This can do nothing.
            }
        });
    }

    //Stop() stops the currently playing book and sets playBook to null
    public void stop()
    {
        abService.stop();
        playing = false;

        playBook = null;
        controls.setPlayBook(null);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("bookTitle", null);
        editor.commit();

        timer.cancel();
        timer.purge();
    }
}