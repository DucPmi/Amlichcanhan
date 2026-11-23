# Lịch Kính — Liquid Glass VN Calendar (Android 17 / API 37)

Ứng dụng Android (Kotlin + Jetpack Compose) phong cách **iOS Liquid Glass**, kèm **2 Widget trong suốt** dùng cho cả **Màn hình chính (Home Screen)** và **Màn hình khóa (Lock Screen)** trên Android 17 (`compileSdk = 37`, `targetSdk = 37`, `minSdk = 31`).

## Widget tích hợp sẵn
1. **Lịch Kính • Đầy đủ (Home & Lock):** Đồng hồ real-time (`TextClock`), ngày Dương lịch, Âm lịch Việt Nam (Can Chi + ngày lễ), sự kiện sắp tới gần nhất và câu English Quote.
2. **Lịch Kính • Gọn (Màn hình khóa):** Tối ưu cho màn hình khóa Android 17 — hiển thị nhanh ngày Dương/Âm lịch và English Quote hằng ngày.

## Cách đẩy lên GitHub để đóng gói APK tự động (không cần cài Android Studio)
1. Giải nén file `.zip`, mở thư mục `LiquidGlassVNCalendar`.
2. Tạo một repository mới trên [GitHub](https://github.com/new) (chọn Private hoặc Public, không tích thêm README).
3. Mở Terminal / PowerShell trong thư mục `LiquidGlassVNCalendar` và chạy:
   ```bash
   git init
   git add .
   git commit -m "Initial Android 17 Liquid Glass app with Home & Lock widgets"
   git branch -M main
   git remote add origin https://github.com/<USERNAME>/<REPO_NAME>.git
   git push -u origin main
   ```
4. Vào tab **Actions** trên GitHub → chọn workflow **Build Android 17 APK** → chờ khoảng 2–3 phút.
5. Tải file **LiquidGlassVNCalendar-Android17-APK** ở mục **Artifacts**, giải nén lấy `app-debug.apk` và cài lên máy Android 17.
