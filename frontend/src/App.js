import React from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import Register from "./components/register/Register";
import Login from "./components/login/Login";
import Home from "./components/home/Home";
import ProtectedRoute from "./components/ProtectedRoute";
import { ToastContainer } from "react-toastify";
import "./App.css";
import "react-toastify/dist/ReactToastify.css";

function App() {
  return (
    <div className="App">
      <Router>
        <Routes>
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Home />
              </ProtectedRoute>
            }
          />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route
            path="*"
            element={
              <ProtectedRoute>
                <Navigate to="/" replace />
              </ProtectedRoute>
            }
          />
        </Routes>
        <ToastContainer 
          autoClose={1500} // Duration in milliseconds before the toast automatically closes
          hideProgressBar={false} // Whether to hide the progress bar
          newestOnTop={false} // Whether to display the newest toast on top
          closeOnClick={true} // Whether to close the toast on click
          pauseOnHover={false} // Whether to pause the auto close timer on hover
          position="bottom-left" // Position of the toast (top-right, top-left, bottom-right, bottom-left, bottom-center, top-center, bottom-center)
          theme="light" // Theme of the toast (light, dark, colored)
        />
      </Router>
    </div>
  );
}

export default App;
