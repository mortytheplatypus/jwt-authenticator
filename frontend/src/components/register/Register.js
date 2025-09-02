import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "./Register.css";

import { URLS } from "../../config";
import { HttpStatusCode } from "axios";

export default function Register() {
  const [user, setUser] = useState({
    name: "",
    email: "",
    username: "",
    password: ""
  });
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  const token = localStorage.getItem("token");

  useEffect(() => {
    if (token) {
      navigate("/");
    }
  }, [token, navigate]);

  const validateForm = () => {
    const newErrors = {};

    // Required field validation
    if (!user.name.trim()) {
      newErrors.name = "Name is required";
    }
    if (!user.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(user.email)) {
      newErrors.email = "Invalid email format";
    }
    if (!user.username.trim()) {
      newErrors.username = "Username is required";
    }
    if (!user.password) {
      newErrors.password = "Password is required";
    }
    if (!confirmPassword) {
      newErrors.confirmPassword = "Confirm password is required";
    }

    if (user.name && !/^[A-Za-z\s]+$/.test(user.name)) {
      newErrors.name = "Name should only contain letters and spaces";
    }
    if (user.username && !/^[A-Za-z][A-Za-z0-9]*$/.test(user.username)) {
      newErrors.username =
        "Username should start with a letter and contain only letters and numbers";
    }

    if (!user.password) {
      newErrors.password = "Password is required";
    } else if (user.password.length < 8) {
      newErrors.password = "Password should be at least 8 characters long";
    } else if (
      !/^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&<>])[A-Za-z\d@$!%*#?&]{8,}$/.test(
        user.password
      )
    ) {
      newErrors.password =
        "Password should contain at least one letter, one number, and one special character (@$!%*#?&<>)";
    }
    if (user.password && confirmPassword && user.password !== confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateForm()) {
      try {
        const response = await fetch(URLS.REGISTER, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(user),
        });

        if (response.status !== HttpStatusCode.Created) {
          throw new Error(response.statusText);
        }

        const data = await response.json();

        toast.success(data.message);
        navigate("/login");
      } catch (error) {
        console.error("Registration error:", error);
        toast.error("An error occurred during registration. Please try again.");
      }
    }
  };

  const handleInputChange = (field) => (e) => {
    setUser(prev => ({
      ...prev,
      [field]: e.target.value
    }));
  };

  return (
    <div className="register-container">
      <form className="register-form" onSubmit={handleSubmit}>
        <h2 className="register-title">Register</h2>

        <div className="form-group">
          <input
            type="text"
            id="name"
            name="name"
            placeholder=""
            value={user.name}
            onChange={handleInputChange('name')}
            required
          />
          <label htmlFor="username">Full Name</label>
          {errors.name && <span className="error-message">{errors.name}</span>}
        </div>

        <div className="form-group">
          <input
            type="text"
            id="email"
            name="email"
            placeholder=""
            value={user.email}
            onChange={handleInputChange('email')}
            required
          />
          <label htmlFor="email">Email</label>
          {errors.email && (
            <span className="error-message">{errors.email}</span>
          )}
        </div>

        <div className="form-group">
          <input
            type="text"
            id="username"
            name="username"
            placeholder=""
            value={user.username}
            onChange={handleInputChange('username')}
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
            placeholder=""
            value={user.password}
            onChange={handleInputChange('password')}
            required
          />
          <label htmlFor="username">Password</label>
          {errors.password && (
            <span className="error-message">{errors.password}</span>
          )}
        </div>

        <div className="form-group">
          <input
            type="password"
            id="password"
            name="password"
            placeholder=""
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
          <label htmlFor="username">Confirm Password</label>
          {errors.confirmPassword && (
            <span className="error-message">{errors.confirmPassword}</span>
          )}
        </div>

        <div className="form-button">
          <button type="submit" className="register-button">
            Register
          </button>
        </div>

        <div className="form-footer">
          <p className="login-prompt">
            Already have an account? <a href="/login">Login</a>
          </p>
        </div>
      </form>
    </div>
  );
}
