package com.sharedclipboard.ui.asyncTasks;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.sharedclipboard.storage.db.DBHelper;
import com.sharedclipboard.storage.db.models.Clipping;

import java.util.ArrayList;

public class TextClippingsDataLoader extends AsyncTaskLoader<ArrayList<Clipping>> {

    Context mContext;

    public TextClippingsDataLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); //Force an asynchronous load.
    }

    @Override
    public ArrayList<Clipping> loadInBackground(){
        //load all of the exercise entries and return them
        DBHelper dataHelper = new DBHelper(mContext);
        try {
            Cursor allClippingsCursor = dataHelper.getClippingAll(true);
            ArrayList<Clipping> values = new ArrayList<>(Clipping.getAllClippings(allClippingsCursor));
            return values;
        } catch(Exception e) {}
        return new ArrayList<Clipping>();
    }
}
