package traces

import (
	"fmt"
	"errors"
	"net/url"
	"net/http"
	"sort"
	"time"
	"internal/github.com/ant0ine/go-json-rest/rest"
	"internal/github.com/enj/golang-geo"
)

// All input must be validated before using this function
func twitterSearchCoordinate(start *geo.Point, h *http.Client, radius float64, since, until *time.Time) (tweets, error) {

	v := url.Values{}
	v.Set("count", twitterCountSearch)
	v.Set("result_type", "recent") // TODO determine best
	v.Set("include_entities", "false")
	v.Set("geocode", fmt.Sprintf("%f,%f,%fmi", start.Lat(), start.Lng(), radius))

	var query string // TODO break this out into a query builder function
	if since != nil && until != nil {
		query = fmt.Sprintf("since:%s until:%s", since.Format(twitterDate), until.Format(twitterDate))
	} else if since != nil {
		query = fmt.Sprintf("since:%s", since.Format(twitterDate))
	} else if until != nil {
		query = fmt.Sprintf("until:%s", until.Format(twitterDate))
	} else {
		query = ""
	}

	twitterAPI.HttpClient = h
	data, err := twitterAPI.GetSearch(query, v)
	if err != nil {
		return nil, err // TODO for production: errors.New("Twitter API error.")
	}

	tweetList := tweets{}

	for _, tweet := range data.Statuses {

		if !tweet.HasCoordinates() {
			continue
		}

		lat, _ := tweet.Latitude()
		lng, _ := tweet.Longitude()

		stop := geo.NewPoint(lat, lng)

		u := tweet.User
		tweetList = append(tweetList, twitterIntel{
			stop,
			tweet.CreatedAt,
			tweet.FavoriteCount,
			tweet.Id,
			tweet.PossiblySensitive,
			tweet.RetweetCount,
			tweet.Text,
			&twitterUser{
				u.Description,
				u.Id,
				u.Location,
				u.Name,
				u.ProfileImageUrlHttps,
				u.ScreenName,
				u.URL,
			},
			start.GreatCircleDistance(stop) * kmToMi, // km to mi
		})
	}

	if len(tweetList) == 0 {
		return nil, errors.New("No results found.")
	}

	sort.Sort(sort.Reverse(tweetList))
	return tweetList, nil
}

func apiCoordinateSearch(w rest.ResponseWriter, r *rest.Request) {

	q := r.URL.Query()

	sLat := q.Get("lat")
	if sLat == "" {
		rest.Error(w, "Latitude required.", http.StatusBadRequest)
		return
	}

	sLng := q.Get("lng")
	if sLng == "" {
		rest.Error(w, "Longitude required.", http.StatusBadRequest)
		return
	}

	lat, errLatFormat := parseLat(&sLat)
	if errLatFormat != nil {
		rest.Error(w, errLatFormat.Error(), http.StatusBadRequest)
		return
	}

	lng, errLngFormat := parseLng(&sLng)
	if errLngFormat != nil {
		rest.Error(w, errLngFormat.Error(), http.StatusBadRequest)
		return
	}

	h := appengineClient(r)
	p := geo.NewPoint(lat, lng)

	// TODO figure out how to do this request concurrently
	googleGeo.HttpClient = h
	a, _ := googleGeo.ReverseGeocode(p) // Don't care about the error since geocode may not have address

	commonAPIResponse(w, &q, h, p, &a)
}
