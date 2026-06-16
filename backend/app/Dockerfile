# syntax=docker/dockerfile:1.7

FROM golang:1.25.11 AS builder

WORKDIR /src

COPY go.mod go.sum ./

RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=cache,target=/root/.cache/go-build \
    go mod download

COPY cmd ./cmd
COPY db ./db
COPY internal ./internal

RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=cache,target=/root/.cache/go-build \
    CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
    go build -tags lambda.norpc -trimpath -ldflags="-s -w" -o /out/api ./cmd/api

RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=cache,target=/root/.cache/go-build \
    CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
    go build -trimpath -ldflags="-s -w" -o /out/migrate ./cmd/migrate

FROM alpine:3.22 AS atlas

RUN apk add --no-cache curl
RUN mkdir -p /atlas-bin
RUN curl -sSf https://atlasgo.sh | sh -s -- -y -o /atlas-bin/atlas

FROM public.ecr.aws/lambda/provided:al2023 AS runner

COPY --from=atlas /atlas-bin/atlas /usr/local/bin/atlas
COPY --from=builder /out/api /api
COPY --from=builder /out/migrate /migrate
COPY db /app/db

ENTRYPOINT ["/api"]
