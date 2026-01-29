import React, { useState, useEffect } from 'react';
import './DateRangePicker.css';

const DateRangePicker = ({ onDateRangeChange, initialFromDate, initialToDate, autoApplyInitial = true }) => {
  const [fromDate, setFromDate] = useState(initialFromDate || '');
  const [toDate, setToDate] = useState(initialToDate || '');

  // Optionally auto-apply initial dates on mount (e.g. to filter by last month).
  // When autoApplyInitial is false, we show suggested dates but fetch all-time until user clicks Apply.
  useEffect(() => {
    if (autoApplyInitial && initialFromDate && initialToDate) {
      const from = new Date(initialFromDate).toISOString();
      const to = new Date(initialToDate + 'T23:59:59').toISOString();
      onDateRangeChange(from, to);
    }
  }, []); // Only run on mount

  const handleApply = () => {
    const from = fromDate ? new Date(fromDate).toISOString() : null;
    const to = toDate ? new Date(toDate + 'T23:59:59').toISOString() : null;
    onDateRangeChange(from, to);
  };

  const handleClear = () => {
    setFromDate('');
    setToDate('');
    onDateRangeChange(null, null);
  };

  return (
    <div className="date-range-picker">
      <label>
        From:
        <input
          type="date"
          value={fromDate}
          onChange={(e) => setFromDate(e.target.value)}
        />
      </label>
      <label>
        To:
        <input
          type="date"
          value={toDate}
          onChange={(e) => setToDate(e.target.value)}
        />
      </label>
      <button onClick={handleApply} className="btn-apply">Apply Filter</button>
      <button onClick={handleClear} className="btn-clear">Clear</button>
    </div>
  );
};

export default DateRangePicker;