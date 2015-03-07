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
	p, errGoogle := googleGeo.Geocode(address)
	if errGoogle != nil {
		rest.Error(w, errGoogle.Error(), http.StatusBadRequest) //TODO "google error" production
		return
	}

	t, err := commonAPIValidation(&w, &q, h, p)
	if err != nil {
		rest.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	w.WriteJson(addressIntel{p, t})
}
