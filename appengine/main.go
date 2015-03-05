package traces

import (
	"log"
	"net/http"
	"encoding/json"
	"github.com/ChimeraCoder/anaconda"
	"internal/github.com/ant0ine/go-json-rest/rest"
	"io/ioutil"
)

var twitterAPI *anaconda.TwitterApi

const (
	twitterConfigFile = "config.json"
	twitterRadius = 0.25 // twitter search radius (km)
	twitterCountSearch = "100" // max results from twitter search API
	twitterLat = 1 // index of latitude in twitter JSON
	twitterLng = 0 // index of longitude in twitter JSON
	maxLat = 90.0 // maximum valid latitude
	maxLng = 180.0 // maximum valid longitude
)

func init() {

	var twitterConfig struct {
		ConsumerKey string
		ConsumerSecret string
		AccessToken string
		AccessTokenSecret string
	}

	configFile, err := ioutil.ReadFile(twitterConfigFile)
	if err != nil {
		log.Fatal(err)
	}
	err = json.Unmarshal(configFile, &twitterConfig)
	if err != nil {
		log.Fatal(err)
	}

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
