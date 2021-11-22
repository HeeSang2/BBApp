package com.example.blackbox_v10;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class MemberSQLiteOpenHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "breakbreaker.db";

    public static final String MEMBER = "member";
    public static final String MEMBER_ID = "id";
    public static final String MEMBER_PASSWORD = "password";
    public static final String MEMBER_NAME = "name";
    public static final String MEMBER_PHONE = "phone";
    public static final String MEMBER_ADDRESS = "address";
    public static final String MEMBER_PORT = "port";


    public static final String QUESTION = "qna";
    public static final String QUESTION_Q = "question";
    public static final String QUESTION_A = "answer";

    public static final String CAPTURE = "capture";
    public static final String CAPTURE_DATE = "date";
    public static final String CAPTURE_TIME = "time";
    public static final String CAPTURE_PICTURE = "picture";

    Context context;

    public MemberSQLiteOpenHelper(@Nullable Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create Member 테이블
        sqLiteDatabase.execSQL("CREATE TABLE "+ MEMBER +" ("
                + MEMBER_ID + " TEXT, "
                + MEMBER_PASSWORD + " TEXT, "
                + MEMBER_NAME + " TEXT, "
                + MEMBER_PHONE + " TEXT,"
                + MEMBER_ADDRESS + " TEXT,"
                + MEMBER_PORT + " TEXT);");

        // Create Qna테이블
        sqLiteDatabase.execSQL("CREATE TABLE "+ QUESTION +" ("
                + QUESTION_Q +" TEXT, "
                + QUESTION_A + " TEXT);");

        // Create Capture 테이블
        sqLiteDatabase.execSQL("CREATE TABLE "+ CAPTURE +" ("
                + CAPTURE_DATE +" TEXT, "
                + CAPTURE_TIME + " TEXT,"
                + CAPTURE_PICTURE + " BLOB);");

        // Qna 테이블에 값 추가
        insertQna(sqLiteDatabase,getStr(R.string.q1), getStr(R.string.a1));
        insertQna(sqLiteDatabase,getStr(R.string.q2), getStr(R.string.a2));
        insertQna(sqLiteDatabase,getStr(R.string.q3), getStr(R.string.a3));
        insertQna(sqLiteDatabase,getStr(R.string.q4), getStr(R.string.a4));
        insertQna(sqLiteDatabase,getStr(R.string.q5), getStr(R.string.a5));
        insertQna(sqLiteDatabase,getStr(R.string.q6), getStr(R.string.a6));
        insertQna(sqLiteDatabase,getStr(R.string.q7), getStr(R.string.a7));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MEMBER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+QUESTION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+CAPTURE);

        onCreate(sqLiteDatabase);
    }

    // Qna 데이터 초기화 메소드
    void insertQna(SQLiteDatabase sqLiteDatabase, String question, String answer){

        ContentValues values=new ContentValues();
        values.put(QUESTION_Q, question);
        values.put(QUESTION_A, answer);

        long result=sqLiteDatabase.insert(QUESTION,null,values);
    }

    // 리소스를 String으로 변환 메소드
    String getStr(int res){
        return this.context.getResources().getString(res);
    }

}
