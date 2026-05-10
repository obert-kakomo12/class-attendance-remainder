package com.classtrack.classtrack.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.classtrack.classtrack.models.ClassModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassRepository {
    private ClassDao classDao;
    private LiveData<List<ClassModel>> allClasses;
    private ExecutorService executorService;
    private android.app.Application application;

    public ClassRepository(android.app.Application application) {
        this.application = application;
        AppDatabase database = AppDatabase.getInstance(application);
        classDao = database.classDao();
        allClasses = classDao.getAllClasses();
        executorService = Executors.newFixedThreadPool(2);
    }

    public interface InsertCallback {
        void onInserted(long id);
    }

    public void insert(ClassModel classModel, InsertCallback callback) {
        executorService.execute(() -> {
            long id = classDao.insert(classModel);
            if (callback != null) callback.onInserted(id);
        });
    }

    public void update(ClassModel classModel) {
        executorService.execute(() -> classDao.update(classModel));
    }

    public void delete(ClassModel classModel) {
        executorService.execute(() -> {
            com.classtrack.classtrack.receivers.NotificationScheduler.cancelReminder(application, classModel.getId());
            classDao.delete(classModel);
        });
    }

    public LiveData<List<ClassModel>> getAllClasses() {
        return allClasses;
    }
}
