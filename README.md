
<div align="center">

# 🚀 Sistema de Gestão de Estoque - Backend

API REST desenvolvida com Java e Spring Boot para gerenciamento de estoque, autenticação de usuários e persistência de dados em PostgreSQL.

<br>


![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge\&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge\&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring_Security-Authentication-6DB33F?style=for-the-badge\&logo=springsecurity)
![JWT](https://img.shields.io/badge/JWT-Security-black?style=for-the-badge\&logo=jsonwebtokens)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-Persistence-6DB33F?style=for-the-badge)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge\&logo=hibernate)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge\&logo=postgresql)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge\&logo=swagger)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge\&logo=apachemaven)
![Railway](https://img.shields.io/badge/Railway-Deploy-0B0D0E?style=for-the-badge\&logo=railway)

</div>

---

## 📖 Sobre o Projeto

Esta API REST foi desenvolvida para fornecer toda a camada de negócio do Sistema de Gestão de Estoque.

A aplicação é responsável pela autenticação de usuários, controle de acesso, gerenciamento de produtos e categorias, além da persistência dos dados em banco PostgreSQL hospedado em nuvem.

O projeto foi construído utilizando boas práticas de desenvolvimento com Spring Boot, arquitetura em camadas e autenticação baseada em JWT.

---

## ✨ Funcionalidades

### Autenticação

* Cadastro de usuários
* Login com JWT
* Criptografia de senhas com BCrypt
* Rotas protegidas com Spring Security

### Produtos

* Cadastro de produtos
* Atualização de produtos
* Exclusão de produtos
* Listagem de produtos

### Categorias

* Cadastro de categorias
* Atualização de categorias
* Exclusão de categorias
* Listagem de categorias

### Infraestrutura

* Integração com PostgreSQL
* Persistência com JPA/Hibernate
* Documentação via Swagger
* Deploy em ambiente cloud

---

## 🏗 Arquitetura

A aplicação segue uma arquitetura em camadas para garantir organização, manutenção e escalabilidade.


Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
=======

DATABASE_URL=jdbc:postgresql://localhost:5432/stock_management
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false
SECURITY_LOG_LEVEL=INFO
CORS_ALLOWED_ORIGINS=http://localhost:*
RESEND_API_KEY=re_sua_chave_resend
MAIL_FROM=Sistema de Estoque <nao-responda@seu-dominio.com>
FRONTEND_RESET_PASSWORD_URL=http://localhost:5173/reset-password



Estrutura principal:


src
├── config
├── security
├── auth
├── user
├── product
├── category
├── dto
├── repository
├── service
└── exception


---

## 🔐 Segurança

A autenticação da aplicação é baseada em JWT (JSON Web Token).

Fluxo de autenticação:


Usuário
    ↓
Login
    ↓
JWT
    ↓
Requisições Autenticadas


Recursos implementados:

* Spring Security
* JWT Authentication
* Password Encoder (BCrypt)
* Filtros de autenticação
* Rotas públicas e protegidas

---

## 🛠 Tecnologias Utilizadas

### Backend

* Java 21
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* Maven

### Banco de Dados

* PostgreSQL

### Documentação

* Swagger / OpenAPI

### Deploy

* Railway

---

## 📂 Integração com Frontend

Frontend da aplicação:

https://github.com/matheus-samuel-dev/sistema-gestao-estoque

Aplicação online:

https://sistema-gestao-estoque-two.vercel.app

---

## ⚙️ Executando Localmente


git clone https://github.com/matheus-samuel-dev/sistema-gestao-estoque-api.git

cd sistema-gestao-estoque-api

mvn spring-boot:run


A aplicação será iniciada em:


http://localhost:8080


---

## 🔮 Melhorias Futuras

* [ ] Controle de permissões por perfis (ADMIN, USER)
* [ ] Refresh Token
* [ ] Testes unitários com JUnit
* [ ] Testes de integração
* [ ] Dockerização da aplicação
* [ ] Pipeline CI/CD
* [ ] Logs centralizados
* [ ] Monitoramento e métricas
* [ ] Auditoria de movimentações
* [ ] Cache com Redis
* [ ] Versionamento da API

---

## 🌐 Contato

### Portfólio

https://matheus-samuel-dev.github.io/Portfolio/

### LinkedIn

https://www.linkedin.com/in/matheus-samuel-dev/

---

## 👨‍💻 Autor

**Matheus Samuel Baena Soares**

Desenvolvedor Full Stack em formação, com foco em Java, Spring Boot e desenvolvimento de aplicações web.

---

⭐ Se gostou do projeto, considere deixar uma estrela no repositório.
