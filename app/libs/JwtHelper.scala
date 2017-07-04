package libs

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

import scala.util.Try

/**
  * Created by chenshijue on 2017/7/4.
  */

case class JwtValidateErrorException() extends Exception

object JwtHelper {
    val algorithm = "HS256"

    val key = """43hn%6l3$jkh"""

    def encode(body: Map[String, Any]): String = {
        val header = JwtHeader("HS256")
        val claimSet = JwtClaimsSet(body)
        JsonWebToken(header, claimSet, key)
    }

    def encode(jsonBody: String): String = {
        val header = JwtHeader("HS256")
        val claimSet = JwtClaimsSet(jsonBody)
        JsonWebToken(header, claimSet, key)
    }

    def verifyAndDecode(jwt: String): Try[Map[String, String]] = {
        jwt match {
            case JsonWebToken(_header, _claim, signature) => {
                val isValid = JsonWebToken.validate(jwt, key)
                if (isValid) _claim.asSimpleMap
                else throw new JwtValidateErrorException
            }
            case x => throw new JwtValidateErrorException
        }
    }
}
