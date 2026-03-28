# DentalGo-Mobile

DentalGo is a comprehensive Android application for seamless dental care management. It provides features for user registration, authentication, profile management, and appointment tracking.

**Source Code Repository:** [https://github.com/VinceAstly/DentalGo-Mobile](https://github.com/VinceAstly/DentalGo-Mobile)

---

## 📸 Screenshots

To view the UI of the application, please refer to the following screenshots:

### Register
<img width="412" height="890" alt="Screenshot 2026-03-28 102245" src="https://github.com/user-attachments/assets/d92dd38e-7ce3-41c9-aeb0-0dc594276aca" />


### Login
<img width="404" height="868" alt="Screenshot 2026-03-28 102315" src="https://github.com/user-attachments/assets/568c9f15-e330-41e8-94f8-a0e7319d4005" />


### Dashboard


### Profile


### Update Profile


### Change Password


---

## 🔌 API Documentation

This application integrates with a backend API (Spring Boot + MongoDB) using Retrofit. Below is the documentation for the endpoints used.

### Base URL
By default, the application is configured to point to a local Spring Boot server.
*   **Android Emulator:** `http://10.0.2.2:8080/`
*   **Physical Device:** `http://<your-PC-local-IP>:8080/`
*   **Production:** `https://your-api-url.com/`


### Endpoints

All protected endpoints require an `Authorization` header with a Bearer token:
`Authorization: Bearer <token>`

#### 1. Register User
*   **URL:** `/api/register`
*   **Method:** `POST`
*   **Auth Required:** No
*   **Request Body (`application/json`):**
    ```json
    {
      "name": "John Doe",
      "email": "john@example.com",
      "password": "password123",
      "password_confirmation": "password123",
      "phone": "09123456789"
    }
    ```

#### 2. Login User
*   **URL:** `/api/login`
*   **Method:** `POST`
*   **Auth Required:** No
*   **Request Body (`application/json`):**
    ```json
    {
      "email": "john@example.com",
      "password": "password123"
    }
    ```

#### 3. Get Dashboard Data
*   **URL:** `/api/dashboard`
*   **Method:** `GET`
*   **Auth Required:** Yes

#### 4. Get User Profile
*   **URL:** `/api/profile`
*   **Method:** `GET`
*   **Auth Required:** Yes

#### 5. Update Profile
*   **URL:** `/api/profile`
*   **Method:** `PUT`
*   **Auth Required:** Yes
*   **Request Body (`application/json`):**
    ```json
    {
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "09123456789",
      "bio": "New bio here"
    }
    ```

#### 6. Change Password
*   **URL:** `/api/change-password`
*   **Method:** `PUT`
*   **Auth Required:** Yes
*   **Request Body (`application/json`):**
    ```json
    {
      "current_password": "oldpassword",
      "new_password": "newpassword123",
      "new_password_confirmation": "newpassword123"
    }
    ```

---

## ⚙️ Tech Stack
*   **Kotlin**
*   **Jetpack Compose**
*   **Retrofit 2** (Networking)
*   **OkHttp3** (Logging Interceptor)
*   **Coroutines & Flow** (Asynchronous Operations and State Management)
*   **Jetpack Navigation**
