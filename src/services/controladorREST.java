package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ejercicio3.AnalizadorSAX;
import ejercicio3.ManejadorSAX;
import ejercicio3.ProgramaResultado;
import ejercicio4.AnalizadorDOM;
import modelo.Favoritos;
import modelo.ListadoProgramas;
import servicio.controlador.ServicioALaCarta;
import servicio.tipos.Emision;
import servicio.tipos.Programa;
import servicio.utilidades.Utilidades;

@Path("programas")
public class controladorREST {

	@Context
	private UriInfo uriInfo;

	private ServicioALaCarta controlador = ServicioALaCarta.getInstance();

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getListadoProgramasXML() {
		return Response.status(Status.OK).entity(controlador.getListadoProgramasXML()).build();
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getPrograma(@PathParam("id") String id) {
		try {
			return Response.status(Response.Status.OK).entity(controlador.getPrograma(id)).build();
		} catch (IllegalArgumentException | UnsupportedEncodingException | FileNotFoundException | JAXBException e) {
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/{id}/atom")
	@Produces({ MediaType.APPLICATION_ATOM_XML })
	public Response getProgramaAtom(@PathParam("id") String id) {
		try {
			return Response.status(Response.Status.OK).entity(controlador.getProgramaAtom(id)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@GET
	@Path("/{id}/filtro")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getProgramaFiltrado(@PathParam("id") String id, @FormParam("titulo") String titulo) {
		try {
			return Response.status(Response.Status.OK).entity(controlador.getProgramaFiltrado(id, titulo)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
	}

	@POST
	public Response crearFavoritos() {

		try {
			String id = controlador.crearFavoritos();

			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			builder.path(controlador.getPathFavoritos(id));

			return Response.created(builder.build()).build();
		} catch (JAXBException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path("/favoritos/{idFavoritos}/programa/{idPrograma}")
	public Response addProgramaFavorito(@PathParam("idFavoritos") String idFavoritos,
			@PathParam("idPrograma") String idPrograma) {
		try {
			if (controlador.addProgramaFavorito(idFavoritos, idPrograma)) {
				return Response.status(Status.NO_CONTENT).entity(controlador.getFavoritos(idFavoritos)).build();
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return Response.status(Status.NOT_MODIFIED).build();
	}

	@DELETE
	@Path("/favoritos/{idFavoritos}/programa/{idPrograma}")
	public Response removeProgramaFavorito(@PathParam("idFavoritos") String idFavoritos,
			@PathParam("idPrograma") String idPrograma) {

		try {
			if (controlador.removeProgramaFavorito(idFavoritos, idPrograma))
				return Response.status(Status.NO_CONTENT).entity(controlador.getFavoritos(idFavoritos)).build();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return Response.status(Status.NOT_MODIFIED).build();
	}

	@GET
	@Path("/favoritos/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getFavoritos(@PathParam("id") String idFavoritos) {
		try {
			return Response.status(Response.Status.OK).entity(controlador.getFavoritos(idFavoritos)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).build();
		}
	}

}
