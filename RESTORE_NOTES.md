# Restore Notes

## Project summary
Hospital Management System — a Java Swing desktop app (NetBeans-generated forms, GroupLayout/AbsoluteLayout) backed by MySQL. Manages login/registration, doctor records, patient registration, room admission/discharge, billing, and service tracking. Originally built against Java 7 / MySQL Connector 5.1 / NetBeans 8.2.

## Stack
- Java (Swing, AWT) — compiled at source/target 1.8, runs fine on JDK 17
- MySQL 8.0 (originally MySQL 5.7)
- Build: NetBeans/Ant project (`nbproject/`, `build.xml`) — Ant is not installed on this machine, so the project is compiled/run directly via `javac`/`java` (see below); the Ant build files were still repaired so the project opens correctly in NetBeans if needed.

## How to install, build, and run (fresh machine)

1. Install a JDK (8+) and MySQL Server 8.0, both on PATH.
2. Create the database and a dedicated app user (run as a MySQL admin):
   ```sql
   CREATE USER IF NOT EXISTS 'hospital_app'@'localhost' IDENTIFIED BY 'your_password_here';
   CREATE DATABASE IF NOT EXISTS hospital_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   GRANT ALL PRIVILEGES ON hospital_management.* TO 'hospital_app'@'localhost';
   FLUSH PRIVILEGES;
   ```
3. Import the schema/seed data:
   ```
   mysql -u hospital_app -p hospital_management < hp_mgmt_db.sql
   ```
4. Copy `db.properties.example` to `db.properties` (same folder as `src`, used as the working directory at runtime) and fill in your `db.user` / `db.password`. `db.properties` is gitignored — never commit real credentials.
5. Build and run: double-click/run `run_hospital.bat`, or manually:
   ```
   javac -encoding UTF-8 -cp "dist/lib/AbsoluteLayout.jar;lib/mysql-connector-j-8.0.33.jar" -d build/classes src/*.java
   java -cp "build/classes;dist/lib/AbsoluteLayout.jar;lib/mysql-connector-j-8.0.33.jar;src" login
   ```
6. Sample login (seeded in `hp_mgmt_db.sql`): username `akash`, password `123456`.

## Bugs fixed
- **nbproject/project.properties**: `file.reference.rs2xml.jar` pointed to `C:\Users\Agarwal\Downloads\...` and `C:\Users\hp pc\Downloads\...` — absolute paths from other developers' machines. Replaced with project-relative paths (`dist/lib/...`) and dropped the NetBeans-global library references (`libs.absolutelayout.classpath`, `libs.MySQLDriver.classpath`) that only resolve inside a NetBeans installation, which isn't present on this machine.
- **nbproject/private/private.properties**: same absolute-path problem (`E:\Projects for code-projects\...`), fixed the same way.
- **src/Connect.java**: used `com.mysql.cj.jdbc.Driver` (MySQL Connector/J 6+ class name) but the only connector jar in the repo was 5.1.23, which doesn't have that class — `ClassNotFoundException` on every DB call. Downloaded MySQL Connector/J 8.0.33 (compatible with the installed MySQL 8.0 server) into `lib/`.
- **src/Connect.java**: DB credentials were hardcoded (`root`/empty password). Externalized into a local `db.properties` file (gitignored) with `db.properties.example` as the template, since the empty-root-password setup doesn't exist on this machine and credentials must never be committed.
- **src/*.java**: compilation failed under default platform encoding (`unmappable character` errors in `Entry.java`, which contains UTF-8 emoji) — fixed by always compiling with `-encoding UTF-8`, matching `source.encoding=UTF-8` already declared in `project.properties`.
- **run_hospital.bat**: hardcoded `JAVA_HOME` to a JDK 8 path that doesn't exist on this machine; compiled without the MySQL/AbsoluteLayout jars on the classpath (would fail at runtime); and launched `Entry` (the Doctor Registration screen) directly instead of `login` (the actual entry point, per `main.class=login`), skipping authentication entirely. Fixed all three.
- **javac.source/target**: bumped from `1.7` to `1.8` in `project.properties` — JDK 17's `javac` still compiles source/target 7 but emits "obsolete" warnings; 8 is the minimum bump that's warning-free and fully compatible with the existing code.

## Dependency changes
- Added `lib/mysql-connector-j-8.0.33.jar` (MySQL Connector/J 8.0.33) — required for `com.mysql.cj.jdbc.Driver` and to talk to the installed MySQL 8.0 server.
- `dist/lib/AbsoluteLayout.jar` and `dist/lib/rs2xml.jar` (already present in the repo) are unchanged and still required; `rs2xml` is largely unused now (one leftover usage is already commented out in `Bill_Room.java` in favor of the project's own `DbUtilsHelper`).

## UI improvements
- Added `src/UITheme.java`: a single place that switches the app from the old Metal/default Swing look to Nimbus and applies a consistent clinical color palette (deep teal primary `#136F63`, soft background `#F6F8F8`) and font scale across every screen's buttons, labels, tables, and menus — applied once at the real app entry point (`login.java`'s `main()`), so it re-skins all ~20 screens without needing per-form rewrites.
- `login.java`: proper window title, teal primary action button, consistent label styling.
- `Main.java` and all other forms: consistent light background instead of the default grey Swing panel.
- Layouts (GroupLayout/AbsoluteLayout) and all screens/flows are unchanged — this is a re-skin, not a redesign.

## Known limitations / things you may still want to address
- **SQL injection**: `login.java`, `Entry.java`, and `NewUser.java` build SQL strings via concatenation (not true parameterized queries) — pre-existing since the original project (already flagged in `ANALYSIS_REPORT.md`). Left as-is per the restore scope (make it build/run/clean without rewriting architecture); worth hardening separately if this will be used beyond a class project.
- **Plaintext passwords**: stored as plain text in the `users`/`registration` tables — pre-existing, same reasoning as above.
- **Ant/NetBeans build**: `build.xml`/`nbproject/` were repaired (no more broken absolute paths) but not verified end-to-end, since Ant isn't installed on this machine. The project builds and runs correctly via plain `javac`/`java` (see above); if you install NetBeans + Ant, the project should now open and build cleanly too.
- **`db.properties`**: contains a real local dev password and is gitignored on purpose — if you set this up on another machine, create your own via `db.properties.example`.
