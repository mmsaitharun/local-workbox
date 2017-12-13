package oneapp.incture.workbox.inbox.services;

import javax.ejb.Local;

import oneapp.incture.workbox.inbox.dto.ResponseMessage;
import oneapp.incture.workbox.inbox.dto.WorkBoxActionDto;
import oneapp.incture.workbox.inbox.dto.WorkBoxActionListDto;

@Local
public interface WorkBoxActionFacadeWsdlConsumerLocal {

	ResponseMessage claimTask(WorkBoxActionDto  dto);

	ResponseMessage release(WorkBoxActionDto  dto);

	ResponseMessage delegate(WorkBoxActionDto  dto);

	ResponseMessage addNote(WorkBoxActionDto  dto);

	ResponseMessage claimAndDelegate(WorkBoxActionDto dto);

	ResponseMessage nominate(WorkBoxActionDto dto);

	ResponseMessage complete(WorkBoxActionListDto dtoList);


}
