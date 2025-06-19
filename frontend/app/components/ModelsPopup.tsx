"use client";
import React, { useState, useEffect, useCallback } from 'react';

interface ZoobyModel {
  model: string;
  name: string;
  description: string;
  features?: string[];
  image?: string;
  price?: number;
}

interface ModelsPopupProps {
  show: boolean;
  onHide: () => void;
}

const ModelsPopup: React.FC<ModelsPopupProps> = ({ show, onHide }) => {
  const [models, setModels] = useState<ZoobyModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState('');
  const [page, setPage] = useState(0);
  const pageSize = 6;

  const fetchModels = useCallback(async () => {
    if (!show) return;

    setLoading(true);
    setError(null);

    const query = `
      query {
        zoobyModels(filter: "${filter}", offset: ${page * pageSize}, limit: ${pageSize}) {
          model
          name
          description
          features
          image
          price
        }
      }
    `;

    try {
      const response = await fetch(process.env.NEXT_PUBLIC_GRAPHQL_URL || '/graphql', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ query }),
        credentials: 'include'
      });

      const result = await response.json();

      if (result.errors) {
        throw new Error(result.errors[0].message);
      }

      setModels(result.data.zoobyModels || []);
    } catch (err: unknown) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }, [show, filter, page, pageSize]);

  useEffect(() => {
    if (show) {
      fetchModels();
    }
  }, [show, filter, page, fetchModels]);

  const handlePurchase = (model: ZoobyModel) => {
    console.log(`Purchasing model: ${model.name}`);
    alert(`Added ${model.name} to cart for $${model.price?.toFixed(2)}`);
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilter(e.target.value);
    setPage(0);
  };

  if (!show) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center overflow-x-hidden overflow-y-auto bg-black/50">
      <div className="relative w-full max-w-4xl p-6 mx-auto bg-slate-900 backdrop-blur-md border border-cyan-500/20 rounded-xl shadow-xl">
        {/* Header */}
        <div className="flex items-center justify-between mb-6 border-b border-cyan-500/20 pb-4">
          <h3 className="text-xl font-bold text-cyan-300">
            Zooby Models
          </h3>
          <button
            onClick={onHide}
            className="text-gray-400 hover:text-white focus:outline-none"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Search */}
        <div className="mb-6">
          <input
            type="text"
            placeholder="Search models..."
            value={filter}
            onChange={handleFilterChange}
            className="w-full px-4 py-2 bg-black/30 border border-cyan-500/30 rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-cyan-400"
          />
        </div>

        {/* Content */}
        <div className="mb-6">
          {loading &&
            <div className="text-center py-10">
              <div className="w-12 h-12 border-4 border-cyan-400 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
              <p className="text-cyan-300">Loading models...</p>
            </div>
          }

          {error &&
            <div className="bg-red-900/30 border border-red-500/50 text-red-300 px-4 py-3 rounded-lg">
              Error: {error}
            </div>
          }

          {!loading && models.length === 0 && (
            <div className="text-center py-10 text-cyan-300/60">
              No models found matching your criteria.
            </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {models.map(model => (
              <div key={model.model} className="bg-black/40 rounded-xl border border-cyan-500/20 overflow-hidden flex flex-col h-full">
                {model.image && (
                  <div className="h-48 relative overflow-hidden">
                    <img
                      src={model.image}
                      alt={model.name}
                      className="w-full h-full object-cover"
                    />
                  </div>
                )}
                <div className="p-4 flex flex-col flex-1">
                  <h4 className="text-lg font-semibold text-cyan-300 mb-1">{model.name}</h4>
                  <p className="text-sm text-cyan-300/60 mb-2">{model.model}</p>
                  <p className="text-sm text-gray-300 mb-3 flex-grow">{model.description}</p>
                  {model.features && model.features.length > 0 && (
                    <div className="mb-3">
                      <p className="text-xs text-cyan-300/60 mb-1">Features:</p>
                      <p className="text-xs text-gray-400">{model.features.join(", ")}</p>
                    </div>
                  )}
                  <div className="flex items-center justify-between mt-auto pt-3 border-t border-cyan-500/10">
                    <span className="font-bold text-cyan-400">
                      ${model.price?.toFixed(2) || "N/A"}
                    </span>
                    <button
                      onClick={() => handlePurchase(model)}
                      className="bg-cyan-500/80 hover:bg-cyan-400 text-black text-sm font-semibold px-3 py-1.5 rounded-lg transition-all duration-200 shadow-md border border-cyan-400 hover:scale-105"
                    >
                      Add to Cart
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Pagination */}
        {models.length > 0 && (
          <div className="flex items-center justify-between pt-4 border-t border-cyan-500/20">
            <button
              disabled={page === 0}
              onClick={() => setPage(p => p - 1)}
              className={`px-4 py-2 rounded-lg border ${
                page === 0
                  ? "border-gray-600 text-gray-600 cursor-not-allowed"
                  : "border-cyan-500/50 text-cyan-400 hover:bg-cyan-900/30"
              }`}
            >
              Previous
            </button>
            <span className="text-cyan-300">Page {page + 1}</span>
            <button
              disabled={models.length < pageSize}
              onClick={() => setPage(p => p + 1)}
              className={`px-4 py-2 rounded-lg border ${
                models.length < pageSize
                  ? "border-gray-600 text-gray-600 cursor-not-allowed"
                  : "border-cyan-500/50 text-cyan-400 hover:bg-cyan-900/30"
              }`}
            >
              Next
            </button>
          </div>
        )}

        {/* Footer */}
        <div className="mt-6 pt-4 border-t border-cyan-500/20 flex justify-end">
          <button
            onClick={onHide}
            className="bg-black/40 text-cyan-300 font-semibold px-6 py-2 rounded-lg hover:bg-black/60 transition-all duration-200 border border-cyan-500/30"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default ModelsPopup;
