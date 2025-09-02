import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import './Home.css';
import paleBlueDotText from '../../assets/home/the-pale-blue-dot.txt';

function Home() {
  const [text, setText] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetch(paleBlueDotText)
      .then(response => response.text())
      .then(data => setText(data))
      .catch(error => console.error('Error loading text:', error));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    toast.success('Logged out successfully');
    navigate('/login');
  };

  return (
    <div>
      <h1>The Pale Blue Dot</h1>
      <div className="home-container">
        <div className="image-container">
        </div>

        <div className="text-container">
          <p>{text.split('\n').map((line) => line + '\n')}</p>
        </div>
      </div>

      
      <div className="logout-container">
        <button onClick={handleLogout} className="logout-button">Logout</button>
      </div>
    </div>
  );
}

export default Home;
