package com.bluntllama.tallytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class NewTallyDialog extends DialogFragment {
	
	public interface NoticeDialogListener {
        public void onDialogOkClick(String title, String initial);
        public void onDialogCancelClick(boolean delete);
    }
	
	// Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    Activity activity;
    EditText mEditTitle;
    EditText mEditInitial;
    
    String mTitle = "";;
	String mValue = "";
    
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
    public void onPause() {
    	super.onPause();
    	if(mEditTitle.getText().toString().equals(android.R.string.untitled) || 
    			mEditTitle.getText().toString().equals("")) {
    		mListener.onDialogCancelClick(true);
    	}
    }
    
    @Override
    public void onCancel (DialogInterface dialog) {
    	if(mEditTitle.getText().toString().equals(android.R.string.untitled) || 
    			mEditTitle.getText().toString().equals("")) {
    		mListener.onDialogCancelClick(true);
    	}
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View v = inflater.inflate(R.layout.dialog_new_tally, null);
	    mEditTitle = (EditText)v.findViewById(R.id.editText1);
	    mEditInitial = (EditText)v.findViewById(R.id.editText2);
	    
	    Bundle a = getArguments();
	    if(a != null) {
	    	mTitle = a.getString("title", "");
	 	    mValue = a.getString("value", "");
	    }
	    
	    if(!mTitle.equals(""))
	    	mEditTitle.setText(mTitle);
	    if(!mValue.equals(""))
	    	mEditInitial.setText(mValue);
	    
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout

        builder.setTitle(a.getString("dialogTitle"));

	    builder.setView(v);
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   mListener.onDialogOkClick(mEditTitle.getText().toString(), mEditInitial.getText().toString());
	               }
	           }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
                       if(mEditTitle.getText().toString().equals(""))
	            	        mListener.onDialogCancelClick(true);

	               }
	           });     
	    return builder.create();
	}
}