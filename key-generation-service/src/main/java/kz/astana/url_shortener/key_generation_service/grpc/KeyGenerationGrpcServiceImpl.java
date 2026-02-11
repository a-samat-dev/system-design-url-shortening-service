package kz.astana.url_shortener.key_generation_service.grpc;

import kz.astana.url_shortener.key_generation_service.service.KeyGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class KeyGenerationGrpcServiceImpl extends KeyGenerationGrpcServiceGrpc.KeyGenerationGrpcServiceImplBase {

    private final KeyGenerationService keyGenerationService;

    @Override
    public void getOneKey(GetOneKeyRequest request,
                          io.grpc.stub.StreamObserver<GetOneKeyResponse> responseObserver) {
        String key = keyGenerationService.getOneKey();
        GetOneKeyResponse response = GetOneKeyResponse.newBuilder()
                .setKey(key)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
