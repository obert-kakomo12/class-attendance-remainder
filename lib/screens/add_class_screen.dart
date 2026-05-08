import 'package:flutter/material.dart';
import '../models/class_model.dart';
import '../services/database_service.dart';
import '../services/notification_service.dart';

class AddClassScreen extends StatefulWidget {
  final ClassModel? classToEdit;
  const AddClassScreen({super.key, this.classToEdit});

  @override
  State<AddClassScreen> createState() => _AddClassScreenState();
}

class _AddClassScreenState extends State<AddClassScreen> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _nameController;
  late TextEditingController _venueController;
  String _selectedDay = 'Monday';
  TimeOfDay _selectedTime = const TimeOfDay(hour: 9, minute: 0);

  final List<String> _days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController(text: widget.classToEdit?.name ?? '');
    _venueController = TextEditingController(text: widget.classToEdit?.venue ?? '');
    if (widget.classToEdit != null) {
      _selectedDay = widget.classToEdit!.day;
      final parts = widget.classToEdit!.time.split(':');
      _selectedTime = TimeOfDay(hour: int.parse(parts[0]), minute: int.parse(parts[1]));
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _venueController.dispose();
    super.dispose();
  }

  Future<void> _selectTime(BuildContext context) async {
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: _selectedTime,
    );
    if (picked != null && picked != _selectedTime) {
      setState(() => _selectedTime = picked);
    }
  }

  void _saveClass() async {
    if (_formKey.currentState!.validate()) {
      final String timeString = '${_selectedTime.hour}:${_selectedTime.minute.toString().padLeft(2, '0')}';
      
      final classModel = ClassModel(
        id: widget.classToEdit?.id,
        name: _nameController.text,
        day: _selectedDay,
        time: timeString,
        venue: _venueController.text,
        totalClasses: widget.classToEdit?.totalClasses ?? 0,
        attendedClasses: widget.classToEdit?.attendedClasses ?? 0,
      );

      int resultId;
      if (widget.classToEdit == null) {
        resultId = await DatabaseService.instance.addClass(classModel);
      } else {
        await DatabaseService.instance.updateClass(classModel);
        resultId = classModel.id!;
      }

      // Schedule notification
      final now = DateTime.now();
      // Calculate next occurrence of this day and time
      // This is a simplified version for MVP
      final scheduledDateTime = DateTime(now.year, now.month, now.day, _selectedTime.hour, _selectedTime.minute);
      
      await NotificationService().scheduleClassReminder(
        resultId, 
        _nameController.text, 
        _venueController.text, 
        scheduledDateTime
      );

      if (mounted) Navigator.pop(context);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(widget.classToEdit == null ? 'Add Class' : 'Edit Class')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(labelText: 'Course Name', border: OutlineInputBorder()),
                validator: (value) => value == null || value.isEmpty ? 'Please enter course name' : null,
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<String>(
                value: _selectedDay,
                decoration: const InputDecoration(labelText: 'Day', border: OutlineInputBorder()),
                items: _days.map((day) => DropdownMenuItem(value: day, child: Text(day))).toList(),
                onChanged: (value) => setState(() => _selectedDay = value!),
              ),
              const SizedBox(height: 16),
              ListTile(
                title: const Text('Class Time'),
                subtitle: Text(_selectedTime.format(context)),
                trailing: const Icon(Icons.access_time),
                shape: RoundedRectangleBorder(side: const BorderSide(color: Colors.grey), borderRadius: BorderRadius.circular(4)),
                onTap: () => _selectTime(context),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _venueController,
                decoration: const InputDecoration(labelText: 'Venue', border: OutlineInputBorder()),
                validator: (value) => value == null || value.isEmpty ? 'Please enter venue' : null,
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _saveClass,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Theme.of(context).primaryColor,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: Text(widget.classToEdit == null ? 'CREATE CLASS' : 'UPDATE CLASS'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
