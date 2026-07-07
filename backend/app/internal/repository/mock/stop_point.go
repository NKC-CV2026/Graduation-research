package mock

import (
	"context"
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"strconv"
	"time"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/domain"
)

type StopPointRepository struct {
	stopPoints []domain.StopPoint
}

const maxStopPointRangeResults = 500

type stopPointRecord struct {
	UniqueKey string `json:"uniqueKey"`
	AZ        string `json:"az"`
	Longitude string `json:"lon"`
	Latitude  string `json:"lat"`
}

func NewStopPointRepository(path string) (*StopPointRepository, error) {
	content, err := os.ReadFile(path)
	if err != nil {
		fallbackPath := filepath.Base(path)
		content, err = os.ReadFile(fallbackPath)
		if err != nil {
			return nil, fmt.Errorf("read stop point mock response: %w", err)
		}
	}

	var records []stopPointRecord
	if err := json.Unmarshal(content, &records); err != nil {
		return nil, fmt.Errorf("unmarshal stop point mock response: %w", err)
	}

	stopPoints := make([]domain.StopPoint, 0, len(records))
	for _, record := range records {
		latitude, err := strconv.ParseFloat(record.Latitude, 64)
		if err != nil {
			return nil, fmt.Errorf("parse latitude for %s: %w", record.UniqueKey, err)
		}

		longitude, err := strconv.ParseFloat(record.Longitude, 64)
		if err != nil {
			return nil, fmt.Errorf("parse longitude for %s: %w", record.UniqueKey, err)
		}

		stopPoints = append(stopPoints, domain.StopPoint{
			UniqueKey: record.UniqueKey,
			AZ:        record.AZ,
			Latitude:  latitude,
			Longitude: longitude,
		})
	}

	return &StopPointRepository{stopPoints: stopPoints}, nil
}

func (r *StopPointRepository) FindByID(_ context.Context, id string) (domain.StopPointDetail, bool, error) {
	for _, stopPoint := range r.stopPoints {
		if stopPoint.UniqueKey == id {
			now := time.Now().UTC().Format(time.RFC3339)

			return domain.StopPointDetail{
				StopPoint: stopPoint,
				CreatedAt: now,
				UpdatedAt: now,
			}, true, nil
		}
	}

	return domain.StopPointDetail{}, false, nil
}

func (r *StopPointRepository) FindByRange(_ context.Context, input domain.GetStopPointsByRangeInput) ([]domain.StopPoint, error) {
	limit := input.Limit
	if limit < 0 {
		limit = 0
	}
	if limit > maxStopPointRangeResults {
		limit = maxStopPointRangeResults
	}
	if limit > len(r.stopPoints) {
		limit = len(r.stopPoints)
	}

	stopPoints := make([]domain.StopPoint, limit)
	copy(stopPoints, r.stopPoints[:limit])

	return stopPoints, nil
}
