import React from 'react'
import ReactDOM from 'react-dom/client'
import ActivationStatus from './components/ActivationStatus'

const App = () => (
  <div style={{ padding: 40, fontFamily: 'sans-serif' }}>
    <h1>🔧 Zooby Activation Status</h1>
    <ActivationStatus transactionId="txn-AABBCCDDEE00-1716515612" />
  </div>
)

ReactDOM.createRoot(document.getElementById('root')).render(<App />)
