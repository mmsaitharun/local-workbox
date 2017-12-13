package oneapp.incture.workbox.inbox.rest;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import oneapp.incture.workbox.inbox.dto.TrackingResponse;
import oneapp.incture.workbox.inbox.dto.WorkboxResponseDto;
import oneapp.incture.workbox.inbox.services.WorkboxFacadeLocal;


@Path("/workbox")
/*@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })*/
public class WorkboxRest {

	/*@EJB
	private WorkboxFacadeLocal workbox;*/

	@GET
	@Path("/toString")
	public String toString(){
		return "It's Working!";
	}
	
	/*@GET
	@Path("/filterdetail")
	public WorkboxResponseDto getWorkboxFilterData(@QueryParam("processName") String processName,
			@QueryParam("status") String status, @QueryParam("requestId") String requestId,
			@QueryParam("createdBy") String createdBy, @QueryParam("createdAt") String createdAt,
			@QueryParam("skipCount") Integer skipCount, @QueryParam("maxCount") Integer maxCount,
			@QueryParam("page") Integer page, @QueryParam("orderBy") String orderBy,
			@QueryParam("orderType") String orderType){
		
		return workbox.getWorkboxFilterData(processName, requestId, createdBy, createdAt, status, skipCount, maxCount,
				page, orderBy, orderType);
	}

	@GET
	@Path("/completed")
	public WorkboxResponseDto getWorkboxCompletedFilterData(@QueryParam("processName") String processName,
			@QueryParam("period") String period, @QueryParam("requestId") String requestId,
			@QueryParam("createdBy") String createdBy, @QueryParam("createdAt") String createdAt,
			@QueryParam("completedAt") String completedAt, @QueryParam("skipCount") Integer skipCount,
			@QueryParam("maxCount") Integer maxCount, @QueryParam("page") Integer page) {
		
		return workbox.getWorkboxCompletedFilterData(processName, requestId, createdBy, createdAt, completedAt, period,
				skipCount, maxCount, page);
	}

	@GET
	@Path("/tracking")
	public TrackingResponse getTrackingResults() {
		return workbox.getTrackingResults();
	}*/
}
