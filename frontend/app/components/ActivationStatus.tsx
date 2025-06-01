"use client";
import { useEffect, useState } from "react";

export default function ActivationStatus() {
  const [status, setStatus] = useState("INPROGRESS");
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const steps = [
    "Initializing",
    "Checking Model",
    "Checking Firmware",
    "Contacting Zoom",
    "Registering Zooby",
    "Bootfile Ready",
    "Zoombified"
  ];

  const getStepColor = (index: number) => {
    const colors = [
      "text-red-600",
      "text-red-400",
      "text-yellow-600",
      "text-yellow-400",
      "text-green-600",
      "text-green-500",
      "text-green-400"
    ];
    return colors[index];
  };

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentStepIndex(prevIndex => {
        if (prevIndex >= steps.length - 1) {
          clearInterval(interval);
          setStatus("SUCCESS");
          return prevIndex;
        }
        return prevIndex + 1;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const statusColor =
    status === "SUCCESS"
      ? "bg-green-500 text-black"
      : status === "FAIL"
        ? "bg-red-600 text-white"
        : "bg-yellow-400 text-black";

  return (
    <section className="bg-slate-800 rounded-xl shadow-lg p-6 min-w-[400px]">
      <h2 className="text-2xl font-bold text-cyan-300 mb-4">
        🔧 Activation Tracker
      </h2>

      <div className="space-y-4">
        <div className="flex items-center gap-4">
          <span className="text-gray-400 font-medium w-32">Current Status:</span>
          <span className={`px-3 py-1 rounded-full text-sm font-bold shadow ${statusColor}`}>
            {status}
          </span>
        </div>

        <div className="bg-black/50 p-3 rounded-lg border border-cyan-800/30">
          <p className={`font-mono text-lg font-bold tracking-wide text-center ${getStepColor(currentStepIndex)}`}>
            {steps[currentStepIndex].toUpperCase()}
          </p>
        </div>
      </div>
    </section>
  );
}
