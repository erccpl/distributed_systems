package currency_server;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.20.0)",
    comments = "Source: currency_rates.proto")
public final class ExchangeRateServiceGrpc {

  private ExchangeRateServiceGrpc() {}

  public static final String SERVICE_NAME = "ExchangeRateService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<CurrencyRates.ExchangeRateServiceRequest,
      CurrencyRates.RateUpdate> getRequestExchangeRateServiceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "requestExchangeRateService",
      requestType = CurrencyRates.ExchangeRateServiceRequest.class,
      responseType = CurrencyRates.RateUpdate.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<CurrencyRates.ExchangeRateServiceRequest,
      CurrencyRates.RateUpdate> getRequestExchangeRateServiceMethod() {
    io.grpc.MethodDescriptor<CurrencyRates.ExchangeRateServiceRequest, CurrencyRates.RateUpdate> getRequestExchangeRateServiceMethod;
    if ((getRequestExchangeRateServiceMethod = ExchangeRateServiceGrpc.getRequestExchangeRateServiceMethod) == null) {
      synchronized (ExchangeRateServiceGrpc.class) {
        if ((getRequestExchangeRateServiceMethod = ExchangeRateServiceGrpc.getRequestExchangeRateServiceMethod) == null) {
          ExchangeRateServiceGrpc.getRequestExchangeRateServiceMethod = getRequestExchangeRateServiceMethod = 
              io.grpc.MethodDescriptor.<CurrencyRates.ExchangeRateServiceRequest, CurrencyRates.RateUpdate>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "ExchangeRateService", "requestExchangeRateService"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  CurrencyRates.ExchangeRateServiceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  CurrencyRates.RateUpdate.getDefaultInstance()))
                  .setSchemaDescriptor(new ExchangeRateServiceMethodDescriptorSupplier("requestExchangeRateService"))
                  .build();
          }
        }
     }
     return getRequestExchangeRateServiceMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ExchangeRateServiceStub newStub(io.grpc.Channel channel) {
    return new ExchangeRateServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ExchangeRateServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ExchangeRateServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ExchangeRateServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ExchangeRateServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ExchangeRateServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void requestExchangeRateService(CurrencyRates.ExchangeRateServiceRequest request,
                                           io.grpc.stub.StreamObserver<CurrencyRates.RateUpdate> responseObserver) {
      asyncUnimplementedUnaryCall(getRequestExchangeRateServiceMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestExchangeRateServiceMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                CurrencyRates.ExchangeRateServiceRequest,
                CurrencyRates.RateUpdate>(
                  this, METHODID_REQUEST_EXCHANGE_RATE_SERVICE)))
          .build();
    }
  }

  /**
   */
  public static final class ExchangeRateServiceStub extends io.grpc.stub.AbstractStub<ExchangeRateServiceStub> {
    private ExchangeRateServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ExchangeRateServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ExchangeRateServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ExchangeRateServiceStub(channel, callOptions);
    }

    /**
     */
    public void requestExchangeRateService(CurrencyRates.ExchangeRateServiceRequest request,
                                           io.grpc.stub.StreamObserver<CurrencyRates.RateUpdate> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getRequestExchangeRateServiceMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ExchangeRateServiceBlockingStub extends io.grpc.stub.AbstractStub<ExchangeRateServiceBlockingStub> {
    private ExchangeRateServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ExchangeRateServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ExchangeRateServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ExchangeRateServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<CurrencyRates.RateUpdate> requestExchangeRateService(
        CurrencyRates.ExchangeRateServiceRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getRequestExchangeRateServiceMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ExchangeRateServiceFutureStub extends io.grpc.stub.AbstractStub<ExchangeRateServiceFutureStub> {
    private ExchangeRateServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ExchangeRateServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ExchangeRateServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ExchangeRateServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_REQUEST_EXCHANGE_RATE_SERVICE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ExchangeRateServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ExchangeRateServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_EXCHANGE_RATE_SERVICE:
          serviceImpl.requestExchangeRateService((CurrencyRates.ExchangeRateServiceRequest) request,
              (io.grpc.stub.StreamObserver<CurrencyRates.RateUpdate>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ExchangeRateServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ExchangeRateServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return CurrencyRates.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ExchangeRateService");
    }
  }

  private static final class ExchangeRateServiceFileDescriptorSupplier
      extends ExchangeRateServiceBaseDescriptorSupplier {
    ExchangeRateServiceFileDescriptorSupplier() {}
  }

  private static final class ExchangeRateServiceMethodDescriptorSupplier
      extends ExchangeRateServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ExchangeRateServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ExchangeRateServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ExchangeRateServiceFileDescriptorSupplier())
              .addMethod(getRequestExchangeRateServiceMethod())
              .build();
        }
      }
    }
    return result;
  }
}
