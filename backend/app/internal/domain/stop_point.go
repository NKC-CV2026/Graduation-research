package domain

type StopPoint struct {
	UniqueKey string
	AZ        string
	Latitude  float64
	Longitude float64
}

type StopPointDetail struct {
	StopPoint
	CreatedAt string
	UpdatedAt string
}

type GetStopPointByIDInput struct {
	ID string
}

type GetStopPointsByRangeInput struct {
	Latitude     float64
	Longitude    float64
	RadiusMeters int
	Limit        int
}
