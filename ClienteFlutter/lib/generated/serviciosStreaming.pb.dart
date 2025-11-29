// This is a generated file - do not edit.
//
// Generated from serviciosStreaming.proto.

// @dart = 3.3

// ignore_for_file: annotate_overrides, camel_case_types, comment_references
// ignore_for_file: constant_identifier_names
// ignore_for_file: curly_braces_in_flow_control_structures
// ignore_for_file: deprecated_member_use_from_same_package, library_prefixes
// ignore_for_file: non_constant_identifier_names, prefer_relative_imports

import 'dart:core' as $core;

import 'package:protobuf/protobuf.dart' as $pb;

export 'package:protobuf/protobuf.dart' show GeneratedMessageGenericExtensions;

/// Mensajes
class peticionDTO extends $pb.GeneratedMessage {
  factory peticionDTO({
    $core.int? id,
  }) {
    final result = create();
    if (id != null) result.id = id;
    return result;
  }

  peticionDTO._();

  factory peticionDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory peticionDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'peticionDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'servicios'),
      createEmptyInstance: create)
    ..aI(1, _omitFieldNames ? '' : 'Id', protoName: 'Id')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  peticionDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  peticionDTO copyWith(void Function(peticionDTO) updates) =>
      super.copyWith((message) => updates(message as peticionDTO))
          as peticionDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static peticionDTO create() => peticionDTO._();
  @$core.override
  peticionDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static peticionDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<peticionDTO>(create);
  static peticionDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.int get id => $_getIZ(0);
  @$pb.TagNumber(1)
  set id($core.int value) => $_setSignedInt32(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);
}

class FragmentoCancion extends $pb.GeneratedMessage {
  factory FragmentoCancion({
    $core.List<$core.int>? data,
  }) {
    final result = create();
    if (data != null) result.data = data;
    return result;
  }

  FragmentoCancion._();

  factory FragmentoCancion.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory FragmentoCancion.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'FragmentoCancion',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'servicios'),
      createEmptyInstance: create)
    ..a<$core.List<$core.int>>(
        1, _omitFieldNames ? '' : 'data', $pb.PbFieldType.OY)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  FragmentoCancion clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  FragmentoCancion copyWith(void Function(FragmentoCancion) updates) =>
      super.copyWith((message) => updates(message as FragmentoCancion))
          as FragmentoCancion;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static FragmentoCancion create() => FragmentoCancion._();
  @$core.override
  FragmentoCancion createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static FragmentoCancion getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<FragmentoCancion>(create);
  static FragmentoCancion? _defaultInstance;

  @$pb.TagNumber(1)
  $core.List<$core.int> get data => $_getN(0);
  @$pb.TagNumber(1)
  set data($core.List<$core.int> value) => $_setBytes(0, value);
  @$pb.TagNumber(1)
  $core.bool hasData() => $_has(0);
  @$pb.TagNumber(1)
  void clearData() => $_clearField(1);
}

class PeticionStreamDTO extends $pb.GeneratedMessage {
  factory PeticionStreamDTO({
    $core.int? idUsuario,
    CancionDTO? cancion,
  }) {
    final result = create();
    if (idUsuario != null) result.idUsuario = idUsuario;
    if (cancion != null) result.cancion = cancion;
    return result;
  }

  PeticionStreamDTO._();

  factory PeticionStreamDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory PeticionStreamDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'PeticionStreamDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'servicios'),
      createEmptyInstance: create)
    ..aI(1, _omitFieldNames ? '' : 'IdUsuario', protoName: 'IdUsuario')
    ..aOM<CancionDTO>(2, _omitFieldNames ? '' : 'Cancion',
        protoName: 'Cancion', subBuilder: CancionDTO.create)
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PeticionStreamDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  PeticionStreamDTO copyWith(void Function(PeticionStreamDTO) updates) =>
      super.copyWith((message) => updates(message as PeticionStreamDTO))
          as PeticionStreamDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static PeticionStreamDTO create() => PeticionStreamDTO._();
  @$core.override
  PeticionStreamDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static PeticionStreamDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<PeticionStreamDTO>(create);
  static PeticionStreamDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.int get idUsuario => $_getIZ(0);
  @$pb.TagNumber(1)
  set idUsuario($core.int value) => $_setSignedInt32(0, value);
  @$pb.TagNumber(1)
  $core.bool hasIdUsuario() => $_has(0);
  @$pb.TagNumber(1)
  void clearIdUsuario() => $_clearField(1);

  @$pb.TagNumber(2)
  CancionDTO get cancion => $_getN(1);
  @$pb.TagNumber(2)
  set cancion(CancionDTO value) => $_setField(2, value);
  @$pb.TagNumber(2)
  $core.bool hasCancion() => $_has(1);
  @$pb.TagNumber(2)
  void clearCancion() => $_clearField(2);
  @$pb.TagNumber(2)
  CancionDTO ensureCancion() => $_ensure(1);
}

/// MENSAJES
class CancionDTO extends $pb.GeneratedMessage {
  factory CancionDTO({
    $core.int? id,
    $core.String? titulo,
    $core.String? autor,
    $core.String? album,
    $core.int? anioLanzamiento,
    $core.int? duracionS,
    $core.String? genero,
    $core.String? idioma,
    $core.String? rutaAlmacenamiento,
  }) {
    final result = create();
    if (id != null) result.id = id;
    if (titulo != null) result.titulo = titulo;
    if (autor != null) result.autor = autor;
    if (album != null) result.album = album;
    if (anioLanzamiento != null) result.anioLanzamiento = anioLanzamiento;
    if (duracionS != null) result.duracionS = duracionS;
    if (genero != null) result.genero = genero;
    if (idioma != null) result.idioma = idioma;
    if (rutaAlmacenamiento != null)
      result.rutaAlmacenamiento = rutaAlmacenamiento;
    return result;
  }

  CancionDTO._();

  factory CancionDTO.fromBuffer($core.List<$core.int> data,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromBuffer(data, registry);
  factory CancionDTO.fromJson($core.String json,
          [$pb.ExtensionRegistry registry = $pb.ExtensionRegistry.EMPTY]) =>
      create()..mergeFromJson(json, registry);

  static final $pb.BuilderInfo _i = $pb.BuilderInfo(
      _omitMessageNames ? '' : 'CancionDTO',
      package: const $pb.PackageName(_omitMessageNames ? '' : 'servicios'),
      createEmptyInstance: create)
    ..aI(1, _omitFieldNames ? '' : 'Id', protoName: 'Id')
    ..aOS(2, _omitFieldNames ? '' : 'Titulo', protoName: 'Titulo')
    ..aOS(3, _omitFieldNames ? '' : 'Autor', protoName: 'Autor')
    ..aOS(4, _omitFieldNames ? '' : 'Album', protoName: 'Album')
    ..aI(5, _omitFieldNames ? '' : 'AnioLanzamiento',
        protoName: 'AnioLanzamiento')
    ..aI(6, _omitFieldNames ? '' : 'DuracionS', protoName: 'DuracionS')
    ..aOS(7, _omitFieldNames ? '' : 'Genero', protoName: 'Genero')
    ..aOS(8, _omitFieldNames ? '' : 'Idioma', protoName: 'Idioma')
    ..aOS(9, _omitFieldNames ? '' : 'RutaAlmacenamiento',
        protoName: 'RutaAlmacenamiento')
    ..hasRequiredFields = false;

  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CancionDTO clone() => deepCopy();
  @$core.Deprecated('See https://github.com/google/protobuf.dart/issues/998.')
  CancionDTO copyWith(void Function(CancionDTO) updates) =>
      super.copyWith((message) => updates(message as CancionDTO)) as CancionDTO;

  @$core.override
  $pb.BuilderInfo get info_ => _i;

  @$core.pragma('dart2js:noInline')
  static CancionDTO create() => CancionDTO._();
  @$core.override
  CancionDTO createEmptyInstance() => create();
  @$core.pragma('dart2js:noInline')
  static CancionDTO getDefault() => _defaultInstance ??=
      $pb.GeneratedMessage.$_defaultFor<CancionDTO>(create);
  static CancionDTO? _defaultInstance;

  @$pb.TagNumber(1)
  $core.int get id => $_getIZ(0);
  @$pb.TagNumber(1)
  set id($core.int value) => $_setSignedInt32(0, value);
  @$pb.TagNumber(1)
  $core.bool hasId() => $_has(0);
  @$pb.TagNumber(1)
  void clearId() => $_clearField(1);

  @$pb.TagNumber(2)
  $core.String get titulo => $_getSZ(1);
  @$pb.TagNumber(2)
  set titulo($core.String value) => $_setString(1, value);
  @$pb.TagNumber(2)
  $core.bool hasTitulo() => $_has(1);
  @$pb.TagNumber(2)
  void clearTitulo() => $_clearField(2);

  @$pb.TagNumber(3)
  $core.String get autor => $_getSZ(2);
  @$pb.TagNumber(3)
  set autor($core.String value) => $_setString(2, value);
  @$pb.TagNumber(3)
  $core.bool hasAutor() => $_has(2);
  @$pb.TagNumber(3)
  void clearAutor() => $_clearField(3);

  @$pb.TagNumber(4)
  $core.String get album => $_getSZ(3);
  @$pb.TagNumber(4)
  set album($core.String value) => $_setString(3, value);
  @$pb.TagNumber(4)
  $core.bool hasAlbum() => $_has(3);
  @$pb.TagNumber(4)
  void clearAlbum() => $_clearField(4);

  @$pb.TagNumber(5)
  $core.int get anioLanzamiento => $_getIZ(4);
  @$pb.TagNumber(5)
  set anioLanzamiento($core.int value) => $_setSignedInt32(4, value);
  @$pb.TagNumber(5)
  $core.bool hasAnioLanzamiento() => $_has(4);
  @$pb.TagNumber(5)
  void clearAnioLanzamiento() => $_clearField(5);

  @$pb.TagNumber(6)
  $core.int get duracionS => $_getIZ(5);
  @$pb.TagNumber(6)
  set duracionS($core.int value) => $_setSignedInt32(5, value);
  @$pb.TagNumber(6)
  $core.bool hasDuracionS() => $_has(5);
  @$pb.TagNumber(6)
  void clearDuracionS() => $_clearField(6);

  @$pb.TagNumber(7)
  $core.String get genero => $_getSZ(6);
  @$pb.TagNumber(7)
  set genero($core.String value) => $_setString(6, value);
  @$pb.TagNumber(7)
  $core.bool hasGenero() => $_has(6);
  @$pb.TagNumber(7)
  void clearGenero() => $_clearField(7);

  @$pb.TagNumber(8)
  $core.String get idioma => $_getSZ(7);
  @$pb.TagNumber(8)
  set idioma($core.String value) => $_setString(7, value);
  @$pb.TagNumber(8)
  $core.bool hasIdioma() => $_has(7);
  @$pb.TagNumber(8)
  void clearIdioma() => $_clearField(8);

  @$pb.TagNumber(9)
  $core.String get rutaAlmacenamiento => $_getSZ(8);
  @$pb.TagNumber(9)
  set rutaAlmacenamiento($core.String value) => $_setString(8, value);
  @$pb.TagNumber(9)
  $core.bool hasRutaAlmacenamiento() => $_has(8);
  @$pb.TagNumber(9)
  void clearRutaAlmacenamiento() => $_clearField(9);
}

const $core.bool _omitFieldNames =
    $core.bool.fromEnvironment('protobuf.omit_field_names');
const $core.bool _omitMessageNames =
    $core.bool.fromEnvironment('protobuf.omit_message_names');
