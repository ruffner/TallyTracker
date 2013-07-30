package com.bluntllama.tallytracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class TallyTrackerActivity extends FragmentActivity
					implements LoaderManager.LoaderCallbacks<Cursor>,
								NewTallyDialog.NoticeDialogListener,
									NewActionDialog.NoticeDialogListener {
	public static final String TAG  = "TallyTracker";
	// This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;
    
    private Uri mUri;
    private int mActiveIndex;
    private ListView mListView;
    
    private static final String[] PROJECTION = new String[] {
    	TallyTable.Tally._ID, // 0
    	TallyTable.Tally.COLUMN_NAME_TITLE,// 1
    	TallyTable.Tally.COLUMN_NAME_COUNT // 2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Gets the intent that started this Activity.
        Intent intent = getIntent();

        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.
        if (intent.getData() == null) {
            intent.setData(TallyTable.Tally.CONTENT_URI);
        }
        
        mListView = (ListView)findViewById(R.id.listView1);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                NewActionDialog dialog = new NewActionDialog();
                dialog.show(getSupportFragmentManager(), "NewActionDialog");
                mActiveIndex = pos;
                mUri = Uri.parse(TallyTable.Tally.CONTENT_ID_URI_BASE.toString() + Long.toString(id));
                //Log.v("long clicked","pos"+" "+pos);
                return true;
            }
        });

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {TallyTable.Tally.COLUMN_NAME_COUNT, TallyTable.Tally.COLUMN_NAME_TITLE };
        int[] toViews = {R.id.count, R.id.countLabel };

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(
        		this, 
                R.layout.list_item, 
                null,
                fromColumns, 
                toViews, 
                0
        );
        mListView.setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getSupportLoaderManager().initLoader(0, null, this);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {
        case R.id.menu_new:
        	NewTallyDialog dialog = new NewTallyDialog();
            Bundle args  = new Bundle();
            args.putString("dialogTitle", "New Tally");
            dialog.setArguments(args);
        	mUri = getContentResolver().insert(getIntent().getData(), null);
    	    dialog.show(getSupportFragmentManager(), "NewTallyDialog");
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Called when a new Loader needs to be created
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this,
        		getIntent().getData(),
                PROJECTION, 
                null,
                null, 
                TallyTable.Tally.DEFAULT_SORT_ORDER
        );
    }

    // Called when a previously created loader has finished loading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
    
    private final void updateTally(String count, String title, Uri which) {

        // Sets up a map to contain values to be updated in the provider.
        ContentValues values = new ContentValues();
        values.put(TallyTable.Tally.COLUMN_NAME_MODIFICATION_DATE, System.currentTimeMillis());

        
        values.put(TallyTable.Tally.COLUMN_NAME_TITLE, title);
        
        // This puts the desired notes text into the map.
        values.put(TallyTable.Tally.COLUMN_NAME_COUNT, count);

        /*
         * Updates the provider with the new values in the map. The ListView is updated
         * automatically. The provider sets this up by setting the notification URI for
         * query Cursor objects to the incoming URI. The content resolver is thus
         * automatically notified when the Cursor for the URI changes, and the UI is
         * updated.
         * Note: This is being done on the UI thread. It will block the thread until the
         * update completes. In a sample app, going against a simple provider based on a
         * local database, the block will be momentary, but in a real app you should use
         * android.content.AsyncQueryHandler or android.os.AsyncTask.
         */
        
        getContentResolver().update(
        		which,    // The URI for the record to update.
                values,  // The map of column names and new values to apply to them.
                null,    // No selection criteria are used, so no where columns are necessary.
                null     // No where columns are used, so no where arguments are necessary.
            );


    }
    
    public void increment(View v) {
    	int pos = mListView.getPositionForView(v);
    	long id = mListView.getItemIdAtPosition(pos);
    	Uri uToUpdate = Uri.parse(TallyTable.Tally.CONTENT_ID_URI_BASE.toString() + Long.toString(id)); 
    	TextView tv = (TextView)mListView.getChildAt(pos).findViewById(R.id.count);
    	String newVal = Integer.toString(Integer.parseInt(tv.getText().toString())+1);
    	tv.setText(newVal);
    	ContentValues cv = new ContentValues();
        cv.put(TallyTable.Tally.COLUMN_NAME_COUNT, newVal);
    	getContentResolver().update(uToUpdate, cv, null, null);
    	
    }
    
    public void decrement(View v) {
    	int pos = mListView.getPositionForView(v);
    	long id = mListView.getItemIdAtPosition(pos);
    	Uri uToUpdate = Uri.parse(TallyTable.Tally.CONTENT_ID_URI_BASE.toString() + Long.toString(id));
    	TextView tv = (TextView)mListView.getChildAt(pos).findViewById(R.id.count);
    	String newVal = Integer.toString(Integer.parseInt(tv.getText().toString())-1);
    	tv.setText(newVal);
    	ContentValues cv = new ContentValues();
        cv.put(TallyTable.Tally.COLUMN_NAME_COUNT, newVal);
    	getContentResolver().update(uToUpdate, cv, null, null);
    	
    }

	@Override
	public void onDialogOkClick(String newTitle, String initial) {
		Log.v(TAG, "in onOKClick,uri is " + mUri.toString());
		if(newTitle.equals(android.R.string.untitled) || newTitle.equals(android.R.string.untitled)) {
				getContentResolver().delete(mUri, null, null);
		} else {
			if(initial.equals(""))
				updateTally("0", newTitle, mUri);
			else
				updateTally(initial, newTitle, mUri);
		}
	}
	
	@Override
	public void onDialogCancelClick(boolean delete) {
        if(delete)
		    getContentResolver().delete(mUri, null, null);
	}

	@Override
	public void onDialogSelect(int choice) {
		Log.v(TAG, "you chose" + choice);
		
		switch(choice) {
			case 0:
				NewTallyDialog dialog = new NewTallyDialog();
				Bundle b = new Bundle();
				b.putString("title", ((TextView) mListView.getChildAt(mActiveIndex).findViewById(R.id.countLabel)).getText().toString());
				b.putString("value", ((TextView) mListView.getChildAt(mActiveIndex).findViewById(R.id.count)).getText().toString());
                b.putString("dialogTitle", "Edit Tally");
				dialog.setArguments(b);
	    	    dialog.show(getSupportFragmentManager(), "EditTallyDialog");
	    	    break;
			case 1:
				getContentResolver().delete(mUri, null, null);
				Log.v(TAG, "deleted " + mUri.toString());
				break;
				
		}
		
	}
}
