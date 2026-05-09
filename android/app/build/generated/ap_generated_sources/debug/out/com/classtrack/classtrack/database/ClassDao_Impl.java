package com.classtrack.classtrack.database;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.classtrack.classtrack.models.ClassModel;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ClassDao_Impl implements ClassDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ClassModel> __insertionAdapterOfClassModel;

  private final EntityDeletionOrUpdateAdapter<ClassModel> __deletionAdapterOfClassModel;

  private final EntityDeletionOrUpdateAdapter<ClassModel> __updateAdapterOfClassModel;

  public ClassDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfClassModel = new EntityInsertionAdapter<ClassModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `classes` (`id`,`name`,`day`,`time`,`venue`,`totalClasses`,`attendedClasses`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ClassModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getDay() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDay());
        }
        if (value.getTime() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTime());
        }
        if (value.getVenue() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getVenue());
        }
        stmt.bindLong(6, value.getTotalClasses());
        stmt.bindLong(7, value.getAttendedClasses());
      }
    };
    this.__deletionAdapterOfClassModel = new EntityDeletionOrUpdateAdapter<ClassModel>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `classes` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ClassModel value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfClassModel = new EntityDeletionOrUpdateAdapter<ClassModel>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `classes` SET `id` = ?,`name` = ?,`day` = ?,`time` = ?,`venue` = ?,`totalClasses` = ?,`attendedClasses` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ClassModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getDay() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDay());
        }
        if (value.getTime() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTime());
        }
        if (value.getVenue() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getVenue());
        }
        stmt.bindLong(6, value.getTotalClasses());
        stmt.bindLong(7, value.getAttendedClasses());
        stmt.bindLong(8, value.getId());
      }
    };
  }

  @Override
  public long insert(final ClassModel classModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfClassModel.insertAndReturnId(classModel);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final ClassModel classModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfClassModel.handle(classModel);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final ClassModel classModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfClassModel.handle(classModel);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<ClassModel>> getAllClasses() {
    final String _sql = "SELECT * FROM classes ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"classes"}, false, new Callable<List<ClassModel>>() {
      @Override
      public List<ClassModel> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfVenue = CursorUtil.getColumnIndexOrThrow(_cursor, "venue");
          final int _cursorIndexOfTotalClasses = CursorUtil.getColumnIndexOrThrow(_cursor, "totalClasses");
          final int _cursorIndexOfAttendedClasses = CursorUtil.getColumnIndexOrThrow(_cursor, "attendedClasses");
          final List<ClassModel> _result = new ArrayList<ClassModel>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ClassModel _item;
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpDay;
            if (_cursor.isNull(_cursorIndexOfDay)) {
              _tmpDay = null;
            } else {
              _tmpDay = _cursor.getString(_cursorIndexOfDay);
            }
            final String _tmpTime;
            if (_cursor.isNull(_cursorIndexOfTime)) {
              _tmpTime = null;
            } else {
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
            }
            final String _tmpVenue;
            if (_cursor.isNull(_cursorIndexOfVenue)) {
              _tmpVenue = null;
            } else {
              _tmpVenue = _cursor.getString(_cursorIndexOfVenue);
            }
            final int _tmpTotalClasses;
            _tmpTotalClasses = _cursor.getInt(_cursorIndexOfTotalClasses);
            final int _tmpAttendedClasses;
            _tmpAttendedClasses = _cursor.getInt(_cursorIndexOfAttendedClasses);
            _item = new ClassModel(_tmpName,_tmpDay,_tmpTime,_tmpVenue,_tmpTotalClasses,_tmpAttendedClasses);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public ClassModel getClassById(final int id) {
    final String _sql = "SELECT * FROM classes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfDay = CursorUtil.getColumnIndexOrThrow(_cursor, "day");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final int _cursorIndexOfVenue = CursorUtil.getColumnIndexOrThrow(_cursor, "venue");
      final int _cursorIndexOfTotalClasses = CursorUtil.getColumnIndexOrThrow(_cursor, "totalClasses");
      final int _cursorIndexOfAttendedClasses = CursorUtil.getColumnIndexOrThrow(_cursor, "attendedClasses");
      final ClassModel _result;
      if(_cursor.moveToFirst()) {
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final String _tmpDay;
        if (_cursor.isNull(_cursorIndexOfDay)) {
          _tmpDay = null;
        } else {
          _tmpDay = _cursor.getString(_cursorIndexOfDay);
        }
        final String _tmpTime;
        if (_cursor.isNull(_cursorIndexOfTime)) {
          _tmpTime = null;
        } else {
          _tmpTime = _cursor.getString(_cursorIndexOfTime);
        }
        final String _tmpVenue;
        if (_cursor.isNull(_cursorIndexOfVenue)) {
          _tmpVenue = null;
        } else {
          _tmpVenue = _cursor.getString(_cursorIndexOfVenue);
        }
        final int _tmpTotalClasses;
        _tmpTotalClasses = _cursor.getInt(_cursorIndexOfTotalClasses);
        final int _tmpAttendedClasses;
        _tmpAttendedClasses = _cursor.getInt(_cursorIndexOfAttendedClasses);
        _result = new ClassModel(_tmpName,_tmpDay,_tmpTime,_tmpVenue,_tmpTotalClasses,_tmpAttendedClasses);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
