import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { QRCodeCanvas } from "qrcode.react";
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import '../css/PaymentStyles.css';

const PaymentPage = () => {
  const { bookingId } = useParams();

  const navigate = useNavigate();
  const location = useLocation();

  const [bookingRequest, setBookingRequest] = useState(location.state?.bookingRequest || null);
  const [booking, setBooking] = useState(location.state?.booking || null);
  const [paymentStatus, setPaymentStatus] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const [invoiceId, setInvoiceId] = useState(null);
  const [copyTooltip, setCopyTooltip] = useState({ accountNumber: false, amount: false, content: false, orderCode:false });

  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState('');

  useEffect(() => {
    if (booking) {
      console.log('Booking data from navigation state:', booking);

      if (booking.status) {
        const initialStatus = booking.status;
        console.log('Setting initial payment status:', initialStatus);
        setPaymentStatus(initialStatus);

        // Nếu trạng thái ban đầu đã là completed/failed/canceled, hãy chuyển hướng ngay
        if (initialStatus === 'COMPLETED') {
          console.log('Initial status is COMPLETED, showing success modal');
          setTimeout(() => {
            setModalType('success');
            setShowModal(true);
          }, 1000);
        } else if (initialStatus === 'FAILED' || initialStatus === 'CANCELED') {
          console.log('Initial status is FAILED/CANCELED, showing failure modal');
          setTimeout(() => {
            setModalType(initialStatus.toLowerCase());
            setShowModal(true);
          }, 1000);
        }
      } else {
        console.log('No payment status in initial booking data');
        // Mặc định là PENDING nếu không có trạng thái
        setPaymentStatus('PENDING');
      }
    } else {
      setError('Không có dữ liệu đặt vé. Vui lòng quay lại trang đặt vé.');
    }
  }, [booking, bookingId, navigate]);
  const closeModal = () => {
    setShowModal(false);
    if (modalType === 'success' || modalType === 'failed' || modalType === 'canceled') {
      navigateToHome();
    }
  };
  const navigateToHome = () => {
    navigate('/'); // hoặc navigate('/home') tùy theo route trang chủ của bạn
  };

  // WebSocket implementation ...
  useEffect(() => {
    if (!booking) return;

    console.log('Connecting to WebSocket with bookingId:', bookingId);

    // Tạo kết nối SockJS với backend
    const socket = new SockJS('http://localhost:8080/booking-websocket');
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => {
        console.log('STOMP Debug:', str);
      }
    });

    client.onConnect = () => {
      console.log('WebSocket connected successfully');

      const currentOrderCode = booking.orderCode;

      // Đăng ký nhận topic với currentOrderCode
      const subscription = client.subscribe(`/topic/payment/${currentOrderCode}`, (message) => {
        console.log('Raw WebSocket message received:', message);
        try {
          const paymentEvent = JSON.parse(message.body);
          console.log('Parsed payment update:', paymentEvent);

          // Trích xuất trạng thái
          const status = paymentEvent.status;
          console.log('Extracted payment status:', status);

          // Lưu invoiceId nếu có
          if (paymentEvent.invoiceId) {
            setInvoiceId(paymentEvent.invoiceId);
            console.log('Extracted invoiceId:', paymentEvent.invoiceId);
          }

          if (status) {
            console.log('Setting payment status to:', status);
            setPaymentStatus(status);

            if (status === 'COMPLETED') {
              console.log('Payment COMPLETED, showing success modal');
              setTimeout(() => {
                setModalType('success');
                setShowModal(true);
              }, 1000);
            } else if (status === 'FAILED' || status === 'CANCELED') {
              console.log('Payment FAILED/CANCELED, showing failure modal');
              setTimeout(() => {
                setModalType(status.toLowerCase());
                setShowModal(true);
              }, 1000);
            }
          } else {
            console.error('Payment status not found in event:', paymentEvent);
          }
        } catch (error) {
          console.error('Error processing WebSocket message:', error, 'Raw message:', message.body);
        }
      });

      console.log('Subscribed to:', `/topic/payment/${currentOrderCode}`);
    };

    client.onStompError = (frame) => {
      console.error('STOMP protocol error:', frame.headers, frame.body);
    };

    client.onWebSocketError = (event) => {
      console.error('WebSocket error occurred:', event);
    };

    client.onWebSocketClose = (event) => {
      console.log('WebSocket connection closed:', event);
    };

    console.log('Activating WebSocket connection');
    client.activate();
    setStompClient(client);

    return () => {
      console.log('Component unmounting, deactivating WebSocket');
      if (client && client.connected) {
        client.deactivate();
      } else if (client) {
        client.deactivate();
      }
    };
  }, [booking, bookingId, navigate]);

  const copyToClipboard = (text, field) => {
    navigator.clipboard.writeText(text).then(() => {
      setCopyTooltip({ ...copyTooltip, [field]: true });
      setTimeout(() => {
        setCopyTooltip({ ...copyTooltip, [field]: false });
      }, 2000);
    });
  };

  if (loading) return <div className="loading-container"><div className="loading-spinner"></div><p>Đang tải...</p></div>;
  if (error) return <div className="error-container"><p>{error}</p></div>;
  if (!booking) return <div className="error-container"><p>Không tìm thấy đơn đặt vé. Vui lòng quay lại trang đặt vé.</p></div>;

  const accountOwner = "NGO THI PHUONG";
  const accountNumber = "0376 338 283";
  const orderCode = booking.orderCode;

  return (
      <div className="mb-payment-container">
        <div className="mb-payment-header">
          <h1>Thanh toán qua ngân hàng</h1>
          <div className="mb-bank-info">
            <div className="mb-logo">
              <img src="https://prophet.com/wp-content/uploads/2019/11/MB_Slide_1-1423x800.jpg" alt="MB Bank" />
              <span>MB</span>
            </div>
            <div className="mb-bank-name">
              <p>Ngân hàng</p>
              <p className="bank-title">Ngân hàng TMCP Quân đội</p>
            </div>
          </div>
        </div>

        <div className="mb-payment-content">
          <div className="mb-qr-section">
            <div className="qr-wrapper">
              {booking.qrCode ? (
                  <QRCodeCanvas
                      value={booking.qrCode}
                      size={220}
                      level="H"
                      includeMargin={true}
                      className="qr-code"
                  />
              ) : (
                  <div className="qr-error">Không thể tải mã QR</div>
              )}
            </div>
          </div>

          <div className="mb-payment-details">
            <div className="payment-field">
              <div className="field-label">Chủ tài khoản:</div>
              <div className="field-value">{accountOwner}</div>
            </div>
            <div className="payment-field">
              <div className="field-label">Số tài khoản:</div>
              <div className="field-value">
                <span>{accountNumber}</span>
                <button
                    className="copy-btn"
                    onClick={() => copyToClipboard(accountNumber, 'accountNumber')}
                >
                  {copyTooltip.accountNumber ? 'Đã sao chép' : 'Sao chép'}
                </button>
              </div>
            </div>
            <div className="payment-field">
              <div className="field-label">Mã giao dịch:</div>
              <div className="field-value">
                <span>{orderCode}</span>
                <button
                    className="copy-btn"
                    onClick={() => copyToClipboard(orderCode, 'orderCode')}
                >
                  {copyTooltip.orderCode ? 'Đã sao chép' : 'Sao chép'}
                </button>
              </div>
            </div>

            <div className="payment-field">
              <div className="field-label">Số tiền:</div>
              <div className="field-value">
                <span>{bookingRequest.finalPrice}</span>
                <button
                    className="copy-btn"
                    onClick={() => copyToClipboard(bookingRequest.finalPrice, 'amount')}
                >
                  {copyTooltip.amount ? 'Đã sao chép' : 'Sao chép'}
                </button>
              </div>
            </div>

            <div className="payment-field">
              <div className="field-label">Nội dung:</div>
              <div className="field-value">
                <span>Đặt vé xem phim</span>
                <button
                    className="copy-btn"
                    onClick={() => copyToClipboard("Đặt vé xem phim", 'content')}
                >
                  {copyTooltip.content ? 'Đã sao chép' : 'Sao chép'}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div className="mb-payment-status">
          <p>Đơn hàng đang chờ được thanh toán</p>
          <div className="payment-status-indicator">
          <span className={`status-${paymentStatus?.toLowerCase() || 'unknown'}`}>
            {paymentStatus === 'PENDING' && 'Đang chờ thanh toán'}
            {paymentStatus === 'COMPLETED' && 'Thanh toán thành công'}
            {paymentStatus === 'FAILED' && 'Thanh toán thất bại'}
            {paymentStatus === 'CANCELED' && 'Đã hủy thanh toán'}
            {!paymentStatus && 'Chưa xác định'}
          </span>
          </div>
        </div>
        {showModal && (
            <div className="payment-modal-overlay">
              <div className={`payment-modal payment-modal-${modalType}`}>
                <div className="modal-header">
                  {modalType === 'success' && <h2>Thanh toán thành công!</h2>}
                  {modalType === 'failed' && <h2>Thanh toán thất bại!</h2>}
                  {modalType === 'canceled' && <h2>Thanh toán đã bị hủy!</h2>}
                  <button className="modal-close-btn" onClick={closeModal}>×</button>
                </div>

                <div className="modal-content">
                  {modalType === 'success' && (
                      <>
                        <div className="success-icon">✓</div>
                        <p>Cảm ơn bạn đã đặt vé xem phim!</p>
                        <p>Thông tin vé đã được gửi đến email: <strong>{bookingRequest?.customerEmail || 'của bạn'}</strong></p>
                        <p>Mã đơn hàng: <strong>{booking.orderCode}</strong></p>
                        <p>Vui lòng kiểm tra thông tin vé trong hộp thư .</p>
                        <button className="modal-action-btn" onClick={closeModal}>Đã hiểu</button>
                      </>
                  )}

                  {modalType === 'failed' && (
                      <>
                        <div className="failed-icon">✗</div>
                        <p>Thanh toán không thành công!</p>
                        <p>Vui lòng thử lại hoặc chọn phương thức thanh toán khác.</p>
                        <p>Mã đơn hàng: <strong>{booking.orderCode}</strong></p>
                        <button className="modal-action-btn" onClick={closeModal}>Thử lại</button>
                      </>
                  )}

                  {modalType === 'canceled' && (
                      <>
                        <div className="canceled-icon">!</div>
                        <p>Giao dịch đã bị hủy.</p>
                        <p>Bạn có thể thử lại hoặc quay lại sau.</p>
                        <p>Mã đơn hàng: <strong>{booking.orderCode}</strong></p>
                        <button className="modal-action-btn" onClick={closeModal}>Đóng</button>
                      </>
                  )}
                </div>
              </div>
            </div>
        )}
      </div>
  );
};

export default PaymentPage;