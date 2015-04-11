package traces

import (
	"fmt"
	"errors"
	"strconv"
	"time"
)

func parseCoordinate(s, t *string, r float64) (f float64, err error) {
	f, err = strconv.ParseFloat(*s, 64)
	if err != nil {
		err = errors.New(fmt.Sprintf("Invalid %s.", *t))
		return
	}

	if f >= -r && f <= r {
		return
	}

	err = errors.New(fmt.Sprintf("Invalid range, %s must be ±%.0f°.", *t, r))
	return
}

func parseLat(s *string) (float64, error) {
	l := "latitude"
	return parseCoordinate(s, &l, maxLat)
}

func parseLng(s *string) (float64, error) {
	l := "longitude"
	return parseCoordinate(s, &l, maxLng)
}

func parseRadius(r *string) (f float64, err error) {
	f, err = strconv.ParseFloat(*r, 64)
	if err != nil {
		err = errors.New("Invalid radius.")
		return
	}

	if f >= minTwitterRadius && f <= maxTwitterRadius {
		return
	}

	err = errors.New("Radius out of valid range.")
	return
}

func validateRadius(radius *string) (float64, error) {
	if *radius != "" {
		rad, err := parseRadius(radius)
		if err != nil {
			return 0.0, err
		}
		return rad, nil
	}
	return defTwitterRadius, nil
}

func validateDate(date *string) (*time.Time, error) {
	var d *time.Time
	if *date != "" {
		out, err := time.Parse(twitterDate, *date)
		if err != nil {
			return nil, err //TODO better error message
		}
		d = &out
	}
	return d, nil // TODO maybe control date's range to -2 weeks to +2 days from today
} // Probably better to modify date range below to handle this using time.Time values

func validateDateRange(since, until *string) error {
	if *since != "" && *until != "" {
		if *since >= *until {
			return errors.New("'Until' date must be later than 'since' date.")
		}
	}
	return nil
}
