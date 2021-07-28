package mg.itu.lazanomentsoa.itutptparis.backendnodejs.service;

import java.util.List;

import mg.itu.lazanomentsoa.itutptparis.backendnodejs.models.LoginRequestBody;
import mg.itu.lazanomentsoa.itutptparis.backendnodejs.models.Match;
import mg.itu.lazanomentsoa.itutptparis.backendnodejs.models.Utilisateur;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NodeJsEndPoints {
    //authentification loginRequestbody(login, password)
    @POST("utilisateur/login")
    Call<Utilisateur> getUtilisateurByLoginAndPassword(@Body LoginRequestBody loginRequestBody);

    //get all match à venir
    @GET("match")
    Call<List<Match>> getAllMatchAVenir();
}
