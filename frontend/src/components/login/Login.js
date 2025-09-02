import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { toast } from "react-toastify";
import "./Login.css";
import { URLS } from "../../config";
import { HttpStatusCode } from "axios";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState({});
  const [loginMessage, setLoginMessage] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (token) {
      navigate("/");
    }

    if (location.state?.message) {
      setLoginMessage(location.state.message);
      navigate(location.pathname, { replace: true, state: {} });
    }
  }, [token, navigate, location]);

  const validateForm = () => {
    const newErrors = {};

    if (!username.trim()) {
      newErrors.username = "Username is required";
    }
    if (!password) {
      newErrors.password = "Password is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateForm()) {
      try {
        const response = await fetch(URLS.LOGIN, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ username, password }),
        });

        if (response.status !== HttpStatusCode.Ok) {
          let errorMessage;
          
          switch (response.status) {
            case 404:
              errorMessage = "User not found. Please check your username.";
              break;
            case 401:
              errorMessage = "Invalid password. Please try again.";
              break;
            case 500:
              errorMessage = "Login service is currently unavailable. Please try again later.";
              break;
            default:
              errorMessage = "Login failed. Please check your credentials.";
          }
          
          toast.error(errorMessage);
          throw new Error(response.statusText);
        }

        const data = await response.json();

        toast.success("Login successful!");
        localStorage.setItem("token", data.token);
        navigate("/");
      } catch (error) {
        console.error("Login error:", error);
        toast.error("An error occurred during login. Please try again.");
      }
    }
  };

  return (
    <div className="login-container">
      {loginMessage && <div className="login-message">{loginMessage}</div>}
      <form className="login-form" onSubmit={handleSubmit}>
        <h2 className="login-title">Login</h2>
        <div className="form-group">
          <input
            type="text"
            id="username"
            name="username"
            placeholder=" "
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <label htmlFor="username">Username</label>
          {errors.username && (
            <span className="error-message">{errors.username}</span>
          )}
        </div>

        <div className="form-group">
          <input
            type="password"
            id="password"
            name="password"
            placeholder=" "
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <label htmlFor="password">Password</label>
          {errors.password && (
            <span className="error-message">{errors.password}</span>
          )}
        </div>

        <div className="form-button">
          <button type="submit" className="login-button">
            Log In
          </button>
        </div>

        <div className="form-footer">
          <p className="register-prompt">
            Don't have an account? <a href="/register">Register</a>
          </p>
        </div>
      </form>
    </div>
  );
}

export default Login;
