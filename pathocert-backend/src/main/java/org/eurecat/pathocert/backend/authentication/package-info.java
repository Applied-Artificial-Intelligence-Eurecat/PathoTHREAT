/**
 * This package contains the necessary classes to perform an authentication
 * using JWT.
 * <p>
 * Although it is weird that it is all in a separate module instead of
 * having classes of models, endpoints and similar, it was thought that
 * getting it in a different package will lessen the work for others to
 * understand how it works.
 * <p>
 * It simply uses an endpoint to create a JWT, and then filters every request
 * to add the authentication wherever it sees a valid JWT.
 * <p>
 * It also needs some little configuration at the config level, adding the
 * filter and making the session stateless, which is the advantage of this
 * kind of configuration.
 *
 * @author sergi.simon
 */
package org.eurecat.pathocert.backend.authentication;