package hu.okrim.droneprojectmanager.security;

public class SecurityConstants {
    //Secret key should always be strong (uppercase, lowercase, numbers, symbols) so that nobody can potentially decode the signature.
    public static final String SECRET_KEY = "H4IItyBcabr4G^TqPA6*rb7HTcfbZaOXu$Yi#azPQ$^%CL*JvlaMTLjOtrkmkBVY6ohQ2ZKQJIX7PsO7w&BqnRH^1P%y6iQ2%R8Z";
    public static final int TOKEN_EXPIRATION = 1800000; // 1800000 milliseconds = 30 minutes.
    public static final String BEARER = "Bearer "; // Authorization : "Bearer " + Token
    public static final String AUTHORIZATION = "Authorization"; // "Authorization" : Bearer Token
    public static final String REGISTER_PATH = "/user/register"; // Public path that users can use to register.
}
