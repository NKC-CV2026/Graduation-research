package repository

import (
	"context"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/domain"
)

type StopPointRepository interface {
	FindByID(context.Context, string) (domain.StopPointDetail, bool, error)
	FindByRange(context.Context, domain.GetStopPointsByRangeInput) ([]domain.StopPoint, error)
}
