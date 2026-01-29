import React from 'react';
import { FaRunning, FaBicycle, FaWalking, FaMountain } from 'react-icons/fa';
import './MemberCard.css';

const MemberCard = ({ member, rank }) => {
  const formatDistance = (km) => {
    if (!km) return '0 km';
    return `${km.toFixed(1)} km`;
  };

  const formatTime = (minutes) => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0) return `${hours}h ${mins}m`;
    return `${mins}m`;
  };

  const formatElevation = (value) => {
    if (!value) return '0 m';
    // If value is suspiciously large (> 50), assume it's already in meters (legacy data or bug)
    // Otherwise, assume it's in km (as per backend DTO) and convert to meters
    let meters;
    if (value > 50) {
      // Already in meters, use directly - always show in meters for clarity
      meters = Math.round(value);
      return `${meters} m`;
    } else {
      // In km, convert to meters
      meters = Math.round(value * 1000);
      if (meters >= 1000) return `${(meters / 1000).toFixed(1)} km`;
      return `${meters} m`;
    }
  };

  const getActivityStats = (activityType) => {
    if (!member.activityTypeBreakdown) return null;
    return member.activityTypeBreakdown.find(
      (a) => a.activityType === activityType
    );
  };

  const runStats = getActivityStats('Run');
  const rideStats = getActivityStats('Ride');
  const walkStats = getActivityStats('Walk');
  const hikeStats = getActivityStats('Hike');

  const activities = [
    { icon: FaRunning, label: 'Running', stats: runStats, color: '#FF6B6B' },
    { icon: FaBicycle, label: 'Cycling', stats: rideStats, color: '#4ECDC4' },
    { icon: FaWalking, label: 'Walking', stats: walkStats, color: '#45B7D1' },
    { icon: FaMountain, label: 'Hiking', stats: hikeStats, color: '#96CEB4' },
  ];

  return (
    <div className="member-card">
      {rank && <div className="member-rank">#{rank}</div>}
      <div className="member-header">
        <h3>{member.athleteName}</h3>
        <div className="member-badge">
          <span className="badge-count">{member.totalActivities}</span>
          <span className="badge-label">Activities</span>
        </div>
      </div>

      <div className="member-stats-summary">
        <div className="stat-item">
          <span className="stat-value">{formatDistance(member.totalDistance)}</span>
          <span className="stat-label">Total Distance</span>
        </div>
        <div className="stat-item">
          <span className="stat-value">{formatTime(member.totalMovingTime)}</span>
          <span className="stat-label">Moving Time</span>
        </div>
        <div className="stat-item">
          <span className="stat-value">{formatElevation(member.totalElevationGain)}</span>
          <span className="stat-label">Elevation</span>
        </div>
      </div>

      <div className="activity-breakdown">
        <h4>Activity Breakdown</h4>
        <div className="activity-grid">
          {activities.map((activity, idx) => {
            const Icon = activity.icon;
            const stats = activity.stats;
            return (
              <div key={idx} className="activity-item" style={{ borderLeftColor: activity.color }}>
                <Icon className="activity-icon" style={{ color: activity.color }} />
                <div className="activity-details">
                  <span className="activity-label">{activity.label}</span>
                  <span className="activity-value">
                    {stats
                      ? `${formatDistance(stats.totalDistance)} (${stats.totalActivities})`
                      : '0 km (0)'}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default MemberCard;