import React, { useState, useEffect } from "react";

export default function ActivationStatus() {
  const [status, setStatus] = useState("INPROGRESS");
  const [steps, setSteps] = useState([
    "Initializing",
    "Contacting Zoomba",
    "Bootfile Ready"
  ]);

  useEffect(() => {
    // Replace with real fetch to GraphQL later
    const timer = setTimeout(() => {
      setStatus("SUCCESS");
    }, 5000);
    return () => clearTimeout(timer);
  }, []);

  const statusColor =
    status === "SUCCESS"
      ? "bg-green-500 text-black"
      : status === "FAIL"
      ? "bg-red-600 text-white"
      : "bg-yellow-400 text-black";

  return (
    <section className="bg-slate-800 rounded-xl shadow-lg p-6 mt-8">
      <h2 className="text-2xl font-bold text-cyan-300 mb-4">
        🔧 Activation Tracker
      </h2>

      <div className="mb-4 flex items-center justify-between">
        <span className="text-gray-400 font-medium">Current Status:</span>
        <span className={`px-3 py-1 rounded-full text-sm font-bold shadow ${statusColor}`}>
        {status}
        </span>
      </div>

      <ol className="border-l-2 border-cyan-400 pl-6 space-y-3">
        {steps.map((step, i) => (
          <li key={i} className="relative text-gray-200">
            <span className="absolute -left-[9px] top-1.5 w-3 h-3 bg-cyan-300 rounded-full"></span>
            {step}
          </li>
        ))}
      </ol>
    </section>
  );
}
