package ir.ahmadandroid.mapproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ir.ahmadandroid.mapproject.model.Person;

public class MyDatabase extends SQLiteOpenHelper {

    private static final int version = 1;
    private static final String TAG = "DataBase";
    private static final String TB_NAME = "tb_peyk";
    private static final String DB_NAME = "db_peyk";
    private static final String[] COLUMNS = new String[]{Person.PERSON_ID, Person.PERSON_IDENTIFYCODE,
            Person.PERSON_NATIONALCODE, Person.PERSON_NAME, Person.PERSON_MOBILE, Person.PERSON_ISADMIN,
            Person.PERSON_STATE};
    private Context context;
    public static final String DB_QUERY = "CREATE TABLE IF NOT EXISTS '" + TB_NAME
            + "' ('" + Person.PERSON_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "'" + Person.PERSON_IDENTIFYCODE + "' INTEGER, " +
            "'" + Person.PERSON_NATIONALCODE + "' TEXT, " +
            "'" + Person.PERSON_NAME + "' TEXT ," +
            "'" + Person.PERSON_MOBILE + "' TEXT ," +
            "'" + Person.PERSON_ISADMIN + "' INTEGER ," +
            "'" + Person.PERSON_STATE + "' INTEGER" +
            ")";

    public MyDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public Long insertPersonToDb(Person person) {
        SQLiteDatabase dBase = getWritableDatabase();
        long insertRes=0;
        try {
            ContentValues values = Person.writeToValues(person);
            insertRes = dBase.insert(TB_NAME, null, values);
            Log.i(TAG,"insertRes => "+insertRes);
        } catch (Exception ex) {

        } finally {
            if (dBase.isOpen()) {
                dBase.close();
            }
        }
        return insertRes;
    }

    public void insertPersonListTodDB() {
        List<Person> personList = Person.getPersonList();
        SQLiteDatabase dBase = getWritableDatabase();

        try {
            for (Person person:personList){
                ContentValues values = Person.writeToValues(person);
                dBase.insert(TB_NAME, null, values);
            }
        } catch (Exception ex) {

        } finally {
            if (dBase.isOpen()) {
                dBase.close();
            }
        }
    }

    public List<Person> readPersonListFromDb() {
        List<Person> personList = new ArrayList<>();
        SQLiteDatabase dBase = getReadableDatabase();
        try {
            Cursor cursor = dBase.query(TB_NAME, COLUMNS, Person.PERSON_ISADMIN+"=0", null, null, null, Person.PERSON_ID + " DESC");
            List<Person> personList1 = Person.cursorToPersonList(cursor);
            if (personList1.size() > 0) {
                personList = personList1;
            }
        } catch (Exception ex){

        }finally {
            if (dBase.isOpen()) {
                dBase.close();
            }
        }
        return personList;
    }

    public List<Person> readPersonListFromDb(String nationalCode) {
        List<Person> personList = new ArrayList<>();
        SQLiteDatabase dBase = getReadableDatabase();
        try {
            Cursor cursor = dBase.query(TB_NAME, COLUMNS, Person.PERSON_NATIONALCODE+"=?", new String[]{nationalCode}, null, null, null);
            List<Person> personList1 = Person.cursorToPersonList(cursor);
            if (personList1.size() > 0) {
                personList = personList1;
            }
        } catch (Exception ex){

        }finally {
            if (dBase.isOpen()) {
                dBase.close();
            }
        }
        return personList;
    }

    public boolean searchPerson(String nationalCode){
        SQLiteDatabase dBase = getReadableDatabase();
        try {
            Cursor cursor = dBase.query(TB_NAME, COLUMNS, Person.PERSON_NATIONALCODE + "=?", new String[]{nationalCode}, null, null, null);
            List<Person> personList = Person.cursorToPersonList(cursor);
            if (personList.size()>0){
                return true;
            }
        }catch (Exception ex){

        }finally {
            if (dBase.isOpen()){
                dBase.close();
            }
        }
        return false;
    }

    public int editTestInDb(Person person) {
        SQLiteDatabase dBase = getWritableDatabase();
        int upd = 0;
        try {
//            Log.i(TAG, "editTest id= " + test.getId() + " count= " + test.getSpermCount() + " isSave= " + test.getIsSave());
            ContentValues values = Person.writeToValues(person);
            upd = dBase.update(TB_NAME, values, Person.PERSON_NATIONALCODE+ "=?", new String[]{person.getNationalCode()});
        } catch (Exception ex) {
            Log.i(TAG, "editTest Exception= " + ex.getMessage());
        } finally {
            if (dBase.isOpen()) {
                dBase.close();
            }
        }
        return upd;
    }

    public int deleteTestFromDb(Person person) {
        SQLiteDatabase dBase = getWritableDatabase();
        int del = 0;
        try {
//            Log.i(TAG, "deleteTest id= " + test.getId() + " count= " + test.getSpermCount() + " isSave= " + test.getIsSave());
            ContentValues values = Person.writeToValues(person);
            del = dBase.update(TB_NAME, values, Person.PERSON_ID+ "=?", new String[]{String.valueOf(person.getId())});
        } catch (Exception ex) {
            Log.i(TAG, "deleteTest Exception= " + ex.getMessage());
        } finally {
            if (dBase.isOpen()) {
                dBase.close();
            }
        }
        return del;
    }

    public int getLastIdentifyCode() {
        SQLiteDatabase dBase = getReadableDatabase();
        try {
            Cursor cursor = dBase.query(TB_NAME, COLUMNS, null, null, null, null, Person.PERSON_IDENTIFYCODE + " DESC");
            List<Person> personList = Person.cursorToPersonList(cursor);
            if (personList.size() == 0) {
                return 111;
            } else {
//                for (Test test:testList){
//                    Log.i(TAG,String.valueOf(test.getId()));
//                }
                return personList.get(0).getIdentifyCode()+1;
            }
        } finally {
            if (dBase.isOpen()) {
                dBase.close();
            }
        }
    }

    /*

  public List<MySms> searchPhoneFromSmsTable(String phone) {
        List<MySms> smsList = new ArrayList<>();
        try {
            SQLiteDatabase dbase = getReadableDatabase();
            Cursor cursor = dbase.query(SMS_TABLE, SMS_COLUMNS, "phone LIKE '%" + phone + "%'", null, null, null, null);
            smsList = MySms.getSmstListFromCursor(cursor);
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return smsList;
    }

    public boolean isEmptySmsTb() {
        try {
            SQLiteDatabase dBase = getReadableDatabase();
            Cursor cursor = dBase.rawQuery("SELECT * FROM '" + SMS_TABLE + "'", null);
            if (cursor.getCount() == 0) {
                return true;
            }
            if (dBase.isOpen()) {
                dBase.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public List<MySms> readSmsListFromDb() {
        List<MySms> smsList = new ArrayList<>();
        try {
            SQLiteDatabase dbase = getReadableDatabase();
            Cursor cursor = dbase.rawQuery("SELECT * FROM '" + SMS_TABLE + "'", null);
            smsList = MySms.getSmstListFromCursor(cursor);
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return smsList;
    }

    public void updateSmsListToDb(String name, ContentValues values) {
        try {
            SQLiteDatabase dbase = getWritableDatabase();
            dbase.update(SMS_TABLE, values, "name=?", new String[]{name});
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public boolean searchSmsToDb(String name) {
        try {
            SQLiteDatabase dbase = getReadableDatabase();
            Cursor cursor = dbase.query(SMS_TABLE, SMS_COLUMNS, "name=?", new String[]{name}
                    , null, null, null);
            List<MySms> smsList = MySms.getSmstListFromCursor(cursor);
            if (smsList.isEmpty()) {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    public boolean deleteSmsFromDb(String name) {
        try {
            SQLiteDatabase dbase = getWritableDatabase();
            int deleteCount = dbase.delete(SMS_TABLE, "name=?", new String[]{name});
            if (dbase.isOpen()) {
                dbase.close();
            }
            if (deleteCount > 0) {
                Log.i("TagDelete", "delete sms from db");
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public void clearSmsTable() {
        try {
            SQLiteDatabase dbase = getWritableDatabase();
            dbase.delete(SMS_TABLE, null, null);
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<MySms> sortSmsTable(String selection, String[] selArg) {
        List<MySms> smsList = new ArrayList<>();
        try {
            SQLiteDatabase dbase = getReadableDatabase();
            Cursor cursor = dbase.query(SMS_TABLE, SMS_COLUMNS, selection, selArg, null, null, "smsSrt" + " DESC");
            smsList = MySms.getSmstListFromCursor(cursor);
            if (dbase.isOpen()) {
                dbase.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return smsList;
    }


    //function for call table
    public void insertcallListToDb(List<MyCall> callList) {
        try {
            SQLiteDatabase dbase = getWritableDatabase();
            for (MyCall call : callList) {
                ContentValues values = MyCall.writeCallToContentValue(call);
                if (!searchCallToDb(call.getName())) {
                    dbase.insert(CALL_TABLE, null, values);
                }
            }
            if (dbase.isOpen()) {
                dbase.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void updateCallListToDb(String name, ContentValues values) {
        try {
            SQLiteDatabase dbase = getWritableDatabase();
            dbase.update(CALL_TABLE, values, "name=?", new String[]{name});
            if (dbase.isOpen()) {
                dbase.close();
                ;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public boolean searchCallToDb(String name) {
        try {
            SQLiteDatabase dbase = getReadableDatabase();
            Cursor cursor = dbase.query(CALL_TABLE, CALL_COLUMNS, "name=?", new String[]{name}
                    , null, null, null);
            List<MyCall> callList = MyCall.getCalltListFromCursor(cursor);
            if (callList.isEmpty()) {
                return false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    public boolean deleteCallFromDb(String name) {
        try {
            SQLiteDatabase dbase = getWritableDatabase();
            int deleteCount = dbase.delete(CALL_TABLE, "name=?", new String[]{name});
            if (dbase.isOpen()) {
                dbase.close();
            }
            if (deleteCount > 0) {
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public boolean isEmptyCallTb() {
        try {
            SQLiteDatabase dBase = getReadableDatabase();
            Cursor cursor = dBase.rawQuery("SELECT * FROM '" + CALL_TABLE + "'", null);
            if (cursor.getCount() == 0) {
                return true;
            }
            if (dBase.isOpen()) {
                dBase.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return false;
    }

    public void clearCallTable() {
        try {
            SQLiteDatabase dbase = getWritableDatabase();
            dbase.delete(CALL_TABLE, null, null);
            if (dbase.isOpen()) {
                dbase.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public List<MyCall> sortCallTable(String selection, String[] selArg) {
        List<MyCall> callList=new ArrayList<>();
        try {
            SQLiteDatabase dbase = getReadableDatabase();
            Cursor cursor = dbase.query(CALL_TABLE, CALL_COLUMNS, selection, selArg, null, null, "callSort" + " DESC");
            callList = MyCall.getCalltListFromCursor(cursor);
            if (dbase.isOpen())
                dbase.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return callList;
    }
     */
}
