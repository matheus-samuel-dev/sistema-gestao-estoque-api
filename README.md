# Stock Management API

API REST do Sistema de Estoque, criada com Java 21, Spring Boot, Spring Security, JWT, Spring Data JPA e PostgreSQL.

## Recursos

- Autenticação e autorização com JWT.
- CRUD de produtos e categorias.
- Controle de movimentações de estoque.
- Entrada aumenta o estoque do produto.
- Saída diminui o estoque do produto.
- Saída maior que o estoque disponível é bloqueada.
- Exclusão de movimentação recalcula o saldo do produto.
- Endpoints de dashboard e relatórios.
- Swagger/OpenAPI disponível quando a aplicação está rodando.

## Variáveis de Ambiente

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/stock_management
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
SECURITY_LOG_LEVEL=INFO
CORS_ALLOWED_ORIGINS=http://localhost:*
```

## Rodando Localmente

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

## Build

```bash
./mvnw clean package -DskipTests
```

No Windows:

```bash
mvnw.cmd clean package -DskipTests
```

## Deploy

Para deploy em Render, Railway, Fly.io ou VPS, configure as variáveis de ambiente acima e use:

```bash
java -jar target/stock-management-0.0.1-SNAPSHOT.jar
```
