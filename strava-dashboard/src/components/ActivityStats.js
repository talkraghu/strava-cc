import React, { useState, useEffect } from 'react';
import { Bar, Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';
import { statsApi } from '../services/api';
import './ActivityStats.css';
import { FaRunning, FaBicycle, FaWalking, FaMountain } from 'react-icons/fa';

ChartJS.register(CategoryScale, LinearScale, BarElement, PointElement, LineElement, Title, Tooltip, Legend);

const ActivityStats = ({ fromDate, toDate }) => {
  const [activityStats, setActivityStats] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadActivityStats();
  }, [fromDate, toDate]);

  const loadActivityStats = async () => {
    setLoading(true);
    try {
      const response = await statsApi.getActivityStats(null, fromDate, toDate);
      setActivityStats(response.data);
    } catch (error) {
      console.error('Error loading activity stats:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDistance = (km) => {
    if (!km) return '0';
    return km.toFixed(1);
  };

  if (loading) return <div>Loading activity statistics...</div>;

  const activityIcons = {
    Run: FaRunning,
    Ride: FaBicycle,
    Walk: FaWalking,
    Hike: FaMountain,
  };

  const activityColors = {
    Run: '#FF6B6B',
    Ride: '#4ECDC4',
    Walk: '#45B7D1',
    Hike: '#96CEB4',
  };

  const distanceData = {
    labels: activityStats.map(a => a.activityType),
    datasets: [
      {
        label: 'Total Distance (km)',
        data: activityStats.map(a => a.totalDistance),
        backgroundColor: activityStats.map(a => activityColors[a.activityType] || '#667eea'),
        borderRadius: 8,
      },
    ],
  };

  const activitiesData = {
    labels: activityStats.map(a => a.activityType),
    datasets: [
      {
        label: 'Total Activities',
        data: activityStats.map(a => a.totalActivities),
        borderColor: '#667eea',
        backgroundColor: 'rgba(102, 126, 234, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
    },
    scales: {
      y: {
        beginAtZero: true,
      },
    },
  };

  return (
    <div className="activity-stats-container">
      <h2>Activity Type Statistics</h2>
      <div className="activity-stats-grid">
        {activityStats.map((activity, idx) => {
          const Icon = activityIcons[activity.activityType] || FaRunning;
          const color = activityColors[activity.activityType] || '#667eea';
          return (
            <div key={idx} className="activity-stat-card" style={{ borderTopColor: color }}>
              <div className="activity-stat-header">
                <Icon className="activity-stat-icon" style={{ color }} />
                <h3>{activity.activityType}</h3>
              </div>
              <div className="activity-stat-body">
                <div className="stat-row">
                  <span className="stat-label">Total Distance:</span>
                  <span className="stat-value">{formatDistance(activity.totalDistance)} km</span>
                </div>
                <div className="stat-row">
                  <span className="stat-label">Total Activities:</span>
                  <span className="stat-value">{activity.totalActivities}</span>
                </div>
                <div className="stat-row">
                  <span className="stat-label">Avg Distance:</span>
                  <span className="stat-value">{formatDistance(activity.averageDistance || 0)} km</span>
                </div>
                <div className="stat-row">
                  <span className="stat-label">Total Time:</span>
                  <span className="stat-value">{Math.floor(activity.totalMovingTime / 60)}h</span>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="activity-charts">
        <div className="chart-card">
          <h3>Distance by Activity Type</h3>
          <div className="chart-wrapper">
            <Bar data={distanceData} options={chartOptions} />
          </div>
        </div>
        <div className="chart-card">
          <h3>Activities Count Trend</h3>
          <div className="chart-wrapper">
            <Line data={activitiesData} options={chartOptions} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ActivityStats;