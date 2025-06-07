'use client';
import { useEffect, useState, useCallback } from 'react';

export default function DeploymentInfo() {
  const [timeSince, setTimeSince] = useState<string>('');
  const [isDefault, setIsDefault] = useState(true);
  const [gitSha, setGitSha] = useState<string | null>(null);
  const [deployedAt, setDeployedAt] = useState<Date | null>(null);

  useEffect(() => {
    fetch("/deploy-meta.json")
      .then(res => res.json())
      .then(data => {
        if (data?.gitSha) setGitSha(data.gitSha);
        if (data?.deployedAt) setDeployedAt(new Date(data.deployedAt));
        setIsDefault(false);
      })
      .catch(() => {
        setIsDefault(true);
      });
  }, []);

  const updateTimeSince = useCallback(() => {
    if (!deployedAt) return;

    const now = new Date();
    const diff = Math.floor((now.getTime() - deployedAt.getTime()) / 1000);

    const days = Math.floor(diff / 86400);
    const hours = Math.floor((diff % 86400) / 3600);
    const minutes = Math.floor((diff % 3600) / 60);
    const seconds = diff % 60;

    setTimeSince(`${days}d ${hours}h ${minutes}m ${seconds}s`);
  }, [deployedAt]);

  useEffect(() => {
    updateTimeSince();
    const interval = setInterval(updateTimeSince, 1000);
    return () => clearInterval(interval);
  }, [updateTimeSince]);

  return (
    <div className={`text-sm ${isDefault ? 'text-red-400' : 'text-cyan-300/60'}`}>
      <p>
        Deployed: {deployedAt?.toLocaleString() || 'unknown'}
        {timeSince && ` (${timeSince} ago)`}
        {isDefault ? ' (estimated)' : ''}
      </p>
      {gitSha && (
        <p className="text-xs opacity-70">
          Version: <code>{gitSha}</code>
        </p>
      )}
    </div>
  );
}
