import React, { useState, useEffect } from 'react';
import './styles/App.css';
import MemberList from './components/MemberList';
import ClubOverview from './components/ClubOverview';
import Leaderboard from './components/Leaderboard';
import ActivityStats from './components/ActivityStats';
import DateRangePicker from './components/DateRangePicker';
import LoadingSpinner from './components/LoadingSpinner';
import { statsApi } from './services/api';
import { FaUsers, FaTrophy, FaChartBar, FaRunning } from 'react-icons/fa';

function App() {
  // Start with no date filter so overview shows all-time totals (proper data).
  // User can set From/To and click Apply to filter by date.
  const [members, setMembers] = useState([]);
  const [clubStats, setClubStats] = useState(null);
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);
  const [fromDate, setFromDate] = useState(null);
  const [toDate, setToDate] = useState(null);
  const [activeTab, setActiveTab] = useState('overview');
  const [activityTypeFilter, setActivityTypeFilter] = useState('');
  const [membersByActivityType, setMembersByActivityType] = useState([]);

  useEffect(() => {
    loadData();
  }, [fromDate, toDate]);

  useEffect(() => {
    if (activityTypeFilter) {
      statsApi.getMembersByActivityType(activityTypeFilter, fromDate, toDate)
        .then((res) => setMembersByActivityType(res.data || []))
        .catch((err) => { console.error(err); setMembersByActivityType([]); });
    } else {
      setMembersByActivityType([]);
    }
  }, [activityTypeFilter, fromDate, toDate]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [membersRes, clubRes, leaderboardRes] = await Promise.all([
        statsApi.getAllMembers(fromDate, toDate),
        statsApi.getClubStats(fromDate, toDate),
        statsApi.getLeaderboard(20, fromDate, toDate),
      ]);
      setMembers(membersRes.data);
      setClubStats(clubRes.data);
      setLeaderboard(leaderboardRes.data);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDateRangeChange = (from, to) => {
    setFromDate(from);
    setToDate(to);
  };

  return (
    <div className="App">
      <header className="app-header">
        <div className="header-content">
          <h1>
            <FaRunning className="header-icon" />
            Strava Club Analytics Dashboard
          </h1>
          <div className="header-filters">
            <DateRangePicker
              onDateRangeChange={handleDateRangeChange}
              autoApplyInitial={false}
            />
            <div className="activity-type-filter">
              <label htmlFor="activity-type">Activity type:</label>
              <select
                id="activity-type"
                value={activityTypeFilter}
                onChange={(e) => setActivityTypeFilter(e.target.value)}
              >
                <option value="">All (total distance)</option>
                <option value="Walk">Walk</option>
                <option value="Ride">Ride</option>
                <option value="Hike">Hike</option>
                <option value="Run">Run</option>
              </select>
            </div>
          </div>
        </div>
      </header>

      <nav className="tab-navigation">
        <button
          className={activeTab === 'overview' ? 'active' : ''}
          onClick={() => setActiveTab('overview')}
        >
          <FaChartBar /> Overview
        </button>
        <button
          className={activeTab === 'members' ? 'active' : ''}
          onClick={() => setActiveTab('members')}
        >
          <FaUsers /> All Members
        </button>
        <button
          className={activeTab === 'leaderboard' ? 'active' : ''}
          onClick={() => setActiveTab('leaderboard')}
        >
          <FaTrophy /> Leaderboard
        </button>
        <button
          className={activeTab === 'activities' ? 'active' : ''}
          onClick={() => setActiveTab('activities')}
        >
          <FaChartBar /> Activity Stats
        </button>
      </nav>

      <main className="main-content">
        {loading ? (
          <LoadingSpinner />
        ) : (
          <>
            {activeTab === 'overview' && (
              <ClubOverview
                stats={clubStats}
                members={members}
                activityTypeFilter={activityTypeFilter}
                membersByActivityType={membersByActivityType}
              />
            )}
            {activeTab === 'members' && <MemberList members={members} />}
            {activeTab === 'leaderboard' && (
              <Leaderboard leaderboard={leaderboard} />
            )}
            {activeTab === 'activities' && (
              <ActivityStats fromDate={fromDate} toDate={toDate} />
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default App;