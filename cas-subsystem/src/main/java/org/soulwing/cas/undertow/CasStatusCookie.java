package org.soulwing.cas.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

/** Handles setting and counting the 'cas-status' cookie.
 * @author Stephan Fuhrmann
 *  */
class CasStatusCookie {
    public static final String STATUS_COOKIE = "cas-status";

    static final int MAX_RETRIES = 2;

    /**
     * Gets the authentication retry count from a session cookie.
     * @param exchange subject exchange
     * @return true if the retry count is exceeded
     */
    boolean isRetryCountExceeded(HttpServerExchange exchange) {
        return getRetryCount(exchange) >= MAX_RETRIES;
    }

    /**
     * Gets the authentication retry count from a session cookie.
     * @param exchange subject exchange
     * @return current retry count
     */
    int getRetryCount(HttpServerExchange exchange) {
        int retries = 0;

        Cookie cookie = exchange.getRequestCookies().get(STATUS_COOKIE);
        if (cookie == null) {
            cookie = newCookie();
        }
        else {
            try {
                String value = cookie.getValue();
                retries = Integer.parseInt(value) + 1;
                if (retries < 0 || retries > MAX_RETRIES) {
                    retries = MAX_RETRIES;
                }
            }
            catch (NumberFormatException ex) {
                retries = MAX_RETRIES;
            }
            finally {
                cookie.setValue(Integer.toString(retries));
            }
        }

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        exchange.getResponseCookies().put(STATUS_COOKIE, cookie);
        return retries;
    }

    /**
     * Resets the authentication retry count in the session cookie.
     * @param exchange the subject exchange
     */
    void resetRetryCount(HttpServerExchange exchange) {
        Cookie cookie = newCookie();
        cookie.setValue("-1");
        exchange.getResponseCookies().put(STATUS_COOKIE, cookie);
    }

    /**
     * Creates a new cookie for the authentication retry count.
     * @return cookie
     */
    private Cookie newCookie() {
        Cookie cookie;
        cookie = new CookieImpl(STATUS_COOKIE, "0");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }
}
