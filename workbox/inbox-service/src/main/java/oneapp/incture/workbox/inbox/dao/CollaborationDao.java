package oneapp.incture.workbox.inbox.dao;

import java.util.ArrayList;
import java.util.Date;
//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import oneapp.incture.workbox.inbox.dto.CollaborationDto;
import oneapp.incture.workbox.inbox.dto.MessageDto;
import oneapp.incture.workbox.inbox.dto.ProcessMessageDto;
import oneapp.incture.workbox.inbox.entity.CollaborationDo;
import oneapp.incture.workbox.util.ExecutionFault;
import oneapp.incture.workbox.util.InvalidInputFault;
import oneapp.incture.workbox.util.NoResultFault;
import oneapp.incture.workbox.util.ServicesUtil;

public class CollaborationDao extends BaseDao<CollaborationDo, CollaborationDto> {

	public CollaborationDao(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected CollaborationDo importDto(CollaborationDto fromDto)
			throws InvalidInputFault, ExecutionFault, NoResultFault {

		CollaborationDo entity = new CollaborationDo();
		if (!ServicesUtil.isEmpty(fromDto.getProcessId()))
			entity.setProcessId(fromDto.getProcessId());
		if (!ServicesUtil.isEmpty(fromDto.getEventId()))
			entity.setEventId(fromDto.getEventId());
		if (!ServicesUtil.isEmpty(fromDto.getUserId()))
			entity.setUserId(fromDto.getUserId());
		if (!ServicesUtil.isEmpty(fromDto.getUserDisplayName()))
			entity.setUserDisplayName(fromDto.getUserDisplayName());
		if (!ServicesUtil.isEmpty(fromDto.getCreatedAt()))
			entity.setCreatedAt(fromDto.getCreatedAt());
		if (!ServicesUtil.isEmpty(fromDto.getMessage()))
			entity.setMessage(fromDto.getMessage());
		if (!ServicesUtil.isEmpty(fromDto.getChatId()))
			entity.setChatId(fromDto.getChatId());
		if (!ServicesUtil.isEmpty(fromDto.getChatDisplayName()))
			entity.setChatDisplayName(fromDto.getChatDisplayName());

		return entity;
	}

	@Override
	protected CollaborationDto exportDto(CollaborationDo entity) {

		CollaborationDto dto = new CollaborationDto();
		if (!ServicesUtil.isEmpty(entity.getProcessId()))
			dto.setProcessId(entity.getProcessId());
		if (!ServicesUtil.isEmpty(entity.getEventId()))
			dto.setEventId(entity.getEventId());
		if (!ServicesUtil.isEmpty(entity.getUserId()))
			dto.setUserId(entity.getUserId());
		if (!ServicesUtil.isEmpty(entity.getUserDisplayName()))
			dto.setUserDisplayName(entity.getUserDisplayName());
		if (!ServicesUtil.isEmpty(entity.getCreatedAt()))
			dto.setCreatedAt(entity.getCreatedAt());
		if (!ServicesUtil.isEmpty(entity.getMessage()))
			dto.setMessage(entity.getMessage());
		if (!ServicesUtil.isEmpty(entity.getChatId()))
			dto.setChatId(entity.getChatId());
		if (!ServicesUtil.isEmpty(entity.getChatDisplayName()))
			dto.setChatDisplayName(entity.getChatDisplayName());
		return dto;
	}

	public List<ProcessMessageDto> getAllDetailsOfProcess(String processId) {
		List<ProcessMessageDto> listOfProcessMessageDto = new ArrayList<ProcessMessageDto>();

		/*
		 * For Task_id= null
		 */
		ProcessMessageDto processMessageDto = new ProcessMessageDto();
		String queryName = "SELECT p.message,p.userDisplayName,p.createdAt FROM CollaborationDo p WHERE p.eventId IS NULL AND p.processId= :processId ORDER BY p.createdAt";
		System.err.println("queryName - " + queryName);
		Query query = this.getEntityManager().createQuery(queryName);
		query.setParameter("processId", processId);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		List<MessageDto> listMessageDto1 = new ArrayList<MessageDto>();
		if (!ServicesUtil.isEmpty(resultList)) {
			//processMessageDto.setTaskId(null);
			for (Object[] obj : resultList) {
				System.err.println("Object in the loop : " + obj.toString());
				MessageDto messageDto = new MessageDto();
				messageDto.setMessage((String) obj[0]);
				messageDto.setUserDisplayName((String) obj[1]);
				messageDto.setCreatedAt((Date) obj[2]);
				listMessageDto1.add(messageDto);
			}
		}
		processMessageDto.setProcessLevelMessageDto(listMessageDto1);
		listOfProcessMessageDto.add(processMessageDto);

		/*
		 * For Task_id not null Process and task level messages first getting
		 * all the task and then iterating each one
		 * 
		 */
//		String queryName2 = "select distinct p.eventId from CollaborationDo p where p.eventId is not null and p.processId= :processId";
//		System.err.println("queryName2 - " + queryName2);
//		Query query2 = this.getEntityManager().createQuery(queryName2);
//		query2.setParameter("processId", processId);
//		@SuppressWarnings("unchecked")
//		List<Object> resultList2 = query2.getResultList();
//		List<String> listStr = new ArrayList<String>();
//		if (!ServicesUtil.isEmpty(resultList2)) {
//			for (Object obj : resultList2) {
//				listStr.add(obj.toString());
//			}
//		}
//		int listSize = listStr.size();
//		System.err.println("Different Tasks " + listSize);

//		for (String str : listStr) {
//			ProcessMessageDto processMessage1Dto = new ProcessMessageDto();
//			String queryName3 = "SELECT p.message,p.userDisplayName,p.createdAt FROM CollaborationDo p WHERE p.eventId= :eventId AND p.processId= :processId ORDER BY p.createdAt";
//			System.err.println("queryName3 - " + queryName3);
//			Query query3 = this.getEntityManager().createQuery(queryName3);
//			query3.setParameter("processId", processId);
//			query3.setParameter("eventId", str);
//			@SuppressWarnings("unchecked")
//			List<Object[]> resultList3 = query3.getResultList();
//			List<MessageDto> listMessageDto2 = new ArrayList<MessageDto>();
//
//			if (!ServicesUtil.isEmpty(resultList3)) {
//				processMessage1Dto.setTaskId(str);
//				for (Object[] obj : resultList3) {
//					// listMessageDto.clear();
//					System.err.println("Object in the loop : " + obj.toString());
//					MessageDto messageDto1 = new MessageDto();
//					messageDto1.setMessage((String) obj[0]);
//					messageDto1.setUserDisplayName((String) obj[1]);
//					messageDto1.setCreatedAt((Date) obj[2]);
//					listMessageDto2.add(messageDto1);
//				}
//			}
//			processMessage1Dto.setTaskLevelMessageDto(listMessageDto2);
//			listOfProcessMessageDto.add(processMessage1Dto);
//
//		}

		return listOfProcessMessageDto;
	}

	
	public List<ProcessMessageDto> getAllDetailsOfTask(String eventId) {
	
		List<ProcessMessageDto> listOfTaskMessageDto = new ArrayList<ProcessMessageDto>();
		ProcessMessageDto taskMessageDto = new ProcessMessageDto();
		String queryName = "SELECT p.message,p.userDisplayName,p.createdAt FROM CollaborationDo p WHERE p.eventId =:eventId ORDER BY p.createdAt";
		System.err.println("queryName - " + queryName);
		Query query = this.getEntityManager().createQuery(queryName);
		query.setParameter("eventId", eventId);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();
		List<MessageDto> listMessageDto = new ArrayList<MessageDto>();
		if (!ServicesUtil.isEmpty(resultList)) {
			for (Object[] obj : resultList) {
				System.err.println("Object in the loop : " + obj.toString());
				MessageDto messageDto = new MessageDto();
				messageDto.setMessage((String) obj[0]);
				messageDto.setUserDisplayName((String) obj[1]);
				messageDto.setCreatedAt((Date) obj[2]);
				listMessageDto.add(messageDto);
			}
		}
		taskMessageDto.setTaskLevelMessageDto(listMessageDto);
		listOfTaskMessageDto.add(taskMessageDto);
	
	return listOfTaskMessageDto;
	}
	
	
}
