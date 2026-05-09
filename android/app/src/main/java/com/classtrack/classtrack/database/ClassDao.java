package com.classtrack.classtrack.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.classtrack.classtrack.models.ClassModel;

import java.util.List;

@Dao
public interface ClassDao {
    @Insert
    long insert(ClassModel classModel);

    @Update
    void update(ClassModel classModel);

    @Delete
    void delete(ClassModel classModel);

    @Query("SELECT * FROM classes ORDER BY id DESC")
    LiveData<List<ClassModel>> getAllClasses();

    @Query("SELECT * FROM classes WHERE id = :id")
    ClassModel getClassById(int id);
}
