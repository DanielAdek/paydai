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