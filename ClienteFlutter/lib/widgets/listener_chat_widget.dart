import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

class ListenerMessage {
  final String type; // "connected", "disconnected", "reaccion"
  final String? content; // "like", "hearth", "sad", "fun"
  final String user;
  final DateTime timestamp;

  ListenerMessage({
    required this.type,
    this.content,
    required this.user,
    DateTime? timestamp,
  }) : timestamp = timestamp ?? DateTime.now();

  factory ListenerMessage.fromJson(Map<String, dynamic> json) {
    return ListenerMessage(
      type: json['type'] as String,
      content: json['content'] as String?,
      user: json['user'] as String,
    );
  }

  String getDisplayText() {
    switch (type) {
      case 'connected':
        return '$user joined the listening session';
      case 'disconnected':
        return '$user left the listening session';
      case 'reaccion':
        final emoji = _getReactionEmoji(content);
        return '$user reacted $emoji';
      default:
        return '$user: $type';
    }
  }

  String _getReactionEmoji(String? reaction) {
    switch (reaction) {
      case 'like':
        return '👍';
      case 'hearth':
        return '❤️';
      case 'sad':
        return '😢';
      case 'fun':
        return '😄';
      default:
        return '👋';
    }
  }

  IconData getIcon() {
    switch (type) {
      case 'connected':
        return Icons.person_add;
      case 'disconnected':
        return Icons.person_remove;
      case 'reaccion':
        return Icons.favorite;
      default:
        return Icons.message;
    }
  }

  Color getColor() {
    switch (type) {
      case 'connected':
        return Colors.green;
      case 'disconnected':
        return Colors.orange;
      case 'reaccion':
        return Colors.pink;
      default:
        return Colors.blue;
    }
  }
}

class ListenerChatWidget extends StatefulWidget {
  final String webSocketUrl;
  final int songId;

  const ListenerChatWidget({
    super.key,
    required this.webSocketUrl,
    required this.songId,
  });

  @override
  State<ListenerChatWidget> createState() => _ListenerChatWidgetState();
}

class _ListenerChatWidgetState extends State<ListenerChatWidget> {
  WebSocketChannel? _channel;
  final List<ListenerMessage> _messages = [];
  bool _isConnected = false;
  String _connectionStatus = 'Connecting...';
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _connectWebSocket();
  }

  void _connectWebSocket() {
    try {
      _channel = WebSocketChannel.connect(Uri.parse(widget.webSocketUrl));

      setState(() {
        _isConnected = true;
        _connectionStatus = 'Connected';
      });

      _channel!.stream.listen(
        (message) {
          try {
            final data = jsonDecode(message as String);
            final listenerMessage = ListenerMessage.fromJson(data);

            if (mounted) {
              setState(() {
                _messages.add(listenerMessage);
              });
              _scrollToBottom();
            }
          } catch (e) {
            print('Error parsing message: $e');
          }
        },
        onError: (error) {
          print('WebSocket error: $error');
          if (mounted) {
            setState(() {
              _isConnected = false;
              _connectionStatus = 'Error: $error';
            });
          }
        },
        onDone: () {
          if (mounted) {
            setState(() {
              _isConnected = false;
              _connectionStatus = 'Disconnected';
            });
          }
        },
      );
    } catch (e) {
      print('Failed to connect: $e');
      setState(() {
        _isConnected = false;
        _connectionStatus = 'Failed to connect';
      });
    }
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      Future.delayed(const Duration(milliseconds: 100), () {
        if (_scrollController.hasClients) {
          _scrollController.animateTo(
            _scrollController.position.maxScrollExtent,
            duration: const Duration(milliseconds: 300),
            curve: Curves.easeOut,
          );
        }
      });
    }
  }

  @override
  void dispose() {
    _channel?.sink.close();
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4,
      margin: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.blue.shade50,
              borderRadius: const BorderRadius.only(
                topLeft: Radius.circular(4),
                topRight: Radius.circular(4),
              ),
            ),
            child: Row(
              children: [
                Icon(
                  _isConnected ? Icons.circle : Icons.circle_outlined,
                  color: _isConnected ? Colors.green : Colors.red,
                  size: 12,
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    'Listener Activity',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Text(
                  _connectionStatus,
                  style: TextStyle(
                    fontSize: 12,
                    color: _isConnected ? Colors.green : Colors.grey,
                  ),
                ),
              ],
            ),
          ),

          // Messages List
          Container(
            height: 300,
            padding: const EdgeInsets.all(16),
            child: _messages.isEmpty
                ? Center(
                    child: Text(
                      _isConnected
                          ? 'No activity yet...'
                          : 'Waiting for connection...',
                      style: const TextStyle(color: Colors.grey),
                    ),
                  )
                : ListView.builder(
                    controller: _scrollController,
                    itemCount: _messages.length,
                    itemBuilder: (context, index) {
                      final message = _messages[index];
                      return Padding(
                        padding: const EdgeInsets.symmetric(vertical: 4),
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Icon(
                              message.getIcon(),
                              size: 16,
                              color: message.getColor(),
                            ),
                            const SizedBox(width: 8),
                            Expanded(
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    message.getDisplayText(),
                                    style: const TextStyle(fontSize: 14),
                                  ),
                                  Text(
                                    _formatTime(message.timestamp),
                                    style: const TextStyle(
                                      fontSize: 10,
                                      color: Colors.grey,
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }

  String _formatTime(DateTime time) {
    final now = DateTime.now();
    final diff = now.difference(time);

    if (diff.inSeconds < 60) {
      return 'just now';
    } else if (diff.inMinutes < 60) {
      return '${diff.inMinutes}m ago';
    } else {
      return '${time.hour}:${time.minute.toString().padLeft(2, '0')}';
    }
  }
}
