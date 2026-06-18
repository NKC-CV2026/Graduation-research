package postgres

import (
	"context"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

type HealthRepository struct {
	pool    *pgxpool.Pool
	timeout time.Duration
}

func NewHealthRepository(pool *pgxpool.Pool, timeout time.Duration) *HealthRepository {
	return &HealthRepository{pool: pool, timeout: timeout}
}

func (r *HealthRepository) Ping(ctx context.Context) error {
	pingCtx, cancel := context.WithTimeout(ctx, r.timeout)
	defer cancel()

	return r.pool.Ping(pingCtx)
}
