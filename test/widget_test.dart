import 'package:flutter_test/flutter_test.dart';
import 'package:classtrack/main.dart';

void main() {
  testWidgets('ClassTrack smoke test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(const ClassTrackApp());
    
    // Note: In a real test we would mock the database to avoid hangs
    // For now we just check if it renders the app bar at least
    expect(find.text('ClassTrack'), findsOneWidget);
  });
}
