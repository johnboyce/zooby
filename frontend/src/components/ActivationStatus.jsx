import { useEffect, useState } from "react";
import { fetchGraphQL } from "../graphql/client";

export default function ActivationStatus({ transactionId }) {
    const [status, setStatus] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        async function getStatus() {
            try {
                const query = `
          query GetActivationStatus($transactionId: String!) {
            activationStatus(transactionId: $transactionId) {
              macAddress
              transactionId
              userId
              status
              stepsLog
              updatedAt
            }
          }
        `;
                const data = await fetchGraphQL(query, { transactionId });
                setStatus(data.activationStatus);
            } catch (err) {
                setError(err.message);
            }
        }

        if (transactionId) getStatus();
    }, [transactionId]);

    if (error) return <div className="text-red-500">Error: {error}</div>;
    if (!status) return <div>Loading...</div>;

    return (
        <div className="p-4 bg-white rounded shadow">
            <h2 className="text-xl font-bold mb-2">Zooby Activation Status</h2>
            <p><strong>Status:</strong> {status.status}</p>
            <p><strong>MAC Address:</strong> {status.macAddress}</p>
            <p><strong>Transaction ID:</strong> {status.transactionId}</p>
            <p><strong>Last Updated:</strong> {status.updatedAt}</p>
            <ul className="mt-2 list-disc list-inside text-sm text-gray-700">
                {status.stepsLog.map((step, i) => (
                    <li key={i}>{step}</li>
                ))}
            </ul>
        </div>
    );
}

