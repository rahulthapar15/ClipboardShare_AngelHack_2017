package com.sharedclipboard.storage.db;

public class DBConstants {

    interface DATA_TYPE {
        public static final String DB_TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";

        public static final String DB_TYPE_INTEGER = "INTEGER";

        public static final String DB_TYPE_BOOLEAN = "BOOLEAN";

        public static final String DB_TYPE_TEXT = "TEXT";

        public static final String DB_TYPE_REAL = "REAL";
    }

    /**
     * DB name for Airfox app
     */
    public static final String DATABASE_NAME = "shared_clipboard.db";
    /**
     * DB version
     */
    public static final int DATABASE_VERSION = 1;

    public static final DBTable[] DB_TABLES = {ClippingContract.ClippingsTable.TABLE};
}
