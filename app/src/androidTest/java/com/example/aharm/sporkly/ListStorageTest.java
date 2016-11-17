package com.example.aharm.sporkly;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by David on 11/16/2016.
 */

@RunWith(AndroidJUnit4.class)
public class ListStorageTest {
    private ListStorage listStorage;
    private String fileName = "test_list_storage.txt";

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        listStorage = new ListStorage(appContext, fileName);
        listStorage.add("TestItem");
        listStorage.save();
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.aharm.sporkly", appContext.getPackageName());
    }

    @Test
    public void getItems() throws Exception {
        assertNotNull("listStorage should not be null", listStorage.getItems());
    }

    @Test
    public void add() throws Exception {
        assertTrue("Add should return true", listStorage.add("NewTestItem"));

        ArrayList<String> items = listStorage.getItems();

        assertEquals("First element of listStorage is NewTestItem", "NewTestItem", items.get(1));
        assertEquals("Size should be 2", 2, items.size());
    }

    @Test
    public void remove() throws Exception {
        ArrayList<String> items = listStorage.getItems();

        assertEquals("Remove should return TestItem", "TestItem", listStorage.remove(0));
        assertEquals("Size should be 0", 0, items.size());
    }

    @Test
    public void save() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertTrue("Add should return true", listStorage.add("NewTestItem"));
        assertTrue("Save should return true", listStorage.save());

        try{
            Scanner scanner = new Scanner(appContext.openFileInput(fileName));
            assertEquals("String should be TestItem", "TestItem", scanner.nextLine());
            assertEquals("String should be NewTestItem", "NewTestItem", scanner.nextLine());
            scanner.close();
        } catch (Exception e) {
            assertTrue("Save Test Failed: Exception: " + e.toString(), false);
        }
    }

    @Test
    public void load() throws Exception {
        ArrayList<String> items = listStorage.getItems();

        items.clear();
        assertEquals("Size should be 0", 0, items.size());

        listStorage.load();
        assertEquals("Size should be 1", 1, items.size());
        assertEquals("First element of listStorage is TestItem", "TestItem", items.get(0));
    }
}
