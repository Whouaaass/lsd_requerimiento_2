// This is a generated file - do not edit.
//
// Generated from serviciosStreaming.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports

import 'dart:async' as $async;
import 'dart:core' as $core;

import 'package:grpc/service_api.dart' as $grpc;
import 'package:protobuf/protobuf.dart' as $pb;

import 'serviciosStreaming.pb.dart' as $0;

export 'serviciosStreaming.pb.dart';

@$pb.GrpcServiceName('servicios.AudioService')
class AudioServiceClient extends $grpc.Client {
  /// The hostname for this service.
  static const $core.String defaultHost = '';

  /// OAuth scopes needed for the client.
  static const $core.List<$core.String> oauthScopes = [
    '',
  ];

  AudioServiceClient(super.channel, {super.options, super.interceptors});

  /// Función de streaming de canción
  $grpc.ResponseStream<$0.FragmentoCancion> enviarCancionMedianteStream(
    $0.peticionDTO request, {
    $grpc.CallOptions? options,
  }) {
    return $createStreamingCall(
        _$enviarCancionMedianteStream, $async.Stream.fromIterable([request]),
        options: options);
  }

  $grpc.ResponseStream<$0.FragmentoCancion> stremearCancion(
    $0.PeticionStreamDTO request, {
    $grpc.CallOptions? options,
  }) {
    return $createStreamingCall(
        _$stremearCancion, $async.Stream.fromIterable([request]),
        options: options);
  }

  // method descriptors

  static final _$enviarCancionMedianteStream =
      $grpc.ClientMethod<$0.peticionDTO, $0.FragmentoCancion>(
          '/servicios.AudioService/enviarCancionMedianteStream',
          ($0.peticionDTO value) => value.writeToBuffer(),
          $0.FragmentoCancion.fromBuffer);
  static final _$stremearCancion =
      $grpc.ClientMethod<$0.PeticionStreamDTO, $0.FragmentoCancion>(
          '/servicios.AudioService/stremearCancion',
          ($0.PeticionStreamDTO value) => value.writeToBuffer(),
          $0.FragmentoCancion.fromBuffer);
}

@$pb.GrpcServiceName('servicios.AudioService')
abstract class AudioServiceBase extends $grpc.Service {
  $core.String get $name => 'servicios.AudioService';

  AudioServiceBase() {
    $addMethod($grpc.ServiceMethod<$0.peticionDTO, $0.FragmentoCancion>(
        'enviarCancionMedianteStream',
        enviarCancionMedianteStream_Pre,
        false,
        true,
        ($core.List<$core.int> value) => $0.peticionDTO.fromBuffer(value),
        ($0.FragmentoCancion value) => value.writeToBuffer()));
    $addMethod($grpc.ServiceMethod<$0.PeticionStreamDTO, $0.FragmentoCancion>(
        'stremearCancion',
        stremearCancion_Pre,
        false,
        true,
        ($core.List<$core.int> value) => $0.PeticionStreamDTO.fromBuffer(value),
        ($0.FragmentoCancion value) => value.writeToBuffer()));
  }

  $async.Stream<$0.FragmentoCancion> enviarCancionMedianteStream_Pre(
      $grpc.ServiceCall $call, $async.Future<$0.peticionDTO> $request) async* {
    yield* enviarCancionMedianteStream($call, await $request);
  }

  $async.Stream<$0.FragmentoCancion> enviarCancionMedianteStream(
      $grpc.ServiceCall call, $0.peticionDTO request);

  $async.Stream<$0.FragmentoCancion> stremearCancion_Pre(
      $grpc.ServiceCall $call,
      $async.Future<$0.PeticionStreamDTO> $request) async* {
    yield* stremearCancion($call, await $request);
  }

  $async.Stream<$0.FragmentoCancion> stremearCancion(
      $grpc.ServiceCall call, $0.PeticionStreamDTO request);
}
