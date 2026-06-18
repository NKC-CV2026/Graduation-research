package handler

import (
	"encoding/json"
	"net/http"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/service"
)

type HealthHandler struct {
	service *service.HealthService
}

func NewHealthHandler(service *service.HealthService) *HealthHandler {
	return &HealthHandler{service: service}
}

func (h *HealthHandler) Get(w http.ResponseWriter, r *http.Request) {
	result := h.service.Check(r.Context())

	statusCode := http.StatusOK
	if result.Status != service.HealthStatusOK {
		statusCode = http.StatusServiceUnavailable
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(statusCode)
	if err := json.NewEncoder(w).Encode(result); err != nil {
		http.Error(w, http.StatusText(http.StatusInternalServerError), http.StatusInternalServerError)
	}
}
