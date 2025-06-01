import Image from 'next/image'
import ActivationStatus from "./components/ActivationStatus";
import DeploymentInfo from './components/DeploymentInfo';

export default function Home() {
  return (
    <main className="min-h-screen bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-slate-900 via-cyan-950/20 to-slate-950 text-white p-8 relative overflow-hidden">
      <div className="absolute inset-0 bg-[url('/grid.svg')] opacity-10"></div>
      <div className="absolute inset-0 bg-gradient-to-t from-slate-950 to-transparent"></div>

      <div className="max-w-6xl mx-auto relative">
        <header className="mb-12">
          <div className="flex items-center justify-between mb-8 backdrop-blur-sm bg-black/20 rounded-2xl p-4 border border-cyan-500/10">
            <h1 className="text-5xl font-bold bg-gradient-to-r from-cyan-400 via-cyan-300 to-blue-500 bg-clip-text text-transparent">
              Zooby Dashboard
            </h1>
            <nav className="flex items-center gap-2">
              <a href="#" className="text-cyan-400 hover:text-cyan-300 font-medium px-4 py-2 rounded-lg hover:bg-cyan-950/30 transition-all border border-cyan-500/20 hover:border-cyan-400/50 backdrop-blur hover:scale-105 hover:shadow-lg">
                Dashboard
              </a>
              <a href="#" className="text-gray-400 hover:text-cyan-300 font-medium px-4 py-2 rounded-lg hover:bg-cyan-950/30 transition-all border border-slate-500/20 hover:border-cyan-400/50 hover:scale-105 hover:shadow-lg">
                Devices
              </a>
              <a href="#" className="text-gray-400 hover:text-cyan-300 font-medium px-4 py-2 rounded-lg hover:bg-cyan-950/30 transition-all border border-slate-500/20 hover:border-cyan-400/50 hover:scale-105 hover:shadow-lg">
                Settings
              </a>
              <div className="h-6 w-px bg-cyan-500/20 mx-2"></div>
              <button className="bg-cyan-500/20 text-cyan-300 font-medium px-6 py-2 rounded-lg hover:bg-cyan-500 hover:text-black transition-all border border-cyan-500/50 hover:scale-105 hover:shadow-lg flex items-center gap-2">
                + Add Device
              </button>
              <button className="bg-black/40 text-cyan-300 font-medium px-4 py-2 rounded-lg hover:bg-cyan-950/50 transition-all border border-cyan-500/20 hover:border-cyan-400/50 hover:scale-105 hover:shadow-lg flex items-center gap-2">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                Login
              </button>
            </nav>
          </div>
          <p className="text-cyan-300/60 backdrop-blur-sm bg-black/20 inline-block px-4 py-2 rounded-lg border border-cyan-500/10">
            System Status: <span className="text-cyan-300">Online</span> • Last Sync: <span className="text-cyan-300">2 mins ago</span>
          </p>
        </header>

        <div className="grid lg:grid-cols-[1fr,1fr] gap-8">
          <div className="bg-black/40 rounded-2xl p-6 backdrop-blur-md border border-cyan-500/10 relative">
            <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-cyan-500/50 to-transparent"></div>
            <div className="relative h-[300px] w-full flex justify-center items-center">
              <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))] from-cyan-500/10 via-transparent to-transparent"></div>
              <Image
                src={`${process.env.NODE_ENV === 'production' ? '/zooby' : ''}/images/zoobies.png`}
                alt="Zooby Alpha Models"
                width={350}
                height={225}
                className="rounded-xl shadow-2xl object-contain relative z-10"
              />
            </div>
            <div className="mt-4 flex gap-4">
              <div className="bg-black/50 rounded-lg p-4 flex-1 border border-cyan-500/10">
                <p className="text-sm text-cyan-300/60">Model</p>
                <p className="text-lg font-medium text-cyan-300">Alpha Series</p>
              </div>
              <div className="bg-black/50 rounded-lg p-4 flex-1 border border-cyan-500/10">
                <p className="text-sm text-cyan-300/60">Serial</p>
                <p className="text-lg font-medium text-cyan-300">ZB-2025-001</p>
              </div>
            </div>
          </div>

          <ActivationStatus />

          <div className="lg:col-span-2">
            <div className="bg-black/40 rounded-xl shadow-lg p-6 backdrop-blur-md border border-cyan-500/10">
              <h2 className="text-2xl font-bold text-cyan-300 mb-4 flex items-center gap-2">
                <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></span>
                Device Stats
              </h2>
              <div className="space-y-4">
                <div className="flex justify-between items-center p-3 bg-black/30 rounded-lg border border-cyan-500/10">
                  <span className="text-cyan-300/60">System Health</span>
                  <span className="text-green-400 font-medium flex items-center gap-2">
                    <span className="w-2 h-2 rounded-full bg-green-400 animate-pulse"></span>
                    Optimal
                  </span>
                </div>
                <div className="flex justify-between items-center p-3 bg-black/30 rounded-lg border border-cyan-500/10">
                  <span className="text-cyan-300/60">Last Updated</span>
                  <span className="text-cyan-300 font-medium">2 mins ago</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <footer className="mt-12 py-4 border-t border-cyan-500/10 backdrop-blur-sm bg-black/20">
        <div className="max-w-6xl mx-auto px-4 flex items-center justify-between">
          <p className="text-cyan-300/60">&copy; {new Date().getFullYear()} Zooby Dashboard</p>
          <DeploymentInfo />
        </div>
      </footer>
    </main>
  );
}
