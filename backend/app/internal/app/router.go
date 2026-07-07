package app

import (
	"time"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/handler"
)

func NewRouter(healthHandlers *handler.HealthHandlers, stopPointHandlers *handler.StopPointHandlers) *chi.Mux {
	r := chi.NewRouter()

	r.Use(middleware.RequestID)
	r.Use(middleware.RealIP)
	r.Use(middleware.Recoverer)
	r.Use(middleware.Timeout(10 * time.Second))

	r.Get("/api/v1/health", healthHandlers.Get)
	r.Get("/api/v1/stop-points", stopPointHandlers.GetByRange)
	r.Get("/api/v1/stop-points/{id}", stopPointHandlers.GetByID)

	return r
}
