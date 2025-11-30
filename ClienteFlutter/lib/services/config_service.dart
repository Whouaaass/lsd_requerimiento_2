import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Service to manage application configuration and user preferences
class ConfigService {
  static final ConfigService _instance = ConfigService._internal();
  late SharedPreferences _prefs;

  factory ConfigService() {
    return _instance;
  }

  ConfigService._internal();

  Future<void> init() async {
    _prefs = await SharedPreferences.getInstance();
  }

  // Keys
  static const String _keyNickname = 'nickname';
  static const String _keyApiBaseUrl = 'api_base_url';
  static const String _keyStreamingHost = 'streaming_host';
  static const String _keyStreamingPort = 'streaming_port';
  static const String _keyStreamingHostWeb = 'streaming_host_web';
  static const String _keyStreamingPortWeb = 'streaming_port_web';
  static const String _keyStompUrl = 'stomp_url';

  // Getters with fallbacks to .env
  String? get nickname => _prefs.getString(_keyNickname);

  String get apiBaseUrl =>
      _prefs.getString(_keyApiBaseUrl) ??
      dotenv.env['CANCIONES_API_URL'] ??
      'http://localhost:8080';

  String get streamingHost =>
      _prefs.getString(_keyStreamingHost) ??
      dotenv.env['STREAMING_API_HOST'] ??
      'localhost';

  int get streamingPort =>
      _prefs.getInt(_keyStreamingPort) ??
      int.tryParse(dotenv.env['STREAMING_API_PORT'] ?? '50051') ??
      50051;

  String get streamingHostWeb =>
      _prefs.getString(_keyStreamingHostWeb) ??
      dotenv.env['STREAMING_API_HOST_WEB'] ??
      'localhost';

  int get streamingPortWeb =>
      _prefs.getInt(_keyStreamingPortWeb) ??
      int.tryParse(dotenv.env['STREAMING_API_PORT_WEB'] ?? '8080') ??
      8080;

  String get stompUrl =>
      _prefs.getString(_keyStompUrl) ??
      'ws://${dotenv.env['REACCIONES_API_URL'] ?? "localhost:5000"}/ws-raw';

  // Setters
  Future<void> setNickname(String value) async {
    await _prefs.setString(_keyNickname, value);
  }

  Future<void> setApiBaseUrl(String value) async {
    await _prefs.setString(_keyApiBaseUrl, value);
  }

  Future<void> setStreamingHost(String value) async {
    await _prefs.setString(_keyStreamingHost, value);
  }

  Future<void> setStreamingPort(int value) async {
    await _prefs.setInt(_keyStreamingPort, value);
  }

  Future<void> setStreamingHostWeb(String value) async {
    await _prefs.setString(_keyStreamingHostWeb, value);
  }

  Future<void> setStreamingPortWeb(int value) async {
    await _prefs.setInt(_keyStreamingPortWeb, value);
  }

  Future<void> setStompUrl(String value) async {
    await _prefs.setString(_keyStompUrl, value);
  }

  Future<void> resetToDefaults() async {
    await _prefs.remove(_keyApiBaseUrl);
    await _prefs.remove(_keyStreamingHost);
    await _prefs.remove(_keyStreamingPort);
    await _prefs.remove(_keyStreamingHostWeb);
    await _prefs.remove(_keyStreamingPortWeb);
    await _prefs.remove(_keyStompUrl);
  }

  Future<void> clearNickname() async {
    await _prefs.remove(_keyNickname);
  }
}
