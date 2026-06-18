package main

import (
	"context"
	"fmt"
	"os"
	"os/exec"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/config"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/db"
)

func main() {
	ctx := context.Background()

	cfg, err := config.Load()
	if err != nil {
		fail(err)
	}

	pool, err := db.NewPool(ctx, cfg)
	if err != nil {
		fail(err)
	}
	defer pool.Close()

	if err := pool.Ping(ctx); err != nil {
		fail(fmt.Errorf("ping database before migration: %w", err))
	}

	password, err := db.ResolvePassword(ctx, cfg)
	if err != nil {
		fail(err)
	}

	cmd := exec.CommandContext(
		ctx,
		cfg.DB.AtlasBinaryPath,
		"migrate",
		"apply",
		"--dir",
		cfg.DB.AtlasMigrationDir,
		"--url",
		db.BuildMigrationURL(cfg.DB, password),
	)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr

	if err := cmd.Run(); err != nil {
		fail(fmt.Errorf("run atlas migrate apply: %w", err))
	}
}

func fail(err error) {
	fmt.Fprintln(os.Stderr, err)
	os.Exit(1)
}
