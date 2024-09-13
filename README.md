## Paydai Server

A Spring Boot application.

[Demo](http://api.itspaydai.com/swagger-ui/index.html)

## Features
- Authentication and authorization
- Stripe Account creation and linking
- Invite Functionality
- Onboarding Feature
- Invoice Feature
- Refund Feature
- Workspace Feature
- Transaction Feature
- etc.

## Installation
- Install [Java 17](https://www.oracle.com/ng/java/technologies/downloads/) on your machine
- Install [Maven 3.9](https://maven.apache.org/download.cgi) locally
- Install [PostgreSQL](https://www.enterprisedb.com/)
- Install [Payday Server](https://github.com/paydai-tech/backend.git)
- create a ```.env``` file and follow the ```.env.example``` file in the project
- Run command ```mvn clean install``` and then ```mvn spring-boot:run```

### API Documentation
```http://localhost:your-port/swagger-ui.html```


### Project Structure Example

This project uses the Domain Driven Design architecture as shown in the example below.

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── paydai
│   │           └── api
│   │               ├── application
│   │               │   ├── auth
│   │               │   │   └── AuthServiceImpl.java
│   │               │   └── merchant
│   │               │       └── MerchantServiceImpl.java
│   │               ├── domain
│   │               │   ├── exception
│   │               │   │   ├── ApiException.java
│   │               │   │   ├── ApiRequestException.java
│   │               │   │   └── NotFoundException.java
│   │               │   ├── model
│   │               │   │   └── AuthModel.java
│   │               │   └── repository
│   │               │       └── AuthRepository.java
│   │               ├── infrastructure
│   │               │   ├── config
│   │               │   │   ├── AppConfig.java
│   │               │   │   ├── SecurityBeanConfig.java
│   │               │   │   └── WebSecurityBeanConfig.java
│   │               │   ├── external
│   │               │   │   └── Stripe.java
│   │               │   └── persistence
│   │               │       └── AuthRepositoryImpl.java
│   │               └── presentation
│   │                   ├── controller
│   │                   │   └── AuthController.java
│   │                   │   └── impl
│   │                   │       └── AuthControllerImpl.java
│   │                   ├── dto
│   │                   │   └── AuthDto.java
│   │                   └── responses
│   │                       └── JapiResponse.java
│   └── resources
│       ├── application.yml
│       ├── static
│       └── template
└── test
    └── java
        └── com
            └── paydai
                └── api
                    ├── application
                    │   └── auth
                    │       └── AuthServiceImplTest.java
                    ├── domain
                    │   └── repository
                    │       └── AuthRepositoryTest.java
                    └── presentation
                        └── controller
                            └── AuthControllerTest.java


```