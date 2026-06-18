package db

import (
	"context"
	"fmt"

	awsconfig "github.com/aws/aws-sdk-go-v2/config"
	rdsauth "github.com/aws/aws-sdk-go-v2/feature/rds/auth"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/config"
)

func ResolvePassword(ctx context.Context, cfg config.Config) (string, error) {
	if cfg.DB.Password != "" {
		return cfg.DB.Password, nil
	}

	if !cfg.DB.IAMAuth {
		return "", fmt.Errorf("DB_PASSWORD is required when DB_IAM_AUTH is false")
	}

	awsCfg, err := awsconfig.LoadDefaultConfig(ctx, awsconfig.WithRegion(cfg.AWSRegion))
	if err != nil {
		return "", fmt.Errorf("load aws config: %w", err)
	}

	endpoint := fmt.Sprintf("%s:%d", cfg.DB.Host, cfg.DB.Port)
	password, err := rdsauth.BuildAuthToken(ctx, endpoint, cfg.AWSRegion, cfg.DB.User, awsCfg.Credentials)
	if err != nil {
		return "", fmt.Errorf("build rds auth token: %w", err)
	}

	return password, nil
}
