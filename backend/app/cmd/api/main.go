package main

import (
	"context"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/awslabs/aws-lambda-go-api-proxy/httpadapter"

	apphttp "github.com/NKC-CV2026/Graduation-research/backend/app/internal/app"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/handler"
	"github.com/NKC-CV2026/Graduation-research/backend/app/internal/service"
)

var adapter *httpadapter.HandlerAdapterV2

func init() {
	healthService := service.NewHealthService()
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
