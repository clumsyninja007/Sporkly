package com.example.aharm.sporkly;

import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by David on 11/16/2016.
 */

class ListStorage {
    private ArrayList<String> items = new ArrayList<>();
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

    boolean add(String item) {
        boolean success = items.add(item);

        if (success) {
            save();
        }

        return success;
    }

    String remove(int index) {
        String removedString = items.remove(index);

        save();

        return removedString;
    }

    boolean save() {
        Log.i("Saving", fileName);

        try{
            PrintWriter pw = new PrintWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));

            for(String toDo : items){
                Log.i("Saving", toDo);
                pw.println(toDo);
            }
            pw.close();

            return true;
        } catch (Exception e) {
            Log.i("Error:", e.getMessage());
        }

        return false;
    }

    boolean load() {
        Log.i("Loading", fileName);

        items.clear();

        try{
            Scanner scanner = new Scanner(context.openFileInput(fileName));

            while (scanner.hasNextLine()) {
                String toDo = scanner.nextLine();
                Log.i("Loading", toDo);

                items.add(toDo);
            }
            scanner.close();

            return true;
        } catch (Exception e) {
            Log.i("loadPantry", e.getMessage());
        }

        return false;
    }
}
