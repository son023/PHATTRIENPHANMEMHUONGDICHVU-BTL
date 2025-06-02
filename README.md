## Giới thiệu đề tài

Hệ thống đặt vé xem phim cho phép khách hàng chọn phim, suất chiếu, ghế ngồi
và tiến hành đặt vé trực tuyến. Sau khi khách hàng chọn phim, hệ thống sẽ kiểm tra tình trạng chỗ ngồi, xác minh thông tin vé, và xử lý thanh toán. Nếu đặt vé thành công, hệ thống sẽ gửi thông báo xác nhận vé đã đặt đến email của khách hàng.

Hệ thống được thiết kế theo kiến trúc hướng dịch vụ (SOA), trong đó các chức năng chính như quản lý phim, thanh toán, người dùng và gửi thông báo được tổ chức thành các **dịch vụ riêng biệt**. Điều này đảm bảo tính linh hoạt, dễ bảo trì và khả năng mở rộng. Dự án sử dụng Docker và Docker Compose để đơn giản hóa quá trình triển khai, cho phép khởi động toàn bộ hệ thống chỉ với một lệnh duy nhất.

## Thành viên nhóm và đóng góp

| MSSV        | Họ tên                    | Vai trò                       |
|-------------|---------------------------|-------------------------------|
| B21DCCN474  | Nguyễn Thị ThanhLam          | Phân tích và thiết kế hệ thống <br> Phát triển và viết tài liệu service MovieService, BookingService, API Gateway<br>Viết tài liệu phân tích và thiết kế kiến trúc hệ thống  <br> Viết tài liệu kiến trúc hệ thống |
| B21DCCN222  | Trần Quý Đạt          | Phân tích và thiết kế hệ thống <br> Phát triển và viết tài liệu service UserService, NotificationService  <br>Viết tài liệu phân tích và thiết kế kiến trúc hệ thống|
| B21DCCN653  | Nguyễn Văn Sơn         | Phân tích và thiết kế hệ thống <br> Phát triển và viết tài liệu service PaymentService, SeatAvailabilityService <br> Đóng gói Docker Compose, triển khai hệ thống<br> Vẽ sơ đồ tuần tự <br> Sơ đồ kiến trúc hệ thống |

---

## Hướng dẫn cài đặt hệ thống

### Yêu cầu

Trước khi bắt đầu, bạn cần cài đặt:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Các bước cài đặt

1. Clone repository dự án về máy:
```bash
git clone https://github.com/jnp2018/mid-project-222474653.git
cd mid-project-222474653
````

2. Khởi chạy toàn bộ hệ thống bằng Docker Compose:

```bash
docker-compose up
```

3. Truy cập hệ thống qua cổng 3000:

```bash
http://localhost:3000/
```

---

