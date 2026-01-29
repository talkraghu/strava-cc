import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const statsApi = {
  getAllMembers: (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/members', { params });
  },

  getClubStats: (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/club', { params });
  },

  getMemberStats: (athleteName, fromDate, toDate) => {
    const params = { athleteName };
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/member', { params });
  },

  getActivityStats: (activityType, fromDate, toDate) => {
    const params = {};
    if (activityType) params.activityType = activityType;
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/activity-type', { params });
  },

  getLeaderboard: (limit = 10, fromDate, toDate) => {
    const params = { limit };
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/leaderboard', { params });
  },

  getMembersByActivityType: (activityType, fromDate, toDate) => {
    const params = { activityType };
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/members-by-activity-type', { params });
  },

  getRunningStats: (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/running', { params });
  },

  getRidingStats: (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/riding', { params });
  },

  getWalkingStats: (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/walking', { params });
  },

  getHikingStats: (fromDate, toDate) => {
    const params = {};
    if (fromDate) params.fromDate = fromDate;
    if (toDate) params.toDate = toDate;
    return api.get('/stats/hiking', { params });
  },
};

export default api;