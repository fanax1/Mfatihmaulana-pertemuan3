PROJECT JDBC LOGIN - fatihrpll

Struktur:
src/main/java/com/mycompany/cobamvc/
  DBConnection.java
  LoginForm.java
  Main.java
database/login.sql
pom.xml

Cara menjalankan:
1. Ekstrak ZIP.
2. NetBeans > File > Open Project > pilih folder fatihrpll.
3. Pastikan MySQL/XAMPP aktif.
4. Buka phpMyAdmin > tab SQL > jalankan database/login.sql.
5. Periksa DBConnection.java. Default koneksi: localhost:3306, user root,
   password kosong. Sesuaikan jika konfigurasi MySQL kamu berbeda.
6. Klik kanan project > Clean and Build.
7. Jalankan Main.java.

Akun latihan:
admin / admin123
fatih / fatih123

Catatan:
- Project Maven dengan target Java 18.
- Maven mengunduh MySQL Connector/J otomatis saat pertama kali build.
- Password teks biasa hanya untuk latihan JDBC dasar, bukan aplikasi produksi.
