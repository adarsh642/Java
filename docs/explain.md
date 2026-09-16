# Java Feedback Application Explanation

## 1. Project structure

```text
Java/
├── backend/
│   ├── Admin.java
│   ├── db/
│   │   ├── DatabaseConnection.java
│   │   ├── FeedbackRepository.java
│   │   ├── PasswordUtil.java
│   │   └── UserRepository.java
│   ├── user/
│   │   ├── Feedback.java
│   │   ├── Register.java
│   │   └── login.java
│   ├── lib/
│   │   └── mysql-connector-j-9.4.0.jar
│   └── classes/
└── db/
    └── schema.sql
```

`backend/classes` contains compiled Java `.class` files. `backend/lib` contains the MySQL JDBC driver. The source code is inside `backend` and the database setup is inside `db`.

## 2. Application entry point

The application starts with `login.java`.

```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new login().setVisible(true));
}
```

`SwingUtilities.invokeLater` starts the Swing user interface on the correct event thread. The login window is shown first. The user can then open the registration page or log in.

## 3. Database connection

`backend/db/DatabaseConnection.java` creates JDBC connections to MySQL.

The default connection is:

```text
jdbc:mysql://localhost:3306/myapp
```

The username defaults to `root`. The password is read from the environment variable `DB_PASSWORD`.

```java
Connection connection = DatabaseConnection.getConnection();
```

The MySQL driver is required at runtime. It is loaded from `backend/lib` through the classpath.

## 4. Database schema

`db/schema.sql` creates the `myapp` database and two tables.

### user table

Stores registered accounts:

- `ID`: Automatically generated primary key.
- `USERNAME`: Unique login name.
- `EMAIL`: Unique email address.
- `PASSWORD`: Salted PBKDF2 password hash.
- `USER_ROLE`: Either `USER` or `ADMIN`.

The script inserts the default administrator:

```text
Username: admin
Password: admin123
```

The database stores only the hash of `admin123`, not the plaintext password.

### feedback table

Stores feedback records:

- `ID`: Feedback primary key.
- `USERNAME`: User who submitted the feedback.
- `FEEDBACK`: Feedback text.
- `CREATED_AT`: Creation time.
- `UPDATED_AT`: Last update time.

The foreign key connects each feedback record to a user. When a user is deleted, that user's feedback is also deleted.

## 5. Password hashing

`backend/db/PasswordUtil.java` protects passwords with PBKDF2-HMAC-SHA256.

When a user registers:

1. A random 16-byte salt is generated.
2. PBKDF2 derives a 256-bit hash using 120,000 iterations.
3. The database value is stored in this format:

```text
iterations$salt$hash
```

Each registration gets a different salt, even when two users choose the same password.

During login, the stored iteration count and salt are read, the entered password is hashed again, and the two hashes are compared using a constant-time comparison.

## 6. Registration flow

The registration flow is implemented in `backend/user/Register.java`.

1. The user enters username, email, password, and confirmation password.
2. The form validates that all fields are filled.
3. The two passwords must match.
4. `UserRepository.register(...)` hashes the password.
5. A prepared SQL statement inserts the new user.
6. The user is returned to the login screen.

Prepared statements use `?` parameters, which helps prevent SQL injection.

## 7. Login flow

The login flow is implemented in `backend/user/login.java`.

1. The user enters a username and password.
2. `UserRepository.authenticate(...)` finds the account by username.
3. `PasswordUtil.verifyPassword(...)` checks the entered password against the stored hash.
4. The account role is read.
5. Admin users open `Admin.java`.
6. Normal users open `Feedback.java`.

Role routing is handled by the account returned from `UserRepository`.

## 8. User feedback flow

The user feedback screen is implemented in `backend/user/Feedback.java`.

When the screen opens, it calls:

```java
FeedbackRepository.findLatestForUser(username)
```

If previous feedback exists, it is loaded into the text area.

### Add feedback

If the user has no feedback record, Submit calls:

```java
FeedbackRepository.add(username, text)
```

The message shown is:

```text
Feedback added successfully!
```

### Edit feedback

If the user already has feedback, Submit calls:

```java
FeedbackRepository.update(feedbackId, text)
```

The message shown is:

```text
Feedback updated successfully!
```

The feedback is stored in MySQL, so it remains available after the application closes.

## 9. Admin dashboard

The admin screen is implemented in `backend/Admin.java`.

When it opens, it loads all feedback through:

```java
FeedbackRepository.findAll()
```

The dashboard supports CRUD operations:

- **Create:** Add new feedback.
- **Read:** View feedback and load all records.
- **Update:** Edit feedback text.
- **Delete:** Delete a feedback record.

Each operation updates both the Swing table and the MySQL database.

## 10. Feedback repository

`backend/db/FeedbackRepository.java` contains the database operations for feedback:

```text
findLatestForUser(username)
add(username, text)
update(id, text)
delete(id)
findAll()
```

The nested `Feedback` class transfers database values to the Swing screens.

## 11. User repository

`backend/db/UserRepository.java` contains account operations:

```text
register(username, email, password)
authenticate(username, password)
```

The nested `Account` class stores the authenticated username and role.

## 12. Compile and run

Run these commands from the project root:

```powershell
cd C:\Users\Adarsh\OneDrive\Desktop\Java

Remove-Item backend\classes -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory backend\classes | Out-Null

javac -cp "backend\lib\*" -d backend\classes `
backend\Admin.java `
backend\db\DatabaseConnection.java `
backend\db\PasswordUtil.java `
backend\db\UserRepository.java `
backend\db\FeedbackRepository.java `
backend\user\Feedback.java `
backend\user\login.java `
backend\user\Register.java

$env:DB_PASSWORD = "Adarsh@123"
java -cp "backend\classes;backend\lib\*" login
```

Do not compile and run `Feedback.java` alone. It imports classes from the `db` package and must be compiled together with the rest of the project.

## 13. Normal test sequence

1. Start the application with `login`.
2. Click **Sign Up**.
3. Register a normal user.
4. Log in with that user.
5. Add feedback and confirm the added message.
6. Edit the feedback and confirm the updated message.
7. Restart the application and log in again to confirm persistence.
8. Log in as `admin` with password `admin123`.
9. Confirm the admin can view, add, edit, and delete feedback.
