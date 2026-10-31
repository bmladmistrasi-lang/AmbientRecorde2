# Ambient Recorder — Paket Uji 30 Menit

## Isi paket
Proyek Android Studio (Kotlin) dengan GitHub Actions untuk membangun APK debug secara online.

## Fungsi yang benar-benar ada pada build uji ini
- Pengguna menekan tombol **MULAI TES 30 MENIT** dari layar aplikasi.
- Meminta izin mikrofon dan notifikasi.
- Menjalankan foreground service dengan notifikasi terlihat.
- Mulai merekam segera, lalu setiap 5 menit.
- Setiap sesi berdurasi 20 detik.
- Tes berhenti otomatis setelah 30 menit.
- Menyimpan hasil lokal dalam folder privat aplikasi `Android/data/id.bml.ambientrecorder/files/AmbientRecorder`.

Perkiraan sesi: menit ke-0, 5, 10, 15, 20, dan 25 (6 file bila perangkat tidak menghentikan proses).

## Batasan penting — belum termasuk
- **Format output saat ini M4A/AAC, bukan MP3.**
- **Belum ada upload Google Drive.**
- **Belum ada penghapusan lokal otomatis 2 hari.**
- Ini adalah build uji perekaman lokal, bukan aplikasi final dengan seluruh spesifikasi awal.

## Cara mendapatkan APK tanpa Android Studio
1. Buat akun/masuk ke GitHub.
2. Buat repository baru, misalnya `AmbientRecorder`.
3. Ekstrak ZIP ini, lalu unggah seluruh isi folder ke repository (pastikan folder `.github/workflows` ikut terunggah).
4. Buka tab **Actions** pada repository.
5. Pilih workflow **Build Android APK**, lalu tekan **Run workflow** (atau tunggu build otomatis setelah push ke `main`).
6. Setelah selesai, buka hasil workflow dan unduh artifact `AmbientRecorder-debug-apk`.
7. Ekstrak artifact, lalu pindahkan `app-debug.apk` ke HP Android dan instal. Izinkan instalasi dari sumber yang digunakan.

## Pengujian 30 menit
1. Pastikan baterai cukup dan mikrofon tidak sedang dipakai aplikasi lain.
2. Buka Ambient Recorder.
3. Berikan izin mikrofon dan notifikasi.
4. Tekan **MULAI TES 30 MENIT**.
5. Biarkan notifikasi layanan tetap terlihat.
6. Uji dengan layar terkunci; jangan force-stop aplikasi.
7. Setelah 30 menit, periksa apakah enam file rekaman tersimpan.

## Privasi dan keselamatan
Gunakan hanya untuk merekam audio milik sendiri atau lingkungan yang semua pihak terkait telah menyetujui perekamannya. Aplikasi menampilkan notifikasi foreground dan menyediakan tombol berhenti. Jangan menyembunyikan indikator atau merekam percakapan orang lain tanpa persetujuan.

## Catatan kompatibilitas
Android dan beberapa produsen dapat membatasi proses latar belakang karena penghemat baterai. Uji dahulu pada perangkat sasaran. Android versi baru mensyaratkan layanan mikrofon dimulai ketika aplikasi terlihat dan izin yang sesuai telah diberikan.
