import React from 'react';
import {BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import NotFound from './pages/NotFound';
import IddaeMohae from './pages/IddaeMohae';
import { ErrorBoundary } from 'react-error-boundary';
import Error from './pages/Error';

function App() {
  return (
    <ErrorBoundary FallbackComponent={Error}>
    <BrowserRouter>
    <Routes>
      <Route exact path="/" element= {<Home  />}  />
      {/* <Route exact path="/edit/:id" element= {<Edit  />} /> */}
      <Route path="/login" element= {<Login  />}  />
      <Route path="/signup" element= {<Signup  />} />
      <Route path="/iddaemohae" element= {<IddaeMohae  />} />
      <Route element= {<NotFound  />}  />
    </Routes>
    </BrowserRouter>
    </ErrorBoundary>
  )
}

export default App;
