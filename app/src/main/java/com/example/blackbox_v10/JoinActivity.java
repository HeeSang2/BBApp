package com.example.blackbox_v10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {

    int state = 0;

    // DB
    MemberSQLiteOpenHelper dbHelper;
    SQLiteDatabase mdb;
    Cursor mCursor;

    // Main
    EditText et_id, et_password, et_name, et_phone, et_address, et_port;
    Button bt_join;

    // Values
    String id, password, name, phone, address, port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // DB
        dbHelper = new MemberSQLiteOpenHelper(this);
        mdb = dbHelper.getWritableDatabase();

        // Main
        et_id = (EditText) findViewById(R.id.et_id);
        et_password = (EditText) findViewById(R.id.et_password);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_address = (EditText) findViewById(R.id.et_address);
        et_port = (EditText) findViewById(R.id.et_port);
        bt_join = (Button) findViewById(R.id.btn_join);
    }

    // 아이디 중복검사
    public void onClickCheckId(View view) {
        id = et_id.getText().toString();

        mCursor = mdb.query(MemberSQLiteOpenHelper.MEMBER, new String[]{"id"}, null, null, null, null, null);

        if (id.length() < 4) {
            Toast.makeText(this, "ID는 4글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            et_id.requestFocus();
        } else {
            while (mCursor.moveToNext()) {
                String id = mCursor.getString(0);

                // 일치하는 아이디가 존재하면
                if (id.equals(et_id.getText().toString())) {

                    AlertDialog.Builder AlertCheckID = new AlertDialog.Builder(JoinActivity.this);

                    // alert의 title과 Messege 세팅
                    AlertCheckID.setTitle("아이디 중복");
                    AlertCheckID.setMessage("일치하는 아이디가 존재합니다.");

                    // OK 버튼을 눌렸을 경우
                    AlertCheckID.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            et_id.requestFocus();
                        }
                    });
                    AlertCheckID.show();
                    break;
                }
            }

            // 끝까지 읽어서 일치하는 아이디가 존재하지 않으면
            if (mCursor.isAfterLast()) {
                // 현재의 아이디로 할 것인지 한 번 더 물어보고 확정짓기.
                AlertDialog.Builder AlertConfirmID = new AlertDialog.Builder(JoinActivity.this);

                AlertConfirmID.setTitle("아이디 확정");
                AlertConfirmID.setMessage("이 아이디로 하시겠습니까?\nid : " + et_id.getText().toString());

                // "예" 누르면 아이디 변경 불가
                AlertConfirmID.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        et_id.setFocusable(false);
                        bt_join.setClickable(true);
                        state = 1;
                    }
                });

                // "아니요" 버튼 누르면 et_id에 포커스
                AlertConfirmID.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        et_id.requestFocus();
                    }
                });
                AlertConfirmID.show();
            }
        }
    }



    // Join : 데이터 베이스 등록
    public void onClickJoin(View view) {
        id = et_id.getText().toString();
        password = et_password.getText().toString();
        name = et_name.getText().toString();
        phone = et_phone.getText().toString();
        address = et_address.getText().toString();
        port = et_port.getText().toString();

        if (state == 0) {
            Toast.makeText(this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
        } else {

            //아이디 비밀번호 개수 및 공백 및 DB저장
            if (password.length() < 4) {
                Toast.makeText(this, "비밀번호는 4글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                et_password.requestFocus();
            } else if (password.equals("")){
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (name.equals("")){
                Toast.makeText(this, "사용자 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (phone.equals("")){
                Toast.makeText(this, "사용자 전화번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (address.equals("")){
                Toast.makeText(this, "발급된 IP 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (port.equals("")){
                Toast.makeText(this, "발급된 Port 번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            }
            else {
                ContentValues values = new ContentValues();

                values.put(MemberSQLiteOpenHelper.MEMBER_ID, id);
                values.put(MemberSQLiteOpenHelper.MEMBER_PASSWORD, password);
                values.put(MemberSQLiteOpenHelper.MEMBER_NAME, name);
                values.put(MemberSQLiteOpenHelper.MEMBER_PHONE, phone);
                values.put(MemberSQLiteOpenHelper.MEMBER_ADDRESS, address);
                values.put(MemberSQLiteOpenHelper.MEMBER_PORT, port);


                long result = mdb.insert(MemberSQLiteOpenHelper.MEMBER, null, values);
                Toast.makeText(this, "회원가입 성공! 로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();

                finish();
            }
            //아이디 갯수 및 DB저장
        }
    }

}