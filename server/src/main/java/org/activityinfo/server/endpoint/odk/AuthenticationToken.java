package org.activityinfo.server.endpoint.odk;

import org.activityinfo.server.database.hibernate.entity.Authentication;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * An authentication token for use in ODK XForm submissions. The zero-argument constructor will securely generate tokens
 * itself, while the other constructor can be used to parse tokens in Base64 encoding. Base64 encoded strings need to be
 * 8 characters long, which corresponds to a raw token size of 6 bytes (or 48 bits). This should contain enough entropy.
 */
public final class AuthenticationToken {
    final private byte token[];

    public AuthenticationToken() {
        token = new byte[6];
        new SecureRandom().nextBytes(token);
    }

    public AuthenticationToken(String token) {
        this.token = DatatypeConverter.parseBase64Binary(token);
        if (this.token.length != 6) throw new IllegalArgumentException("AuthenticationToken must be 8 characters long");
    }

    public String getToken() {
        return DatatypeConverter.printBase64Binary(token);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(token);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Authentication) {
            AuthenticationToken other = (AuthenticationToken) object;
            return Arrays.equals(token, other.token);
        } else return false;
    }
}
