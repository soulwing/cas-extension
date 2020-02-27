package org.soulwing.cas.undertow;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

/** A dummy handler for the cas status cookie that is used when
 * cookie handling is disabled.
 * @author Stephan Fuhrmann
 *  */
class NoCasStatusCookie {

    /**
     * Gets the authentication retry count from a session cookie.
     * @param exchange subject exchange
     * @return true if the retry count is exceeded
     */
    boolean isRetryCountExceeded(HttpServerExchange exchange) {
        return false;
    }

    /**
     * Resets the authentication retry count in the session cookie.
     * @param exchange the subject exchange
     */
    void resetRetryCount(HttpServerExchange exchange) {
    }
}
