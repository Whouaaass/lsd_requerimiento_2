import 'dart:async';
import 'dart:convert';
import 'package:spotifake_player/services/app_logger.dart';
import 'package:stomp_dart_client/stomp_dart_client.dart';
import 'models/listener_message.dart';

/// Service to manage STOMP connection for listener activity
/// Based on JavaScript ClienteWebSocket implementation
class StompListenerService {
  StompClient? _stompClient;
  StompUnsubscribe? _subscription;

  final StreamController<ListenerMessage> _messageController =
      StreamController<ListenerMessage>.broadcast();
  final StreamController<bool> _connectionController =
      StreamController<bool>.broadcast();

  bool _isConnected = false;
  String? _currentNickname;

  /// Stream of incoming listener messages
  Stream<ListenerMessage> get messageStream => _messageController.stream;

  /// Stream of connection state (true = connected, false = disconnected)
  Stream<bool> get connectionStateStream => _connectionController.stream;

  /// Current connection status
  bool get isConnected => _isConnected;

  /// Connect to STOMP server and subscribe to song channel
  ///
  /// [url] - WebSocket URL (e.g., 'ws://localhost:5000/ws')
  /// [nickname] - User nickname for connection
  /// [songId] - Song ID to subscribe to
  Future<void> connect(String url, String nickname, int songId) async {
    try {
      if (_stompClient != null) {
        await disconnect();
      }

      _currentNickname = nickname;

      _stompClient = StompClient(
        config: StompConfig(
          url: url,
          onConnect: (StompFrame frame) {
            _isConnected = true;
            if (!_connectionController.isClosed) {
              _connectionController.add(true);
            }
            AppLogger.info("✅ STOMP connected successfully");

            // Subscribe to song channel
            _subscribeToSongChannel(songId);
          },
          onWebSocketError: (dynamic error) {
            AppLogger.error('❌ WebSocket error: $error');
            _isConnected = false;
            if (!_connectionController.isClosed) {
              _connectionController.add(false);
            }
          },
          onStompError: (StompFrame frame) {
            AppLogger.error('❌ STOMP error: ${frame.body}');
            _isConnected = false;
            if (!_connectionController.isClosed) {
              _connectionController.add(false);
            }
          },
          onDisconnect: (StompFrame frame) {
            _isConnected = false;
            if (!_connectionController.isClosed) {
              _connectionController.add(false);
            }
            AppLogger.info('🔌 STOMP disconnected');
          },
          // Add nickname to connection headers
          stompConnectHeaders: {'nickname': nickname},
          webSocketConnectHeaders: {'nickname': nickname},
        ),
      );

      if (_stompClient != null) {
        _stompClient?.activate();
      }
    } catch (e) {
      AppLogger.error('❌ Error creating STOMP client: $e');
      _isConnected = false;
      if (!_connectionController.isClosed) {
        _connectionController.add(false);
      }
      rethrow;
    }
  }

  /// Subscribe to song channel to receive listener activity
  void _subscribeToSongChannel(int songId) {
    if (_stompClient == null || !_isConnected) {
      AppLogger.error('⚠️ Cannot subscribe: not connected');
      return;
    }

    // Unsubscribe from previous channel if exists
    _subscription?.call();

    // Subscribe to /cancion/{songId} channel
    final destination = '/cancion/$songId';
    AppLogger.info('📡 Subscribing to $destination');

    _subscription = _stompClient?.subscribe(
      destination: destination,
      callback: (StompFrame frame) {
        if (frame.body != null) {
          try {
            final data = jsonDecode(frame.body ?? '{}');
            final message = ListenerMessage.fromJson(data);
            if (!_messageController.isClosed) {
              _messageController.add(message);
            }
            AppLogger.info(
              '📨 Received message: ${message.type} from ${message.user}',
            );
          } catch (e) {
            AppLogger.error('❌ Error parsing message: $e');
          }
        }
      },
    );
  }

  /// Send a reaction to the server
  ///
  /// [songId] - Song ID
  /// [reaction] - Reaction type: "like", "heart", "sad", "fun"
  void sendReaction(int songId, String reaction) {
    if (_stompClient == null || !_isConnected) {
      AppLogger.error('⚠️ Cannot send reaction: not connected');
      return;
    }

    final message = {
      'type': 'reaction',
      'content': reaction,
      'idCancion': songId,
      'userNickname': _currentNickname ?? 'Anonymous',
    };

    _stompClient?.send(
      destination: '/apiCanciones/enviar',
      body: jsonEncode(message),
    );

    AppLogger.info('📤 Sent reaction: $reaction for song $songId');
  }

  /// Send playing status to the server
  ///
  /// [songId] - Song ID that started playing
  void sendPlayingStatus(int songId) {
    if (_stompClient == null || !_isConnected) {
      AppLogger.warning('⚠️ Cannot send playing status: not connected');
      return;
    }

    final message = {
      'type': 'playing',
      'content': null,
      'idCancion': songId,
      'userNickname': _currentNickname ?? 'Anonymous',
    };

    _stompClient?.send(
      destination: '/apiCanciones/enviar',
      body: jsonEncode(message),
    );

    AppLogger.info('▶️ Sent playing status for song $songId');
  }

  /// Send stopped status to the server
  ///
  /// [songId] - Song ID that stopped playing
  void sendStoppedStatus(int songId) {
    if (_stompClient == null || !_isConnected) {
      AppLogger.warning('⚠️ Cannot send stopped status: not connected');
      return;
    }

    final message = {
      'type': 'stopped',
      'content': null,
      'idCancion': songId,
      'userNickname': _currentNickname ?? 'Anonymous',
    };

    _stompClient?.send(
      destination: '/apiCanciones/enviar',
      body: jsonEncode(message),
    );

    AppLogger.info('⏹️ Sent stopped status for song $songId');
  }

  /// Disconnect from STOMP server
  Future<void> disconnect() async {
    _subscription?.call();
    _subscription = null;

    if (_stompClient != null) {
      _stompClient?.deactivate();
      _stompClient = null;
    }

    _isConnected = false;
    if (!_connectionController.isClosed) {
      _connectionController.add(false);
    }
    AppLogger.info('👋 Disconnected from STOMP server');
  }

  /// Dispose of resources
  Future<void> dispose() async {
    await disconnect();
    await _messageController.close();
    await _connectionController.close();
  }
}
