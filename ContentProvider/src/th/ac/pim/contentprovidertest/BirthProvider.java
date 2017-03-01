package th.ac.pim.contentprovidertest;

import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class BirthProvider extends ContentProvider {
	// fields for my content provider
	static final String PROVIDER_NAME = "th.ac.pim.provider.BirthdayProv";
	static final String URL = "content://" + PROVIDER_NAME + "/friends";
	static final Uri CONTENT_URI = Uri.parse(URL);

	DBHelper dbHelper;

	// projection map for a query
	private static HashMap<String, String> BirthMap;

	// database declarations
	private SQLiteDatabase database;

	static final String DATABASE_NAME = "BirthdayReminder";

	static final String TABLE_NAME = "birthTable";

	static final int DATABASE_VERSION = 1;

	static final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME
			+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ " name TEXT NOT NULL, " + " birthday TEXT NOT NULL);";

	// class that creates and manages the provider's database
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(DBHelper.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ". Old data will be destroyed");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}

	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Context context = getContext();
		dbHelper = new DBHelper(context);
		// permissions to be writable
		database = dbHelper.getWritableDatabase();

		if (database == null)
			return false;
		else
			return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		// the TABLE_NAME to query on
		queryBuilder.setTables(TABLE_NAME);

		queryBuilder.setProjectionMap(BirthMap);

		Cursor cursor = queryBuilder.query(database, projection, selection,
				selectionArgs, null, null, sortOrder);
		/**
		 * register to watch a content URI for changes
		 */
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		long row = database.insert(TABLE_NAME, "", values);

		// If record is added successfully
		if (row > 0) {
			Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		throw new SQLException("Fail to add a new record into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count = 0;

		// delete all the records of the table
		count = database.delete(TABLE_NAME, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub

		return "vnd.android.cursor.dir/vnd.example.friends";

	}

}
