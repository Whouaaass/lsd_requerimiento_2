import 'package:flutter/material.dart';
import '../services/config_service.dart';
import 'login_screen.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final _formKey = GlobalKey<FormState>();

  late TextEditingController _apiBaseUrlController;
  late TextEditingController _streamingHostController;
  late TextEditingController _streamingPortController;
  late TextEditingController _streamingHostWebController;
  late TextEditingController _streamingPortWebController;
  late TextEditingController _stompUrlController;

  bool _isDirty = false;

  @override
  void initState() {
    super.initState();
    final config = ConfigService();

    _apiBaseUrlController = TextEditingController(text: config.apiBaseUrl);
    _streamingHostController = TextEditingController(
      text: config.streamingHost,
    );
    _streamingPortController = TextEditingController(
      text: config.streamingPort.toString(),
    );
    _streamingHostWebController = TextEditingController(
      text: config.streamingHostWeb,
    );
    _streamingPortWebController = TextEditingController(
      text: config.streamingPortWeb.toString(),
    );
    _stompUrlController = TextEditingController(text: config.stompUrl);
  }

  @override
  void dispose() {
    _apiBaseUrlController.dispose();
    _streamingHostController.dispose();
    _streamingPortController.dispose();
    _streamingHostWebController.dispose();
    _streamingPortWebController.dispose();
    _stompUrlController.dispose();
    super.dispose();
  }

  Future<void> _saveSettings() async {
    if (!_formKey.currentState!.validate()) return;

    final config = ConfigService();
    await config.setApiBaseUrl(_apiBaseUrlController.text);
    await config.setStreamingHost(_streamingHostController.text);
    await config.setStreamingPort(int.parse(_streamingPortController.text));
    await config.setStreamingHostWeb(_streamingHostWebController.text);
    await config.setStreamingPortWeb(
      int.parse(_streamingPortWebController.text),
    );
    await config.setStompUrl(_stompUrlController.text);

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Configuración guardada exitosamente')),
      );
      setState(() {
        _isDirty = false;
      });
    }
  }

  Future<void> _resetToDefaults() async {
    await ConfigService().resetToDefaults();
    final config = ConfigService();

    setState(() {
      _apiBaseUrlController.text = config.apiBaseUrl;
      _streamingHostController.text = config.streamingHost;
      _streamingPortController.text = config.streamingPort.toString();
      _streamingHostWebController.text = config.streamingHostWeb;
      _streamingPortWebController.text = config.streamingPortWeb.toString();
      _stompUrlController.text = config.stompUrl;
      _isDirty = false;
    });

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Restaurado a valores por defecto')),
      );
    }
  }

  Future<void> _logout() async {
    await ConfigService().clearNickname();
    if (mounted) {
      Navigator.of(context).pushAndRemoveUntil(
        MaterialPageRoute(builder: (context) => const LoginScreen()),
        (route) => false,
      );
    }
  }

  void _onChanged(String value) {
    setState(() {
      _isDirty = true;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Configuración'),
        actions: [
          if (_isDirty)
            IconButton(icon: const Icon(Icons.save), onPressed: _saveSettings),
        ],
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            const Text(
              'Configuración de API',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),

            // Canciones API
            TextFormField(
              controller: _apiBaseUrlController,
              decoration: const InputDecoration(
                labelText: 'Canciones API URL',
                hintText: 'http://localhost:8080',
                border: OutlineInputBorder(),
              ),
              onChanged: _onChanged,
              validator: (value) {
                if (value == null || value.isEmpty) return 'Required';
                return null;
              },
            ),
            const SizedBox(height: 16),

            // Reacciones API (STOMP)
            TextFormField(
              controller: _stompUrlController,
              decoration: const InputDecoration(
                labelText: 'Reacciones API WebSocket URL',
                hintText: 'ws://localhost:5000/ws-raw',
                border: OutlineInputBorder(),
              ),
              onChanged: _onChanged,
              validator: (value) {
                if (value == null || value.isEmpty) return 'Required';
                return null;
              },
            ),
            const SizedBox(height: 24),

            const Text(
              'Configuración de Streaming (Nativo)',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),

            Row(
              children: [
                Expanded(
                  flex: 2,
                  child: TextFormField(
                    controller: _streamingHostController,
                    decoration: const InputDecoration(
                      labelText: 'Host',
                      hintText: 'localhost',
                      border: OutlineInputBorder(),
                    ),
                    onChanged: _onChanged,
                    validator: (value) {
                      if (value == null || value.isEmpty) return 'Requerido';
                      return null;
                    },
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: TextFormField(
                    controller: _streamingPortController,
                    decoration: const InputDecoration(
                      labelText: 'Puerto',
                      hintText: '50051',
                      border: OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.number,
                    onChanged: _onChanged,
                    validator: (value) {
                      if (value == null || value.isEmpty) return 'Requerido';
                      if (int.tryParse(value) == null) return 'Número inválido';
                      return null;
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),

            const Text(
              'Streaming Configuration (Web)',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),

            Row(
              children: [
                Expanded(
                  flex: 2,
                  child: TextFormField(
                    controller: _streamingHostWebController,
                    decoration: const InputDecoration(
                      labelText: 'Host',
                      hintText: 'localhost',
                      border: OutlineInputBorder(),
                    ),
                    onChanged: _onChanged,
                    validator: (value) {
                      if (value == null || value.isEmpty) return 'Requerido';
                      return null;
                    },
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: TextFormField(
                    controller: _streamingPortWebController,
                    decoration: const InputDecoration(
                      labelText: 'Puerto',
                      hintText: '8080',
                      border: OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.number,
                    onChanged: _onChanged,
                    validator: (value) {
                      if (value == null || value.isEmpty) return 'Requerido';
                      if (int.tryParse(value) == null) return 'Número inválido';
                      return null;
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: 32),

            OutlinedButton.icon(
              onPressed: _resetToDefaults,
              icon: const Icon(Icons.restore),
              label: const Text('Restaurar valores por defecto'),
              style: OutlinedButton.styleFrom(foregroundColor: Colors.red),
            ),
            if (ConfigService().nickname != null) ...[
              const SizedBox(height: 16),
              ElevatedButton.icon(
                onPressed: _logout,
                icon: const Icon(Icons.logout),
                label: const Text('Cerrar sesión'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red,
                  foregroundColor: Colors.white,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}
