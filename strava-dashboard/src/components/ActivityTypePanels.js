import React, { useState, useEffect } from 'react';
import './ActivityTypePanels.css';
import { FaRunning, FaBicycle, FaWalking, FaHiking, FaUsers, FaRoute, FaClock, FaMountain } from 'react-icons/fa';
import { statsApi } from '../services/api';
import LoadingSpinner from './LoadingSpinner';

const ActivityTypePanels = ({ fromDate, toDate }) => {
  const [activityData, setActivityData] = useState({
    Walk: { stats: null, members: [], loading: true },
    Run: { stats: null, members: [], loading: true },
    Hike: { stats: null, members: [], loading: true },
    Ride: { stats: null, members: [], loading: true },
  });

  const activityTypes = ['Walk', 'Run', 'Hike', 'Ride'];
  const activityIcons = {
    Walk: FaWalking,
    Run: FaRunning,
    Hike: FaHiking,
    Ride: FaBicycle,
  };

  const activityColors = {
    Walk: '#4ECDC4',
    Run: '#FF6B6B',
    Hike: '#96CEB4',
    Ride: '#45B7D1',
  };

  useEffect(() => {
    loadAllActivityData();
  }, [fromDate, toDate]);

  const loadAllActivityData = async () => {
    for (const activityType of activityTypes) {
      try {
        setActivityData(prev => ({
          ...prev,
          [activityType]: { ...prev[activityType], loading: true }
        }));

        const [statsRes, membersRes] = await Promise.all([
          getActivityStats(activityType),
          statsApi.getMembersByActivityType(activityType, fromDate, toDate)
        ]);

        setActivityData(prev => ({
          ...prev,
          [activityType]: {
            stats: statsRes,
            members: membersRes.data || [],
            loading: false
          }
        }));
      } catch (error) {
        console.error(`Error loading ${activityType} data:`, error);
        setActivityData(prev => ({
          ...prev,
          [activityType]: { stats: null, members: [], loading: false }
        }));
      }
    }
  };

  const getActivityStats = async (activityType) => {
    try {
      let response;
      switch (activityType) {
        case 'Walk':
          response = await statsApi.getWalkingStats(fromDate, toDate);
          break;
        case 'Run':
          response = await statsApi.getRunningStats(fromDate, toDate);
          break;
        case 'Hike':
          response = await statsApi.getHikingStats(fromDate, toDate);
          break;
        case 'Ride':
          response = await statsApi.getRidingStats(fromDate, toDate);
          break;
        default:
          return null;
      }
      return response.data;
    } catch (error) {
      console.error(`Error fetching ${activityType} stats:`, error);
      return null;
    }
  };

  const formatDistance = (km) => {
    if (!km) return '0.0';
    return km.toFixed(1);
  };

  const formatTime = (minutes) => {
    if (!minutes) return '0h';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0 && mins > 0) return `${hours}h ${mins}m`;
    if (hours > 0) return `${hours}h`;
    return `${mins}m`;
  };

  const formatElevation = (meters) => {
    if (!meters) return '0';
    return Math.round(meters);
  };

  const renderActivityPanel = (activityType) => {
    const data = activityData[activityType];
    const Icon = activityIcons[activityType];
    const color = activityColors[activityType];

    if (data.loading) {
      return (
        <div className="activity-type-panel" key={activityType} style={{ borderTopColor: color }}>
          <div className="panel-header" style={{ background: `linear-gradient(135deg, ${color} 0%, ${color}dd 100%)` }}>
            <Icon className="panel-icon" />
            <h2>{activityType}</h2>
          </div>
          <div className="panel-content">
            <LoadingSpinner />
          </div>
        </div>
      );
    }

    const stats = data.stats || {};
    const members = data.members || [];

    return (
      <div className="activity-type-panel" key={activityType} style={{ borderTopColor: color }}>
        <div className="panel-header" style={{ background: `linear-gradient(135deg, ${color} 0%, ${color}dd 100%)` }}>
          <Icon className="panel-icon" />
          <h2>{activityType}</h2>
        </div>

        <div className="panel-content">
          {/* Overall Statistics */}
          <div className="panel-stats-grid">
            <div className="panel-stat-card">
              <FaRoute className="stat-card-icon" style={{ color }} />
              <div className="stat-card-content">
                <span className="stat-card-value">{formatDistance(stats.totalDistance)} km</span>
                <span className="stat-card-label">Total Distance</span>
              </div>
            </div>
            <div className="panel-stat-card">
              <FaUsers className="stat-card-icon" style={{ color }} />
              <div className="stat-card-content">
                <span className="stat-card-value">{stats.totalActivities || 0}</span>
                <span className="stat-card-label">Total Activities</span>
              </div>
            </div>
            <div className="panel-stat-card">
              <FaClock className="stat-card-icon" style={{ color }} />
              <div className="stat-card-content">
                <span className="stat-card-value">{formatTime(stats.totalMovingTime)}</span>
                <span className="stat-card-label">Total Time</span>
              </div>
            </div>
            <div className="panel-stat-card">
              <FaMountain className="stat-card-icon" style={{ color }} />
              <div className="stat-card-content">
                <span className="stat-card-value">{formatElevation(stats.totalElevationGain)} m</span>
                <span className="stat-card-label">Elevation Gain</span>
              </div>
            </div>
          </div>

          {/* Members List */}
          <div className="panel-members-section">
            <h3>
              <FaUsers style={{ marginRight: '6px', color, fontSize: '0.75rem' }} />
              Members ({members.length})
            </h3>
            {members.length > 0 ? (
              <div className="panel-members-list">
                {members.map((member, idx) => (
                  <div className="panel-member-card" key={idx}>
                    <div className="member-rank" style={{ background: color }}>
                      #{idx + 1}
                    </div>
                    <div className="member-info">
                      <span className="member-name">{member.athleteName}</span>
                      <div className="member-stats">
                        <span className="member-stat">
                          <FaRoute /> {formatDistance(member.totalDistance)} km
                        </span>
                        <span className="member-stat">
                          <FaUsers /> {member.totalActivities} activities
                        </span>
                        <span className="member-stat">
                          <FaClock /> {formatTime(member.totalMovingTime)}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="no-members">
                No members found for {activityType}
              </div>
            )}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="activity-type-panels-container">
      <div className="panels-grid">
        {activityTypes.map(activityType => renderActivityPanel(activityType))}
      </div>
    </div>
  );
};

export default ActivityTypePanels;
