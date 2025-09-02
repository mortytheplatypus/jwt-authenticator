# JWT Authenticator

## Overview
JWT Authenticator is a full-stack web application template that provides secure user registration and authentication features using JSON Web Tokens (JWT). The application consists of a React frontend and a Spring Boot backend, allowing users to create accounts, log in, and manage their sessions with JWT-based authentication.

## Table of Contents
- [JWT Authenticator](#jwt-authenticator)
  - [Overview](#overview)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Technologies](#technologies)
  - [Prerequisites](#prerequisites)
  - [Project Structure](#project-structure)
  - [Frontend Setup](#frontend-setup)
  - [Backend Setup](#backend-setup)
  - [API Endpoints](#api-endpoints)
  - [Usage](#usage)

## Features
- User registration with validation
- User login and JWT-based authentication
- Protected routes and session management
- Secure JWT token handling with expiration

## Technologies
- **Frontend**: React 18, React Router DOM, React Bootstrap, Axios, React Toastify
- **Backend**: Spring Boot 3.3.4, Java 17, Spring Security, Spring Data JPA
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)

## Prerequisites
Before running this application, ensure you have the following installed:
- **Node.js** (version 16 or higher)
- **npm** (comes with Node.js)
- **Java 17** or higher
- **Maven** (for building the backend)
- **PostgreSQL** database server

## Project Structure
```
jwt-authenticator/
├── frontend/          # React frontend application
├── backend/           # Spring Boot backend application
└── readme.md          # This file
```

## Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. The frontend will run on `http://localhost:3000`

## Backend Setup
1. Set up PostgreSQL database:
   - Create a database named `jwt-authenticator-db`
   - Run the following SQL to create the required schema:
   ```sql
   -- Create the users table
   CREATE TABLE users (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       username VARCHAR(255) NOT NULL UNIQUE,
       name VARCHAR(255) NOT NULL,
       email VARCHAR(255) NOT NULL UNIQUE,
       password VARCHAR(255) NOT NULL,
       role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'USER'))
   );
   ```
   - Update database credentials in `backend/src/main/resources/application.yml` if needed

2. Navigate to the backend directory:
   ```bash
   cd backend
   ```

3. Build the project:
   ```bash
   ./mvnw clean install
   ```

4. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. The backend will run on `http://localhost:8888`

## API Endpoints
- **POST /auth/register**: Register a new user
- **POST /auth/login**: Log in an existing user

## Usage
1. Start both the backend and frontend servers following the setup instructions above
2. Navigate to `http://localhost:3000` in your browser (Should be redirected to `/login`)
3. Log in with existing credentials or register a new account. (Option in `/login` page that redirects to `/register`)
4. Upon successful authentication, you'll be redirected to the protected home page
5. Use the logout button to end your session

## Configuration
The application uses the following default configurations:
- **Frontend Port**: 3000
- **Backend Port**: 8888
- **Database**: PostgreSQL on localhost:5432
- **JWT Expiration**: 24 hours (86400000 ms)

Update the `application.yml` file in the backend to modify these settings as needed.



