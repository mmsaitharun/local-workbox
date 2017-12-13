package oneapp.incture.workbox.inbox.services;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.rpc.ServiceException;
import javax.xml.ws.WebServiceRef;

import com.incture.pmc.poadapter.services.WorkBoxActionFacadeService;

import oneapp.incture.workbox.inbox.dto.ResponseMessage;
import oneapp.incture.workbox.inbox.dto.WorkBoxActionDto;
import oneapp.incture.workbox.inbox.dto.WorkBoxActionListDto;
import oneapp.incture.workbox.util.ServicesUtil;

/**
 * Session Bean implementation class UserManagementFacadeWsdlConsumer
 */
@Stateless
public class WorkBoxActionFacadeWsdlConsumer implements WorkBoxActionFacadeWsdlConsumerLocal {

	public WorkBoxActionFacadeWsdlConsumer() {
	}

	@WebServiceRef(name = "WorkBoxActionFacadeService")
	private WorkBoxActionFacadeService actionServices;

	@Override
	public ResponseMessage claimTask(WorkBoxActionDto dto) {
		System.err.println("[PMC][WorkBoxAction][services][claim] method invoked with input" + dto);
		ResponseMessage responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		if (!ServicesUtil.isEmpty(dto.getTaskInstanceId())) {
			try {
				System.err.println("[PMC][WorkBoxAction][services][claim][taskInstanceId] " + dto.getTaskInstanceId());
				if (actionServices.getWorkBoxActionFacadePort().claimTask(dto.getTaskInstanceId())) {
					responseDto.setMessage("Task claimed successfully");
					responseDto.setStatus("SUCCESS");
					responseDto.setStatusCode("0");
					return responseDto;
				}
			} catch (Exception e) {
				responseDto.setMessage("Task claim Failed because" + e.getMessage());
			}
		} else {
			responseDto.setMessage("Instance Id required to claim");
		}
		return responseDto;
	}

	@Override
	public ResponseMessage release(WorkBoxActionDto dto) {
		System.err.println("[PMC][WorkBoxAction][services][release] method invoked with input" + dto);
		ResponseMessage responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		if (!ServicesUtil.isEmpty(dto.getTaskInstanceId())) {
			try {
				System.err
				.println("[PMC][WorkBoxAction][services][release][taskInstanceId] " + dto.getTaskInstanceId());
				if (actionServices.getWorkBoxActionFacadePort().release(dto.getTaskInstanceId())) {
					responseDto.setMessage("Task released successfully");
					responseDto.setStatus("SUCCESS");
					responseDto.setStatusCode("0");
					return responseDto;
				}
			} catch (Exception e) {
				responseDto.setMessage("Task release Failed because" + e.getMessage());
			}
		} else {
			responseDto.setMessage("Instance Id required to release");
		}
		return responseDto;
	}

	@Override
	public ResponseMessage delegate(WorkBoxActionDto dto) {

		System.err.println("[PMC][WorkBoxAction][services][delegate] method invoked with input" + dto);
		ResponseMessage responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		if (!ServicesUtil.isEmpty(dto.getTaskInstanceId())) {
			try {
				System.err
				.println("[PMC][WorkBoxAction][services][delegate][taskInstanceId] " + dto.getTaskInstanceId());
				if (actionServices.getWorkBoxActionFacadePort().delegate(dto.getTaskInstanceId(), dto.getUserId())) {
					responseDto.setMessage("Task delegated successfully");
					responseDto.setStatus("SUCCESS");
					responseDto.setStatusCode("0");
					return responseDto;
				}
			} catch (Exception e) {
				responseDto.setMessage("Task delegate Failed because" + e.getMessage());
			}
		} else {
			responseDto.setMessage("Instance Id required to delegate");
		}
		return responseDto;
	}

	@Override
	public ResponseMessage addNote(WorkBoxActionDto dto) {

		System.err.println("[PMC][WorkBoxAction][services][addNote] method invoked with input" + dto);
		ResponseMessage responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		if (!ServicesUtil.isEmpty(dto.getTaskInstanceId())) {
			try {
				System.err
				.println("[PMC][WorkBoxAction][services][addNote][taskInstanceId] " + dto.getTaskInstanceId());

				String noteId = actionServices.getWorkBoxActionFacadePort().addNote(dto.getTaskInstanceId(),
						dto.getComment());
				if (!ServicesUtil.isEmpty(noteId)) {
					responseDto.setMessage("Note added to the task successfully with id" + noteId);
					responseDto.setStatus("SUCCESS");
					responseDto.setStatusCode("0");
					return responseDto;
				} else {
					responseDto.setMessage("Note added to the task Failed");
				}
			} catch (Exception e) {
				responseDto.setMessage("Note added to the task Failed because" + e.getMessage());
			}
		} else {
			responseDto.setMessage("Instance Id required to add Note to the task");
		}
		return responseDto;

	}

	@Override
	public ResponseMessage complete(WorkBoxActionListDto dtoList) {
		System.err.println("[PMC][WorkBoxAction][services][complete] method invoked with input" + dtoList);
		ResponseMessage responseDto = new ResponseMessage();
		List<WorkBoxActionDto> taskInstanceList = dtoList.getTaskInstanceList();
		if(!ServicesUtil.isEmpty(dtoList)){
			for(WorkBoxActionDto dto : taskInstanceList){

				if (!ServicesUtil.isEmpty(dto.getTaskInstanceId())) {
					try {
						System.err .println("[PMC][WorkBoxAction][services][complete][taskInstanceId] " + dto.getTaskInstanceId()+"[status]"+dto.getStatus()+"[action]"+dto.getAction());
						boolean returnedValue = false;
						if(!ServicesUtil.isEmpty(dto.getStatus()) && dto.getStatus().equals("READY")){
							if(this.claimTask(dto).getStatus().equals("SUCCESS")){
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								returnedValue = this.completeAction(dto);
							}
							else{
								responseDto.setMessage("Task complete Failed as claim is failed");
								returnedValue = false;
							}
						}
						else {
							returnedValue = this.completeAction(dto);
						}
						if (returnedValue) {
							responseDto.setMessage("Task completed successfully");
						}

						else{
							responseDto.setMessage("Task complete Failed");
							break;
						}
					} catch (Exception e) {
						responseDto.setMessage("Task complete Failed because" + e.getMessage());
						break;
					}
				} else {
					responseDto.setMessage("Instance Id required to complete");
					break;
				}
			}
		}
		else{
			responseDto.setMessage("Input Data is empty");
		}
		if(responseDto.getMessage().equals("Task completed successfully")){
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");	
		}else{
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		return responseDto;

	}
	private boolean completeAction(WorkBoxActionDto dto){
		boolean response = false;
		try {
			response = actionServices.getWorkBoxActionFacadePort().complete(dto.getTaskInstanceId(),dto.getAction());
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public ResponseMessage claimAndDelegate(WorkBoxActionDto dto) {

		System.err.println("[PMC][WorkBoxAction][services][claimAndDelegate] method invoked with input" + dto);
		ResponseMessage claimResponse = this.claimTask(dto);
		if (claimResponse.getStatusCode().equals("0")) {
			ResponseMessage delegateResponse = this.delegate(dto);
			return delegateResponse;
		}
		else{
			return claimResponse;
		}
	}
	@Override
	public ResponseMessage nominate(WorkBoxActionDto dto) {

		System.err.println("[PMC][WorkBoxAction][services][nominate] method invoked with input" + dto);
		ResponseMessage responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		if (!ServicesUtil.isEmpty(dto.getTaskInstanceId())) {
			try {
				System.err
				.println("[PMC][WorkBoxAction][services][nominate][taskInstanceId] " + dto.getTaskInstanceId());
				String status = actionServices.getWorkBoxActionFacadePort().nominate(dto.getTaskInstanceId(), dto.getUserId());
				if (status.equals("SUCCESS")) {
					responseDto.setMessage("Task nominated successfully");
					responseDto.setStatus("SUCCESS");
					responseDto.setStatusCode("0");
					return responseDto;
				}
				else{
					responseDto.setMessage(status);
				}
			} catch (Exception e) {
				responseDto.setMessage("Task nominate Failed because" + e.getMessage());
			}
		} else {
			responseDto.setMessage("Instance Id required to nominate");
		}
		return responseDto;
	}

}
