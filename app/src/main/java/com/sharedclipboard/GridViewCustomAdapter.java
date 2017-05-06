package com.sharedclipboard;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sharedclipboard.storage.db.models.Clipping;
import com.sharedclipboard.ui.activity.EditClipping;

import java.util.List;

public class GridViewCustomAdapter extends ArrayAdapter
{
    Context context;
    List<Clipping> list;

    final CharSequence[] items = {
            "View","Edit", "Delete"
    };

    public Clipping getClipping(int position){
        return list.get(position);
    }

    public GridViewCustomAdapter(Context context)
    {
        super(context, 0);
        this.context=context;

        //get clippings from database
        list = Clipping.getAllClippings(SharedClipperApp.getDb(context).getClippingAll(true));

    }

    public int getCount()
    {
        return list.size();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        View row = convertView;
        Log.d("TAGG", "Came here");
        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.grid_row, parent, false);
        }

        TextView textViewTitle = (TextView) row.findViewById(R.id.textView);
        TextView textViewInfo = (TextView) row.findViewById(R.id.textViewInfo);
        ImageView imageViewIte = (ImageView) row.findViewById(R.id.imageView);
        imageViewIte.setImageResource(R.drawable.settings);
//            imageViewIte.setImageResource(R.mipmap.info_icon);
        textViewInfo.setText("" + list.get(position).getClipping());
//            imageViewIte.setImageResource(R.drawable.ic_launcher);


        imageViewIte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //                                  builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        if(item==2) {
                            SharedClipperApp.getDb(context).deleteClipping(list.get(position).getId());
                            MainActivity.gridViewCustomeAdapter = new GridViewCustomAdapter(context);
                            // Set the Adapter to GridView
                            MainActivity.gridView.setAdapter(MainActivity.gridViewCustomeAdapter);
                        }else if(item == 1){ // Edit
                            Intent intent = new Intent(context, EditClipping.class);
                            intent.putExtra(EditClipping.EXTRA_CLIPPING_ID,list.get(position).getId());
                            context.startActivity(intent);
                        }else if(item == 0){ //
                            Clipping clip = list.get(position);
                            ((MainActivity)context).showAlertDialog(clip.getDurationString(),clip.getClipping(),false);
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        textViewTitle.setText(""+getDurationString(list.get(position).getDate()));
//            if(position%2==0)
//            {
//                textViewTitle.setText("3 mins ago");
//
//            } else {
//                textViewTitle.setText("12 mins ago");
////                imageViewIte.setImageResource(R.drawable.psm_alarm_clock2);
//            }
        return row;

    }
    private String getDurationString(long time) {
        //Log.e("VVV", "getDurationString = " + seconds);
        long seconds = (System.currentTimeMillis() - time) / 1000;
        long hours = seconds / 3600;
        long mins = (seconds / 60) - (hours * 60);
        long sec = seconds - ((hours * 3600) + (mins * 60));
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours + " hours ago" );
        } else if (mins > 0) {
            sb.append(mins + " mins ago" );
        } else {
            sb.append(sec + " secs ago" );
        }
        return sb.toString();
    }

}
