package db

import (
	"context"
	"fmt"
	"net/url"

	"github.com/jackc/pgx/v5/pgxpool"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/config"
)

func NewPool(ctx context.Context, cfg config.Config) (*pgxpool.Pool, error) {
	password, err := ResolvePassword(ctx, cfg)
	if err != nil {
		return nil, err
	}

	dsn := buildDSN(cfg.DB, password)
	poolConfig, err := pgxpool.ParseConfig(dsn)
	if err != nil {
		return nil, fmt.Errorf("parse pgx config: %w", err)
	}

	poolConfig.MaxConns = cfg.DB.MaxConns
	poolConfig.MinConns = cfg.DB.MinConns
	poolConfig.MaxConnLifetime = cfg.DB.MaxConnLifetime
	poolConfig.MaxConnIdleTime = cfg.DB.MaxConnIdleTime

	pool, err := pgxpool.NewWithConfig(ctx, poolConfig)
	if err != nil {
		return nil, fmt.Errorf("create pgx pool: %w", err)
	}

	return pool, nil
}

func BuildMigrationURL(cfg config.DBConfig, password string) string {
	return buildDSN(cfg, password)
}

func buildDSN(cfg config.DBConfig, password string) string {
	user := url.QueryEscape(cfg.User)
	pass := url.QueryEscape(password)
	name := url.QueryEscape(cfg.Name)
	sslMode := url.QueryEscape(cfg.SSLMode)

	return fmt.Sprintf("postgres://%s:%s@%s:%d/%s?sslmode=%s", user, pass, cfg.Host, cfg.Port, name, sslMode)
}
