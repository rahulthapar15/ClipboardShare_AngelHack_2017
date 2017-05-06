package com.sharedclipboard.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.sharedclipboard.R;
import com.sharedclipboard.SharedClipperApp;
import com.sharedclipboard.service.ClipListenerService;
import com.sharedclipboard.storage.db.models.Clipping;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class ClippingWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e("VVV"," ClippingWidget :- onReceive()");
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.e("VVV","updateAppWidget");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.clipping_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        updateRemoteView(context,views);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void updateRemoteView(Context context,RemoteViews views) {
        resetRemoteView(context,views);
        List<Clipping> clippings = Clipping.getAllClippings(SharedClipperApp.getDb(context).getClippingAll(true));
        Log.e("VVV","Total clippings = " + clippings.size());
        int[] textViews = {R.id.txt1,R.id.txt2,R.id.txt3,R.id.txt4};
        for (int i = 0 ; (i < clippings.size() && i < 4 ) ; i++){
            Clipping clip = clippings.get(i);
            Log.e("VVV","date = " + clip.getDate());
            String text = clip.getDurationString() + "\n\n" + clip.getClipping();
            views.setTextViewText(textViews[i],text);
            views.setOnClickPendingIntent(textViews[i],getPendingIntent(context,clip));
        }
    }

    private static PendingIntent getPendingIntent(Context context, Clipping clip) {
        PendingIntent pIntent = PendingIntent.getService(context,(int)clip.getId(),ClipListenerService.getSwapClippingIntent(context,clip),PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    private static void resetRemoteView(Context context, RemoteViews views){
        /*views.setInt(R.id.txt1, "setVisibility", View.GONE);
        views.setInt(R.id.txt2, "setVisibility", View.GONE);
        views.setInt(R.id.txt3, "setVisibility", View.GONE);
        views.setInt(R.id.txt4, "setVisibility", View.GONE);
        views.setInt(R.id.img4, "setVisibility", View.GONE);*/
        views.setInt(R.id.img4, "setVisibility", View.GONE);
        views.setTextViewText(R.id.txt1,context.getString(R.string.no_clippings));
        views.setTextViewText(R.id.txt2,context.getString(R.string.no_clippings));
        views.setTextViewText(R.id.txt3,context.getString(R.string.no_clippings));
        views.setTextViewText(R.id.txt4,context.getString(R.string.no_clippings));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e("VVV","onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            ClippingWidget.updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.e("VVV","onEnabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.e("VVV","onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }
}

