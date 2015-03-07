package traces

import (
	"net/http"
	"net/url"
	"internal/github.com/kellydunn/golang-geo"
)

func commonAPIValidation(q *url.Values, h *http.Client, p *geo.Point) (tweets, error) {
	radius := q.Get("rad")
	rad, errRadius := validateRadius(&radius)
	if errRadius != nil {
		return nil, errRadius
	}

	sSince := q.Get("since")
	since, errSince := validateDate(&sSince)
	if errSince != nil {
		return nil, errSince
	}

	sUntil := q.Get("until")
	until, errUntil := validateDate(&sUntil)
	if errUntil != nil {
		return nil, errUntil
	}

	errRange := validateDateRange(&sSince, &sUntil)
	if errRange != nil {
		return nil, errRange
	}

	t, errAPI := twitterSearchCoordinate(p, h, rad, since, until)
	if errAPI != nil {
		return nil, errAPI
	}

	return t, nil
}
