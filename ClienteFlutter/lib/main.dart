import 'package:flutter/material.dart';
// 1. Importa tu nuevo widget
import 'grpc_player_widget.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'gRPC Audio Player', // Título actualizado
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      // 2. Establece GrpcPlayerWidget como la pantalla de inicio
      home: const GrpcPlayerWidget(),
    );
  }
}

// 3. El widget MyHomePage y su estado han sido eliminados.