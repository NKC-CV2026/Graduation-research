package service

import (
	"context"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/domain"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/repository"
)

type StopPointService struct {
	repository repository.StopPointRepository
}

func NewStopPointService(repository repository.StopPointRepository) *StopPointService {
	return &StopPointService{repository: repository}
}

func (s *StopPointService) GetByID(ctx context.Context, input domain.GetStopPointByIDInput) (domain.StopPointDetail, bool, error) {
	return s.repository.FindByID(ctx, input.ID)
}

func (s *StopPointService) GetByRange(ctx context.Context, input domain.GetStopPointsByRangeInput) ([]domain.StopPoint, error) {
	return s.repository.FindByRange(ctx, input)
}
