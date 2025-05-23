import { useEffect, useState } from "react";
import { fetchGraphQL } from "../graphql/client";

export default function ActivationStatus({ transactionId }) {
  const [status, setStatus] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const query = `
      query GetActivationStatus($transactionId: String!) {
        activationStatus(transactionId: $transactionId) {
          status
          macAddress
          stepsLog
        }
      }
    `;
    fetchGraphQL(query, { transactionId })
      .then(data => setStatus(data.activationStatus))
      .catch(err => setError(err.message));
  }, [transactionId]);

  if (error) return <div style={{ color: 'red' }}>Error: {error}</div>;
  if (!status) return <div>Loading activation status...</div>;

  return (
    <div>
      <h2>Status: {status.status}</h2>
      <p>MAC: {status.macAddress}</p>
      <ul>
        {status.stepsLog.map((step, i) => (
          <li key={i}>{step}</li>
        ))}
      </ul>
    </div>
  );
}
