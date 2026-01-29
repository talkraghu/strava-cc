import React from 'react';
import MemberCard from './MemberCard';
import { FaTrophy } from 'react-icons/fa';
import './Leaderboard.css';

const Leaderboard = ({ leaderboard }) => {
  return (
    <div className="leaderboard-container">
      <div className="leaderboard-header">
        <FaTrophy className="trophy-icon" />
        <h2>Top Performers Leaderboard</h2>
      </div>
      <div className="leaderboard-grid">
        {leaderboard.map((member, index) => (
          <MemberCard key={index} member={member} rank={index + 1} />
        ))}
      </div>
    </div>
  );
};

export default Leaderboard;