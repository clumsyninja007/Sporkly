package com.example.aharm.sporkly;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is used to save/load data from a file, to be displayed
 * in a ListView. It stores data using json, every entry into the file
 * is loaded as a JSONObject.
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

    /*
     * Sets the file that will be saved or loaded from.
     */
    void setFile (String fileName) {
        this.fileName = fileName;
    }

    /*
     * Gets the Array for the display text items, this is what will
     * be used when creating an array adapter, so that the ListView
     * using that adapter can show the strings within the items list.
     */
    ArrayList<String> getItems () {
        return items;
    }

    /*
     * Gets a JSONObject stored within this list storage. Each entry into
     * this list storage is stored as a JSONObject.
     */
    JSONObject getData (int index) {
        return data.get(index);
    }

    /*
     * Updates the items list array with the "test" property of each JSONObject,
     * so they can be displayed in a ListView. Example of what would be stored in this
     * property is a recipe name.
     */
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

    /*
     * Adds an entry to this list storage, creating the JSONObject internally.
     * @param text the text that will be displayed in the ListView.
     */
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

    /*
     * Adds an entry to this list storage, using a pre create JSONObject
     * @param objAdd the JSONObject to add to the list storage.
     */
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

    /*
     * Adds an entry to this list storage, allowing you to specify display text
     * and also a JSONObject that can contain more data. Exmaple would be the favorites
     * storage, which stores the recipe name as the text, and the JSONObject contains
     * the id, so it can be sent to the API.
     * @param text the text
     * @param objAdd the JSONObject to add additional properties.
     */
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

    /*
     * Looks to see if an integer value with a specific name exists
     * in any of the stored JSONObjects, such as an id, to see if we already
     * have that recipe id stored.
     * @param name the name of the property.
     * @param value the value that will be looked for.
     * @return the index of the property if found, it will return -1 if not found.
     */
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

    /*
     * Removes an entry from the list storage.
     * @param index the index to remove.
     */
    JSONObject remove(int index) {
        if (index < data.size()) {
            JSONObject obj = data.remove(index);

            refresh();
            save();

            return obj;
        } else {
            return null;
        }
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
