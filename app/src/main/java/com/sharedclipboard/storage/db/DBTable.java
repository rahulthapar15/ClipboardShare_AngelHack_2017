package com.sharedclipboard.storage.db;

import android.provider.BaseColumns;

public interface DBTable extends BaseColumns {

    String getCreateQuery();
    String getDropQuery();
    String getTableName();
    String[] getAllColumns(); // not necessary

}
