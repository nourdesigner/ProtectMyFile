package PMF.protectMyFile.PMF;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper  extends SQLiteOpenHelper{
 private static final int DATABASE_VERSION=1;
 private static final  String DATABASE_NAME="contacts.db";
 private static final   String TABLE_NAME="contacts";
 private static final   String COLUMN_ID="id";
 private static final   String COLUMN_USERNAME="username";
 private static final   String COLUMN_PASSWORD="password";
// private static final   String COLUMN_CHECK="check";
 private static final   String COLUMN_EMAIL="email";
 private static final   String  TABLE_CREATE="create table contacts(id integer primary key not null ,"+"username text not null,email text not null,password text not null);";
 SQLiteDatabase db;

 public void insertcontact(contact c)
 {
	db =this.getWritableDatabase();
	ContentValues values=new ContentValues() ;
	String query="select * from contacts";
	Cursor cursor=db.rawQuery(query, null);
	int count =cursor.getCount();
	values.put(COLUMN_ID,count);
	values.put(COLUMN_USERNAME,c.getusername());
	values.put(COLUMN_EMAIL,c.getemail());
	values.put(COLUMN_PASSWORD,c.getpassword());
	db.insert(TABLE_NAME, null, values);
	db.close();
 }
 public boolean checkuser(String username)
 { 
	 db = this.getReadableDatabase();
     String query = "select username from "+TABLE_NAME;
     Cursor cursor = db.rawQuery(query , null);
     String a;
boolean available = true; //initially assuming that it is available


     if(cursor.moveToFirst())
     {
         do{
             a = cursor.getString(0);

             if(a.equals(username))
             {
                 available = false;
                 break;
             }
         }
         while(cursor.moveToNext());
     }
     return available;
	 
 }
 
 
 public String searchPass(String username)
 {  
	 db=this.getReadableDatabase();
	String query="select username,password from "+TABLE_NAME;
	 Cursor cursor=db.rawQuery(query,null);
	String a ,b ;
	b="not found";
	 if(cursor.moveToFirst())
	 
	 {
		do{
			a=cursor.getString(0);
			
			
			if(a.equals(username))
			{
				b=cursor.getString(1);
				break;
			}
			
		} while(cursor.moveToNext());
		 
	 }
	 
	 
	 
	 return b;
	 
 }
 
 
 
 
 public DatabaseHelper(Context context)
 {
	super(context,DATABASE_NAME,null,DATABASE_VERSION); 
 }
 
 public void onCreate(SQLiteDatabase db){
	db.execSQL(TABLE_CREATE);
	this.db=db;
}
	
public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion )
{
	String query="DROP TABLE IF EXISTS"+TABLE_NAME;
	db.execSQL(query);
	this.onCreate(db);
}



	
	

}
