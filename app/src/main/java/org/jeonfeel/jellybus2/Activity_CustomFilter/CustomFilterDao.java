package org.jeonfeel.jellybus2.Activity_CustomFilter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomFilterDao {

    @Query("SELECT * FROM CustomFilter")
    List<CustomFilter> getAll();

    @Query("INSERT INTO CustomFilter values(:key,:name,:matrix)")
    void insert(Long key, String name, String matrix);

    @Query("DELETE FROM CustomFilter WHERE `key` =  :key")
    void delete(long key);


}
