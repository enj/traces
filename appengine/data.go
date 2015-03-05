package traces

import (
	"internal/github.com/kellydunn/golang-geo"
)

var googleGeo = geo.GoogleGeocoder{}

type geoJSON struct { //TODO remove once twitter library updated
	Coordinates []float64 `json:"coordinates"`
}

type twitterIntel struct { //TODO fix this with new fields
	Name string `json:"name"`
	ScreenName string `json:"screenname"`
	ImageURL string `json:"imageurl"`
	// location *geo.Point
	distance float64
}

type tweets []twitterIntel

type addressIntel struct {
	Location *geo.Point
	Intel tweets
}

func (t tweets) Len() int { return len(t) }
func (t tweets) Swap(i, j int) { t[i], t[j] = t[j], t[i] }
func (t tweets) Less(i, j int) bool { return t[i].distance < t[j].distance }
