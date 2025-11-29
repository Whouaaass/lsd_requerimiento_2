import 'dart:async';
import 'package:flutter/material.dart';
import '../services/reacciones_api/models/listener_message.dart';
import '../services/reacciones_api/stomp_listener_service.dart';

class ListenerChatWidget extends StatefulWidget {
  final String webSocketUrl;
  final int songId;
  final String nickname;

  const ListenerChatWidget({
    super.key,
    required this.webSocketUrl,
    required this.songId,
    this.nickname = 'Anonymous',
  });

  @override
  State<ListenerChatWidget> createState() => _ListenerChatWidgetState();
}

class _ListenerChatWidgetState extends State<ListenerChatWidget> {
  final StompListenerService _stompService = StompListenerService();
  final List<ListenerMessage> _messages = [];
  bool _isConnected = false;
  String _connectionStatus = 'Connecting...';
  final ScrollController _scrollController = ScrollController();

  StreamSubscription<ListenerMessage>? _messageSubscription;
  StreamSubscription<bool>? _connectionSubscription;

  @override
  void initState() {
    super.initState();
    _connectToService();
  }

  void _connectToService() {
    // Subscribe to message stream
    _messageSubscription = _stompService.messageStream.listen(
      (message) {
        if (mounted) {
          setState(() {
            _messages.add(message);
          });
          _scrollToBottom();
        }
      },
      onError: (error) {
        print('Message stream error: $error');
      },
    );

    // Subscribe to connection state stream
    _connectionSubscription = _stompService.connectionStateStream.listen((
      isConnected,
    ) {
      if (mounted) {
        setState(() {
          _isConnected = isConnected;
          _connectionStatus = isConnected ? 'Connected' : 'Disconnected';
        });
      }
    });

    // Connect to STOMP server
    _stompService
        .connect(widget.webSocketUrl, widget.nickname, widget.songId)
        .catchError((error) {
          print('Failed to connect: $error');
          if (mounted) {
            setState(() {
              _connectionStatus = 'Failed to connect';
            });
          }
        });
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

  void _sendReaction(String reaction) {
    _stompService.sendReaction(widget.songId, reaction);
  }

  @override
  void dispose() {
    _messageSubscription?.cancel();
    _connectionSubscription?.cancel();
    _stompService.dispose();
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

          // Reaction Buttons
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            decoration: BoxDecoration(
              color: Colors.grey.shade50,
              border: Border(top: BorderSide(color: Colors.grey.shade300)),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _ReactionButton(
                  emoji: '👍',
                  label: 'Like',
                  onPressed: _isConnected ? () => _sendReaction('like') : null,
                ),
                _ReactionButton(
                  emoji: '❤️',
                  label: 'Heart',
                  onPressed: _isConnected
                      ? () => _sendReaction('hearth')
                      : null,
                ),
                _ReactionButton(
                  emoji: '😢',
                  label: 'Sad',
                  onPressed: _isConnected ? () => _sendReaction('sad') : null,
                ),
                _ReactionButton(
                  emoji: '😄',
                  label: 'Fun',
                  onPressed: _isConnected ? () => _sendReaction('fun') : null,
                ),
              ],
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

class _ReactionButton extends StatelessWidget {
  final String emoji;
  final String label;
  final VoidCallback? onPressed;

  const _ReactionButton({
    required this.emoji,
    required this.label,
    this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onPressed,
      borderRadius: BorderRadius.circular(8),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: onPressed != null ? Colors.white : Colors.grey.shade200,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(
            color: onPressed != null
                ? Colors.blue.shade200
                : Colors.grey.shade300,
          ),
        ),
        child: Column(
          children: [
            Text(emoji, style: const TextStyle(fontSize: 20)),
            const SizedBox(height: 4),
            Text(
              label,
              style: TextStyle(
                fontSize: 10,
                color: onPressed != null ? Colors.black87 : Colors.grey,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
