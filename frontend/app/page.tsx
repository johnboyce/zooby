"use client";
import Image from 'next/image'
import ActivationStatus from "./components/ActivationStatus";
import DeploymentInfo from './components/DeploymentInfo';
import { signIn, signOut, useSession } from 'next-auth/react';

export default function Home() {
  const { data: session, status } = useSession();
  return (
    <main className="min-h-screen bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-slate-900 via-cyan-950/20 to-slate-950 text-white p-4 sm:p-6 md:p-8 relative overflow-hidden">
      <div className="absolute inset-0 bg-[url('/grid.svg')] opacity-10"></div>
      <div className="absolute inset-0 bg-gradient-to-t from-slate-950 to-transparent"></div>

      <div className="max-w-6xl mx-auto relative">
        <header className="mb-8 sm:mb-10 md:mb-12">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-6 sm:mb-8 backdrop-blur-sm bg-black/20 rounded-2xl p-3 sm:p-4 border border-cyan-500/10 gap-4 sm:gap-0">
            <h1 className="text-3xl sm:text-4xl md:text-5xl font-bold bg-gradient-to-r from-cyan-400 via-cyan-300 to-blue-500 bg-clip-text text-transparent">
              Zooby Dashboard
            </h1>
            <nav className="flex flex-col sm:flex-row items-stretch sm:items-center gap-2 w-full sm:w-auto bg-black/30 backdrop-blur-md rounded-xl px-4 py-2 shadow-lg border border-cyan-500/10">
              <a href="#" className="text-cyan-400 hover:text-white font-semibold px-4 py-2 rounded-lg transition-all duration-200 border border-transparent hover:bg-cyan-500/10 focus:outline-none focus:ring-2 focus:ring-cyan-400">
                <span className="inline-flex items-center gap-2">
                  <Image src="/favicon.ico" alt="Zooby Logo" width={24} height={24} className="rounded-full shadow w-6 h-6" />
                  Dashboard
                </span>
              </a>
              <a href="#" className="text-gray-400 hover:text-cyan-300 font-semibold px-4 py-2 rounded-lg transition-all duration-200 border border-transparent hover:bg-cyan-500/10 focus:outline-none focus:ring-2 focus:ring-cyan-400">
                Devices
              </a>
              <a href="#" className="text-gray-400 hover:text-cyan-300 font-semibold px-4 py-2 rounded-lg transition-all duration-200 border border-transparent hover:bg-cyan-500/10 focus:outline-none focus:ring-2 focus:ring-cyan-400">
                Settings
              </a>
              <div className="hidden sm:block h-6 w-px bg-cyan-500/20 mx-2"></div>
              {status === 'loading' ? (
                <button className="bg-black/40 text-cyan-300 font-medium px-4 py-2 rounded-lg animate-pulse" disabled>Loading...</button>
              ) : session ? (
                <div className="flex items-center gap-2">
                  <div className="relative group">
                    {session.user?.image ? (
                      <Image src={session.user.image} alt="avatar" width={32} height={32} className="w-8 h-8 rounded-full border-2 border-cyan-400 shadow-md" />
                    ) : (
                      <div className="w-8 h-8 rounded-full bg-cyan-900 border-2 border-cyan-400 flex items-center justify-center text-cyan-200 font-bold shadow-md">
                        {session.user?.name?.[0]?.toUpperCase() || 'U'}
                      </div>
                    )}
                    <span className="absolute left-1/2 -translate-x-1/2 mt-2 px-3 py-1 bg-black/80 text-cyan-100 text-xs rounded shadow-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none z-20">
                      {session.user?.name}
                    </span>
                  </div>
                  <span className="bg-gradient-to-r from-cyan-400 via-blue-400 to-cyan-300 text-black font-bold text-base drop-shadow-lg px-3 py-1 rounded-full shadow-md animate-pulse border border-cyan-300">
                    {session.user?.name || session.user?.email || 'User'}
                  </span>
                  <button
                    className="bg-cyan-500/80 hover:bg-cyan-400 text-black font-semibold px-4 py-2 rounded-lg transition-all duration-200 shadow-md border border-cyan-400 hover:scale-105 focus:outline-none focus:ring-2 focus:ring-cyan-400 flex items-center gap-2 justify-center"
                    onClick={() => signOut()}
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                    Logout
                  </button>
                </div>
              ) : (
                <button
                  className="bg-black/40 text-cyan-300 font-semibold px-4 py-2 rounded-lg hover:bg-cyan-950/50 transition-all duration-200 border border-cyan-500/20 hover:border-cyan-400/50 hover:scale-105 hover:shadow-lg flex items-center gap-2 justify-center focus:outline-none focus:ring-2 focus:ring-cyan-400"
                  onClick={() => signIn('connellboyce')}
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  Login
                </button>
              )}
            </nav>
          </div>
          <p className="text-cyan-300/60 backdrop-blur-sm bg-black/20 inline-block px-3 sm:px-4 py-2 rounded-lg border border-cyan-500/10 text-sm sm:text-base">
            System Status: <span className="text-cyan-300">Online</span> • Last Sync: <span className="text-cyan-300">2 mins ago</span>
          </p>
        </header>

        <div className="grid gap-6 md:gap-8 grid-cols-1 lg:grid-cols-[1fr,1fr]">
          <div className="bg-black/40 rounded-2xl p-4 sm:p-6 backdrop-blur-md border border-cyan-500/10 relative">
            <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-cyan-500/50 to-transparent"></div>
            <div className="relative h-48 sm:h-[300px] w-full flex justify-center items-center">
              <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))] from-cyan-500/10 via-transparent to-transparent"></div>
              <Image
                src="/images/zoobies.png"
                alt="Zooby Alpha Models"
                width={350}
                height={225}
                className="rounded-xl shadow-2xl object-contain relative z-10 w-full h-auto max-w-[350px] max-h-[225px]"
              />
            </div>
            <div className="mt-4 flex flex-col sm:flex-row gap-2 sm:gap-4">
              <div className="bg-black/50 rounded-lg p-3 sm:p-4 flex-1 border border-cyan-500/10">
                <p className="text-xs sm:text-sm text-cyan-300/60">Model</p>
                <p className="text-base sm:text-lg font-medium text-cyan-300">Alpha Series</p>
              </div>
              <div className="bg-black/50 rounded-lg p-3 sm:p-4 flex-1 border border-cyan-500/10">
                <p className="text-xs sm:text-sm text-cyan-300/60">Serial</p>
                <p className="text-base sm:text-lg font-medium text-cyan-300">ZB-2025-001</p>
              </div>
            </div>
          </div>

          <ActivationStatus />

          <div className="lg:col-span-2">
            <div className="bg-black/40 rounded-xl shadow-lg p-4 sm:p-6 backdrop-blur-md border border-cyan-500/10">
              <h2 className="text-xl sm:text-2xl font-bold text-cyan-300 mb-3 sm:mb-4 flex items-center gap-2">
                <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></span>
                Device Stats
              </h2>
              <div className="space-y-3 sm:space-y-4">
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center p-2 sm:p-3 bg-black/30 rounded-lg border border-cyan-500/10">
                  <span className="text-cyan-300/60 text-sm">System Health</span>
                  <span className="text-green-400 font-medium flex items-center gap-2 text-sm">
                    <span className="w-2 h-2 rounded-full bg-green-400 animate-pulse"></span>
                    Optimal
                  </span>
                </div>
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center p-2 sm:p-3 bg-black/30 rounded-lg border border-cyan-500/10">
                  <span className="text-cyan-300/60 text-sm">Last Updated</span>
                  <span className="text-cyan-300 font-medium text-sm">2 mins ago</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <footer className="mt-8 sm:mt-12 py-3 sm:py-4 border-t border-cyan-500/10 backdrop-blur-sm bg-black/20">
        <div className="max-w-6xl mx-auto px-2 sm:px-4 flex flex-col sm:flex-row items-center justify-between gap-2 sm:gap-0">
          <p className="text-cyan-300/60 text-xs sm:text-base">&copy; {new Date().getFullYear()} Zooby Dashboard</p>
          <DeploymentInfo />
        </div>
      </footer>
    </main>
  );
}
