package com.sharedclipboard.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sharedclipboard.R;
import com.sharedclipboard.SharedClipperApp;
import com.sharedclipboard.service.ClipListenerService;
import com.sharedclipboard.storage.db.models.Clipping;

public class EditClipping extends BaseActivity {
    public static final String EXTRA_CLIPPING_ID = "clipping_id";
    private EditText edtClipping = null;
    private Button btnSave = null ;
    private Button btnCancel = null;
    private Clipping clipping = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clipping);
        if(!getIntent().hasExtra(EXTRA_CLIPPING_ID)){
            showToast(getString(R.string.clip_id_required));
            finish();
        }
        long id = getIntent().getLongExtra(EXTRA_CLIPPING_ID,-1);
        try {
            clipping = new Clipping(SharedClipperApp.getDb(getBaseContext()).getClipping(id));
        }catch (Exception ex){

        }
        if(clipping == null){
            showToast(getString(R.string.clip_id_required));
            finish();
        }
        initViews();
        edtClipping.setText(clipping.getClipping());
    }

    private void initViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtClipping = (EditText)findViewById(R.id.edtClipping);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnCancel.setOnClickListener(mOnClickListener);
        btnSave.setOnClickListener(mOnClickListener);

    }

    private void onClickSave(){
        clipping.setClipping(edtClipping.getText().toString());
        SharedClipperApp.getDb(getBaseContext()).updateClipping(clipping);
        ClipListenerService.updateWidgets(getBaseContext());
        finish();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSave:{
                    onClickSave();
                }
                break;
                case R.id.btnCancel:{
                    finish();
                }
                break;
            }
        }
    };
}
