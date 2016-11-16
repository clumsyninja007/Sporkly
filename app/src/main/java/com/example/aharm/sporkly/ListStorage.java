package com.example.aharm.sporkly;

import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by David on 11/16/2016.
 */

public class ListStorage {
    private ArrayList<String> items = new ArrayList<String>();
    private Context context;
    private String fileName;

    public ListStorage (Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public ArrayList<String> getItems () {
        return items;
    }

    public void add(String item) {
        items.add(item);

        save();
    }

    public void remove(int index) {
        items.remove(index);

        save();
    }

    public void save() {
        Log.i("Saving", fileName);

        try{
            PrintWriter pw = new PrintWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));

            for(String toDo : items){
                Log.i("Saving", toDo);
                pw.println(toDo);
            }
            pw.close();
        } catch (Exception e) {
            Log.i("Error:", e.getMessage());
        }
    }

    public void load() {
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
        } catch (Exception e) {
            Log.i("loadPantry", e.getMessage());
        }
    }
}
