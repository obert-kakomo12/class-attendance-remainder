import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import '../models/class_model.dart';

class DatabaseService {
  static final DatabaseService instance = DatabaseService._init();
  static Database? _database;

  DatabaseService._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB('classtrack.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(
      path,
      version: 1,
      onCreate: _createDB,
    );
  }

  Future _createDB(Database db, int version) async {
    await db.execute('''
      CREATE TABLE classes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        day TEXT NOT NULL,
        time TEXT NOT NULL,
        venue TEXT NOT NULL,
        totalClasses INTEGER NOT NULL,
        attendedClasses INTEGER NOT NULL
      )
    ''');
  }

  Future<int> addClass(ClassModel classModel) async {
    final db = await instance.database;
    return await db.insert('classes', classModel.toMap());
  }

  Future<List<ClassModel>> getAllClasses() async {
    final db = await instance.database;
    final result = await db.query('classes');
    return result.map((json) => ClassModel.fromMap(json)).toList();
  }

  Future<int> updateClass(ClassModel classModel) async {
    final db = await instance.database;
    return await db.update(
      'classes',
      classModel.toMap(),
      where: 'id = ?',
      whereArgs: [classModel.id],
    );
  }

  Future<int> deleteClass(int id) async {
    final db = await instance.database;
    return await db.delete(
      'classes',
      where: 'id = ?',
      whereArgs: [id],
    );
  }

  Future<void> markAttendance(int id, bool isPresent) async {
    final db = await instance.database;
    final List<Map<String, dynamic>> maps = await db.query(
      'classes',
      where: 'id = ?',
      whereArgs: [id],
    );

    if (maps.isNotEmpty) {
      ClassModel classModel = ClassModel.fromMap(maps.first);
      int newTotal = classModel.totalClasses + 1;
      int newAttended = isPresent ? classModel.attendedClasses + 1 : classModel.attendedClasses;

      await db.update(
        'classes',
        {
          'totalClasses': newTotal,
          'attendedClasses': newAttended,
        },
        where: 'id = ?',
        whereArgs: [id],
      );
    }
  }

  Future<void> close() async {
    final db = await instance.database;
    db.close();
  }
}
