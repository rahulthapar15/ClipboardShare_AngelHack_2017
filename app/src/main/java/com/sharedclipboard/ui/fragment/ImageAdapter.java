package com.sharedclipboard.ui.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter
{
    private Context mContext;
    private Integer mGrid_number;

    //Initialize with the grid number that needs to be displayed in the fragment. And load
    //that grid id.
    public ImageAdapter(Context c, Integer grid_number)
    {
        mContext = c;
        mGrid_number = grid_number;
    }

    @Override
    public int getCount()
    {
        return GridImages.getGridById(mGrid_number).length;
    }

    //to return the actual object at the specified position in the adapter
    @Override
    public Integer getItem(int position) {
        return null;
    }

    //returns the row id of the item
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null)
        {
            //Set the size and layout for the images in the grid view.
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        //Load all images from the corresponding grid number.
        imageView.setImageResource(GridImages.getGridById(mGrid_number)[position]);
        return imageView;
    }
}
