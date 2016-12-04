package com.example.aharm.sporkly;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by David on 11/16/2016.
 */

class ListStorage {
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<JSONObject> data = new ArrayList<>();
    private Context context;
    private String fileName;

    ListStorage (Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    void setFile (String fileName) {
        this.fileName = fileName;
    }

    ArrayList<String> getItems () {
        return items;
    }

    JSONObject getData (int index) {
        return data.get(index);
    }

    void refresh() {
        items.clear();
        for (JSONObject obj : data) {
            try{
                items.add(obj.getString("text"));
            } catch (Exception e) {
                Log.e("refresh", e.getMessage());
            }
        }
    }

    boolean add(String text) {
        try{
            JSONObject obj = new JSONObject();

            obj.put("text", text);

            boolean success = data.add(obj);

            if (success) {
                save();
                refresh();
                return true;

            }
        } catch (Exception e) {
            Log.e("add", e.getMessage());
        }

        return false;
    }

    boolean add(JSONObject objAdd) {
        try{
            JSONObject obj = new JSONObject(objAdd.toString());

            boolean success = data.add(obj);

            if (success) {
                save();
                refresh();
                return true;

            }
        } catch (Exception e) {
            Log.e("add", e.getMessage());
        }

        return false;
    }

    boolean add(String text, JSONObject objAdd) {
        try{
            JSONObject obj = new JSONObject(objAdd.toString());

            obj.put("text", text);

            boolean success = data.add(obj);

            if (success) {
                save();
                refresh();
                return true;

            }
        } catch (Exception e) {
            Log.e("add", e.getMessage());
        }

        return false;
    }

    int findInt(String name, int value) {
        for (int i = 0; i < data.size(); i++) {
            try{
                JSONObject obj = data.get(i);
                if (obj.getInt(name) == value) {
                    return i;
                }
            } catch (Exception e) {
                Log.e("refresh", e.getMessage());
            }
        }

        return -1;
    }

    JSONObject remove(int index) {
        JSONObject obj = data.remove(index);

        refresh();
        save();

        return obj;
    }

    boolean save() {
        Log.i("Saving", fileName);

        try{
            PrintWriter pw = new PrintWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));

            for(JSONObject obj : data){
                String str = obj.toString();
                Log.i("Saving", str);
                if (obj.has("text")) {
                    pw.println(str);
                }
            }
            pw.close();

            return true;
        } catch (Exception e) {
            Log.e("save", e.getMessage());
        }

        return false;
    }

    boolean load() {
        Log.i("Loading", fileName);

        items.clear();

        try{
            Scanner scanner = new Scanner(context.openFileInput(fileName));

            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();

                JSONObject obj = new JSONObject(str);

                Log.i("Loading", str);

                if (obj.has("text")) {
                    data.add(obj);
                }
            }
            scanner.close();

            refresh();

            return true;
        } catch (Exception e) {
            Log.e("load", e.getMessage());
        }

        clear();

        return false;
    }

    void clear() {
        data.clear();
        save();
    }
}
