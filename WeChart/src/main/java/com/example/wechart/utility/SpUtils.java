package com.example.wechart.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class SpUtils {

    private static final String SP_NAME = "sp_student_manager";
    public static final String LOGIN_KEY = "login_id_key";
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public SpUtils(Context context) {
        this(context, SP_NAME, Context.MODE_PRIVATE);
    }

    public SpUtils(Context context, String name, int mode){
        mContext = context;
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(name, mode);
        }
    }

    /**
     * 存入字符串
     *
     * @param key     字符串的键
     * @param value   字符串的值
     */
    public  void putString(String key, String value) {
        //存入数据
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取字符串
     *
     * @param key     字符串的键
     * @return 得到的字符串
     */
    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    /**
     * 获取字符串
     *
     * @param key     字符串的键
     * @param value   字符串的默认值
     * @return 得到的字符串
     */
    public String getString(String key, String value) {
        return mSharedPreferences.getString(key, value);
    }

    /**
     * 保存布尔值
     *
     * @param key     键
     * @param value   值
     */
    public  void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 获取布尔值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 返回保存的值
     */
    public  boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 保存long值
     *
     * @param key     键
     * @param value   值
     */
    public  void putLong(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 获取long值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    public  long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    /**
     * 保存int值
     *
     * @param key     键
     * @param value   值
     */
    public  void putInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 获取long值
     *
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    public  int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    /**
     * 保存对象
     *
     * @param key     键
     * @param obj     要保存的对象（Serializable的子类）
     * @param <T>     泛型定义
     */
    public  <T extends Serializable> void putObject(String key, T obj) {
        try {
            put(key, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象
     *
     * @param key     键
     * @param <T>     指定泛型
     * @return 泛型对象
     */
    public  <T extends Serializable> T getObject(String key) {
        try {
            return (T) get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储List集合
     * @param key 存储的键
     * @param list 存储的集合
     */
    public  void putList(String key, List<? extends Serializable> list) {
        try {
            put(key, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取List集合
     * @param key 键
     * @param <E> 指定泛型
     * @return List集合
     */
    public  <E extends Serializable> List<E> getList(String key) {
        try {
            return (List<E>) get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**存储对象*/
    private  void put(String key, Object obj)
            throws IOException
    {
        if (obj == null) {//判断对象是否为空
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos  = null;
        oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        String objectStr = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        baos.close();
        oos.close();

        putString(key, objectStr);
    }

    /**获取对象*/
    private Object get(String key)
            throws IOException, ClassNotFoundException
    {
        String wordBase64 = getString(key);
        // 将base64格式字符串还原成byte数组
        if (TextUtils.isEmpty(wordBase64)) { //不可少，否则在下面会报java.io.StreamCorruptedException
            return null;
        }
        byte[]               objBytes = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais     = new ByteArrayInputStream(objBytes);
        ObjectInputStream ois      = new ObjectInputStream(bais);
        // 将byte数组转换成product对象
        Object obj = ois.readObject();
        bais.close();
        ois.close();
        return obj;
    }

    /**
     * 保存String set集合
     *
     * @param key     键
     * @param value   值
     */
    public void putStringSet(String key, Set<String> value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    /**
     * 获取String set集合
     *
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    public Set<String> getStringSet(String key, Set<String> defValue) {
        return mSharedPreferences.getStringSet(key, defValue);
    }

    /**
     * 清除指定数据
     *
     * @param key      键
     */
    public void remove(String key){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 清除数据
     */
    public void clear(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
