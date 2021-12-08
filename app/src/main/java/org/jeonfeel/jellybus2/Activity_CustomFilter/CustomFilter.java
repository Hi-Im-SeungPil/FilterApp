package org.jeonfeel.jellybus2.Activity_CustomFilter;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CustomFilter {

    @PrimaryKey(autoGenerate = true)
    public long key;
    @NonNull
    public String name;
    @NonNull
    public String matrix;

    public CustomFilter(Long key, @NonNull String name, @NonNull String matrix) {
        this.key = key;
        this.name = name;
        this.matrix = matrix;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }
}
