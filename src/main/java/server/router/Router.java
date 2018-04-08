/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.router;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author stuart
 */
public class Router {

    private final List<Route> routes = new ArrayList<>();

    public void add(String[] names, Object o) {
        if ((names.length == 0)) {
            return;
        }
        Route route = null;
        for (Route r : routes) {
            if (r.getName().equals(names[0])) {
                route = r;
                break;
            }
        }
        if (route == null) {
            route = new Route(names[0]);
            routes.add(route);
        }
        route.add(names, 1, o);
    }

    public String list(StringBuilder sb, int n) {
        for (Route r : routes) {
            sb.append(n).append(':').append(r.getName()).append(':');
            r.list(sb, n + 1);
        }
        return sb.toString().trim();
    }

    public Object match(String[] path) {
        for (Route r : routes) {
            if (r.matchThisExact(path, 0)) {
                if (path.length == 1) {
                    if (!r.hasNext()) {
                        return r.getObjectAtRoute();
                    } else {
                        return null;
                    }
                }
                if (r.hasNext()) {
                    return r.matchNext(path, 1);
                }
            }
        }
        return null;
    }

}
