# 📝 ToDoApp

ToDoApp to prosta aplikacja mobilna stworzona w Kotlinie (Jetpack Compose), z backendem opartym o Spring Boot i bazą danych H2. Aplikacja umożliwia logowanie przez Google oraz zarządzanie listą zadań użytkownika.

---

## 🔧 Technologie

**Frontend (Android):**
- Kotlin
- Jetpack Compose
- Google One Tap Sign-In
- Firebase Auth
- OkHttp

**Backend (Java):**
- Spring Boot
- Spring Security (OAuth2 Resource Server)
- H2 Database
- Spring Data JPA

---

## ✅ Funkcje

- ✅ Logowanie przez Google One Tap (Firebase)
- ✅ Weryfikacja tokena JWT na backendzie
- ✅ Zapis danych użytkownika do bazy (H2)
- ✅ Ekran główny z listą zadań (ToDo)
- ✅ Przechowywanie zadań użytkownika (powiązanych z kontem)
- ✅ Wbudowana konsola H2 do testowania zapytań

---

## ▶️ Uruchamianie

### 🔽 Android
1. Otwórz projekt w Android Studio.
2. Upewnij się, że masz `google-services.json` i poprawny `client_id`.
3. Uruchom aplikację na emulatorze lub telefonie.
4. Backend nasłuchuje pod `http://10.0.2.2:8080`.

### 💻 Backend
1. Przejdź do katalogu `java_backend`.
2. Uruchom aplikację Spring Boot (`BackendApplication`).
3. Konsola H2: `http://localhost:8080/h2-console`  
   - JDBC URL: `jdbc:h2:mem:todoappdb`

---

## 🔐 Logowanie

- Logowanie odbywa się przez Google One Tap.
- Token JWT jest przesyłany do backendu w nagłówku `Authorization: Bearer <token>`.
- Backend weryfikuje token przez `spring-security-oauth2-resource-server`.



