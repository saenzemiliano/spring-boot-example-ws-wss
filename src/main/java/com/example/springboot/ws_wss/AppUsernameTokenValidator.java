package com.example.springboot.ws_wss;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.message.token.UsernameToken;
import org.apache.wss4j.dom.validate.Credential;
import org.apache.wss4j.dom.validate.Validator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * This class validates a processed UsernameToken, extracted from the Credential
 * passed to the validate method.
 */
public class AppUsernameTokenValidator implements Validator {

	
	private static final Logger LOG = Logger.getLogger(AppUsernameTokenValidator.class.getName());

	/**
	 * Validate the credential argument. It must contain a non-null UsernameToken. A
	 * CallbackHandler implementation is also required to be set.
	 *
	 * If the password type is either digest or plaintext, it extracts a password
	 * from the CallbackHandler and then compares the passwords appropriately.
	 *
	 * If the password is null it queries a hook to allow the user to validate
	 * UsernameTokens of this type.
	 *
	 * @param credential the Credential to be validated
	 * @param data       the RequestData associated with the request
	 * @throws WSSecurityException on a failed validation
	 */
	public Credential validate(Credential credential, RequestData data) throws WSSecurityException {
		if (credential == null || credential.getUsernametoken() == null) {
			throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "noCredential");
		}

		boolean handleCustomPasswordTypes = data.isHandleCustomPasswordTypes();
		boolean passwordsAreEncoded = data.isEncodePasswords();
		String requiredPasswordType = data.getRequiredPasswordType();

		UsernameToken usernameToken = credential.getUsernametoken();
		usernameToken.setPasswordsAreEncoded(passwordsAreEncoded);

		String pwType = usernameToken.getPasswordType();
		LOG.log(Level.INFO, "UsernameToken user {0}", usernameToken.getName());
		LOG.log(Level.INFO, "UsernameToken password type {0}", pwType);

		if (requiredPasswordType != null && !requiredPasswordType.equals(pwType)) {
			LOG.log(Level.INFO, "Authentication failed as the received password type does not "
					+ "match the required password type of: {0}", requiredPasswordType);
			throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
		}

		//
		// If the UsernameToken is hashed or plaintext, then retrieve the password from
		// the
		// callback handler and compare directly. If the UsernameToken is of some
		// unknown type,
		// then delegate authentication to the callback handler
		//
		String password = usernameToken.getPassword();
		if (usernameToken.isHashed()) {
			verifyDigestPassword(usernameToken, data);
		} else if (WSConstants.PASSWORD_TEXT.equals(pwType)
				|| password != null && (pwType == null || "".equals(pwType.trim()))) {
			verifyPlaintextPassword(usernameToken, data);
		} else if (password != null) {
			if (!handleCustomPasswordTypes) {
				LOG.log(Level.INFO, "Authentication failed as handleCustomUsernameTokenTypes is false");
				throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
			}
			verifyCustomPassword(usernameToken, data);
		} else {
			verifyUnknownPassword(usernameToken, data);
		}
		return credential;
	}

	/**
	 * Verify a UsernameToken containing a password of some unknown (but specified)
	 * password type. It does this by querying a CallbackHandler instance to obtain
	 * a password for the given username, and then comparing it against the received
	 * password. This method currently uses the same LOG.c as the
	 * verifyPlaintextPassword case, but it in a separate protected method to allow
	 * users to override the validation of the custom password type specific case.
	 * 
	 * @param usernameToken The UsernameToken instance to verify
	 * @throws WSSecurityException on a failed authentication.
	 */
	protected void verifyCustomPassword(UsernameToken usernameToken, RequestData data) throws WSSecurityException {
		verifyPlaintextPassword(usernameToken, data);
	}

	/**
	 * Verify a UsernameToken containing a plaintext password. It does this by
	 * querying a CallbackHandler instance to obtain a password for the given
	 * username, and then comparing it against the received password. This method
	 * currently uses the same LOG.c as the verifyDigestPassword case, but it in a
	 * separate protected method to allow users to override the validation of the
	 * plaintext password specific case.
	 * 
	 * @param usernameToken The UsernameToken instance to verify
	 * @throws WSSecurityException on a failed authentication.
	 */
	protected void verifyPlaintextPassword(UsernameToken usernameToken, RequestData data) throws WSSecurityException {
		verifyDigestPassword(usernameToken, data);
	}

	/**
	 * Verify a UsernameToken containing a password digest. It does this by querying
	 * a CallbackHandler instance to obtain a password for the given username, and
	 * then comparing it against the received password.
	 * 
	 * @param usernameToken The UsernameToken instance to verify
	 * @throws WSSecurityException on a failed authentication.
	 */
	protected void verifyDigestPassword(UsernameToken usernameToken, RequestData data) throws WSSecurityException {
		if (data.getCallbackHandler() == null) {
			throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "noCallback");
		}

		String user = usernameToken.getName();
		String password = usernameToken.getPassword();
		String nonce = usernameToken.getNonce();
		String createdTime = usernameToken.getCreated();
		String pwType = usernameToken.getPasswordType();
		boolean passwordsAreEncoded = usernameToken.getPasswordsAreEncoded();

		WSPasswordCallback pwCb = new WSPasswordCallback(user, null, pwType, WSPasswordCallback.USERNAME_TOKEN);
		try {
			data.getCallbackHandler().handle(new Callback[] { pwCb });
		} catch (IOException | UnsupportedCallbackException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION, e);
		}
		String origPassword = pwCb.getPassword();
		if (origPassword == null) {
			LOG.log(Level.SEVERE, "Callback supplied no password for: {0}", user);
			throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
		}
		if (usernameToken.isHashed()) {
			/*String passDigest;
			if (passwordsAreEncoded) {
				LOG.log(Level.SEVERE, "/////////////////////////////222");
				passDigest = UsernameToken.doPasswordDigest(nonce, createdTime,
						Base64.getMimeDecoder().decode(origPassword));
			} else {
				LOG.log(Level.SEVERE, "/////////////////////////////111////" + new String(Base64.getMimeDecoder().decode(password)));
				passDigest = UsernameToken.doPasswordDigest(nonce, createdTime, origPassword);
			}
			
			
			if (!matches(passDigest, password)) {
				throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
			}*/
			throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION, new Exception("PasswordDigest is not allowed"));
		} else {
			if (!matches(origPassword, password)) {
				throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
			}
		}
	}
	
	

	private boolean matches(String encodedPassword, String rawPassword) {
		//LOG.log(Level.INFO, "EncodedPassword: "+ encodedPassword +"  RawPassword: " + rawPassword);
		return (new BCryptPasswordEncoder()).matches(rawPassword, encodedPassword);
	}

	/**
	 * Verify a UsernameToken containing no password. An exception is thrown unless
	 * the user has explicitly allowed this use-case via
	 * WSHandlerConstants.ALLOW_USERNAMETOKEN_NOPASSWORD
	 * 
	 * @param usernameToken The UsernameToken instance to verify
	 * @throws WSSecurityException on a failed authentication.
	 */
	protected void verifyUnknownPassword(UsernameToken usernameToken, RequestData data) throws WSSecurityException {

		boolean allowUsernameTokenDerivedKeys = data.isAllowUsernameTokenNoPassword();
		if (!allowUsernameTokenDerivedKeys) {
			LOG.log(Level.SEVERE,"Authentication failed as the received UsernameToken does not " + "contain any password element");
			throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
		}
	}

}
