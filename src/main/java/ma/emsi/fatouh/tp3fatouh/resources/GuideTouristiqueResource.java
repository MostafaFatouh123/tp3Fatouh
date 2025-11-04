package ma.emsi.fatouh.tp3fatouh.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.emsi.fatouh.tp3fatouh.llm.LlmClientPourGemini;

@Path("/guide")
public class GuideTouristiqueResource {

    @Inject
    LlmClientPourGemini llm;

    @GET
    @Path("lieu/{ville_ou_pays}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response villeOuPays(@PathParam("ville_ou_pays") String lieu) {
        String resultat = llm.envoyerRequete(lieu);
        return Response.ok(resultat).build();
    }
}

