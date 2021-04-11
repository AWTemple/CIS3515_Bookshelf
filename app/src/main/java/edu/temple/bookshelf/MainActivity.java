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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity {

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

        //Set up our Volley Request Queue
        requestQueue = Volley.newRequestQueue(this);

        //Instantiate BookList Containing Titles and Authors
        bookAL = new ArrayList<Book>();
        myBooks = new BookList(this, bookAL);
        currBook = null;

        //Load booklist fragment and control by default
        controls = ControlFragment.newInstance(this);
        loadFragment(R.id.control_fragment, controls, false);
        loadFragment(R.id.booklist_fragment, BookListFragment.newInstance(myBooks, this), false);

        //If two panes, load details fragment as well
        currDetails = BookDetailsFragment.newInstance(currBook, this);
        if (twoPanes)
            loadFragment(R.id.details_fragment, currDetails, false);

        //We'll use the SharedPreferences API to store our search term in case of restart
        prefs = this.getSharedPreferences("edu.temple.bookshelf", Context.MODE_PRIVATE);
        String searchTerm = prefs.getString("searchTerm", "");

        //Use our updateBookList Method to set the BookList to make an initial query
        updateBookList(searchTerm);

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

        Intent intent = null;
        intent = new Intent(this, AudiobookService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

    } // End of onCreate()--------------------------------------------------------------------------

    public void StartBookSearch() {
        Intent intent = new Intent(this, BookSearchActivity.class);
        startActivityForResult(intent, 1);
    }

    public void selectBook(int position) {
        currBook = myBooks.get(position);

        //Again Determine if only one or two panes are visible - as shown in class example code
        twoPanes = (findViewById(R.id.details_fragment) != null);

        //If details fragment is not visible, we replace the booklist fragment
        if (!twoPanes) {
            Fragment newFrag = BookDetailsFragment.newInstance(currBook, this);
            currDetails = (BookDetailsFragment) newFrag;
            loadFragment(R.id.booklist_fragment, newFrag, true);
        } else //Otherwise we simply ask details fragment to update its book
        {
            currDetails.displayBook(currBook);
        }
    }

    // Load fragment in a specified frame -- Adapted from Class Sample Code
    private void loadFragment(int paneId, Fragment fragment, boolean placeOnBackstack) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction()
                .replace(paneId, fragment);
        if (placeOnBackstack)
            ft.addToBackStack(null);
        ft.commit();

        //  Ensure fragment is attached before attempting to call its public methods
        fm.executePendingTransactions();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
        controls = ControlFragment.newInstance(this);
        loadFragment(R.id.control_fragment, controls, false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
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

    public void playThisBook()
    {
        //Button is visible before book is selected, deal with null reference
        if(currBook == null)
            return;

        playBook = currBook;

        controls.setPlayBook(playBook);
        abService.play(playBook.getId());
    }

    private void updateBookList(String searchTerm) {
        //Set up a URL based on our search term and make a query
        String urlString = baseURL + searchTerm;

        //This code was directly adapted from the class sample code
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    ArrayList<Book> newList = new ArrayList<Book>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject currObj = (JSONObject) response.getJSONObject(i);
                        int id = currObj.getInt("id");
                        String title = currObj.getString("title");
                        String author = currObj.getString("author");
                        String coverURL = currObj.getString("cover_url");
                        newList.add(new Book(id, title, author, coverURL));
                    }

                    //Reset myBooks with updated results, Load New Fragment
                    myBooks = new BookList(MainActivity.this, newList);
                    Fragment list = BookListFragment.newInstance(myBooks, MainActivity.this);
                    loadFragment(R.id.booklist_fragment, list, false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonArrayRequest);

    }
}