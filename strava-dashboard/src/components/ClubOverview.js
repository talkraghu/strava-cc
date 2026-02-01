import React, { useState, useEffect } from 'react';
import { Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import './ClubOverview.css';
import { FaUsers, FaRoute, FaClock, FaMountain, FaCalendarAlt, FaRunning, FaBicycle, FaWalking } from 'react-icons/fa';

ChartJS.register(ArcElement, Tooltip, Legend);

const ClubOverview = ({ stats, members, activityTypeFilter, membersByActivityType }) => {
  const formatDistance = (km) => {
    if (!km) return '0 km';
    return `${km.toFixed(1)} km`;
  };

  const formatTime = (minutes) => {
    if (!minutes) return '0h';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0 && mins > 0) return `${hours}h ${mins}m`;
    if (hours > 0) return `${hours}h`;
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

  if (!stats) return <div>Loading...</div>;

  // When club stats return zeros but we have member list data, derive totals from members
  const listForTotals = activityTypeFilter ? membersByActivityType : members;
  const statsAreEmpty = (stats.totalMembers == null || stats.totalMembers === 0) && (stats.totalActivities == null || stats.totalActivities === 0);
  const derivedTotals = listForTotals?.length > 0 && statsAreEmpty
    ? listForTotals.reduce(
        (acc, m) => ({
          totalMembers: listForTotals.length,
          totalDistance: (acc.totalDistance || 0) + (Number(m.totalDistance) || 0),
          totalActivities: (acc.totalActivities || 0) + (Number(m.totalActivities) || 0),
          totalMovingTime: (acc.totalMovingTime || 0) + (Number(m.totalMovingTime) || 0),
        }),
        { totalMembers: 0, totalDistance: 0, totalActivities: 0, totalMovingTime: 0 }
      )
    : null;

  const totalMembers = derivedTotals ? derivedTotals.totalMembers : (stats.totalMembers ?? 0);
  const totalDistance = derivedTotals ? derivedTotals.totalDistance : (stats.totalDistance ?? 0);
  const totalActivities = derivedTotals ? derivedTotals.totalActivities : (stats.totalActivities ?? 0);
  const totalMovingTime = derivedTotals ? derivedTotals.totalMovingTime : (stats.totalMovingTime ?? 0);

  const activityData = stats.activityTypeBreakdown || [];
  const activityLabels = activityData.map(a => a.activityType);
  const activityDistances = activityData.map(a => a.totalDistance);

  const doughnutData = {
    labels: activityLabels,
    datasets: [
      {
        data: activityDistances,
        backgroundColor: [
          '#FF6B6B',
          '#4ECDC4',
          '#45B7D1',
          '#96CEB4',
          '#FFE66D',
          '#A8E6CF',
        ],
        borderWidth: 2,
        borderColor: '#fff',
      },
    ],
  };

  const activityIcons = { Run: FaRunning, Ride: FaBicycle, Walk: FaWalking, Hike: FaMountain };
  const activityColors = { Run: '#FF6B6B', Ride: '#4ECDC4', Walk: '#45B7D1', Hike: '#96CEB4' };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
      },
    },
  };

  const clubEvents = stats?.clubEvents ?? [];
  const hasMembers = (activityTypeFilter ? membersByActivityType : members)?.length > 0;
  const formatEventDate = (isoStr) => {
    if (!isoStr) return '';
    try {
      const d = new Date(isoStr);
      return d.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch {
      return isoStr;
    }
  };

  return (
    <div className="club-overview">
      <div className="overview-stats-grid">
        <div className="stat-card">
          <FaUsers className="stat-icon" />
          <div className="stat-content">
            <span className="stat-value">{totalMembers}</span>
            <span className="stat-label">Total Members</span>
          </div>
        </div>
        <div className="stat-card">
          <FaRoute className="stat-icon" />
          <div className="stat-content">
            <span className="stat-value">{formatDistance(totalDistance)}</span>
            <span className="stat-label">Total Distance</span>
          </div>
        </div>
        <div className="stat-card">
          <FaClock className="stat-icon" />
          <div className="stat-content">
            <span className="stat-value">{formatTime(totalMovingTime)}</span>
            <span className="stat-label">Total Time</span>
          </div>
        </div>
        <div className="stat-card">
          <FaMountain className="stat-icon" />
          <div className="stat-content">
            <span className="stat-value">{totalActivities}</span>
            <span className="stat-label">Total Activities</span>
          </div>
        </div>
        <div className="stat-card">
          <FaCalendarAlt className="stat-icon" />
          <div className="stat-content">
            <span className="stat-value">{stats?.totalClubEvents ?? 0}</span>
            <span className="stat-label">Club Events (in date range)</span>
          </div>
        </div>
      </div>

      <div className="charts-and-activity-stats-row">
        <div className="charts-container charts-and-activity-stats-chart">
          <div className="chart-card">
          <h3>Distance by Activity Type</h3>
            <div className="chart-wrapper">
              <Doughnut data={doughnutData} options={chartOptions} />
            </div>
          </div>
        </div>
        <div className="overview-activity-stats">
          <h3>Activity Type Statistics</h3>
          <div className="activity-stats-grid">
          {activityData.map((activity, idx) => {
            const Icon = activityIcons[activity.activityType] || FaRunning;
            const color = activityColors[activity.activityType] || '#667eea';
            return (
              <div key={idx} className="activity-stat-card" style={{ borderTopColor: color }}>
                <div className="activity-stat-header">
                  <Icon className="activity-stat-icon" style={{ color }} />
                  <h4>{activity.activityType}</h4>
                </div>
                <div className="activity-stat-body">
                  <div className="stat-row">
                    <span className="stat-label">Total Distance:</span>
                    <span className="stat-value">{activity.totalDistance != null ? activity.totalDistance.toFixed(1) : '0'} km</span>
                  </div>
                  <div className="stat-row">
                    <span className="stat-label">Total Activities:</span>
                    <span className="stat-value">{activity.totalActivities ?? 0}</span>
                  </div>
                  <div className="stat-row">
                    <span className="stat-label">Avg Distance:</span>
                    <span className="stat-value">{(activity.averageDistance != null ? activity.averageDistance : 0).toFixed(1)} km</span>
                  </div>
                  <div className="stat-row">
                    <span className="stat-label">Total Time:</span>
                    <span className="stat-value">{activity.totalMovingTime != null ? Math.floor(activity.totalMovingTime / 60) : 0}h</span>
                  </div>
                </div>
              </div>
            );
          })}
          </div>
        </div>
      </div>

      <div className="overview-panels-row">
        <div key={activityTypeFilter || 'all'} className="top-members-preview">
          <h3>
            {activityTypeFilter
              ? `Distance per member — ${activityTypeFilter}`
              : 'All Members (by distance)'}
          </h3>
          <div className={`top-members-list ${activityTypeFilter ? 'with-details' : 'simple-list'}`}>
            {hasMembers ? (
              activityTypeFilter ? (
                <>
                  <div className="top-members-header">
                    <span className="col-rank">#</span>
                    <span className="col-name">Member</span>
                    <span className="col-distance">Distance</span>
                    <span className="col-time">Time</span>
                    <span className="col-elevation">Elevation</span>
                  </div>
                  {membersByActivityType.map((member, idx) => (
                    <div key={member.athleteName || idx} className="top-member-item">
                      <span className="rank">#{idx + 1}</span>
                      <span className="name">{member.athleteName}</span>
                      <span className="distance">{formatDistance(member.totalDistance || 0)}</span>
                      <span className="time">{formatTime(member.totalMovingTime || 0)}</span>
                      <span className="elevation">{formatElevation(member.totalElevationGain || 0)}</span>
                    </div>
                  ))}
                </>
              ) : (
                <>
                  <div className="top-members-header top-members-header-simple">
                    <span className="col-rank">#</span>
                    <span className="col-name">Member</span>
                    <span className="col-distance">Distance</span>
                    <span className="col-time">Time</span>
                    <span className="col-activities">Activities</span>
                  </div>
                  {members.map((member, idx) => (
                    <div key={member.athleteName || idx} className="top-member-item">
                      <span className="rank">#{idx + 1}</span>
                      <span className="name">{member.athleteName}</span>
                      <span className="distance">{formatDistance(member.totalDistance || 0)}</span>
                      <span className="time">{formatTime(member.totalMovingTime || 0)}</span>
                      <span className="activities">{member.totalActivities ?? 0}</span>
                    </div>
                  ))}
                </>
              )
            ) : (
              <div className="club-event-item" style={{ borderLeft: 'none' }}>
                No members in date range
              </div>
            )}
          </div>
        </div>

        <div className="club-events-preview">
          <h3>
            Club events
            {stats?.totalClubEvents != null && (
              <span style={{ fontWeight: 'normal', color: '#666', fontSize: '0.75rem', marginLeft: '0.35rem' }}>
                ({stats.totalClubEvents} in range)
              </span>
            )}
          </h3>
          <div className="club-events-list">
            {clubEvents.length > 0 ? (
              clubEvents.map((evt, idx) => (
                <div key={evt.id ?? idx} className="club-event-item">
                  <span className="event-title">{evt.title || 'Untitled event'}</span>
                  <span className="event-meta">
                    {evt.activityType || ''}
                    {evt.upcomingOccurrences?.[0] ? ` · ${formatEventDate(evt.upcomingOccurrences[0])}` : ''}
                  </span>
                </div>
              ))
            ) : (
              <div className="club-event-item" style={{ borderLeft: 'none' }}>
                No club events
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ClubOverview;