import { useState, useEffect } from 'react';
import axios from 'axios';
import {
  AreaChart, Area, XAxis, YAxis, ResponsiveContainer, Tooltip,
  PieChart, Pie, Cell, RadialBarChart, RadialBar
} from 'recharts';
import {
  Shield, ShieldAlert, Server, Router, Network, Cpu,
  Activity, AlertTriangle, Wifi, WifiOff, Monitor, Lock
} from 'lucide-react';
import './App.css';

const API = 'http://localhost:8081/api';

function App() {
  const [devices, setDevices] = useState([]);
  const [events, setEvents] = useState([]);
  const [score, setScore] = useState({ score: 100, rating: 'SECURE' });
  const [stats, setStats] = useState({});
  const [trafficHistory, setTrafficHistory] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [d, e, s, st] = await Promise.all([
          axios.get(`${API}/devices`),
          axios.get(`${API}/events`),
          axios.get(`${API}/security-score`),
          axios.get(`${API}/stats`),
        ]);
        setDevices(d.data);
        setEvents(e.data);
        setScore(s.data);
        setStats(st.data);

        setTrafficHistory(prev => {
          const next = [...prev, {
            time: new Date().toLocaleTimeString(),
            traffic: st.data.totalTrafficMbps || 0,
          }];
          return next.slice(-20);
        });
      } catch (err) {
        console.error('Fetch error:', err);
      }
    };

    fetchData();
    const interval = setInterval(fetchData, 3000);
    return () => clearInterval(interval);
  }, []);

  const deviceIcon = (type) => {
    switch (type) {
      case 'ROUTER': return <Router size={18} />;
      case 'SWITCH': return <Network size={18} />;
      case 'SERVER': return <Server size={18} />;
      case 'FIREWALL': return <Shield size={18} />;
      case 'WORKSTATION': return <Monitor size={18} />;
      default: return <Server size={18} />;
    }
  };

  const severityColor = (sev) => {
    switch (sev) {
      case 'CRITICAL': return '#ff3b5c';
      case 'HIGH': return '#ff9f40';
      case 'MEDIUM': return '#ffd93d';
      case 'LOW': return '#4dd4ac';
      default: return '#6b7280';
    }
  };

  const scoreColor = score.score >= 90 ? '#4dd4ac' :
                     score.score >= 70 ? '#7dd3fc' :
                     score.score >= 50 ? '#ffd93d' :
                     score.score >= 30 ? '#ff9f40' : '#ff3b5c';

  const statusData = [
    { name: 'Online', value: stats.devicesOnline || 0, color: '#4dd4ac' },
    { name: 'Warning', value: stats.devicesWarning || 0, color: '#ffd93d' },
    { name: 'Offline', value: stats.devicesOffline || 0, color: '#ff3b5c' },
  ];

  return (
    <div className="dashboard">
      {/* Header */}
      <header className="header">
        <div className="logo">
          <Shield className="logo-icon" size={32} />
          <div>
            <h1>WATCHTOWER</h1>
            <span className="subtitle">Network Security Operations Center</span>
          </div>
        </div>
        <div className="header-status">
          <div className="live-indicator">
            <span className="live-dot"></span> LIVE
          </div>
          <div className="clock">{new Date().toLocaleTimeString()}</div>
        </div>
      </header>

      {/* Top stats row */}
      <div className="stats-row">
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'rgba(125,211,252,0.15)' }}>
            <Network size={22} color="#7dd3fc" />
          </div>
          <div>
            <div className="stat-value">{stats.totalDevices || 0}</div>
            <div className="stat-label">Total Devices</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'rgba(77,212,172,0.15)' }}>
            <Wifi size={22} color="#4dd4ac" />
          </div>
          <div>
            <div className="stat-value">{stats.devicesOnline || 0}</div>
            <div className="stat-label">Online</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'rgba(255,59,92,0.15)' }}>
            <AlertTriangle size={22} color="#ff3b5c" />
          </div>
          <div>
            <div className="stat-value">{stats.criticalEvents || 0}</div>
            <div className="stat-label">Critical Threats</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'rgba(77,212,172,0.15)' }}>
            <Lock size={22} color="#4dd4ac" />
          </div>
          <div>
            <div className="stat-value">{stats.blockedThreats || 0}</div>
            <div className="stat-label">Blocked</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'rgba(255,159,64,0.15)' }}>
            <Activity size={22} color="#ff9f40" />
          </div>
          <div>
            <div className="stat-value">{stats.totalTrafficMbps || 0}</div>
            <div className="stat-label">Mbps Traffic</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon" style={{ background: 'rgba(167,139,250,0.15)' }}>
            <Cpu size={22} color="#a78bfa" />
          </div>
          <div>
            <div className="stat-value">{stats.avgCpuUsage || 0}%</div>
            <div className="stat-label">Avg CPU</div>
          </div>
        </div>
      </div>

      {/* Main grid */}
      <div className="main-grid">
        {/* Security Score */}
        <div className="panel score-panel">
          <h2>Security Score</h2>
          <div className="score-gauge">
            <ResponsiveContainer width="100%" height={180}>
              <RadialBarChart
                innerRadius="70%" outerRadius="100%"
                data={[{ value: score.score, fill: scoreColor }]}
                startAngle={90} endAngle={-270}
              >
                <RadialBar background={{ fill: 'rgba(255,255,255,0.05)' }} dataKey="value" cornerRadius={10} />
              </RadialBarChart>
            </ResponsiveContainer>
            <div className="score-center">
              <div className="score-number" style={{ color: scoreColor }}>{score.score}</div>
              <div className="score-rating" style={{ color: scoreColor }}>{score.rating}</div>
            </div>
          </div>
        </div>

        {/* Traffic Chart */}
        <div className="panel traffic-panel">
          <h2>Network Traffic (Live)</h2>
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={trafficHistory}>
              <defs>
                <linearGradient id="trafGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#7dd3fc" stopOpacity={0.6} />
                  <stop offset="100%" stopColor="#7dd3fc" stopOpacity={0} />
                </linearGradient>
              </defs>
              <XAxis dataKey="time" stroke="#4b5563" fontSize={10} />
              <YAxis stroke="#4b5563" fontSize={10} />
              <Tooltip contentStyle={{ background: '#111827', border: '1px solid #374151', borderRadius: 8 }} />
              <Area type="monotone" dataKey="traffic" stroke="#7dd3fc" fill="url(#trafGrad)" strokeWidth={2} />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Device Status Pie */}
        <div className="panel status-panel">
          <h2>Device Status</h2>
          <ResponsiveContainer width="100%" height={200}>
            <PieChart>
              <Pie data={statusData} dataKey="value" nameKey="name" innerRadius={50} outerRadius={80} paddingAngle={4}>
                {statusData.map((entry, i) => <Cell key={i} fill={entry.color} />)}
              </Pie>
              <Tooltip contentStyle={{ background: '#111827', border: '1px solid #374151', borderRadius: 8 }} />
            </PieChart>
          </ResponsiveContainer>
          <div className="status-legend">
            {statusData.map((s, i) => (
              <div key={i} className="legend-item">
                <span className="legend-dot" style={{ background: s.color }}></span>
                {s.name}: {s.value}
              </div>
            ))}
          </div>
        </div>

        {/* Device Grid */}
        <div className="panel devices-panel">
          <h2>Network Devices</h2>
          <div className="device-list">
            {devices.map(d => (
              <div key={d.id} className={`device-row status-${d.status.toLowerCase()}`}>
                <div className="device-info">
                  <span className="device-icon">{deviceIcon(d.deviceType)}</span>
                  <div>
                    <div className="device-name">{d.name}</div>
                    <div className="device-ip">{d.ipAddress} · {d.location}</div>
                  </div>
                </div>
                <div className="device-metrics">
                  <div className="metric">
                    <span className="metric-label">TRAFFIC</span>
                    <span className="metric-val">{d.trafficMbps} Mbps</span>
                  </div>
                  <div className="metric">
                    <span className="metric-label">CPU</span>
                    <span className="metric-val">{d.cpuUsage}%</span>
                  </div>
                  <div className="metric">
                    <span className="metric-label">THREAT</span>
                    <span className="metric-val" style={{ color: d.threatLevel > 50 ? '#ff3b5c' : d.threatLevel > 25 ? '#ffd93d' : '#4dd4ac' }}>
                      {d.threatLevel}
                    </span>
                  </div>
                  <span className={`status-badge status-${d.status.toLowerCase()}`}>{d.status}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Security Events Feed */}
        <div className="panel events-panel">
          <h2>Live Threat Feed</h2>
          <div className="event-list">
            {events.map(e => (
              <div key={e.id} className="event-row">
                <span className="event-severity" style={{ background: severityColor(e.severity) }}></span>
                <div className="event-content">
                  <div className="event-top">
                    <span className="event-type">{e.eventType.replace('_', ' ')}</span>
                    <span className="event-sev-tag" style={{ color: severityColor(e.severity) }}>{e.severity}</span>
                  </div>
                  <div className="event-desc">{e.description}</div>
                  <div className="event-meta">
                    <span>{e.sourceIp} → {e.targetDevice}</span>
                    <span className={`event-status es-${e.status.toLowerCase()}`}>{e.status}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
