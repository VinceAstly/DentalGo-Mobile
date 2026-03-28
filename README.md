# DentalGo-Mobile

DentalGo is a comprehensive Android application for seamless dental care management. It provides features for user registration, authentication, profile management, and appointment tracking.

**Source Code Repository:** [https://github.com/VinceAstly/DentalGo-Mobile](https://github.com/VinceAstly/DentalGo-Mobile)

---

## 📸 Screenshots

To view the UI of the application, please refer to the following screenshots:

### Register
<!-- TODO: Add your Register screenshot here -->
![Register](screenshots/register.png)

### Login
<!-- TODO: Add your Login screenshot here -->
![Login](screenshots/login.png)

### Dashboard
<!-- TODO: Add your Dashboard screenshot here -->
![Dashboard](screenshots/dashboard.png)

### Profile
<!-- TODO: Add your Profile screenshot here -->
![Profile](screenshots/profile.png)

### Update Profile
<!-- TODO: Add your Update Profile screenshot here -->
![Update Profile](screenshots/update_profile.png)

### Change Password
<!-- TODO: Add your Change Password screenshot here -->
![Change Password](screenshots/change_password.png)

---

## 🔌 API Documentation

This application integrates with a backend API (Spring Boot + MongoDB) using Retrofit. Below is the documentation for the endpoints used.

### Base URL
By default, the application is configured to point to a local Spring Boot server.
*   **Android Emulator:** `http://10.0.2.2:8080/`
*   **Physical Device:** `http://<your-PC-local-IP>:8080/`
*   **Production:** `https://your-api-url.com/`

*(Configure this in `app/src/main/java/com/dentalgo/app/data/api/RetrofitClient.kt`)*

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