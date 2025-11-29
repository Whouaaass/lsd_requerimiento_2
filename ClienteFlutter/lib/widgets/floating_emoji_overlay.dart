import 'dart:math';
import 'package:flutter/material.dart';

/// Overlay widget that displays floating emoji animations
/// Similar to Facebook's reaction animations
class FloatingEmojiOverlay extends StatefulWidget {
  final Widget child;

  const FloatingEmojiOverlay({super.key, required this.child});

  @override
  State<FloatingEmojiOverlay> createState() => FloatingEmojiOverlayState();

  /// Find the overlay state in the widget tree
  static FloatingEmojiOverlayState? of(BuildContext context) {
    return context.findAncestorStateOfType<FloatingEmojiOverlayState>();
  }
}

class FloatingEmojiOverlayState extends State<FloatingEmojiOverlay> {
  final List<_FloatingEmoji> _emojis = [];
  final Random _random = Random();

  /// Show a floating emoji animation
  void showEmoji(String emoji) {
    setState(() {
      _emojis.add(
        _FloatingEmoji(
          emoji: emoji,
          key: UniqueKey(),
          startX: _random.nextBool() ? 0.1 : 0.9, // Left or right side
          onComplete: (key) {
            setState(() {
              _emojis.removeWhere((e) => e.key == key);
            });
          },
        ),
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        widget.child,
        // Emoji overlay layer
        IgnorePointer(
          child: Stack(
            children: _emojis
                .map((emoji) => _FloatingEmojiWidget(emoji: emoji))
                .toList(),
          ),
        ),
      ],
    );
  }
}

class _FloatingEmoji {
  final String emoji;
  final Key key;
  final double startX;
  final Function(Key) onComplete;

  _FloatingEmoji({
    required this.emoji,
    required this.key,
    required this.startX,
    required this.onComplete,
  });
}

class _FloatingEmojiWidget extends StatefulWidget {
  final _FloatingEmoji emoji;

  const _FloatingEmojiWidget({required this.emoji});

  @override
  State<_FloatingEmojiWidget> createState() => _FloatingEmojiWidgetState();
}

class _FloatingEmojiWidgetState extends State<_FloatingEmojiWidget>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _floatAnimation;
  late Animation<double> _fadeAnimation;
  late Animation<double> _scaleAnimation;
  late Animation<double> _horizontalAnimation;

  final Random _random = Random();
  late double _horizontalOffset;

  @override
  void initState() {
    super.initState();

    // Random horizontal drift
    _horizontalOffset = _random.nextDouble() * 100 - 50; // -50 to 50

    _controller = AnimationController(
      duration: const Duration(milliseconds: 2500),
      vsync: this,
    );

    // Float up animation
    _floatAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeOut));

    // Fade out animation
    _fadeAnimation = TweenSequence<double>([
      TweenSequenceItem(
        tween: Tween<double>(
          begin: 0.0,
          end: 1.0,
        ).chain(CurveTween(curve: Curves.easeIn)),
        weight: 20,
      ),
      TweenSequenceItem(tween: ConstantTween<double>(1.0), weight: 60),
      TweenSequenceItem(
        tween: Tween<double>(
          begin: 1.0,
          end: 0.0,
        ).chain(CurveTween(curve: Curves.easeOut)),
        weight: 20,
      ),
    ]).animate(_controller);

    // Scale animation (pop effect)
    _scaleAnimation = TweenSequence<double>([
      TweenSequenceItem(
        tween: Tween<double>(
          begin: 0.5,
          end: 1.3,
        ).chain(CurveTween(curve: Curves.easeOut)),
        weight: 15,
      ),
      TweenSequenceItem(
        tween: Tween<double>(
          begin: 1.3,
          end: 1.0,
        ).chain(CurveTween(curve: Curves.easeIn)),
        weight: 10,
      ),
      TweenSequenceItem(tween: ConstantTween<double>(1.0), weight: 60),
      TweenSequenceItem(
        tween: Tween<double>(
          begin: 1.0,
          end: 1.5,
        ).chain(CurveTween(curve: Curves.easeIn)),
        weight: 15,
      ),
    ]).animate(_controller);

    // Horizontal drift animation
    _horizontalAnimation = Tween<double>(
      begin: 0.0,
      end: _horizontalOffset,
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeInOut));

    _controller.forward().then((_) {
      widget.emoji.onComplete(widget.emoji.key);
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;

    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return Positioned(
          left: widget.emoji.startX * size.width + _horizontalAnimation.value,
          bottom: _floatAnimation.value * size.height,
          child: Opacity(
            opacity: _fadeAnimation.value,
            child: Transform.scale(
              scale: _scaleAnimation.value,
              child: Text(
                widget.emoji.emoji,
                style: const TextStyle(
                  fontSize: 48,
                  shadows: [
                    Shadow(
                      blurRadius: 10,
                      color: Colors.black26,
                      offset: Offset(2, 2),
                    ),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}
