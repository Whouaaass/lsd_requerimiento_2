// This is a generated file - do not edit.
//
// Generated from serviciosStreaming.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports
// ignore_for_file: unused_import

import 'dart:convert' as $convert;
import 'dart:core' as $core;
import 'dart:typed_data' as $typed_data;

@$core.Deprecated('Use peticionDTODescriptor instead')
const peticionDTO$json = {
  '1': 'peticionDTO',
  '2': [
    {'1': 'Id', '3': 1, '4': 1, '5': 5, '10': 'Id'},
  ],
};

/// Descriptor for `peticionDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List peticionDTODescriptor =
    $convert.base64Decode('CgtwZXRpY2lvbkRUTxIOCgJJZBgBIAEoBVICSWQ=');

@$core.Deprecated('Use fragmentoCancionDescriptor instead')
const FragmentoCancion$json = {
  '1': 'FragmentoCancion',
  '2': [
    {'1': 'data', '3': 1, '4': 1, '5': 12, '10': 'data'},
  ],
};

/// Descriptor for `FragmentoCancion`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List fragmentoCancionDescriptor = $convert
    .base64Decode('ChBGcmFnbWVudG9DYW5jaW9uEhIKBGRhdGEYASABKAxSBGRhdGE=');

@$core.Deprecated('Use peticionStreamDTODescriptor instead')
const PeticionStreamDTO$json = {
  '1': 'PeticionStreamDTO',
  '2': [
    {'1': 'IdUsuario', '3': 1, '4': 1, '5': 5, '10': 'IdUsuario'},
    {
      '1': 'Cancion',
      '3': 2,
      '4': 1,
      '5': 11,
      '6': '.servicios.CancionDTO',
      '10': 'Cancion'
    },
  ],
};

/// Descriptor for `PeticionStreamDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List peticionStreamDTODescriptor = $convert.base64Decode(
    'ChFQZXRpY2lvblN0cmVhbURUTxIcCglJZFVzdWFyaW8YASABKAVSCUlkVXN1YXJpbxIvCgdDYW'
    '5jaW9uGAIgASgLMhUuc2VydmljaW9zLkNhbmNpb25EVE9SB0NhbmNpb24=');

@$core.Deprecated('Use cancionDTODescriptor instead')
const CancionDTO$json = {
  '1': 'CancionDTO',
  '2': [
    {'1': 'Id', '3': 1, '4': 1, '5': 5, '10': 'Id'},
    {'1': 'Titulo', '3': 2, '4': 1, '5': 9, '10': 'Titulo'},
    {'1': 'Autor', '3': 3, '4': 1, '5': 9, '10': 'Autor'},
    {'1': 'Album', '3': 4, '4': 1, '5': 9, '10': 'Album'},
    {'1': 'AnioLanzamiento', '3': 5, '4': 1, '5': 5, '10': 'AnioLanzamiento'},
    {'1': 'DuracionS', '3': 6, '4': 1, '5': 5, '10': 'DuracionS'},
    {'1': 'Genero', '3': 7, '4': 1, '5': 9, '10': 'Genero'},
    {'1': 'Idioma', '3': 8, '4': 1, '5': 9, '10': 'Idioma'},
    {
      '1': 'RutaAlmacenamiento',
      '3': 9,
      '4': 1,
      '5': 9,
      '10': 'RutaAlmacenamiento'
    },
  ],
};

/// Descriptor for `CancionDTO`. Decode as a `google.protobuf.DescriptorProto`.
final $typed_data.Uint8List cancionDTODescriptor = $convert.base64Decode(
    'CgpDYW5jaW9uRFRPEg4KAklkGAEgASgFUgJJZBIWCgZUaXR1bG8YAiABKAlSBlRpdHVsbxIUCg'
    'VBdXRvchgDIAEoCVIFQXV0b3ISFAoFQWxidW0YBCABKAlSBUFsYnVtEigKD0FuaW9MYW56YW1p'
    'ZW50bxgFIAEoBVIPQW5pb0xhbnphbWllbnRvEhwKCUR1cmFjaW9uUxgGIAEoBVIJRHVyYWNpb2'
    '5TEhYKBkdlbmVybxgHIAEoCVIGR2VuZXJvEhYKBklkaW9tYRgIIAEoCVIGSWRpb21hEi4KElJ1'
    'dGFBbG1hY2VuYW1pZW50bxgJIAEoCVISUnV0YUFsbWFjZW5hbWllbnRv');
