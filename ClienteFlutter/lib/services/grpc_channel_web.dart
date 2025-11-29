import 'package:grpc/grpc_web.dart';

dynamic createChannel(String host, int port) {
  return GrpcWebClientChannel.xhr(Uri.parse('http://$host:$port'));
}
