package org.adroidtown.automata;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 색상이나 선굵기를 선택할 수 있도록 기능 추가
 *
 * @author Mike
 *
 */
public class MainActivity extends AppCompatActivity {

    GoodPaintBoard board;
    Button undoBtn;
    SQLiteDatabase db;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout boardLayout = (LinearLayout) findViewById(R.id.boardLayout);
        undoBtn = (Button) findViewById(R.id.undoBtn);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);

        board = new GoodPaintBoard(this);
        board.setLayoutParams(params);
        board.setPadding(2, 2, 2, 2);

        boardLayout.addView(board);

        undoBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                board.undo();
            }
        });
        ////////////////////////////////////////////////////////////////////////
        Button Recog = (Button) findViewById(R.id.Recog);
        final TextView editText = (TextView) findViewById((R.id.editText));

        Recog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String letter = "";
                letter = board.chainVal;
                //Toast.makeText(getApplicationContext(), letter, Toast.LENGTH_LONG).show();
                String s = "";
                s = GetLetterFromDB_new(letter, board.shape, board.length);
                System.out.println("possible : " + s);
                editText.setText(s);
            }
        });
        ///////////////////////////////////////////////////////////////////////////////
        Button Save = (Button) findViewById(R.id.Save);
        Save.setOnClickListener(new OnClickListener() {
            @Override
            /**
             * 경량화 된 것과 함께 자음이든 모음이든 받아서 같이 저장
             */
            public void onClick(View v) {
                String letter = editText.getText().toString();
                InsertCase(letter, board.chainVal);
            }
        });

    }
    String GetLetterFromDB_new(String args, int shape, int length) {

        System.out.println("length_count=============" + length);
        System.out.println("shape==============" + shape);
        System.out.println("args==============" + args);
        String possible = "";
        char cho = '\0';
        char jung = '\0';
        char jong = '\0';
        try {
            db = openOrCreateDatabase(
                    "automata.db",
                    Activity.MODE_PRIVATE,
                    null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Cursor c;
        String SQL;
        ///////////////////////////////////////////////예외상황
        SQL = "select *"
                + " from " + " exceptionCase "
                + " where code = '" + args + "';";
        c = db.rawQuery(SQL, null);

        if (c.moveToFirst()) {
            String s = c.getString(1);
            c.close();
            return s;
        }
        c.close();
        //////////////////////////////////////////초성
        int i = 1;
        System.out.println("시작");
        while(i<=args.length()-1)  {
            cho = '\0';
            jung = '\0';
            jong = '\0';
            String temp = "";
            while(args.charAt(i)!='0' && i!=args.length() - 1) {
                i++;
            }
            if (i==args.length() - 1)
                i ++;
            temp = args.substring(0, i);
            SQL = "select * from " +
                    "(select * from l_a where code = '" + temp + "')  " +
                    "where length = '" + length + "';";
            c = db.rawQuery(SQL, null);
            System.out.println("========초성");
            System.out.println("temp : " + temp);
            if (!c.moveToFirst()) {
                System.out.println("cho 없음");
                c.close();
                i++;
                continue;
            }
            else {
                System.out.println("cho 발견");
                c.moveToFirst();
                String s = c.getString(1);
                cho = s.charAt(0);
                System.out.println("발견한 것" + cho);
                c.close();
            }
            ////////////////////////중성
            int j = i;
            while(j<=args.length()-1)  {
                jung = '\0';
                jong = '\0';
                temp = "";
                while(args.charAt(j)!='0' && j!=args.length() - 1) {
                    j++;
                }
                if (j==args.length() - 1)
                    j++;

                temp = args.substring(i, j);
                SQL = "select * from " +
                        "(select * from l_b where code = '" + temp + "')  " +
                        "where shape = '" + shape + "';";
                c = db.rawQuery(SQL, null);
                System.out.println("========중성");
                System.out.println("shape : " + shape);
                System.out.println("temp : " + temp);

                if (!c.moveToFirst()) {
                    System.out.println("jung 없음");
                    c.close();
                    j++;
                    continue;
                }
                else {
                    System.out.println("jung 발견");
                    c.moveToFirst();
                    String s = c.getString(1);
                    jung = s.charAt(0);
                    System.out.println("발견한 것" + jung);
                    c.close();
                }
                //////////////////////// 종성
                int k = j;
                while(k<=args.length()-1)  {
                    jong = '\0';
                    temp = "";
                    while(args.charAt(k)!='0' && k!=args.length() - 1) {
                        k++;
                    }
                    if (k==args.length() - 1)
                        k++;
                    temp = args.substring(j);

                    if (temp.charAt(1) == '*') {
                        System.out.println("======================================================??????");
                        temp = temp.replace('*', '0');
                        temp = temp.substring(1);
                    }

                    System.out.println("*없어졌나 확인 : " + temp );

                    SQL = "select *"
                            + " from " + " l_c "
                            + " where code = '" + temp + "';";
                    c = db.rawQuery(SQL, null);
                    System.out.println("========종성");
                    System.out.println("temp : " + temp);
                    if (!c.moveToFirst()) {
                        System.out.println("jong 없음");
                        c.close();
                        break;
                    }
                    else{
                        System.out.println("jong 발견");
                        c.moveToFirst();
                        String s = c.getString(1);
                        jong = s.charAt(0);
                        System.out.println("발견한 것" + jong);
                        c.close();
                        break;
                    }
                }
                //////////////////////////확인 단계
                if (jung != '\0' && (j == args.length() || jong != '\0')) {
                    System.out.println("확인3");
                    if(cho == 'ㄱ')
                        cho = 0;
                    if(cho == 'ㄲ')
                        cho = 1;
                    if(cho == 'ㄴ')
                        cho = 2;
                    if(cho == 'ㄷ')
                        cho = 3;
                    if(cho == 'ㄸ')
                        cho = 4;
                    if(cho == 'ㄹ')
                        cho = 5;
                    if(cho == 'ㅁ')
                        cho = 6;
                    if(cho == 'ㅂ')
                        cho = 7;
                    if(cho == 'ㅃ')
                        cho = 8;
                    if(cho == 'ㅅ')
                        cho = 9;
                    if(cho == 'ㅆ')
                        cho = 10;
                    if(cho == 'ㅇ')
                        cho = 11;
                    if(cho == 'ㅈ')
                        cho = 12;
                    if(cho == 'ㅉ')
                        cho = 13;
                    if(cho == 'ㅊ')
                        cho = 14;
                    if(cho == 'ㅋ')
                        cho = 15;
                    if(cho == 'ㅌ')
                        cho = 16;
                    if(cho == 'ㅍ')
                        cho = 17;
                    if(cho == 'ㅎ')
                        cho = 18;
                    jung = (char) ((char) ((jung - 0x314F)) % 21);
                    if(jong!='\0') {
                        if(jong == 'ㄱ')
                            jong = 1;
                        if(jong== 'ㄲ')
                            jong = 2;
                        if(jong == 'ㄳ')
                            jong = 3;
                        if(jong == 'ㄴ')
                            jong = 4;
                        if(jong== 'ㄵ')
                            jong = 5;
                        if(jong == 'ㄶ')
                            jong = 6;
                        if(jong == 'ㄷ')
                            jong = 7;
                        if(jong == 'ㄹ')
                            jong = 8;
                        if(jong == 'ㄺ')
                            jong = 9;
                        if(jong == 'ㄻ')
                            jong = 10;
                        if(jong == 'ㄼ')
                            jong = 11;
                        if(jong == 'ㄽ')
                            jong = 12;
                        if(jong == 'ㄾ')
                            jong = 13;
                        if(jong == 'ㄿ')
                            jong = 14;
                        if(jong == 'ㅀ')
                            jong = 15;
                        if(jong == 'ㅁ')
                            jong = 16;
                        if(jong == 'ㅂ')
                            jong = 17;
                        if(jong == 'ㅄ')
                            jong = 18;
                        if(jong == 'ㅅ')
                            jong = 19;
                        if(jong == 'ㅆ')
                            jong =20;
                        if(jong == 'ㅇ')
                            jong =21;
                        if(jong == 'ㅈ')
                            jong =22;
                        if(jong == 'ㅊ')
                            jong =23;
                        if(jong == 'ㅋ')
                            jong =24;
                        if(jong == 'ㅌ')
                            jong =25;
                        if(jong == 'ㅍ')
                            jong =26;
                        if(jong == 'ㅎ')
                            jong =27;
                    }
                    possible+= (char)(0xAC00 + 28 * 21 * (cho) + 28 * (jung) + (jong));
                }
                j++;
            }
            if(cho != '\0' && i == args.length()) {
                possible += cho;
            }
            i++;
        }
        db.close();
        if (possible == "")
            possible = "?";
        return possible;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    char GetLetterFromDB(String args, int shape) {
        char cho = '\0';
        char jung = '\0';
        char jong = '\0';
        String temp;
        try {
            db = openOrCreateDatabase(
                    "automata.db",
                    Activity.MODE_PRIVATE,
                    null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Cursor c;
        String SQL;

        temp = args;
        int check = 0;
        int check_jong = 0;
        for(int j=0; j<6; j++) {
            SQL = "select *"
                    + " from " + " l_a "
                    + " where code = '" + temp + "';";
            c = db.rawQuery(SQL, null);

            if (!c.moveToFirst()) {
                System.out.println("111     "+temp);
                System.out.println("cho 없음");
                c.close();
            }
            else{
                System.out.println("cho 발견");
                c.moveToFirst();
                String s = c.getString(1);
                System.out.println("cho : " + s);
                cho = s.charAt(0);
                c.close();
                break;
            }
            for (int i = temp.length() - 1; i >= 0; i--) {
                if (temp.charAt(i) == '0') {
                    check = i;
                    break;
                }
            }
            if (check == 0) {
                break;
            }
            temp = temp.substring(0, check);
        }
        /**
         * 중성
         */
        temp = args.substring(check);
        check_jong += check;
        int check_jung = 0;
        for(int j=0; j<6; j++) {
            if(check == 0)
                break;
            SQL = "select *"
                    + " from " + " l_b "
                    + " where code = '" + temp + "';";
            c = db.rawQuery(SQL, null);

            if (!c.moveToFirst()) {
                System.out.println("222     "+temp);
                System.out.println("jung 없음");
                c.close();
            }
            else{
                System.out.println("jung 발견");
                System.out.println(temp);
                System.out.println(shape);
                SQL = "select * from " +
                        "(select * from l_b where code = '" + temp + "')  " +
                        "where shape = '" + shape + "';";
                c = db.rawQuery(SQL, null);
                if (!c.moveToFirst()) {
                    SQL = "select *"
                            + " from " + " l_b "
                            + " where code = '" + temp + "';";
                    c = db.rawQuery(SQL, null);

                    c.moveToFirst();
                    String s = c.getString(1);
                    System.out.println("jung : " + s);
                    jung = s.charAt(0);
                    c.close();
                    break;
                }
                else {
                    c.moveToFirst();
                    String s = c.getString(1);
                    System.out.println("jung : " + s);
                    jung = s.charAt(0);
                    c.close();
                    break;
                }
            }
            for (int i = temp.length() - 1; i >= 0; i--) {
                if (temp.charAt(i) == '0') {
                    check_jung = i;
                    break;
                }
            }
            if (check_jung == 0) {
                break;
            }
            temp = temp.substring(0, check_jung);
        }
        /**
         * 종성
         */
        check_jong += check_jung;
        temp = args.substring(check_jong);
        for(int j=0; j<6; j++) {
            if(check_jung == 0) {
                break;
            }
            SQL = "select *"
                    + " from " + " l_c "
                    + " where code = '" + temp + "';";
            c = db.rawQuery(SQL, null);

            if (!c.moveToFirst()) {
                System.out.println("333     "+temp);
                System.out.println("jong 없음");
                c.close();
            }
            else{
                System.out.println("jong 발견");
                c.moveToFirst();
                String s = c.getString(1);
                System.out.println("jong : " + s);
                jong = s.charAt(0);
                c.close();
                break;
            }
        }
        db.close();
        if(cho == '\0' && jung == '\0' && jong == '\0')
            return '?';
        if(jung == '\0' && jong == '\0')
            return cho;

        if(jung!='\0') {
            if(cho == 'ㄱ')
                cho = 0;
            if(cho == 'ㄲ')
                cho = 1;
            if(cho == 'ㄴ')
                cho = 2;
            if(cho == 'ㄷ')
                cho = 3;
            if(cho == 'ㄸ')
                cho = 4;
            if(cho == 'ㄹ')
                cho = 5;
            if(cho == 'ㅁ')
                cho = 6;
            if(cho == 'ㅂ')
                cho = 7;
            if(cho == 'ㅃ')
                cho = 8;
            if(cho == 'ㅅ')
                cho = 9;
            if(cho == 'ㅆ')
                cho = 10;
            if(cho == 'ㅇ')
                cho = 11;
            if(cho == 'ㅈ')
                cho = 12;
            if(cho == 'ㅉ')
                cho = 13;
            if(cho == 'ㅊ')
                cho = 14;
            if(cho == 'ㅋ')
                cho = 15;
            if(cho == 'ㅌ')
                cho = 16;
            if(cho == 'ㅍ')
                cho = 17;
            if(cho == 'ㅎ')
                cho = 18;

        }

        if (jung!='\0')
            jung = (char) ((char) ((jung - 0x314F)) % 21);

        if(jong!='\0') {
            jong = (char) ((char) (jong - 0x3131 + 1) % 28);
        }

        return (char) (0xAC00 + cho * 588 + jung * 28 + jong);
    }

    void InsertCase(String s_letter, String s_code) {
        try {
            db = openOrCreateDatabase(
                    "automata.db",
                    Activity.MODE_PRIVATE,
                    null);
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        db.execSQL("create table if not exists " + "exceptionCase" + "("
                + " _id integer PRIMARY KEY autoincrement, "
                + " letter text, "
                + " code text);");
        db.execSQL( "insert into " + "exceptionCase" + " (letter, code) values " +
                "('" + s_letter + "','" + s_code + "');" );
        db.close();
    }


///////////////////////////////////////////////////////////////////////////////////////
    /**
     * update records using parameters
     */
    private int updateRecordParam(String name) {

        ContentValues recordValues = new ContentValues();
        recordValues.put("age", 43);
        String[] whereArgs = {"Rice"};

        int rowAffected = db.update(name,
                recordValues,
                "name = ?",
                whereArgs);
        db.close();
        return rowAffected;
    }

    /**
     * delete records using parameters
     */
    private int deleteRecordParam(String name) {

        String[] whereArgs = {"Rice"};

        int rowAffected = db.delete(name,
                "name = ?",
                whereArgs);

        db.close();
        return rowAffected;
    }

}