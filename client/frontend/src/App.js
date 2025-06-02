import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import BookingPage from './page/BookingPage';
import PaymentPage from './page/PaymentPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<BookingPage />} />
        <Route path="/payment/:bookingId" element={<PaymentPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;