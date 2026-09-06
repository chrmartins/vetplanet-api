# Build em dois estágios: o JDK e o Gradle não vão para a imagem final.
#
# Não existe Dockerfile "de desenvolvimento" — local sobe com `./gradlew
# bootRun`, que já levanta o Postgres do compose. Este arquivo serve ao
# deploy, e é o que torna o projeto portátil entre plataformas em vez de
# depender do autodetect de uma delas.

FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build

# O wrapper e os descritores vêm primeiro, sozinhos: enquanto eles não mudam,
# a camada de download de dependências fica em cache e o build não repete a
# parte mais lenta a cada commit de código.
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet || true

COPY src ./src
# `-x test` de propósito: os testes usam Testcontainers, que precisa de um
# Docker acessível — coisa que não existe dentro do build de imagem. Quem roda
# a suíte é a CI (.github/workflows/ci.yml), antes de qualquer deploy.
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Usuário sem privilégio: se a aplicação for comprometida, o atacante não
# começa como root dentro do contêiner.
RUN addgroup -S vetplanet && adduser -S vetplanet -G vetplanet
USER vetplanet

COPY --from=build /build/build/libs/*.jar app.jar

# A porta real vem de PORTA_HTTP (ver application.yml); plataformas de deploy
# costumam injetar a delas. Este EXPOSE é documentação, não configuração.
EXPOSE 8080

# Sem perfil ativo: `dev` precisa ser pedido explicitamente, então uma imagem
# de produção nunca semeia credencial de desenvolvimento nem expõe o Swagger.
ENTRYPOINT ["java", "-jar", "app.jar"]
