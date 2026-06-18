package service

type HealthStatus string

const (
	HealthStatusOK       HealthStatus = "ok"
	HealthStatusDegraded HealthStatus = "degraded"
)

type HealthResult struct {
	Status   HealthStatus `json:"status"`
	Database string       `json:"database"`
}

type HealthService struct{}

func NewHealthService() *HealthService {
	return &HealthService{}
}

func (s *HealthService) Check() HealthResult {
	return HealthResult{Status: HealthStatusOK, Database: "not checked"}
}
