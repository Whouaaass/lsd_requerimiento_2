import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'screens/canciones_list_screen.dart';
import 'screens/login_screen.dart';
import 'services/config_service.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env");
  await ConfigService().init();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    // Check if nickname exists to determine initial screen
    final hasNickname = ConfigService().nickname != null;

    return MaterialApp(
      title: 'Spotifake Player',
      theme: ThemeData(primarySwatch: Colors.blue, useMaterial3: true),
      home: hasNickname ? const CancionesListScreen() : const LoginScreen(),
    );
  }
}
