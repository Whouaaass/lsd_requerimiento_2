import 'package:flutter/material.dart';

class ListenerMessage {
  // "reaction", "connected", "disconnected", "playing", "stopped"
  final String type;
  // "like", "heart", "sad", "fun"
  final String? content;
  final String user;
  final int? idCancion;
  final DateTime timestamp;

  ListenerMessage({
    required this.type,
    this.content,
    required this.user,
    this.idCancion,
    DateTime? timestamp,
  }) : timestamp = timestamp ?? DateTime.now();

  factory ListenerMessage.fromJson(Map<String, dynamic> json) {
    return ListenerMessage(
      type: json['type'] as String,
      content: json['content'] as String?,
      user: json['userNickname'] as String,
      idCancion: json['idCancion'] as int?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'type': type,
      'content': content,
      'idCancion': idCancion,
      'userNickname': user,
    };
  }

  String getDisplayText() {
    switch (type) {
      case 'connected':
        return '$user joined the listening session';
      case 'disconnected':
        return '$user left the listening session';
      case 'reaction':
        final emoji = _getReactionEmoji(content);
        return '$user reacted $emoji';
      case 'playing':
        return '$user started playing';
      case 'stopped':
        return '$user stopped playing';
      default:
        return '$user: $type';
    }
  }

  String _getReactionEmoji(String? reaction) {
    switch (reaction) {
      case 'like':
        return '👍';
      case 'heart':
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
      case 'connection':
        return Icons.person_add;
      case 'disconnection':
        return Icons.person_remove;
      case 'reaction':
        return Icons.favorite;
      case 'playing':
        return Icons.play_arrow;
      case 'stopped':
        return Icons.stop;
      default:
        return Icons.message;
    }
  }

  Color getColor() {
    switch (type) {
      case 'connection':
        return Colors.green;
      case 'disconnection':
        return Colors.orange;
      case 'reaction':
        return Colors.pink;
      case 'playing':
        return Colors.blue;
      case 'stopped':
        return Colors.grey;
      default:
        return Colors.blue;
    }
  }
}
