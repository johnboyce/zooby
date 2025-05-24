import React from "react";
import ActivationStatus from "./ActivationStatus";

export default function App() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-slate-900 to-gray-950 text-white p-6 max-w-5xl mx-auto">
      <h1 className="text-4xl font-bold text-cyan-400 mb-8">Zooby Dashboard</h1>
      <ActivationStatus />
    </main>
  );
}