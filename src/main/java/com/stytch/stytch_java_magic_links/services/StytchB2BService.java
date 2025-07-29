package com.stytch.stytch_java_magic_links.services;

import com.stytch.java.b2b.StytchB2BClient;
import com.stytch.java.b2b.models.magiclinks.AuthenticateRequest;
import com.stytch.java.b2b.models.magiclinks.AuthenticateResponse;
import com.stytch.java.b2b.models.passwordsemail.ResetRequest;
import com.stytch.java.b2b.models.passwordsemail.ResetResponse;
import com.stytch.java.common.StytchException;
import com.stytch.java.common.StytchResult;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StytchB2BService {

    @Autowired
    private Environment env;

    public static String STYTCH_SESSION_JWT_COOKIE_NAME = "stytch_session_jwt";

    @Bean
    public StytchB2BClient getB2BClient() {
        String id = env.getProperty("stytch.project.id");
        String secret = env.getProperty("stytch.project.secret");
        assert id != null;
        assert secret != null;
        return new StytchB2BClient(id, secret);
    }

    public AuthenticateResponse authenticateToken(String token) throws ExecutionException, InterruptedException, StytchException {
        StytchB2BClient client = getB2BClient();
        StytchResult<AuthenticateResponse> response = client.magicLinks.authenticateCompletable(new AuthenticateRequest("")).get();
        if (response instanceof StytchResult.Success<AuthenticateResponse>) {
            return ((StytchResult.Success<AuthenticateResponse>) response).getValue();
        } else {
            throw ((StytchResult.Error) response).getException();
        }
    }

    public ResetResponse resetPassword(String token, String password)
        throws ExecutionException, InterruptedException, StytchException {
        StytchB2BClient client = getB2BClient();
        StytchResult<ResetResponse> response = client.passwords.getEmail().resetCompletable(new ResetRequest(token, password, null, 5)).get();
        if(response instanceof StytchResult.Success<ResetResponse>) {
            return ((StytchResult.Success<ResetResponse>)response).getValue();
        } else {
            throw ((StytchResult.Error) response).getException();
        }
    }
}
