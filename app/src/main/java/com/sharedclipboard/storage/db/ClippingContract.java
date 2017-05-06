package com.sharedclipboard.storage.db;


public class ClippingContract {
    public static final  String COLUMN_TOTAL = "total";
    public static final class ClippingsTable implements DBTable{

        public static final ClippingsTable TABLE = new ClippingsTable();

        private static final String TABLE_NAME = "CLIPPINGS_ENTRY";
        public static final String COLUMN_CLIPPING = "clipping";
        public static final String COLUMN_CLIPPING_TYPE = "clipping_type";
        public static final String COLUMN_DATE_TIME = "date";
        private static final String ALL_COLUMNS[] = {_ID, COLUMN_CLIPPING, COLUMN_CLIPPING_TYPE, COLUMN_DATE_TIME};

        private ClippingsTable(){
            // cant be used from outside
        }

        @Override
        public String getCreateQuery() {
            return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                    _ID + " " + DBConstants.DATA_TYPE.DB_TYPE_PRIMARY_KEY + "," +
                    COLUMN_CLIPPING + " " + DBConstants.DATA_TYPE.DB_TYPE_TEXT + "," +
                    COLUMN_CLIPPING_TYPE + " " + DBConstants.DATA_TYPE.DB_TYPE_INTEGER+ "," +
                    COLUMN_DATE_TIME + " " + DBConstants.DATA_TYPE.DB_TYPE_INTEGER +
                    ");";
        }

        @Override
        public String getDropQuery() {
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }

        @Override
        public String[] getAllColumns() {
            return ALL_COLUMNS;
        }
    }

}
