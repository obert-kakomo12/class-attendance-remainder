import 'dart:developer' as developer;
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/class_model.dart';
import '../services/database_service.dart';
import 'add_class_screen.dart';
import 'timetable_screen.dart';
import 'attendance_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  List<ClassModel> _todayClasses = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadTodayClasses();
  }

  Future<void> _loadTodayClasses() async {
    developer.log('Loading classes for today...');
    setState(() => _isLoading = true);
    try {
      final String currentDay = DateFormat('EEEE').format(DateTime.now());
      final allClasses = await DatabaseService.instance.getAllClasses();
      developer.log('Found ${allClasses.length} total classes.');
      
      setState(() {
        _todayClasses = allClasses.where((c) => c.day == currentDay).toList();
        _isLoading = false;
      });
      developer.log('Today has ${_todayClasses.length} classes.');
    } catch (e) {
      developer.log('Failed to load classes: $e', error: e, name: 'HomeScreen');
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('ClassTrack')),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadTodayClasses,
              child: CustomScrollView(
                slivers: [
                  _buildHeader(),
                  _buildUpcomingSection(),
                  _buildTodayList(),
                ],
              ),
            ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: 0,
        onTap: (index) {
          if (index == 1) {
            Navigator.push(context, MaterialPageRoute(builder: (context) => const TimetableScreen()));
          } else if (index == 2) {
            Navigator.push(context, MaterialPageRoute(builder: (context) => const AttendanceScreen()));
          }
        },
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
          BottomNavigationBarItem(icon: Icon(Icons.calendar_today), label: 'Timetable'),
          BottomNavigationBarItem(icon: Icon(Icons.bar_chart), label: 'Attendance'),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          await Navigator.push(context, MaterialPageRoute(builder: (context) => const AddClassScreen()));
          _loadTodayClasses();
        },
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildHeader() {
    return SliverToBoxAdapter(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Hello Student!',
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(fontWeight: FontWeight.bold),
            ),
            Text(
              DateFormat('EEEE, d MMMM').format(DateTime.now()),
              style: Theme.of(context).textTheme.titleMedium?.copyWith(color: Colors.grey[600]),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUpcomingSection() {
    if (_todayClasses.isEmpty) return const SliverToBoxAdapter(child: SizedBox.shrink());

    // Simple logic for next class (can be improved with time comparison)
    final nextClass = _todayClasses.first;

    return SliverToBoxAdapter(
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          gradient: const LinearGradient(colors: [Color(0xFF2E7D32), Color(0xFF66BB6A)]),
          borderRadius: BorderRadius.circular(20),
          boxShadow: [BoxShadow(color: Colors.green.withValues(alpha: 0.3), blurRadius: 10, offset: const Offset(0, 5))],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('NEXT CLASS', style: TextStyle(color: Colors.white70, fontWeight: FontWeight.bold, letterSpacing: 1.2)),
            const SizedBox(height: 8),
            Text(nextClass.name, style: const TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Row(
              children: [
                const Icon(Icons.access_time, color: Colors.white70, size: 16),
                const SizedBox(width: 4),
                Text(nextClass.time, style: const TextStyle(color: Colors.white70)),
                const SizedBox(width: 16),
                const Icon(Icons.location_on, color: Colors.white70, size: 16),
                const SizedBox(width: 4),
                Text(nextClass.venue, style: const TextStyle(color: Colors.white70)),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTodayList() {
    return SliverPadding(
      padding: const EdgeInsets.all(16),
      sliver: _todayClasses.isEmpty
          ? const SliverFillRemaining(
              hasScrollBody: false,
              child: Center(child: Text('No classes today! 🎉')),
            )
          : SliverList(
              delegate: SliverChildBuilderDelegate(
                (context, index) {
                  final c = _todayClasses[index];
                  return Card(
                    child: ListTile(
                      leading: CircleAvatar(
                        backgroundColor: Theme.of(context).colorScheme.primary.withValues(alpha: 0.1),
                        child: Text(c.name[0].toUpperCase(), style: TextStyle(color: Theme.of(context).colorScheme.primary)),
                      ),
                      title: Text(c.name, style: const TextStyle(fontWeight: FontWeight.bold)),
                      subtitle: Text('${c.time} • ${c.venue}'),
                      trailing: IconButton(
                        icon: const Icon(Icons.check_circle_outline),
                        onPressed: () => _showAttendanceDialog(c),
                      ),
                    ),
                  );
                },
                childCount: _todayClasses.length,
              ),
            ),
    );
  }

  void _showAttendanceDialog(ClassModel c) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Mark Attendance for ${c.name}'),
        content: const Text('Did you attend this class?'),
        actions: [
          TextButton(
            onPressed: () async {
              await DatabaseService.instance.markAttendance(c.id!, false);
              if (context.mounted) Navigator.pop(context);
              _loadTodayClasses();
            },
            child: const Text('Absent', style: TextStyle(color: Colors.red)),
          ),
          ElevatedButton(
            onPressed: () async {
              await DatabaseService.instance.markAttendance(c.id!, true);
              if (context.mounted) Navigator.pop(context);
              _loadTodayClasses();
            },
            child: const Text('Present'),
          ),
        ],
      ),
    );
  }
}
