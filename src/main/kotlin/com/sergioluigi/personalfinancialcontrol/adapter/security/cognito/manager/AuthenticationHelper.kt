package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.manager

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder
import com.amazonaws.services.cognitoidp.model.*
import com.amazonaws.util.Base64
import com.amazonaws.util.StringUtils
import com.sergioluigi.personalfinancialcontrol.adapter.security.extension.toByteArrayUTF8
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.ShortBufferException
import javax.crypto.spec.SecretKeySpec

/**
 * Private class for SRP client side math.
 */
@Component
class AuthenticationHelper{
	
	private var a: BigInteger? = null
	private var A: BigInteger? = null
	private val userPoolID: String
	private val clientId: String
	private val secretKey: String?
	private val region: String
	private val userPoolIdHexa: String
	
	init {
		do {
			a = BigInteger(EPHEMERAL_KEY_LENGTH, SECURE_RANDOM).mod(N)
			A = g.modPow(a, N)
		} while(A!!.mod(N) == BigInteger.ZERO)
		userPoolID = System.getenv("COGNITO_POOL_ID")
		clientId = System.getenv("COGNITO_CLIENT_ID")
		region = System.getenv("COGNITO_REGION")
		secretKey = System.getenv("COGNITO_CLIENT_SECRET")
		userPoolIdHexa = System.getenv("COGNITO_POOL_ID_HEXA")
		
	}
	
	private fun getA(): BigInteger? {
		return A
	}
	
	private fun getPasswordAuthenticationKey(
		userId: String?, userPassword: String, B: BigInteger, salt: BigInteger
	): ByteArray {
		// Authenticate the password
		// u = H(A, B)
		val messageDigest = THREAD_MESSAGE_DIGEST.get()
		messageDigest.reset()
		messageDigest.update(A!!.toByteArray())
		val u = BigInteger(1, messageDigest.digest(B.toByteArray()))
		if(u == BigInteger.ZERO) {
			throw SecurityException("Hash of A and B cannot be zero")
		}
		
		// x = H(salt | H(poolName | userId | ":" | password))
		messageDigest.reset()
		messageDigest.update(
			userPoolIdHexa.toByteArrayUTF8()
		)
		messageDigest.update(userId!!.toByteArrayUTF8())
		messageDigest.update(":".toByteArrayUTF8())
		val userIdHash = messageDigest.digest(userPassword.toByteArrayUTF8())
		messageDigest.reset()
		messageDigest.update(salt.toByteArray())
		val x = BigInteger(1, messageDigest.digest(userIdHash))
		val S = B.subtract(
			k!!.multiply(
				g.modPow(
					x, N
				)
			)
		).modPow(
			a!!.add(u.multiply(x)), N
		).mod(N)
		val hkdf: Hkdf
		hkdf = try {
			Hkdf.getInstance("HmacSHA256")
		} catch(e: NoSuchAlgorithmException) {
			throw SecurityException(e.message, e)
		}
		hkdf.init(S.toByteArray(), u.toByteArray())
		return hkdf.deriveKey(DERIVED_KEY_INFO, DERIVED_KEY_SIZE)
	}
	
	/**
	 * Method to orchestrate the SRP Authentication
	 *
	 * @param username Username for the SRP request
	 * @param password Password for the SRP request
	 * @return the JWT token if the request is successful else null.
	 */
	fun performSRPAuthentication(username: String, password: String): AuthenticationResultType {
		val initiateAuthRequest = initiateUserSrpAuthRequest(username)
		try {
			val awsCreds = AnonymousAWSCredentials()
			val cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
				.withCredentials(AWSStaticCredentialsProvider(awsCreds)).withRegion(Regions.fromName(region)).build()
			val initiateAuthResult = cognitoIdentityProvider.initiateAuth(initiateAuthRequest)
			if(ChallengeNameType.PASSWORD_VERIFIER.toString() == initiateAuthResult.challengeName) {
				val challengeRequest = userSrpAuthRequest(
					initiateAuthResult, password, initiateAuthRequest.authParameters["SECRET_HASH"]
				)
				val result = cognitoIdentityProvider.respondToAuthChallenge(challengeRequest)
				return result.authenticationResult ?: throw NullPointerException("Wrong username or password")
			}
		} catch(ex: Exception) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong username or password")
		}
		throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong username or password")
	}
	
	/**
	 * Initialize the authentication request for the first time.
	 *
	 * @param username The user for which the authentication request is created.
	 * @return the Authentication request.
	 */
	private fun initiateUserSrpAuthRequest(username: String): InitiateAuthRequest {
		val initiateAuthRequest = InitiateAuthRequest()
		initiateAuthRequest.setAuthFlow(AuthFlowType.USER_SRP_AUTH)
		initiateAuthRequest.clientId = clientId
		//Only to be used if the pool contains the secret key.
		if(secretKey != null && !secretKey.isEmpty()) {
			initiateAuthRequest.addAuthParametersEntry(
				"SECRET_HASH",
				calculateSecretHash(clientId, secretKey, username)
			)
		}
		initiateAuthRequest.addAuthParametersEntry("USERNAME", username)
		initiateAuthRequest.addAuthParametersEntry("SRP_A", getA()!!.toString(16))
		return initiateAuthRequest
	}
	
	/**
	 * Method is used to respond to the Auth challange from the user pool
	 *
	 * @param challenge The authenticaion challange returned from the cognito user pool
	 * @param password  The password to be used to respond to the authentication challenge.
	 * @return the Request created for the previous authentication challenge.
	 */
	private fun userSrpAuthRequest(
		challenge: InitiateAuthResult, password: String, secretHash: String?
	): RespondToAuthChallengeRequest {
		val userIdForSRP = challenge.challengeParameters["USER_ID_FOR_SRP"]
		val usernameInternal = challenge.challengeParameters["USERNAME"]
		val B = BigInteger(challenge.challengeParameters["SRP_B"], 16)
		if(B.mod(N) == BigInteger.ZERO) {
			throw SecurityException("SRP error, B cannot be zero")
		}
		val salt = BigInteger(challenge.challengeParameters["SALT"], 16)
		val key = getPasswordAuthenticationKey(userIdForSRP, password, B, salt)
		val timestamp = Date()
		var hmac: ByteArray? = null
		try {
			val mac = Mac.getInstance("HmacSHA256")
			val keySpec = SecretKeySpec(key, "HmacSHA256")
			mac.init(keySpec)
			mac.update(userPoolIdHexa.toByteArrayUTF8())
			mac.update(userIdForSRP!!.toByteArrayUTF8())
			val secretBlock = Base64.decode(challenge.challengeParameters["SECRET_BLOCK"])
			mac.update(secretBlock)
			val simpleDateFormat = SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US)
			simpleDateFormat.timeZone = SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC")
			val dateString = simpleDateFormat.format(timestamp)
			val dateBytes = dateString.toByteArrayUTF8()
			hmac = mac.doFinal(dateBytes)
		} catch(e: Exception) {
			println(e)
		}
		val formatTimestamp = SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US)
		formatTimestamp.timeZone = SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC")
		val srpAuthResponses: MutableMap<String, String?> = HashMap()
		srpAuthResponses["PASSWORD_CLAIM_SECRET_BLOCK"] = challenge.challengeParameters["SECRET_BLOCK"]
		srpAuthResponses["PASSWORD_CLAIM_SIGNATURE"] = String(Base64.encode(hmac), StringUtils.UTF8)
		srpAuthResponses["TIMESTAMP"] = formatTimestamp.format(timestamp)
		srpAuthResponses["USERNAME"] = usernameInternal
		if(secretHash != null && !secretHash.isEmpty()) {
			srpAuthResponses["SECRET_HASH"] = secretHash
		}
		val authChallengeRequest = RespondToAuthChallengeRequest()
		authChallengeRequest.challengeName = challenge.challengeName
		authChallengeRequest.clientId = clientId
		authChallengeRequest.session = challenge.session
		authChallengeRequest.challengeResponses = srpAuthResponses
		return authChallengeRequest
	}
	
	/**
	 * Calculate the secret hash to be sent along with the authentication request.
	 *
	 * @param userPoolClientId     : The client id of the app.
	 * @param userPoolClientSecret : The secret for the userpool client id.
	 * @param userName             : The username of the user trying to authenticate.
	 * @return Calculated secret hash.
	 */
	fun calculateSecretHash(userPoolClientId: String, userPoolClientSecret: String, userName: String): String {
		val HMAC_SHA256_ALGORITHM = "HmacSHA256"
		val signingKey = SecretKeySpec(
			userPoolClientSecret.toByteArray(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM
		)
		return try {
			val mac = Mac.getInstance(HMAC_SHA256_ALGORITHM)
			mac.init(signingKey)
			mac.update(userName.toByteArray(StandardCharsets.UTF_8))
			val rawHmac = mac.doFinal(userPoolClientId.toByteArray(StandardCharsets.UTF_8))
			java.util.Base64.getEncoder().encodeToString(rawHmac)
		} catch(e: Exception) {
			throw RuntimeException("Error while calculating ")
		}
	}
	
	/**
	 * Internal class for doing the Hkdf calculations.
	 */
	internal class Hkdf private constructor(algorithm: String) {
		private val EMPTY_ARRAY = ByteArray(0)
		private var algorithm: String? = null
		private var prk: SecretKey? = null
		
		/**
		 * @param algorithm REQUIRED: The type of HMAC algorithm to be used.
		 */
		init {
			require(algorithm.startsWith("Hmac")) { "Invalid algorithm $algorithm. Hkdf may only be used with Hmac algorithms." }
			this.algorithm = algorithm
		}
		
		/**
		 * @param ikm REQUIRED: The input key material.
		 */
		fun init(ikm: ByteArray) {
			this.init(ikm, null as ByteArray?)
		}
		
		/**
		 * @param ikm  REQUIRED: The input key material.
		 * @param salt REQUIRED: Random bytes for salt.
		 */
		fun init(ikm: ByteArray, salt: ByteArray?) {
			var realSalt = if(salt == null) EMPTY_ARRAY else salt.clone()
			var rawKeyMaterial = EMPTY_ARRAY
			try {
				val e = Mac.getInstance(algorithm)
				if(realSalt.size == 0) {
					realSalt = ByteArray(e.macLength)
					Arrays.fill(realSalt, 0.toByte())
				}
				e.init(SecretKeySpec(realSalt, algorithm))
				rawKeyMaterial = e.doFinal(ikm)
				val key = SecretKeySpec(rawKeyMaterial, algorithm)
				Arrays.fill(rawKeyMaterial, 0.toByte())
				unsafeInitWithoutKeyExtraction(key)
			} catch(var10: GeneralSecurityException) {
				throw RuntimeException("Unexpected exception", var10)
			} finally {
				Arrays.fill(rawKeyMaterial, 0.toByte())
			}
		}
		
		/**
		 * @param rawKey REQUIRED: Current secret key.
		 * @throws InvalidKeyException
		 */
		@Throws(InvalidKeyException::class)
		private fun unsafeInitWithoutKeyExtraction(rawKey: SecretKey) {
			if(rawKey.algorithm != algorithm) {
				throw InvalidKeyException(
					"Algorithm for the provided key must match the algorithm for this Hkdf. Expected " + algorithm + " but found " + rawKey.algorithm
				)
			} else {
				prk = rawKey
			}
		}
		
		/**
		 * @param info   REQUIRED
		 * @param length REQUIRED
		 * @return converted bytes.
		 */
		fun deriveKey(info: String?, length: Int): ByteArray {
			return this.deriveKey(info?.toByteArrayUTF8(), length)
		}
		
		/**
		 * @param info   REQUIRED
		 * @param length REQUIRED
		 * @return converted bytes.
		 */
		private fun deriveKey(info: ByteArray?, length: Int): ByteArray {
			val result = ByteArray(length)
			return try {
				this.deriveKey(info, length, result, 0)
				result
			} catch(var5: ShortBufferException) {
				throw RuntimeException(var5)
			}
		}
		
		/**
		 * @param info   REQUIRED
		 * @param length REQUIRED
		 * @param output REQUIRED
		 * @param offset REQUIRED
		 * @throws ShortBufferException
		 */
		@Throws(ShortBufferException::class)
		private fun deriveKey(info: ByteArray?, length: Int, output: ByteArray, offset: Int) {
			assertInitialized()
			require(length >= 0) { "Length must be a non-negative value." }
			if(output.size < offset + length) {
				throw ShortBufferException()
			} else {
				val mac = createMac()
				require(length <= MAX_KEY_SIZE * mac.macLength) { "Requested keys may not be longer than 255 times the underlying HMAC length." }
				var t = EMPTY_ARRAY
				try {
					var loc = 0
					var i: Byte = 1
					while(loc < length) {
						mac.update(t)
						mac.update(info)
						mac.update(i)
						t = mac.doFinal()
						var x = 0
						while(x < t.size && loc < length) {
							output[loc] = t[x]
							++x
							++loc
						}
						++i
					}
				} finally {
					Arrays.fill(t, 0.toByte())
				}
			}
		}
		
		/**
		 * @return the generates message authentication code.
		 */
		private fun createMac(): Mac {
			return try {
				val ex = Mac.getInstance(algorithm)
				ex.init(prk)
				ex
			} catch(var2: NoSuchAlgorithmException) {
				throw RuntimeException(var2)
			} catch(var3: InvalidKeyException) {
				throw RuntimeException(var3)
			}
		}
		
		/**
		 * Checks for a valid pseudo-random key.
		 */
		private fun assertInitialized() {
			checkNotNull(prk) { "Hkdf has not been initialized" }
		}
		
		companion object {
			private const val MAX_KEY_SIZE = 255
			
			@Throws(NoSuchAlgorithmException::class)
			fun getInstance(algorithm: String): Hkdf {
				return Hkdf(algorithm)
			}
		}
	}
	
	companion object {
		private const val HEX_N =
			("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" +
			 "29024E088A67CC74020BBEA63B139B22514A08798E3404DD" +
			 "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245" +
			 "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED" +
			 "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D" +
			 "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F" +
			 "83655D23DCA3AD961C62F356208552BB9ED529077096966D" +
			 "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B" +
			 "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9" +
			 "DE2BCBF6955817183995497CEA956AE515D2261898FA0510" +
			 "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64" +
			 "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7" +
			 "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B" +
			 "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C" +
			 "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31" +
			 "43DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF")
		private val N = BigInteger(HEX_N, 16)
		private val g = BigInteger.valueOf(2)
		private var k: BigInteger? = null
		private const val EPHEMERAL_KEY_LENGTH = 1024
		private const val DERIVED_KEY_SIZE = 16
		private const val DERIVED_KEY_INFO = "Caldera Derived Key"
		private val THREAD_MESSAGE_DIGEST: ThreadLocal<MessageDigest> = object : ThreadLocal<MessageDigest>() {
			override fun initialValue(): MessageDigest {
				return try {
					MessageDigest.getInstance("SHA-256")
				} catch(e: NoSuchAlgorithmException) {
					throw SecurityException("Exception in authentication", e)
				}
			}
		}
		private var SECURE_RANDOM: SecureRandom? = null
		
		init {
			try {
				SECURE_RANDOM = SecureRandom.getInstance("SHA1PRNG")
				val messageDigest = THREAD_MESSAGE_DIGEST.get()
				messageDigest.reset()
				messageDigest.update(N.toByteArray())
				val digest: ByteArray =
					messageDigest.digest(
						g.toByteArray()
					)
				k = BigInteger(1, digest)
			} catch(e: NoSuchAlgorithmException) {
				throw SecurityException(e.message, e)
			}
		}
	}
}