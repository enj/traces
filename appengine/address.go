package traces

import (
	"net/http"
	"internal/github.com/ant0ine/go-json-rest/rest"
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
	p, a, errGoogle := googleGeo.GeocodeWithAddress(address)
	if errGoogle != nil {
		rest.Error(w, errGoogle.Error(), http.StatusBadRequest) //TODO "google error" production
		return
	}

	commonAPIResponse(w, &q, h, p, &a)
}
