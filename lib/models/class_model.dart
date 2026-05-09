class ClassModel {
  final int? id;
  final String name;
  final String day;
  final String time;
  final String venue;
  final int totalClasses;
  final int attendedClasses;

  ClassModel({
    this.id,
    required this.name,
    required this.day,
    required this.time,
    required this.venue,
    this.totalClasses = 0,
    this.attendedClasses = 0,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'name': name,
      'day': day,
      'time': time,
      'venue': venue,
      'totalClasses': totalClasses,
      'attendedClasses': attendedClasses,
    };
  }

  factory ClassModel.fromMap(Map<String, dynamic> map) {
    return ClassModel(
      id: map['id'],
      name: map['name'],
      day: map['day'],
      time: map['time'],
      venue: map['venue'],
      totalClasses: map['totalClasses'],
      attendedClasses: map['attendedClasses'],
    );
  }

  double get attendancePercentage {
    if (totalClasses == 0) return 0.0;
    return (attendedClasses / totalClasses) * 100;
  }
}
