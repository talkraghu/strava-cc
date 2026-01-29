# Strava Analytics Dashboard

A beautiful, modern React.js dashboard for visualizing Strava club activity statistics.

## Features

- ðŸ“Š **Club Overview**: Visual statistics with charts and key metrics
- ðŸ‘¥ **Member List**: Detailed breakdown of all club members' activities
- ðŸ† **Leaderboard**: Top performers ranked by distance
- ðŸ“ˆ **Activity Stats**: Comprehensive statistics by activity type (Run, Ride, Walk, Hike)
- ðŸ“… **Date Range Filtering**: Filter data by custom date ranges
- ðŸŽ¨ **Modern UI**: Beautiful, responsive design with animations

## Prerequisites

- Node.js 16+ and npm
- Spring Boot backend running on `http://localhost:8080`

## Installation

1. Navigate to the dashboard directory:
```bash
cd strava-dashboard
```

2. Install dependencies:
```bash
npm install
```

## Running the Dashboard

Start the development server:
```bash
npm start
```

The dashboard will open at `http://localhost:3000`

## Project Structure

```
strava-dashboard/
â”œâ”€â”€ src/
â”‚   â”œâ”€â”€ App.js                 # Main application component
â”‚   â”œâ”€â”€ index.js               # Entry point
â”‚   â”œâ”€â”€ components/            # React components
â”‚   â”‚   â”œâ”€â”€ MemberCard.js      # Individual member card
â”‚   â”‚   â”œâ”€â”€ MemberList.js      # List of all members
â”‚   â”‚   â”œâ”€â”€ ClubOverview.js    # Club statistics overview
â”‚   â”‚   â”œâ”€â”€ Leaderboard.js     # Top performers
â”‚   â”‚   â”œâ”€â”€ ActivityStats.js   # Activity type statistics
â”‚   â”‚   â”œâ”€â”€ DateRangePicker.js # Date filtering
â”‚   â”‚   â””â”€â”€ LoadingSpinner.js  # Loading indicator
â”‚   â”œâ”€â”€ services/
â”‚   â”‚   â””â”€â”€ api.js            # API service layer
â”‚   â””â”€â”€ styles/               # CSS files
â””â”€â”€ public/
    â””â”€â”€ index.html
```

## Usage

1. **Overview Tab**: View overall club statistics, charts, and top performers
2. **All Members Tab**: Browse all club members with their activity breakdowns
3. **Leaderboard Tab**: See top performers ranked by total distance
4. **Activity Stats Tab**: View detailed statistics for each activity type

Use the date range picker in the header to filter data by specific time periods.

## Technologies Used

- React 18
- Chart.js & react-chartjs-2
- Axios
- React Icons
- CSS3 with animations

## Building for Production

```bash
npm run build
```

This creates an optimized production build in the `build` folder.