package com.sharedclipboard.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.sharedclipboard.R;
import com.sharedclipboard.SharedClipperApp;


/**
 * Common parent for most of the activities of this app.
 * Should not be used as an activity to display layouts.
 */
public class BaseActivity extends AppCompatActivity {


    private ProgressDialog mProgressDialog = null;
    private AlertDialog mAlertDialog = null;

    /**
     * Display progress message to the user
     * @param progressMessage main message
     * @param isCancelable whether the dialog is cancelable
     */
    protected void showProgressDialog(String progressMessage, boolean isCancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(BaseActivity.this);
        }
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(isCancelable);
        mProgressDialog.setMessage(progressMessage);
        mProgressDialog.show();
    }

    /**
     *  /**
     * Display error message if something goes wrong
     * @param result
     * @param editText
     * @param errorMessage
     */
    protected void showError(boolean result,EditText editText,String errorMessage){
        if(!result) {
            editText.setError(errorMessage);
            editText.requestFocus();
        }
    }

    /**
     * Show alert to the user
     * @param title the title of the alert
     * @param alertMessage the main message of the alert
     * @param isCancelable whether the dialog is cancelable
     */
    public void showAlertDialog(String title, String alertMessage, boolean isCancelable) {
        if (mAlertDialog == null) {
            mAlertDialog = getNewAlertDialog();
        }
        mAlertDialog.setCancelable(isCancelable);
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(alertMessage);
        mAlertDialog.show();

    }

    /**
     * @return
     */
    private AlertDialog getNewAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setPositiveButton(getString(R.string.ok), null);
        return builder.create();
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        dismissAlertDialog();
        super.onDestroy();
    }

    /**
     * Dismiss alert to the user
     */
    protected void dismissAlertDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = null;
    }

    /**
     * Dismiss progress message to the user
     */
    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    /**
     * Return simple name of class. Override this to return your own custom tag.
     * @return returns simple name of class.
     */
    protected String getTag() {
        return SharedClipperApp.getTag(this.getClass().getSimpleName());
    }


    /**
     * Print trace in Log.v format. Warp the implementation to pass TAG as a parameter and provide a single point to print logs.
     * @param traceMessage message to be printed
     */
    public void printTrace(String traceMessage) {
        SharedClipperApp.printTrace(getTag(), traceMessage);
    }

    public void printError(String traceMsg, Exception ex) {
        printTrace(traceMsg);
        printTrace(ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * Display toast to the user
     * @param msg the message to be displayed
     */
    public void showToast(String msg) {
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:{
                onBackPressed();
                return true ;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
