package main

import (
	"context"
	"log"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/awslabs/aws-lambda-go-api-proxy/httpadapter"

	apphttp "github.com/NKC-CV2026/Graduation-research/backend/app/internal/app"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/config"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/db"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/handler"
	postgresrepo "github.com/NKC-CV2026/Graduation-research/backend/app/internal/repository/postgres"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/service"
)

var adapter *httpadapter.HandlerAdapterV2

func init() {
	ctx := context.Background()

	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("load config: %v", err)
	}

	pool, err := db.NewPool(ctx, cfg)
	if err != nil {
		log.Fatalf("create db pool: %v", err)
	}

	healthRepository := postgresrepo.NewHealthRepository(pool, cfg.DB.HealthCheckTimeout)
	healthService := service.NewHealthService(healthRepository)
	healthHandler := handler.NewHealthHandler(healthService)

	router := apphttp.NewRouter(healthHandler)
	adapter = httpadapter.NewV2(router)
}

func main() {
	lambda.Start(handle)
}

func handle(ctx context.Context, request events.APIGatewayV2HTTPRequest) (events.APIGatewayV2HTTPResponse, error) {
	return adapter.ProxyWithContext(ctx, request)
}
