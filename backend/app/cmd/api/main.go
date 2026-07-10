package main

import (
	"context"
	"log"
	"os"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	chiadapter "github.com/awslabs/aws-lambda-go-api-proxy/chi"

	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/app"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/handler"
	mockrepository "github.com/NKC-CV2026/Graduation-research/backend/app/internal/repository/mock"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/service"
)

func main() {
	stopPointRepository, err := mockrepository.NewStopPointRepository(stopPointDataPath())
	if err != nil {
		log.Fatalf("initialize stop point repository: %v", err)
	}

	healthHandlers := handler.NewHealthHandlers(service.NewHealthService())
	stopPointHandlers := handler.NewStopPointHandlers(service.NewStopPointService(stopPointRepository))
	router := app.NewRouter(healthHandlers, stopPointHandlers)
	adapter := chiadapter.NewV2(router)

	lambda.Start(func(ctx context.Context, request events.APIGatewayV2HTTPRequest) (events.APIGatewayV2HTTPResponse, error) {
		return adapter.ProxyWithContextV2(ctx, request)
	})
}

func stopPointDataPath() string {
	if path := os.Getenv("STOP_POINT_DATA_PATH"); path != "" {
		return path
	}

	return "/app/mockResponse.json"
}
