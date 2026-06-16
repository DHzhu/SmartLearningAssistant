import { Link, useLocation } from 'react-router-dom';
import './Navbar.css';

export function Navbar() {
  const location = useLocation();

  const navItems = [
    { path: '/', label: '对话', icon: '💬' },
    { path: '/knowledge', label: '知识库', icon: '📚' },
    { path: '/billing', label: '配额', icon: '⚡' },
  ];

  return (
    <nav className="navbar">
      <div className="navbar__container">
        <div className="navbar__brand">
          <span className="navbar__logo">🧠</span>
          <span className="navbar__brand-text">Smart Learning Assistant</span>
        </div>

        <div className="navbar__links">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`navbar__link ${
                location.pathname === item.path ? 'navbar__link--active' : ''
              }`}
            >
              <span className="navbar__link-icon">{item.icon}</span>
              <span className="navbar__link-text">{item.label}</span>
            </Link>
          ))}
        </div>

        <div className="navbar__user">
          <button className="navbar__logout" onClick={() => {
            // Add logout functionality
            console.log('Logout clicked');
          }}>
            <span>🚪</span>
          </button>
        </div>
      </div>
    </nav>
  );
}