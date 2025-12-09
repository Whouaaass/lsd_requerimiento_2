// user_notification.dart

/// Modelo simplificado para notificaciones de usuario (ej. tokens agotados).
class UserNotification {
  final String type;
  final String content;
  final DateTime timestamp;

  UserNotification({
    required this.type,
    required this.content,
    required this.timestamp,
  });

  factory UserNotification.fromJson(Map<String, dynamic> json) {
    return UserNotification(
      type: json['type'] as String,
      content: json['content'] as String,
      // Asume que el servidor envía la marca de tiempo o usa la actual
      timestamp: DateTime.now(), 
    );
  }
}