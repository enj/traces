package traces

import (
	"internal/github.com/kellydunn/golang-geo"
)

var googleGeo = geo.GoogleGeocoder{}

type twitterUser struct {
	Description            string   `json:"description"`
	Id                     int64    `json:"id"`
	ProfileLocation        string   `json:"profile_location"` // User defined location
	Name                   string   `json:"name"`
	ProfileImageUrlHttps   string   `json:"profile_image_url_https"`
	ScreenName             string   `json:"screen_name"`
	URL                    string   `json:"url"` // User defined URL
}

type twitterIntel struct {
	Location             *geo.Point        `json:"location"`
	CreatedAt            string            `json:"created_at"`
	FavoriteCount        int               `json:"favorite_count"`
	Id                   int64             `json:"id"`
	PossiblySensitive    bool              `json:"possibly_sensitive"`
	RetweetCount         int               `json:"retweet_count"`
	Text                 string            `json:"text"`
	User                 *twitterUser      `json:"user"`
	Distance             float64           `json:"distance"`
}

type tweets []twitterIntel

type apiIntel struct {
	Location    *geo.Point      `json:"search_location"`
	Intel       tweets          `json:"intel"`
}

func (t tweets) Len() int { return len(t) }
func (t tweets) Swap(i, j int) { t[i], t[j] = t[j], t[i] }
func (t tweets) Less(i, j int) bool { return t[i].RetweetCount < t[j].RetweetCount } // TODO make this smarter
