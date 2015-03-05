package traces

import (
	"net/http"
	"github.com/ant0ine/go-json-rest/rest"
)

func apiAddressSearch(w rest.ResponseWriter, r *rest.Request) {

	q := r.URL.Query()

	address := q.Get("s")
	if address == "" {
		rest.Error(w, "Address required.", http.StatusBadRequest)
		return
	}

	h := appengineClient(r)
	googleGeo.HttpClient = h
	p, errGoogle := googleGeo.Geocode(address)
	if errGoogle != nil {
		rest.Error(w, errGoogle.Error(), http.StatusBadRequest) //TODO "google error" production
		return
	}

	t, errAPI := twitterSearchCoordinate(p, h)
	if errAPI != nil {
		rest.Error(w, errAPI.Error(), http.StatusBadRequest)
		return
	}

	w.WriteJson(addressIntel{p, t})
}
