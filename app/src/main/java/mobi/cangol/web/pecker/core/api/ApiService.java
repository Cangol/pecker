package mobi.cangol.web.pecker.core.api;

import mobi.cangol.web.pecker.core.model.SeaToken;
import mobi.cangol.web.pecker.core.model.SeaFile;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {

    @POST("/api2/auth-token/")
    @FormUrlEncoded
    Call<SeaToken> getAuthToken(@Field("username") String username, @Field("password") String password);

    @GET("/api2/repos/{repoId}/file/")
    Call<String> getFileDownloadUrl(@Header("Authorization") String token, @Path("repoId") String repoId, @Query("p") String filepath);

    @GET("/api2/repos/{repoId}/dir/")
    Call<List<SeaFile>> getDirFiles(@Header("Authorization") String token, @Path("repoId") String repoId, @Query("p") String filepath);

}