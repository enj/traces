package traces

import (
	"fmt"
	"errors"
	"net/url"
	"net/http"
	"strconv"
	"sort"
	"encoding/json"
	"github.com/ant0ine/go-json-rest/rest"
	"github.com/kellydunn/golang-geo"
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

func twitterSearchCoordinate(start *geo.Point, h *http.Client) (tweets, error) {

	v := url.Values{}
	v.Set("count", twitterCountSearch)
	v.Set("geocode", fmt.Sprintf("%f,%f,%fkm", start.Lat(), start.Lng(), twitterRadius))

	twitterAPI.HttpClient = h
	data, err := twitterAPI.GetSearch("", v)
	if err != nil {
		return nil, err // TODO for production: errors.New("Twitter API error.")
	}

	tweetList := tweets{}

	c := geoJSON{}
	for _, tweet := range data.Statuses {

		tc := tweet.Coordinates
		if tc == nil {
			continue
		}

		s, err := json.Marshal(tc)
		if err != nil {
			continue
		}
		json.Unmarshal(s, &c) //TODO remove with new API
		stop := geo.NewPoint(c.Coordinates[twitterLat], c.Coordinates[twitterLng])

		u := tweet.User
		tweetList = append(tweetList, twitterIntel{
			u.Name,
			u.ScreenName,
			u.ProfileImageUrlHttps,
			// stop,
			start.GreatCircleDistance(stop),
		})
	}

	if len(tweetList) == 0 {
		return nil, errors.New("No results found.")
	}

	sort.Sort(tweetList)
	return tweetList, nil
}

func apiCoordinateSearch(w rest.ResponseWriter, r *rest.Request) {

	q := r.URL.Query()

	slat := q.Get("lat")
	if slat == "" {
		rest.Error(w, "Latitude required.", http.StatusBadRequest)
		return
	}

	slng := q.Get("lng")
	if slng == "" {
		rest.Error(w, "Longitude required.", http.StatusBadRequest)
		return
	}

	lat, errLatFormat := parseLat(&slat)
	if errLatFormat != nil {
		rest.Error(w, errLatFormat.Error(), http.StatusBadRequest)
		return
	}

	lng, errLngFormat := parseLng(&slng)
	if errLngFormat != nil {
		rest.Error(w, errLngFormat.Error(), http.StatusBadRequest)
		return
	}

	h := appengineClient(r)
	t, errAPI := twitterSearchCoordinate(geo.NewPoint(lat, lng), h)
	if errAPI != nil {
		rest.Error(w, errAPI.Error(), http.StatusBadRequest)
		return
	}

	w.WriteJson(t)
}
