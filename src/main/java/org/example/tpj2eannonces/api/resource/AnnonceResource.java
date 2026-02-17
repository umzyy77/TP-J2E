package org.example.tpj2eannonces.api.resource;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.example.tpj2eannonces.api.dto.annonce.AnnonceCreateDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceResponseDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceSearchParams;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceStatusDTO;
import org.example.tpj2eannonces.api.dto.annonce.AnnonceUpdateDTO;
import org.example.tpj2eannonces.api.dto.common.PaginatedResponseDTO;
import org.example.tpj2eannonces.api.mapper.AnnonceMapper;
import org.example.tpj2eannonces.api.security.UserPrincipal;
import org.example.tpj2eannonces.exception.NotFoundException;
import org.example.tpj2eannonces.model.Annonce;
import org.example.tpj2eannonces.service.AnnonceService;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnonceResource {

    private final AnnonceService annonceService;

    public AnnonceResource() {
        this.annonceService = new AnnonceService();
    }

    protected AnnonceResource(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    @Context
    private SecurityContext securityContext;

    @GET
    @PermitAll
    public Response list(@Valid @BeanParam AnnonceSearchParams params) {
        List<Annonce> annonces;
        long totalCount;

        if (params.hasKeyword()) {
            annonces = annonceService.searchByFilters(params.getKeyword(), params.getCategoryId(), params.getStatus(), params.getPage(), params.getSize());
            totalCount = annonceService.countBySearchAndFilters(params.getKeyword(), params.getCategoryId(), params.getStatus());
        } else if (params.hasFilters()) {
            annonces = annonceService.findByFilters(params.getCategoryId(), params.getStatus(), params.getPage(), params.getSize());
            totalCount = annonceService.countByFilters(params.getCategoryId(), params.getStatus());
        } else {
            annonces = annonceService.findAll(params.getPage(), params.getSize());
            totalCount = annonceService.count();
        }

        List<AnnonceResponseDTO> dtos = AnnonceMapper.toResponseDTOList(annonces);
        return Response.ok(PaginatedResponseDTO.of(dtos, totalCount, params.getPage(), params.getSize())).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getById(@PathParam("id") Long id) {
        Annonce annonce = annonceService.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Annonce non trouvee: " + id));
        return Response.ok(AnnonceMapper.toResponseDTO(annonce)).build();
    }

    @POST
    public Response create(@Valid AnnonceCreateDTO dto, @Context UriInfo uriInfo) {
        UUID currentUserId = getCurrentUserId();
        Annonce annonce = AnnonceMapper.toEntity(dto);
        Annonce created = annonceService.create(annonce, currentUserId, dto.categoryId());

        AnnonceResponseDTO responseDTO = annonceService.findByIdWithRelations(created.getId())
                .map(AnnonceMapper::toResponseDTO)
                .orElseThrow();

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(created.getId()))
                .build();

        return Response.created(location).entity(responseDTO).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid AnnonceUpdateDTO dto) {
        UUID currentUserId = getCurrentUserId();

        Annonce updated = annonceService.updateFields(
                id, currentUserId, dto.title(), dto.description(), dto.adress(), dto.mail(), dto.categoryId());

        AnnonceResponseDTO responseDTO = annonceService.findByIdWithRelations(updated.getId())
                .map(AnnonceMapper::toResponseDTO)
                .orElseThrow();

        return Response.ok(responseDTO).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        UUID currentUserId = getCurrentUserId();

        annonceService.delete(id, currentUserId);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}")
    public Response changeStatus(@PathParam("id") Long id, @Valid AnnonceStatusDTO dto) {
        UUID currentUserId = getCurrentUserId();

        Annonce updated = annonceService.changeStatus(id, currentUserId, dto.action());

        AnnonceResponseDTO responseDTO = annonceService.findByIdWithRelations(updated.getId())
                .map(AnnonceMapper::toResponseDTO)
                .orElseThrow();

        return Response.ok(responseDTO).build();
    }

    /**
     * Extrait le userId de l'utilisateur authentifie depuis le SecurityContext.
     */
    private UUID getCurrentUserId() {
        UserPrincipal principal = (UserPrincipal) securityContext.getUserPrincipal();
        return principal.getUserId();
    }
}
