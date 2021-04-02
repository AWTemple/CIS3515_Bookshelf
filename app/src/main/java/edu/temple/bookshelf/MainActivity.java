package edu.temple.bookshelf;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;
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

public class MainActivity extends AppCompatActivity {

    ArrayList<Book> bookAL;
    BookList myBooks;
    boolean twoPanes;
    Book currBook;
    BookDetailsFragment currDetails;
    Button searchButton;
    String baseURL;
    RequestQueue requestQueue;
    SharedPreferences prefs;

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

        //Load booklist fragment by default
        loadFragment(R.id.booklist_fragment, BookListFragment.newInstance(myBooks, this), false);

        //If two panes, load details fragment as well
        currDetails = BookDetailsFragment.newInstance(currBook);
        if (twoPanes)
            loadFragment(R.id.details_fragment, currDetails, false);

        //Use our trusty setupButton Method to set up the onclick listener
        setupButton();

        //We'll use the SharedPreferences API to store our search term in case of restart
        prefs = this.getSharedPreferences("edu.temple.bookshelf", Context.MODE_PRIVATE);
        String searchTerm = prefs.getString("searchTerm", "");

        //Use our updateBookList Method to set the BookList to make an initial query
        updateBookList(searchTerm);

    }

    //SetupButton is called onCreate and onConfigChange because the button ID Changes
    private void setupButton() {
        //Set up our onclick listener for our searchbutton
        searchButton = findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartBookSearch();
            }
        });
    }

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
            Fragment newFrag = BookDetailsFragment.newInstance(currBook);
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
        twoPanes = (findViewById(R.id.details_fragment) != null);

        //Reset our search button (It's different in different configs
        setupButton();

        //Declare new instances because orientation changes cause massive glitches for some reason
        currDetails = BookDetailsFragment.newInstance(currBook);
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