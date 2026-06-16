package service

import (
	"context"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/repository"
)

type HealthStatus string

const (
	HealthStatusOK       HealthStatus = "ok"
	HealthStatusDegraded HealthStatus = "degraded"
)

type HealthResult struct {
	Status   HealthStatus `json:"status"`
	Database string       `json:"database"`
}

type HealthService struct {
	repo repository.HealthRepository
}

func NewHealthService(repo repository.HealthRepository) *HealthService {
	return &HealthService{repo: repo}
}

func (s *HealthService) Check(ctx context.Context) HealthResult {
	if err := s.repo.Ping(ctx); err != nil {
		return HealthResult{Status: HealthStatusDegraded, Database: "error"}
	}

	return HealthResult{Status: HealthStatusOK, Database: "ok"}
}
