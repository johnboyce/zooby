import React from 'react';
import ReactDOM from 'react-dom/client';
import ActivationStatus from './components/ActivationStatus';

const App = () => {
    // Replace this with a real transactionId
    const testTransactionId = "txn-AABBCCDDEE00-1716515612";

    return (
        <div className="min-h-screen p-8 bg-gray-100">
            <ActivationStatus transactionId={testTransactionId} />
        </div>
    );
};

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
