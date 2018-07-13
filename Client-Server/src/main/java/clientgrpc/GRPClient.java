/*
 * Copyright 2015, gRPC Authors All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package clientgrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import proto.SolicitationReply;
import proto.SolicitationRequest;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class GRPClient {
    private static final Logger logger = Logger.getLogger(GRPClient.class.getName());

    private final ManagedChannel channel;
    private final proto.CreateRequestGrpc.CreateRequestStub stub;

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public GRPClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true)
                .build());
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    GRPClient(ManagedChannel channel) {
        this.channel = channel;
        stub = proto.CreateRequestGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Say hello to server.
     */
    public void createRequest(String crud, BigInteger key, String message) {
        Integer chave = new Integer(key.intValue());
        SolicitationRequest request = proto.SolicitationRequest.newBuilder().setCrud(crud).setChave(chave).setMensagem(message).build();
        StreamObserver<SolicitationReply> response = new StreamObserver<SolicitationReply>(){
            @Override
            public void onNext(SolicitationReply value) {
                System.out.println(value.getMessage());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        };
        try {
                stub.solicitation(request, response);
        } catch (StatusRuntimeException e) {
            return;
        }
        //logger.info(response.getMessage());
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
        GRPClient client = new GRPClient("localhost", 8085);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("Bem vindo cliente GRPC. \n CREATE | READ | UPDATE | DELETE | OBSERVE");
            String comando = scanner.nextLine();
            if (comando.equals("READ") || comando.equals("DELETE") || comando.equals("OBSERVE")) {
                System.out.println("Digite a chave:");
                BigInteger chave = new BigInteger(scanner.nextLine());
                client.createRequest(comando, chave, "");
            }
            if(comando.equals("UPDATE") || comando.equals("CREATE")){
                System.out.println("Digite a chave a ser colocada:");
                BigInteger chave = new BigInteger(scanner.nextLine());
                System.out.println("Digite a mensagem a ser enviada:");
                String mensagem = scanner.nextLine();
                client.createRequest(comando, chave, mensagem);
            } else {
                System.out.println("Comando Inválido!");
            }
        }
    }
}
