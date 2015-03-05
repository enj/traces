package traces

import (
	"appengine"
	"appengine/urlfetch"
	"net/http"
	"github.com/ant0ine/go-json-rest/rest"
)

func appengineClient(r *rest.Request) *http.Client {
	return urlfetch.Client(appengine.NewContext(r.Request))
}
