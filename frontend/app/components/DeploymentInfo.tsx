'use client';
import { useEffect, useState, useCallback } from 'react';

export default function DeploymentInfo() {
  const [deployTime] = useState(() => new Date().toISOString());
  const [timeSince, setTimeSince] = useState<string>('');
  const [isDefault, setIsDefault] = useState(true);

  useEffect(() => {
    fetch("deploy-meta.json")
      .then(res => res.json())
      .then(() => {
        setIsDefault(false);
      })
      .catch(() => {
        setIsDefault(true);
      });
  }, []);

  const updateTimeSince = useCallback(() => {
    const now = new Date();
    const diff = Math.floor((now.getTime() - new Date(deployTime).getTime()) / 1000);

    const days = Math.floor(diff / 86400);
    const hours = Math.floor((diff % 86400) / 3600);
    const minutes = Math.floor((diff % 3600) / 60);
    const seconds = diff % 60;

    setTimeSince(`${days}d ${hours}h ${minutes}m ${seconds}s`);
  }, [deployTime]);

  useEffect(() => {
    updateTimeSince();
    const interval = setInterval(updateTimeSince, 1000);
    return () => clearInterval(interval);
  }, [updateTimeSince]);

  return (
    <div className={`text-sm ${isDefault ? 'text-red-400' : 'text-cyan-300/60'}`}>
      <p>Deployed: {timeSince} ago{isDefault ? ' (estimated)' : ''}</p>
    </div>
  );
}
