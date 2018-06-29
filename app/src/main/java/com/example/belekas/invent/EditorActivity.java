package com.example.belekas.invent;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import com.example.belekas.invent.data.BookContract.BooksEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * text fields for inputs
     */

    private static final int EXISTING_BOOK_LOADER = 0;

    private EditText mBookName;

    private EditText mBookPrice;

    private EditText mBookQuantity;

    private EditText mBookSupplier;

    private EditText mBookSupplierPhone;

    private boolean mBookHasChanged = false;

    private Uri mCurrentBookUri;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
    Intent intent = getIntent();
    mCurrentBookUri = intent.getData();

    if (mCurrentBookUri == null) {
        // This is a new book, so change the app bar to say "Add a book
        //
        //"
        setTitle(getString(R.string.new_book_add));

        // Invalidate the options menu, so the "Delete" menu option can be hidden.

        invalidateOptionsMenu();
    } else {
        // Otherwise this is an existing book, so change app bar to say "Edit Book"
        setTitle(getString(R.string.edit_book_entry));

        // Initialize a loader to read the book data from the database
        // and display the current values in the editor
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
    }

        /**
         * find related textView
         */
        mBookName = (EditText) findViewById(R.id.edit_book_name);
        mBookPrice = (EditText) findViewById(R.id.edit_book_price);
        mBookQuantity = (EditText) findViewById(R.id.edit_book_quantity);
        mBookSupplier = (EditText) findViewById(R.id.edit_book_supplier);
        mBookSupplierPhone = (EditText) findViewById(R.id.edit_supplier_phone);

        mBookName.setOnTouchListener(mTouchListener);
        mBookPrice.setOnTouchListener(mTouchListener);
        mBookQuantity.setOnTouchListener(mTouchListener);
        mBookSupplier.setOnTouchListener(mTouchListener);
        mBookSupplierPhone.setOnTouchListener(mTouchListener);

}

    private void saveBook() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mBookName.getText().toString().trim();
        String priceString = mBookPrice.getText().toString().trim();
        String quantityString = mBookQuantity.getText().toString().trim();
        String supplierString = mBookSupplier.getText().toString().trim();
        String supplierPhoneString = mBookSupplierPhone.getText().toString().trim();

        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(supplierPhoneString)) {

            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BooksEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BooksEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(BooksEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        // If the weight is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(BooksEntry.COLUMN_BOOK_PRICE, price);

        int quantity = 0;

        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(BooksEntry.COLUMN_BOOK_QUANTITY, quantity);

        //check if new or edited values are not 0.
        if (mCurrentBookUri == null) {

            Uri newUri = getContentResolver().insert(BooksEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_in_inset),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.successful_insert),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.edit_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.successful_edit),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    /** Inflate the menu options from the res/menu/menu_editor.xml file.
    * This adds menu items to the app bar.
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link ListActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
                return super.onOptionsItemSelected(item);
        }


        /**
         * This method is called when the back button is pressed.
         */
        @Override
        public void onBackPressed() {
            // if not changes made go back
            if (!mBookHasChanged) {
                super.onBackPressed();
                return;
            }

            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
           //editor shows all book attributes, define a projection that contains
            // all columns from the book table
            String[] projection = {
                    BooksEntry._ID,
                    BooksEntry.COLUMN_BOOK_NAME,
                    BooksEntry.COLUMN_BOOK_PRICE,
                    BooksEntry.COLUMN_BOOK_QUANTITY,
                    BooksEntry.COLUMN_SUPPLIER_NAME,
                    BooksEntry.COLUMN_SUPPLIER_PHONE };

            // This loader will execute the ContentProvider's query method on a background thread
            return new CursorLoader(this,   // Parent activity context
                    mCurrentBookUri,         // Query the content URI for the current book
                    projection,             // Columns to include in the resulting Cursor
                    null,                   // No selection clause
                    null,                   // No selection arguments
                    null);                  // Default sort order
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // Bail early if the cursor is null or there is less than 1 row in the cursor
            if (cursor == null || cursor.getCount() < 1) {
                return;
            }

            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            if (cursor.moveToFirst()) {
                // Find the columns of book attributes that we're interested in
                int nameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_NAME);
                int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOK_QUANTITY);
                int supplierColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_SUPPLIER_PHONE);

                // Extract out the value from the Cursor for the given column index
                String name = cursor.getString(nameColumnIndex);
                String supplier = cursor.getString(supplierColumnIndex);
                String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

                int price = cursor.getInt(priceColumnIndex);
                final int quantity = cursor.getInt(quantityColumnIndex);

                // Update the views on the screen with the values from the database
                mBookName.setText(name);
                mBookPrice.setText(Integer.toString(price));
                mBookQuantity.setText(Integer.toString(quantity));
                mBookSupplier.setText(supplier);
                mBookSupplierPhone.setText(supplierPhone);


                /**
                 * test for decrement button

                Button decrement = (Button) findViewById(R.id.decrement);
                decrement.setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                       if (quantity ==0){
                        return;
                       }
                       else{
                           quantity = quantity -1;
                       }

                    }
                });
                 */

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // If the loader is invalidated, clear out all the data from the input fields.
            mBookName.setText("");
            mBookPrice.setText("");
            mBookQuantity.setText("");
            mBookSupplier.setText("");
            mBookSupplierPhone.setText("");
        }
        private void showUnsavedChangesDialog(
                DialogInterface.OnClickListener discardButtonClickListener) {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_dialog_msg);
            builder.setPositiveButton(R.string.discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Keep editing" button, so dismiss the dialog
                    // and continue editing the book.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        private void showDeleteConfirmationDialog() {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Delete" button, so delete the book.
                    deleteBook();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    // and continue editing the book.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        /**
         * Perform the deletion of the book in the database.
         */
        private void deleteBook() {
            // Only perform the delete if this is an existing book.
            if (mCurrentBookUri != null) {
                // deleted book in given row
                int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(this, getString(R.string.edit_deletion_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.edit_delete_success),
                            Toast.LENGTH_SHORT).show();
                }
            }

            // Close the activity
            finish();
        }



}
