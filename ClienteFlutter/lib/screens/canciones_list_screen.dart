import 'package:flutter/material.dart';
import '../services/canciones_api/models/metadato_cancion_dto.dart';
import '../services/canciones_api/canciones_api_client.dart';
import 'player_screen.dart';
import 'settings_screen.dart';

class CancionesListScreen extends StatefulWidget {
  const CancionesListScreen({super.key});

  @override
  State<CancionesListScreen> createState() => _CancionesListScreenState();
}

class _CancionesListScreenState extends State<CancionesListScreen> {
  final CancionesAPIClient _client = CancionesAPIClient();
  late Future<List<MetadatoCancionDTO>> _cancionesFuture;

  @override
  void initState() {
    super.initState();
    _cancionesFuture = _client.listarCanciones();
  }

  // Method to fetch and refresh the song list (same as before)
  Future<void> _refreshCanciones() async {
    setState(() {
      // Reassigning the Future will cause the FutureBuilder to rebuild
      _cancionesFuture = _client.listarCanciones();
    });
    // Await the new future to ensure the RefreshIndicator/UI updates correctly
    await _cancionesFuture;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Lista de Canciones'),
        actions: [
          // 🚀 NEW: Reload Button
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _refreshCanciones, // Calls the refresh method
          ),
          
          // Settings Button (same as before)
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const SettingsScreen()),
              );
            },
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _refreshCanciones,
        child: FutureBuilder<List<MetadatoCancionDTO>>(
          future: _cancionesFuture,
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const Center(child: CircularProgressIndicator());
            } else if (snapshot.hasError) {
              return Center(child: Text('Error: ${snapshot.error}'));
            } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
              return ListView(
                physics: const AlwaysScrollableScrollPhysics(),
                children: const [
                  Center(child: Text('No hay canciones disponibles')),
                ],
              );
            }

            final canciones = snapshot.data!;
            return ListView.builder(
              itemCount: canciones.length,
              itemBuilder: (context, index) {
                final cancion = canciones[index];
                return ListTile(
                  title: Text(cancion.titulo),
                  subtitle: Text('${cancion.artista} - ${cancion.genero}'),
                  trailing: Text(cancion.idioma),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => PlayerScreen(cancion: cancion),
                      ),
                    );
                  },
                );
              },
            );
          },
        ),
      ),
    );
  }
}