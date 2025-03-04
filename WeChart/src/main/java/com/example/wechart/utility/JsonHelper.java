package com.example.wechart.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class JsonHelper {

    private void UseJSONObject() {
        try {
            String jsonString = "{\"name\":\"John Doe\",\"age\":30,\"isStudent\":false}";
            JSONObject jsonObject = new JSONObject(jsonString);

            String name = jsonObject.getString("name");
            int age = jsonObject.getInt("age");
            boolean isStudent = jsonObject.getBoolean("isStudent");

            Log.d("Example", "Name: " + name + ", Age: " + age + ", Is Student: " + isStudent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void UseJSONArray() {
        try {
            String jsonString = "[{\"name\":\"John Doe\",\"age\":30},{\"name\":\"Jane Doe\",\"age\":25}]";
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("name");
                int age = jsonObject.getInt("age");

                Log.d("Example", "Name: " + name + ", Age: " + age);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void UseGson() {
        Gson gson = new Gson();
        String json = "{\"name\":\"John Doe\",\"age\":30,\"isStudent\":false}";
        Person person = gson.fromJson(json, Person.class);

        // 现在你可以使用person对象了
        Log.d("PersonInfo", "Name: " + person.getName() + ", Age: " + person.getAge() + ", Is Student: " + person.isStudent());
    }

    private void UseGsonAarry() {
        Gson gson = new Gson();
        String jsonArray = "[{\"name\":\"John Doe\",\"age\":30},{\"name\":\"Jane Doe\",\"age\":25}]";
        Type personType = new TypeToken<List<Person>>() {
        }.getType();
        List<Person> persons = gson.fromJson(jsonArray, personType);

        // 现在你可以遍历persons列表了
        for (Person person : persons) {
            Log.d("PersonInfo", "Name: " + person.getName() + ", Age: " + person.getAge());
        }
    }

    class Person {
        public String name;
        @SerializedName("age")
        public int age;
        public boolean isStudent;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public boolean isStudent() {
            return isStudent;
        }
    }
}
