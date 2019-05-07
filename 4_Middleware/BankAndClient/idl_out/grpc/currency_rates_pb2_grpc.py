# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
import grpc

import src.idl_out.grpc.currency_rates_pb2 as currency__rates__pb2


class ExchangeRateServiceStub(object):
  # missing associated documentation comment in .proto file
  pass

  def __init__(self, channel):
    """Constructor.

    Args:
      channel: A grpc.Channel.
    """
    self.requestExchangeRateService = channel.unary_stream(
        '/ExchangeRateService/requestExchangeRateService',
        request_serializer=currency__rates__pb2.ExchangeRateServiceRequest.SerializeToString,
        response_deserializer=currency__rates__pb2.RateUpdate.FromString,
        )


class ExchangeRateServiceServicer(object):
  # missing associated documentation comment in .proto file
  pass

  def requestExchangeRateService(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')


def add_ExchangeRateServiceServicer_to_server(servicer, server):
  rpc_method_handlers = {
      'requestExchangeRateService': grpc.unary_stream_rpc_method_handler(
          servicer.requestExchangeRateService,
          request_deserializer=currency__rates__pb2.ExchangeRateServiceRequest.FromString,
          response_serializer=currency__rates__pb2.RateUpdate.SerializeToString,
      ),
  }
  generic_handler = grpc.method_handlers_generic_handler(
      'ExchangeRateService', rpc_method_handlers)
  server.add_generic_rpc_handlers((generic_handler,))