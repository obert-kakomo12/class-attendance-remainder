package com.classtrack.classtrack.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.classtrack.classtrack.database.ClassRepository;
import com.classtrack.classtrack.models.ClassModel;

import java.util.List;

public class ClassViewModel extends AndroidViewModel {
    private ClassRepository repository;
    private LiveData<List<ClassModel>> allClasses;

    public ClassViewModel(@NonNull Application application) {
        super(application);
        repository = new ClassRepository(application);
        allClasses = repository.getAllClasses();
    }

    public void insert(ClassModel classModel, ClassRepository.InsertCallback callback) {
        repository.insert(classModel, callback);
    }

    public void update(ClassModel classModel) {
        repository.update(classModel);
    }

    public void delete(ClassModel classModel) {
        repository.delete(classModel);
    }

    public LiveData<List<ClassModel>> getAllClasses() {
        return allClasses;
    }
}
