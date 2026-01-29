import React from 'react';
import MemberCard from './MemberCard';
import './MemberList.css';

const MemberList = ({ members }) => {
  return (
    <div className="member-list-container">
      <h2>Club Members Activity Breakdown</h2>
      <div className="member-grid">
        {members.map((member, index) => (
          <MemberCard key={index} member={member} />
        ))}
      </div>
    </div>
  );
};

export default MemberList;