package com.sergioluigi.personalfinancialcontrol.adapter.security.cognito.manager

import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.security.InvalidParameterException
import java.util.*

/**
 * Utility class for all operations on JWT.
 */
object CognitoJWTParser {
	private const val HEADER = 0
	private const val PAYLOAD = 1
	private const val SIGNATURE = 2
	private const val JWT_PARTS = 3
	
	/**
	 * Returns header for a JWT as a JSON object.
	 *
	 * @param jwt REQUIRED: valid JSON Web Token as String.
	 * @return header as a JSONObject.
	 */
	fun getHeader(jwt: String): JSONObject {
		return try {
			validateJWT(jwt)
			val dec = Base64.getDecoder()
			val sectionDecoded = dec.decode(jwt.split("\\.").toTypedArray()[HEADER])
			val jwtSection = String(sectionDecoded, Charsets.UTF_8)
			JSONObject(jwtSection)
		} catch(e: UnsupportedEncodingException) {
			throw InvalidParameterException(e.message)
		} catch(e: Exception) {
			throw InvalidParameterException("error in parsing JSON")
		}
	}
	
	/**
	 * Returns payload of a JWT as a JSON object.
	 *
	 * @param jwt REQUIRED: valid JSON Web Token as String.
	 * @return payload as a JSONObject.
	 */
	private fun getPayload(jwt: String): JSONObject {
		return try {
			validateJWT(jwt)
			val dec = Base64.getDecoder()
			val payload = jwt.split("\\.").toTypedArray()[PAYLOAD]
			val sectionDecoded = dec.decode(payload)
			val jwtSection = String(sectionDecoded, Charsets.UTF_8)
			JSONObject(jwtSection)
		} catch(e: UnsupportedEncodingException) {
			throw InvalidParameterException(e.message)
		} catch(e: Exception) {
			throw InvalidParameterException("error in parsing JSON")
		}
	}
	
	/**
	 * Returns signature of a JWT as a String.
	 *
	 * @param jwt REQUIRED: valid JSON Web Token as String.
	 * @return signature as a String.
	 */
	fun getSignature(jwt: String): String {
		return try {
			validateJWT(jwt)
			val dec = Base64.getDecoder()
			val sectionDecoded = dec.decode(jwt.split("\\.").toTypedArray()[SIGNATURE])
			String(sectionDecoded, Charsets.UTF_8)
		} catch(e: Exception) {
			throw InvalidParameterException("error in parsing JSON")
		}
	}
	
	/**
	 * Returns a claim, from the `JWT`s' payload, as a String.
	 *
	 * @param jwt   REQUIRED: valid JSON Web Token as String.
	 * @param claim REQUIRED: claim name as String.
	 * @return claim from the JWT as a String.
	 */
	fun getClaim(jwt: String, claim: String?): String? {
		try {
			val payload = getPayload(jwt)
			val claimValue = payload[claim]
			if(claimValue != null) {
				return claimValue.toString()
			}
		} catch(e: Exception) {
			throw InvalidParameterException("invalid token")
		}
		return null
	}
	
	/**
	 * Checks if `JWT` is a valid JSON Web Token.
	 *
	 * @param jwt REQUIRED: The JWT as a [String].
	 */
	private fun validateJWT(jwt: String) {
		// Check if the the JWT has the three parts
		val jwtParts = jwt.split("\\.").toTypedArray()
		if(jwtParts.size != JWT_PARTS) {
			throw InvalidParameterException("not a JSON Web Token")
		}
	}
}