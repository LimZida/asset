/* eslint-disable */
import "./App.css";
import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login.js";
import Main from "./pages/Main.js";
// import Hardwares from "./pages/Hardwares.js";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/main/*" element={<Main />} />
        {/* <Route path="/hardwares" element={<Hardwares />} /> */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

export default App;
