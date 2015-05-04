package traces

import (
	"log"
	"strings"
	"net/http"
	"encoding/json"
	"internal/github.com/enj/anaconda"
	"internal/github.com/ant0ine/go-json-rest/rest"
	"io/ioutil"
)

var twitterAPI *anaconda.TwitterApi

const (
	twitterConfigFile = "config.json"
	minTwitterRadius = 0.10 // minimum twitter search radius (mi)
	defTwitterRadius = 1.00 // default twitter search radius (mi)
	maxTwitterRadius = 10.00 // maximum twitter search radius (mi)
	twitterCountSearch = "100" // max results from twitter search API
	twitterDate = "2006-01-02" // ISO format
	maxLat = 90.0 // maximum valid latitude
	maxLng = 180.0 // maximum valid longitude
	kmToMi = 0.621371 // convert kilometers to miles
	googleServerKeyFile = "google_server_key.txt" // for use with Google's geocoding API
)

func init() {

	var twitterConfig struct {
		ConsumerKey string
		ConsumerSecret string
		AccessToken string
		AccessTokenSecret string
	}

	googleRawKey, err := ioutil.ReadFile(googleServerKeyFile)
	if err != nil {
		log.Fatal(err)
	}

	googleGeo.Key = strings.TrimSpace(string(googleRawKey))

	configFile, err := ioutil.ReadFile(twitterConfigFile)
	if err != nil {
		log.Fatal(err)
	}
	err = json.Unmarshal(configFile, &twitterConfig)
	if err != nil {
		log.Fatal(err)
	}

	// TODO determine if we want to use User vs App auth for rate limiting purposes
	twitterAPI = anaconda.NewTwitterApi(twitterConfig.AccessToken, twitterConfig.AccessTokenSecret)
	anaconda.SetConsumerKey(twitterConfig.ConsumerKey)
	anaconda.SetConsumerSecret(twitterConfig.ConsumerSecret)

	api := rest.NewApi()
	api.Use(rest.DefaultDevStack...) //TODO change to DefaultProdStack or something similar
	router, err := rest.MakeRouter(
		&rest.Route{"GET", "/api/coordinate", apiCoordinateSearch},
		&rest.Route{"GET", "/api/address", apiAddressSearch},
	)
	if err != nil {
		log.Fatal(err)
	}
	api.SetApp(router)
	http.Handle("/", api.MakeHandler())

}

//TODO search sentiment
// https://dev.twitter.com/rest/public/search
// https://twitter.com/search-home
// https://dev.twitter.com/rest/reference/get/search/tweets
