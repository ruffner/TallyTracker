package com.bluntllama.tallytracker;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class NewActionDialog extends DialogFragment {
	private static final String TAG = "TallyTracker";
	
	public interface NoticeDialogListener {
        public void onDialogSelect(int choice);
    }
	
	// Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    Activity activity;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
    	this.activity = activity;
    	
    	super.onAttach(activity);
        
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Actions");

	    builder.setItems(R.array.action_dialog, new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int id) {
    			Log.v(TAG, "item " + Integer.toString(id) + " was clicked");
    			mListener.onDialogSelect(id);
    		}
	    });
  
	    return builder.create();
	}
}