# 🚀 Sistema de Gestão de Estoque - Backend

<p align="center">
  <strong>API REST para gerenciamento de estoque, autenticação de usuários, produtos, categorias, fornecedores e movimentações.</strong>
</p>

<p align="center">
  <a href="https://sistema-gestao-estoque-two.vercel.app">🌐 Demo Online</a>
  •
  <a href="https://github.com/matheus-samuel-dev/sistema-gestao-estoque">💻 Frontend</a>
  •
  <a href="https://github.com/matheus-samuel-dev/sistema-gestao-estoque-api">⚙️ Backend</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk">
  <img src="https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot">
  <img src="https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity">
  <img src="https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql">
  <img src="https://img.shields.io/badge/Railway-Deploy-0B0D0E?style=for-the-badge&logo=railway">
</p>

---

## 🏗 Arquitetura

```text
React + Vite
      ↓
Spring Boot REST API
      ↓
PostgreSQL
```

O backend segue uma arquitetura em camadas, separando responsabilidades entre controllers, services, repositories, DTOs, entidades e configurações de segurança.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

---

## ✨ Principais Funcionalidades

### 🔐 Autenticação e Segurança

* Cadastro de usuários
* Login com JWT
* Recuperação de senha
* Criptografia de senhas com BCrypt
* Rotas públicas e protegidas
* Filtro de autenticação com Spring Security

### 📦 Produtos

* Cadastro de produtos
* Edição de produtos
* Exclusão de produtos
* Consulta de produtos
* Controle de estoque
* Código automático de produto
* Importação de produtos por CSV/XLSX
* Anexos de imagens e documentos

### 🏷 Categorias e Fornecedores

* CRUD de categorias
* CRUD de fornecedores
* Organização de produtos por categoria
* Associação de fornecedores aos produtos

### 📈 Movimentações de Estoque

* Entrada de estoque
* Saída de estoque
* Ajuste
* Transferência
* Devolução
* Histórico de movimentações
* Atualização automática do saldo

### 📊 Dashboard

* Total de produtos
* Total de categorias
* Produtos sem estoque
* Produtos com estoque baixo
* Entradas e saídas do mês
* Últimas movimentações

---

## 📸 Preview

### 📊 Dashboard

<p align="center">
  <img src="./docs/dashboard.png" width="100%">
</p>

### 🔍 Principais Telas

| 🔐 Login              | 📈 Movimentações              |
| --------------------- | ----------------------------- |
| ![](./docs/login.png) | ![](./docs/movimentacoes.png) |

| 📦 Produtos              | 📊 Dashboard              |
| ------------------------ | ------------------------- |
| ![](./docs/produtos.png) | ![](./docs/dashboard.png) |

---

## 🛠 Tecnologias Utilizadas

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* JWT
* BCrypt
* Maven

### Banco de Dados

* PostgreSQL

### Documentação e Testes

* Swagger / OpenAPI
* JUnit

### Deploy

* Railway

### Integração

* React
* Vite
* Axios
* Vercel

---

## 📂 Estrutura do Projeto

```text
src
├── main
│   ├── java
│   │   └── com
│   │       └── matheus
│   │           └── estoque
│   │               ├── config
│   │               ├── security
│   │               ├── auth
│   │               ├── user
│   │               ├── product
│   │               ├── category
│   │               ├── supplier
│   │               ├── stockmovement
│   │               ├── attachment
│   │               ├── settings
│   │               └── exception
│   └── resources
└── test
```

---

## 🔐 Segurança

A API utiliza autenticação baseada em JWT.

```text
Usuário faz login
        ↓
Backend valida credenciais
        ↓
API retorna token JWT
        ↓
Frontend envia o token nas próximas requisições
        ↓
Backend valida o token e libera acesso aos recursos protegidos
```

Recursos aplicados:

* Spring Security
* JWT Authentication
* BCrypt Password Encoder
* Stateless Session
* CORS configurado para integração com o frontend
* Validação de usuário autenticado nas operações

---

## 🔗 Principais Endpoints

### Autenticação

```http
POST /auth/register
POST /auth/login
POST /auth/forgot-password
POST /auth/reset-password
```

### Produtos

```http
GET /products
POST /products
PUT /products/{id}
DELETE /products/{id}
POST /products/import
```

### Categorias

```http
GET /categories
POST /categories
PUT /categories/{id}
DELETE /categories/{id}
```

### Fornecedores

```http
GET /suppliers
POST /suppliers
PUT /suppliers/{id}
DELETE /suppliers/{id}
```

### Movimentações

```http
GET /stock-movements
POST /stock-movements
DELETE /stock-movements/{id}
```

### Anexos

```http
POST /attachments/products/{id}
POST /attachments/movements/{id}
GET /attachments/products/{id}
GET /attachments/movements/{id}
GET /attachments/{id}/download
DELETE /attachments/{id}
```

---

## ⚙️ Executando Localmente

### Pré-requisitos

* Java 21
* Maven
* PostgreSQL
* Frontend em execução

### Clonar o repositório

```bash
git clone https://github.com/matheus-samuel-dev/sistema-gestao-estoque-api.git
cd sistema-gestao-estoque-api
```

### Configurar variáveis de ambiente

Crie ou configure as variáveis necessárias para conexão com o banco e autenticação.

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/stock_management
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta
```

### Executar o backend

```bash
mvn spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

---

## 🚀 Deploy

O backend foi preparado para deploy no Railway com PostgreSQL em nuvem.

O frontend está publicado na Vercel:

```text
https://sistema-gestao-estoque-two.vercel.app
```

---

## 🚀 Próximas Evoluções

* [ ] Controle avançado de permissões ADMIN e USER
* [ ] Testes unitários com JUnit
* [ ] Testes de integração
* [ ] Pipeline CI/CD
* [ ] Cache com Redis
* [ ] Logs centralizados
* [ ] Auditoria de movimentações
* [ ] Monitoramento e métricas
* [ ] Versionamento de API
* [ ] Relatórios avançados
* [ ] Docker e Docker Compose

---

## 👨‍💻 Autor

### Matheus Samuel Baena Soares

Desenvolvedor de Software com foco em Java, Spring Boot e desenvolvimento de aplicações web.

🔗 Portfólio: https://matheus-samuel-dev.github.io/Portfolio/

🔗 LinkedIn: https://linkedin.com/in/matheus-samuel-dev/

---

⭐ Se gostou do projeto, considere deixar uma estrela no repositório.
