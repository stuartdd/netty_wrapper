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
 * @author stuar
 */
public class Route {

    private Object objectAtRoute;
    private final String name;
    private final List<Route> routes = new ArrayList<>();

    public Route(String name) {
        this.name = name;
    }

    public void add(String[] names, int index, Object objectAtRoute) {
        if (index > (names.length - 1)) {
            this.objectAtRoute = objectAtRoute;
            return;
        }
        Route route = null;
        for (Route r : routes) {
            if (r.getName().equals(names[index])) {
                route = r;
                break;
            }
        }
        if (route == null) {
            route = new Route(names[index]);
            routes.add(route);
        }
        route.add(names, index + 1, objectAtRoute);
    }

    public String getName() {
        return name;
    }

    public Object getObjectAtRoute() {
        return objectAtRoute;
    }

    public Object matchNext(String[] path, int index) {
        for (Route r : routes) {
            if (r.name.equals("...")) {
                return returnObject(true, r.getObjectAtRoute());
            }
            if (r.matchThis(path, index)) {
                if (r.hasNext()) {
                    return returnObject(true, r.matchNext(path, index + 1));
                } else {
                    return returnObject((index == (path.length - 1)), r.getObjectAtRoute());
                }
            }
        }
        return returnObject(false, null);
    }

    public boolean matchThis(String[] path, int index) {
        if (index > (path.length - 1)) {
            return false;
        }
        return name.equals("*") || (name.equalsIgnoreCase(path[index]));
    }

    public boolean matchThisExact(String[] path, int index) {
        if (index > (path.length - 1)) {
            return false;
        }
        return name.equalsIgnoreCase(path[index]);
    }

    private Object returnObject(boolean yes, Object objectToReturn) {
        if (yes) {
            return objectToReturn;
        } else {
            return null;
        }
    }

    public boolean hasNext() {
        return routes.size() > 0;
    }

    public int length() {
        return routes.size();
    }

    public String list(StringBuilder sb, int index) {
        for (Route r : routes) {
            sb.append(index).append(':').append(r.getName()).append(':');
            r.list(sb, index + 1);
        }
        if (sb.charAt(sb.length() - 1) != ' ') {
            sb.append(" --> ");
        }
        return sb.toString().trim();
    }

}
