
# hello-armeria

Hanging out with [Spring](https://spring.io/) and [Armeria](https://armeria.dev/) 

### Components
| Name | gRPC Port | Web Port | Docs |
|-----|-----|-----|-----|
| rest-service | x | 8081 | 8091 |
| rest-service-reactive | x | 8082 | 8092 |
| rest-service-reactive-functional | x | 8083 | 8093 |
| grpc-service | 50051 | x | 50051 |
| grpc-service-reactive | 50052 | x | 50052 |
| grpc-service-transcoding | 50053 | x | 50053 |
| grpc-service-reactive-transcoding | 50054 | x | 50054 |
| eureka-discovery | x | 8761 | x |
| grpc-service-multi-instances | 55555 | x | 55555 |
| grpc-client-service-multi-instance | 0 | x | 0 |
| grpc-service-multi-instances-arm | 0 | x | 0 |
| grpc-client-service-multi-instance-arm | 0 | x | 0 |

### Installation

Download and compile

```
mvn clean compile
```
Then if you don't find all generated classes, please **update your project** from the specific pom.xml of that module

### Usage

For REST services just use your preferred HTTP tool like [SoapUI](https://www.soapui.org/)

You can use [grpcurl](https://github.com/fullstorydev/grpcurl) to call gRPC services choosing the suitable port

```
- List services
    grpcurl --plaintext localhost:50051 list
   
- Call method
    grpcurl --plaintext localhost:50051 net.protobufs.TreeService.ListTrees
    grpcurl --plaintext -d '{"kingdom":"arborus","family":"ent","species":"sheep"}' localhost:50051 net.protobufs.TreeService.CreateTree
    grpcurl --plaintext -d '{"treeId":"59679", "kingdom":"arborus","family":"ent","species":"shepherd"}' localhost:50051 net.protobufs.TreeService.UpdateTree
    grpcurl --plaintext -d '{"treeId":"151526"}' localhost:50051 net.protobufs.TreeService.DeleteTree        
```

As the option UnframedRequests is enabled in gRPC services, you can also run [curl](https://curl.se/)

```
curl -v -H "Content-Type: application/json" -d '{}' http://localhost:50052/net.protobufs.TreeService/ListTrees
curl -v -H "Content-Type: application/json" -d '{"treeId":"7952"}' http://localhost:50052/net.protobufs.TreeService/GetTree
curl -v -H "Content-Type: application/json" -d '{"kingdom":"arborus","family":"ent","species":"sheep"}' http://localhost:50052/net.protobufs.TreeService/CreateTree
```

HTTP/JSON to gRPC transcoding is explicitly enabled using .enableHttpJsonTranscoding(). This allows the server to convert HTTP/JSON requests into gRPC requests and vice versa. This option is currently developed in **grpc-server-transcoding** and **grpc-server-reactive-transcoding** modules.

```
curl -v http://localhost:50053/api/v1/trees
curl -v http://localhost:50053/api/v1/trees/465984
curl -v -H "Content-Type: application/json" -d '{"kingdom":"arborus","family":"ent","species":"wild"}' http://localhost:50053/api/v1/trees
curl -v -H "Content-Type: application/json" -d '{"treeId":"611605","kingdom":"arborus","family":"ent","species":"oak"}' http://localhost:50053/api/v1/trees/611605
curl -v -X "DELETE" http://localhost:50053/api/v1/treess/339919
```

It is possible to use a browser to call a service using the documentation feature and the correct port shown in the Components section

```
http://localhost:8091/internal/docs
```

There is a ready Eureka server necessary to check the compatibility with discovery services. The microservice **grpc-service-multi-instances** registers itself on Eureka and sends the gRPC number port. On the other hand, **grpc-client-service-multi-instance** exposes its endpoint but it doesn's have its own database, instead it uses a gRPC stub client to call the methods in grpc-service-multi-instances. Before doing so, it looks up the suitable hostname and port on the eureka-discovery microservice. So if you want to test this proof of concept (PoC), you should run the three components and call the port of grpc-client-service-multi-instance. In this case, It is used the dependency eureka client from Spring framework.

Armeria has its own eureka dependecy, so I have created the same test environment but using this dependency. The microservicies are **grpc-service-multi-instances-arm** and **grpc-client-service-multi-instance-arm**. You could use the same Eureka server to check both of them.

---

[![Java](https://badgen.net/static/JavaSE/21/orange)](https://www.java.com/es/)
[![Maven](https://badgen.net/badge/icon/maven?icon=maven&label&color=red)](https://https://maven.apache.org/)
[![Spring](https://img.shields.io/badge/spring-blue?logo=Spring&logoColor=white)](https://spring.io)
[![GitHub](https://badgen.net/badge/icon/github?icon=github&label)](https://github.com)
[![Eclipse](https://badgen.net/badge/icon/eclipse?icon=eclipse&label)](https://https://eclipse.org/)
[![Armeria](https://badgen.net/static/Armeria/1.6/black)](https://www.java.com/es/)
[![GPLv3 license](https://badgen.net/static/License/GPLv3/blue)](https://choosealicense.com/licenses/gpl-3.0/)
