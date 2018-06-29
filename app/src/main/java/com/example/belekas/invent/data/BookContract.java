package com.example.belekas.invent.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    /** To prevent someone from accidentally instantiating the contract class,
    * give it an empty constructor.
    */
    private BookContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.belekas.invent";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";
    /**
     * columns in table described bellow
     */
    public static final class BooksEntry  implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        /** Name of database table for books */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * name of the book.
         *
         * Type: TEXT
         */
        public final static String COLUMN_BOOK_NAME  = "name";

        /**
         * price of the book.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_BOOK_PRICE ="price";

        /**
         * Quantity of books
         *
         * Type: INTEGER
         */
        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        /**
         * name of suppliers
         *
         * Type: text
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * number of suppliers
         *
         * Type: text
         */
        public final static String COLUMN_SUPPLIER_PHONE = "suppliers_phone";

    }

}