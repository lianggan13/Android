package com.example.wechart.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wechart.bean.NotepadBean;
import com.example.wechart.bean.Student;

import java.util.ArrayList;
import java.util.List;

public class SqliteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SqliteHelper";
    private static final String DATABASE_NAME = "wechart.db";// 数据库名
    private static final int DATABASE_VERSION = 1;// 数据库版本号
    private static final String TABLE_NAME_STUDENT = "student"; //学生表
    private static final String TABLE_NAME_COURSE = "course"; //课程表
    private static final String TABLE_NAME_STUDENT_COURSE = "student_course"; // 关联表
    private static final String TABLE_NAME_SETTINGS = "settings"; // 设置表

    private static SqliteHelper mHelper = null;

    private SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //单例模式
    public static SqliteHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new SqliteHelper(context);
        }
        return mHelper;
    }

    //数据库创建时被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        initTable(db);
        initData(db);
    }

    //数据库升级
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) { // 数据库 1 -> 2 升级
            // 创建设置表
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SETTINGS + " (keycolumn text primary key,valuecolumn text)");
        }
    }

    /**
     * 初始化数据表
     *
     * @param db 数据库
     */
    private void initTable(SQLiteDatabase db) {
        // 创建学生表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_STUDENT + " (" +
                "_id integer primary key autoincrement," +
                "student_name text," +
                "student_code text," +
                "username text," +
                "password text," +
                "class_name text)");
        // 创建课程表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_COURSE + " (" +
                "_id integer primary key autoincrement," +
                "course_name text," +
                "course_code text," +
                "course_teacher text," +
                "learning_hours text)");
        // 创建选课表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_STUDENT_COURSE + " (" +
                "student_id integer," +
                "course_id  integer," +
                "FOREIGN KEY (student_id) REFERENCES student(_id)," +
                "FOREIGN KEY (course_id) REFERENCES course(_id))");

        // 创建用户表
        db.execSQL("CREATE TABLE IF NOT EXISTS user(id integer primary key autoincrement,name varchar(10),phone integer)");

        // 创建笔记表
        db.execSQL("CREATE TABLE IF NOT EXISTS notepad(id integer primary key autoincrement,content text,time text)");
    }

    /**
     * 初始化数据库内部模拟数据(仅在第一次安装时执行)
     *
     * @param db 数据库
     */
    private void initData(SQLiteDatabase db) {
        db.execSQL("insert into " + TABLE_NAME_STUDENT +
                " (student_name , student_code, username, password, class_name) " +
                "values" +
                "('张老三', '202100000001', 'helloworld', '12345678', '计算机1班')");
        db.execSQL("insert into " + TABLE_NAME_STUDENT +
                " (student_name , student_code, username, password, class_name) " +
                "values" +
                "('李老四', '202100000002', 'helloworld', 'abcdefgh', '计算机2班')");
        db.execSQL("insert into " + TABLE_NAME_STUDENT +
                " (student_name , student_code, username, password, class_name) " +
                "values" +
                "('王老五', '202100000003', '你好和都很好的', 'aaaaaaaa', '计算机3班')");
        db.execSQL("insert into " + TABLE_NAME_STUDENT +
                " (student_name , student_code, username, password, class_name) " +
                "values" +
                "('刘老六', '202100000004', 'myname', '66665678', '计算机4班')");

        db.execSQL("insert into " + TABLE_NAME_COURSE +
                " (course_name , course_code, course_teacher, learning_hours) " +
                "values" +
                "('Android', '99996666', '张老师', '80')");
        db.execSQL("insert into " + TABLE_NAME_COURSE +
                " (course_name , course_code, course_teacher, learning_hours) " +
                "values" +
                "('Java', '8888aaaa', '黄老师', '48')");
        db.execSQL("insert into " + TABLE_NAME_COURSE +
                " (course_name , course_code, course_teacher, learning_hours) " +
                "values" +
                "('Web', '6666cccc', '周老师', '64')");
    }

    public void insertStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into " + TABLE_NAME_STUDENT +
                " (student_name , student_code, username, password, class_name) " +
                "values" +
                "('" + student.getStudent_name() + "','" + student.getStudent_code() + "','" + student.getUsername() + "','" + student.getPassword() + "','" + student.getClass_name() + "')");
        db.close();
    }

    //添加学生
    public int insertStudent2(Student student) {
        //获取可写数据库
        SQLiteDatabase db = getWritableDatabase();

        //创建ContentValues以Key-Value的形式封装数据
        ContentValues values = new ContentValues();
        values.put("student_name", student.getStudent_name());
        values.put("student_code", student.getStudent_code());
        values.put("username", student.getUsername());
        values.put("password", student.getPassword());
        values.put("class_name", student.getClass_name());

        //执行insert方法，插入数据
        long newRowId = db.insert(TABLE_NAME_STUDENT, null, values);
        Log.d(TAG, "insertStudent2 newRowId = " + newRowId);

        db.close();
        return (int) newRowId;
    }

    //根据名字删除学生
    public void deleteStudentByName(String name) {
        if (name != null) {
            SQLiteDatabase db = getReadableDatabase();
            int changedRowCount = db.delete(TABLE_NAME_STUDENT, "student_name = ?", new String[]{name});
            Log.d(TAG, "deleteStudentByName changedRowCount = " + changedRowCount);
            db.close();
        }
    }

    //根据id删除学生
    public void deleteStudentById(int id) {
        if (id >= 0) {
            SQLiteDatabase db = getReadableDatabase();
            int changedRowCount = db.delete(TABLE_NAME_STUDENT, "_id = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "deleteStudentById changedRowCount = " + changedRowCount);
            db.close();
        }
    }

    //根据id修改学生名字
    public void updateStudentNameById(String name, int id) {
        if (name != null && id >= 0) {
            SQLiteDatabase db = getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("student_name", name);
            int changedRowCount = db.update(TABLE_NAME_STUDENT, values, "_id = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "updateStudentNameById changedRowCount = " + changedRowCount);
            db.close();
        }
    }

    //查询所有学生
    @SuppressLint("Range")
    public List<Student> selectAllStudent() {
        List<Student> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME_STUDENT,     // 表名
                null,                   // 要查询的列数据
                null,                   // 条件语句
                null,                   // 条件语句的值
                null,                   // group by
                null,                   // having
                null                    // 排序语句
        );
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Student student = new Student();
                student.setStudent_id(cursor.getInt(cursor.getColumnIndex("_id")));
                student.setStudent_name(cursor.getString(cursor.getColumnIndex("student_name")));
                student.setStudent_code(cursor.getString(cursor.getColumnIndex("student_code")));
                student.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                student.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                student.setClass_name(cursor.getString(cursor.getColumnIndex("class_name")));
                list.add(student);
            }
        }
        Log.d(TAG, "selectAllStudent = " + list.size());
        db.close();
        return list;
    }

    //登录
    @SuppressLint("Range")
    public Student login(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME_STUDENT,                                                        // 表名
                null,                                                                      // 要查询的列数据
                "(username = ? or student_name = ? or student_code = ?) and password = ?", // 条件语句
                new String[]{username, username, username, password},                     // 条件语句的值
                null,                                                                      // group by
                null,                                                                      // having
                null                                                                       // 排序语句
        );
        Student student = null;
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                student = new Student();
                student.setStudent_id(cursor.getInt(cursor.getColumnIndex("_id")));
                student.setStudent_name(cursor.getString(cursor.getColumnIndex("student_name")));
                student.setStudent_code(cursor.getString(cursor.getColumnIndex("student_code")));
                student.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                student.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                student.setClass_name(cursor.getString(cursor.getColumnIndex("class_name")));
                Log.d(TAG, "login = " + student);
            }
        }
        db.close();
        return student;
    }

    //根据id查询学生信息
    @SuppressLint("Range")
    public Student getStudentById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME_STUDENT,                                                        // 表名
                null,                                                                      // 要查询的列数据
                "_id = ?",                                                                 // 条件语句
                new String[]{String.valueOf(id)},                                         // 条件语句的值
                null,                                                                      // group by
                null,                                                                      // having
                null                                                                       // 排序语句
        );
        Student student = null;
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                student = new Student();
                student.setStudent_id(cursor.getInt(cursor.getColumnIndex("_id")));
                student.setStudent_name(cursor.getString(cursor.getColumnIndex("student_name")));
                student.setStudent_code(cursor.getString(cursor.getColumnIndex("student_code")));
                student.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                student.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                student.setClass_name(cursor.getString(cursor.getColumnIndex("class_name")));
                Log.d(TAG, "current student = " + student);
            }
        }
        db.close();
        return student;
    }

    //添加笔记
    public boolean insertNote(String userContent, String userTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("content", userContent);
        contentValues.put("time", userTime);
        SQLiteDatabase db = getReadableDatabase();
        boolean pass = db.insert("notepad", null, contentValues) > 0;
        db.close();
        return pass;
    }

    //删除笔记
    public boolean deleteNote(String id) {
        String sql = id + "=?";
        String[] contentValuesArray = new String[]{String.valueOf(id)};
        SQLiteDatabase db = getReadableDatabase();
        boolean pass = db.delete("notepad", sql, contentValuesArray) > 0;
        db.close();
        return pass;
    }

    //修改笔记
    public boolean updateNote(String id, String content, String userYear) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(content, content);
        contentValues.put("time", userYear);
        String sql = id + "=?";
        String[] strings = new String[]{id};
        SQLiteDatabase db = getReadableDatabase();
        boolean pass = db.update("notepad", contentValues, sql, strings) > 0;
        db.close();
        return pass;
    }

    //查询笔记
    public List<NotepadBean> queryNote() {//将遍历的笔记存放在一个List<NotepadBean>类型的合集中
        List<NotepadBean> list = new ArrayList<NotepadBean>();
//        通过query()方法查询笔记库表中的所有笔记，并返回一个Cursor对象
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("notepad", null, null, null,
                null, null, "id" + " desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {//通过while循环遍历Cursor对象中的笔记
                NotepadBean noteInfo = new NotepadBean();
                @SuppressLint("Range") String id = String.valueOf(cursor.getInt
                        (cursor.getColumnIndex("id")));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex
                        ("content"));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex
                        ("time"));
                noteInfo.setId(id);
                noteInfo.setNotepadContent(content);
                noteInfo.setNotepadTime(time);
                list.add(noteInfo);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    public void Close() {
        this.close();
    }
}