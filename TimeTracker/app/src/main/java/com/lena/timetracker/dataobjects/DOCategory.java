package com.lena.timetracker.dataobjects;

public class DOCategory {
    private long id;
    private String name;

    public DOCategory() {
    }

    public DOCategory(long id, String name) {
        this.id = id;
        this.name = name;
    }

    // For arrayAdapter for spinner
    @Override
    public String toString() {
        return name;            // What to display in the Spinner list.
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}