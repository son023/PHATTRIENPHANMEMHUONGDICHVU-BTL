import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../css/BookingStyles.css';
import { defaultToken } from '../data/token';


const convertDateTime = (dateTime) => {
  const obj = new Date(dateTime);

  const date = obj.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
  const time = obj.toLocaleTimeString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit'
  });

    return [date, time].join(' ');
}

const BookingPage = () => {
  const navigate = useNavigate();
  const [movies, setMovies] = useState([]);
  const [selectedMovie, setSelectedMovie] = useState('');
  const [shows, setShows] = useState([]);
  const [selectedShow, setSelectedShow] = useState('');
  const [seats, setSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [verityStatus, setVerifyStatus] = useState(false);
  const [customerInfo, setCustomerInfo] = useState({
    name: '',
    email: '',
    phone: ''
  });
  const [continueStatus, setContinueStatus] = useState(false)
  const [summary, setSummary] = useState({
    movieName: '',
    roomName: '',
    scheduleTime: '',
    seatNames: '',
    totalPrice: 0
  })

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [isDragging, setIsDragging] = useState(false);
  const [startX, setStartX] = useState(0);
  const [scrollLeft, setScrollLeft] = useState(0);
  const sliderRef = useRef(null);
  const seatSelectionRef = useRef(null);


  useEffect(() => {
    const fetchData = async () => {
      const response = await axios.get(
            'http://localhost:8080/bookings/get-all-movies',
            {
              headers: {
                'Authorization': defaultToken
              }
            }
        );

        setMovies(response.data);
    }

    fetchData();
  }, []);

  useEffect(() => {
    if (selectedMovie) {

      let filteredSchedules = [];

      const fetchData = async () => {
      const response = await axios.get(
            'http://localhost:8080/bookings/get-all-schedules-by-movie/' + selectedMovie,
            {
              headers: {
                'Authorization': defaultToken
              }
            }
        );

        filteredSchedules = response.data;

        const formattedShows = filteredSchedules.map(schedule => {
        const startTime = new Date(schedule.start);

        return {
          id: schedule.id,
          date: startTime.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
          }),
          time: startTime.toLocaleTimeString('vi-VN', {
            hour: '2-digit',
            minute: '2-digit'
          }),
          roomName: schedule.room.name,
          roomType: schedule.room.type
        };
      });

      setShows(formattedShows);
      }

      fetchData();
    } else {
      setShows([]);
    }
  }, [selectedMovie]);

  useEffect(() => {
    if (selectedShow) {
      setLoading(true);

      try {
        const fetchData = async () => {
        const response = await axios.get(
              'http://localhost:8080/bookings/get-all-seat-schedules-by-schedule/' + selectedShow,
              {
                headers: {
                  'Authorization': defaultToken
                }
              }
          );

        const formattedSeats = response.data.map(item => ({
          id: item.id,
          name: item.seat.name,
          available: item.status === "Empty",
          holding: item.status === "Holding",
          roomName: item.seat.room.name,
          scheduleId: item.schedule.id
        }));

        setSeats(formattedSeats);
        setLoading(false);
      }

      fetchData();
        
      } catch (error) {
        console.error('Lỗi khi lấy danh sách ghế:', error);
        setError('Không thể tải danh sách ghế. Vui lòng thử lại sau.');
        setLoading(false);
      }
    } else {
      setSeats([]);
    }
  }, [selectedShow]);

  const handleSeatSelection = (seatId) => {
    setSelectedSeats(prevSelected => {
      if (prevSelected.includes(seatId)) {
        return prevSelected.filter(id => id !== seatId);
      } else {
        return [...prevSelected, seatId];
      }
    });
    setVerifyStatus(false);
  };

  const handleVerify = async () => {
    if(selectedSeats.length === 0) {
      return;
    }

    const checkAvailableRequest = selectedSeats

    const checkAvailableResponse = await axios.post(
      'http://localhost:8080/bookings/check-selected-seats-available/' + selectedShow,
      checkAvailableRequest,
      {
        headers: {
          'Authorization': defaultToken
        }
      }
    )

    const checkOk = checkAvailableResponse.data;
    if(!checkOk) {
      setError('Ghế ko có sẵn');
      window.location.reload();
      return;
    } else {
      setVerifyStatus(true);
    }
    
  }

  const handleCustomerInfoChange = (e) => {
    const { name, value } = e.target;
    setCustomerInfo(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleContinue = async () => {
    if (customerInfo.email === '' || customerInfo.name === '' || customerInfo.phone === '') {
      return;
    }

    const getTicketSummaryRequest = {
      movieId: selectedMovie,
      seatScheduleIds: selectedSeats
    }

    const checkAvailableResponse = await axios.post(
      'http://localhost:8080/bookings/get-ticket-summary',
      getTicketSummaryRequest,
      {
        headers: {
          'Authorization': defaultToken
        }
      }
    )

    const ticketSummaryData = checkAvailableResponse.data;
    setSummary({
      movieName: ticketSummaryData.movie.name,
      roomName: ticketSummaryData.room.name,
      scheduleTime: convertDateTime(ticketSummaryData.schedule.start),
      seatNames: ticketSummaryData.seats.map(seat => seat.name).join(', '),
      totalPrice: ticketSummaryData.totalPrice
    })
    setContinueStatus(true);
  }


  const handleCreateBooking = async () => {
    if (!selectedMovie || !selectedShow || selectedSeats.length === 0) {
      setError('Vui lòng chọn phim, suất chiếu và ghế');
      return;
    }

    if (!customerInfo.name || !customerInfo.email || !customerInfo.phone) {
      setError('Vui lòng nhập đầy đủ thông tin khách hàng');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const seatScheduleIds = selectedSeats;

      const updateSeatsRequest = {
        seatScheduleIds: seatScheduleIds,
        targetStatus: "Holding"
      }

      const updateSeatsResponse = await axios.post(
        'http://localhost:8080/bookings/update-seat-schedules/' + selectedShow,
        updateSeatsRequest,
        {
          headers: {
            'Authorization': defaultToken
          }
        }
      )

      const updateOk = updateSeatsResponse.data;
      if(!updateOk) {
        alert('Ghế đã có người khác đặt');
        window.location.reload();
        setError('Cập nhật ghế thất bại');
        return;
      }

      const totalPrice = selectedSeats.length * 90000;
      const bookingRequest = {
        customerName: customerInfo.name,
        customerEmail: customerInfo.email,
        customerPhone: customerInfo.phone,
        seatScheduleIds: seatScheduleIds,
        totalPrice: totalPrice,
        discount: 0,
        finalPrice: totalPrice
      };

      console.log('Sending booking request:', bookingRequest);

      const response = await axios.post(
          'http://localhost:8080/bookings/create',
          bookingRequest,
          {
            headers: {
              'Authorization': defaultToken
            }
          }
      );

      if (response.data) {
        const redirectId = response.data.orderCode;

        if (redirectId) {
          navigate(`/payment/${redirectId}`, {
            state: {
              booking: response.data,
              bookingRequest: bookingRequest
            }
          });
        } else {
          setError('Không thể tạo đơn đặt vé vì không tìm thấy ID hợp lệ.');
        }
      } else {
        setError('Không thể tạo đơn đặt vé. Vui lòng thử lại sau.');
      }
    } catch (err) {
      console.error('Lỗi khi tạo booking:', err);
      setError(
          err.response?.data?.message ||
          'Không thể tạo đơn đặt vé. Vui lòng thử lại sau.'
      );
    } finally {
      setLoading(false);
    }
  };

  const renderSeats = () => {
    if (!seats.length) return null;

    const seatRows = {};
    seats.forEach(seat => {
      const row = seat.name.charAt(0);
      if (!seatRows[row]) seatRows[row] = [];
      seatRows[row].push(seat);
    });

    const sortedRows = Object.keys(seatRows).sort();

    sortedRows.forEach(row => {
      seatRows[row].sort((a, b) => {
        const numA = parseInt(a.name.substring(1));
        const numB = parseInt(b.name.substring(1));
        return numA - numB;
      });
    });

    return (
        <div className="seat-selection"ref={seatSelectionRef}>
          <h3>Chọn ghế - {seats[0]?.roomName || "Phòng chiếu phim"}</h3>
          <div className="screen">Màn hình</div>
          <div className="seat-container">
            {sortedRows.map(row => (
                <div key={row} className="seat-row">
                  <div className="row-label">{row}</div>
                  {seatRows[row].map(seat => (
                      <button
                          key={seat.id}
                          className={`seat 
                            ${!seat.available && !seat.holding ? 'unavailable' : ''} 
                            ${seat.holding ? 'holding' : ''} 
                            ${selectedSeats.includes(seat.id) ? 'selected' : ''}
                          `}
                          disabled={!seat.available}
                          onClick={() => handleSeatSelection(seat.id)}
                      >
                        {seat.name}
                      </button>
                  ))}
                </div>
            ))}
          </div>
          <div className="seat-legend">
            <div className="legend-item">
              <div className="seat-sample"></div>
              <span>Ghế trống</span>
            </div>
            <div className="legend-item">
              <div className="seat-sample selected"></div>
              <span>Ghế đã chọn</span>
            </div>
            <div className="legend-item">
              <div className="seat-sample holding"></div>
              <span>Ghế đang giữ</span>
            </div>
            <div className="legend-item">
              <div className="seat-sample unavailable"></div>
              <span>Ghế đã đặt</span>
            </div>
          </div>
          <div className="verify-container">
            <button className="verify-btn" onClick={handleVerify} disabled={verityStatus}>
              {verityStatus ? "Đã xác minh" : "Xác minh"}
            </button>
          </div>    
        </div>
    );
  };

  useEffect(() => {
    if (seats.length > 0 && seatSelectionRef.current) {
      seatSelectionRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [seats]);

  const handleMouseDown = (e) => {
    if (!sliderRef.current) return;
    e.preventDefault();
    setIsDragging(true);
    setStartX(e.pageX - sliderRef.current.offsetLeft);
    setScrollLeft(sliderRef.current.scrollLeft);
  };

  const handleMouseMove = (e) => {
    if (!isDragging || !sliderRef.current) return;
    e.preventDefault();
    const x = e.pageX - sliderRef.current.offsetLeft;
    const walk = (x - startX) * 2; // *2 for faster scrolling
    sliderRef.current.scrollLeft = scrollLeft - walk;
  };

  const handleMouseUp = () => {
    setIsDragging(false);
  };

  useEffect(() => {
    const slider = sliderRef.current;
    if (slider) {
      slider.addEventListener('mouseup', handleMouseUp);
      slider.addEventListener('mouseleave', handleMouseUp);
    }

    return () => {
      if (slider) {
        slider.removeEventListener('mouseup', handleMouseUp);
        slider.removeEventListener('mouseleave', handleMouseUp);
      }
    };
  }, []);

  return (
      <div className="booking-container">
        <h1 className="booking-title">Đặt vé xem phim</h1>

        {error && <div className="error-message">{error}</div>}

        <div className="booking-section">
          <h2 className="section-title">
            <span className="section-number">1</span>
            Chọn phim
          </h2>
          <div className="movie-selection">
            {loading ? (
                <div className="loading">Đang tải danh sách phim...</div>
            ) : (
                <div
                    className="movie-list"
                    ref={sliderRef}
                    onMouseDown={handleMouseDown}
                    onMouseMove={handleMouseMove}
                    onMouseUp={handleMouseUp}
                    onMouseLeave={handleMouseUp}
                >
                  {movies.map(movie => (
                      <div
                          key={movie.id}
                          className={`movie-card ${selectedMovie === movie.id ? 'selected' : ''}`}
                          onClick={() => {
                            setSelectedMovie(movie.id);
                            setSelectedShow('');
                            setSelectedSeats([]);
                          }}
                      >
                        <div className="movie-poster">
                          <img src={movie.cover} alt={movie.name} />
                        </div>
                        <div className="movie-info">
                          <h3>{movie.name}</h3>
                          <p className="movie-genre">{movie.genre}</p>
                          <p className="movie-duration">{movie.duration} phút</p>
                        </div>
                      </div>
                  ))}
                </div>
            )}
          </div>
        </div>
        {selectedMovie && (
            <div className="booking-section">
              <h2 className="section-title">
                <span className="section-number">2</span>
                Chọn suất chiếu
              </h2>
              <div className="show-list">
                {shows.map(show => (
                    <button
                        key={show.id}
                        className={`show-item ${selectedShow === show.id ? 'selected' : ''}`}
                        onClick={() => setSelectedShow(show.id)}
                    >
                      <div className="show-date">{show.date}</div>
                      <div className="show-time">{show.time}</div>
                      <div className="show-room">{show.roomName} ({show.roomType})</div>
                    </button>
                ))}
              </div>
            </div>
        )}

        {selectedShow && renderSeats()}

        {verityStatus && (
            <div className="booking-section">
              <h2 className="section-title">
                <span className="section-number">3</span>
                Thông tin khách hàng
              </h2>
              <div className="customer-form">
                <div className="form-group">
                  <label htmlFor="name">Họ và tên</label>
                  <input
                      type="text"
                      id="name"
                      name="name"
                      value={customerInfo.name}
                      onChange={handleCustomerInfoChange}
                      placeholder="Nguyễn Văn A"
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="email">Email</label>
                  <input
                      type="email"
                      id="email"
                      name="email"
                      value={customerInfo.email}
                      onChange={handleCustomerInfoChange}
                      placeholder="email@example.com"
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="phone">Số điện thoại</label>
                  <input
                      type="tel"
                      id="phone"
                      name="phone"
                      value={customerInfo.phone}
                      onChange={handleCustomerInfoChange}
                      placeholder="0912345678"
                  />
                </div>

                {!continueStatus && (
                  <div className="verify-container">
                    <button className="verify-btn" onClick={handleContinue}>
                      Tiếp tục
                    </button>
                  </div>  
                )} 
              </div>

              {continueStatus && (
                <>
                  <div className="booking-summary">
                    <h3 className="section-title customer-icon">Tóm tắt đặt vé</h3>
                    <p><strong>Phim:</strong> {summary.movieName}</p>
                    <p><strong>Suất chiếu:</strong> {summary.scheduleTime}</p>
                    <p><strong>Ghế:</strong> {summary.seatNames}</p>
                    <p><strong>Tổng tiền:</strong> {summary.totalPrice} VNĐ</p>
                  </div>
                  <button
                      className="create-booking-btn"
                      onClick={handleCreateBooking}
                      disabled={loading}
                  >
                    {loading ? 'Đang xử lý...' : 'Đặt vé và thanh toán'}
                  </button>
                </>
              )}
            </div>
        )}
      </div>
  );
};

export default BookingPage;