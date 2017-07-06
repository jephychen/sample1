package libs

import authentikat.jwt._

import scala.util.{Failure, Success}
import scala.util.Try

/**
  * Created by chenshijue on 2017/7/4.
  */

case class JwtValidateErrorException() extends Exception
case class JwtExpireErrorException() extends Exception

object JwtHelper {
    val algorithm = "HS256"

    final val key = """43hn%6l3$jkh"""

    def encode(body: Map[String, Any]): String = {
        val header = JwtHeader("HS256")
        val bodyExp = body + ("exp" -> (System.currentTimeMillis() + 30 * 60 * 1000))
        val claimSet = JwtClaimsSet(bodyExp)
        JsonWebToken(header, claimSet, key)
    }

    def encode(jsonBody: String): String = {
        val header = JwtHeader("HS256")
        val claimSet = JwtClaimsSet(jsonBody)
        JsonWebToken(header, claimSet, key)
    }

    def verifyAndDecode(jwt: String): Try[Map[String, String]] = {
        jwt match {
            case JsonWebToken(_header, _claim, signature) => doVerify(jwt, _claim)
            case x => Failure(new JwtValidateErrorException)
        }
    }

    private def doVerify(jwt: String, claim: JwtClaimsSetJValue ): Try[Map[String, String]] = {
        val isValid = JsonWebToken.validate(jwt, key)
        if (isValid){
            val result = claim.asSimpleMap
            result match {
                case Success(body) => {
                    body.get("exp") match {
                        case Some(exp) => if (System.currentTimeMillis() > exp.toLong)
                            Failure(new JwtExpireErrorException) else result
                        case None => Failure(new JwtExpireErrorException)
                    }
                }
            }
        }
        else Failure(new JwtValidateErrorException)
    }
}
