# Kalahandi Connect

A responsive district news portal built with Java 17, Spring Boot, Thymeleaf, Spring Security, MongoDB, and Cloudinary.

## Workflow

1. Public visitors submit a news title, category, location, description, required Indian mobile number and required email, plus up to five optional photographs.
2. Submission is saved as `PENDING_APPROVAL`. Contact flags are false by default.
3. The server generates separate reference codes (`KAL-M-####` and `KAL-E-####`) for the admin's manual review. These codes are **not OTPs** and are not sent to the submitter.
4. An authenticated admin opens `/admin`, reviews the details, and saves the mobile and email verification checkboxes independently.
5. Server-side approval refuses to publish until both flags are true. Only `PUBLISHED` items with both verification flags true are displayed publicly.
6. Verification actor and timestamp are persisted. Approval/rejection actor and timestamp are also persisted. Revoking either verification on a published item automatically returns it to pending and removes it from public pages.

## Requirements

- JDK 17+
- Maven 3.9+
- MongoDB local instance or MongoDB Atlas
- Cloudinary account for image uploads

## Configure and run on Windows PowerShell

```powershell
$env:MONGODB_URI="mongodb://localhost:27017/kalahandi_connect"
$env:APP_ADMIN_USERNAME="admin"
$env:APP_ADMIN_PASSWORD="Use-A-Strong-Password-Here-123!"
$env:CLOUDINARY_CLOUD_NAME="your-cloud-name"
$env:CLOUDINARY_API_KEY="your-api-key"
$env:CLOUDINARY_API_SECRET="your-api-secret"
mvn spring-boot:run
```

Or copy `.env.example` values into environment variables in your IDE/Render dashboard. Spring Boot does not automatically load `.env` files. Do not commit real credentials.

Open:
- Public homepage: http://localhost:8080/
- Submit news: http://localhost:8080/submit-news
- Admin login: http://localhost:8080/admin/login

The admin user is provisioned in Spring Security from `APP_ADMIN_USERNAME` and `APP_ADMIN_PASSWORD` at startup; the password is BCrypt-hashed. Startup intentionally fails if the admin password is missing or shorter than 12 characters.

## Render deployment

Create a Web Service from this repository, runtime Docker or Java buildpack. For a Maven service, build command `mvn clean package -DskipTests`, start command `java -jar target/kalahandi-connect-0.1.0.jar`. Add environment variables `MONGODB_URI`, `APP_ADMIN_USERNAME`, `APP_ADMIN_PASSWORD`, `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, and `CLOUDINARY_API_SECRET`. Configure MongoDB Atlas network access for the deployed service. Use a unique strong admin password.

## Security / operations notes

- Contact details are only shown in the authenticated admin area, not public story pages.
- Admin checkbox verification records an administrator's manual assessment, not proof of contact ownership. For actual ownership proof, integrate SMS/email OTP delivery and validation later.
- Codes are admin reference codes only. They are not used as authentication secrets.
- Upload accepts JPG, PNG, and WEBP up to 5 MB each, max 5 images. Configure Cloudinary upload restrictions and rate limiting/WAF for production before broad public launch.
- Add a durable multi-admin account store, CAPTCHA/rate limiting, moderation reporting, retention policy, and formal audit event collection before high-volume production use.
