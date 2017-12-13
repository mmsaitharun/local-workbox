package oneapp.incture.workbox.inbox.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import oneapp.incture.workbox.inbox.dto.ResponseMessage;
import oneapp.incture.workbox.inbox.dto.WorkBoxDto;
import oneapp.incture.workbox.inbox.dto.WorkboxResponseDto;
import oneapp.incture.workbox.util.NoResultFault;
import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;

public class WorkBoxDao {
	
	EntityManager em;
	
	public WorkBoxDao(EntityManager em){
		this.em = em;
	}
	
	public WorkboxResponseDto getWorkboxCompletedFilterData(String userId, String processName, String requestId, String createdBy, String createdAt, String completedAt, String period, Integer skipCount, Integer maxCount, Integer page) {
		System.err.println("[PMC][WorkBoxFacade][getWorkboxFilterData] method invoked ");
		WorkboxResponseDto workboxResponseDto = new WorkboxResponseDto();
		ResponseMessage message = new ResponseMessage();
		String taskOwner = userId;

		if (!ServicesUtil.isEmpty(taskOwner)) {
			System.err.println("[PMC][WorkBoxFacade][getWorkboxCompletedFilterData][getLoggedInUser] " + taskOwner);
			if (taskOwner.equals(userId)) {
				SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");
				String dataQuery = "SELECT pe.REQUEST_ID AS REQUEST_ID, pe.NAME AS PROCESS_NAME ,te.EVENT_ID AS TASK_ID, te.DESCRIPTION AS DESCRIPTION, te.NAME AS TASK_NAME, te.SUBJECT AS TASK_SUBJECT, pe.STARTED_BY_DISP AS STARTED_BY, te.CREATED_AT AS TASK_CREATED_AT, te.STATUS AS TASK_STATUS,te.CUR_PROC AS CUR_PROC,te.COMPLETED_AT AS COMPLETED_AT, te.PROCESS_ID AS PROCESS_ID, TE.COMP_DEADLINE AS SLA_DATE, PCT.PROCESS_DISPLAY_NAME as DISP_NAME FROM TASK_EVENTS te, PROCESS_EVENTS pe left join PROCESS_CONFIG_TB pct on pe.NAME = pct.PROCESS_NAME, TASK_OWNERS tw WHERE pe.PROCESS_ID = te.PROCESS_ID AND tw.EVENT_ID = te.EVENT_ID AND pe.STATUS IN('IN_PROGRESS','COMPLETED') AND te.STATUS = 'COMPLETED'";
				String query="";
				if (!ServicesUtil.isEmpty(taskOwner)) {
					query = query + " AND tw.TASK_OWNER = '" + taskOwner + "' AND  te.CUR_PROC = '" + taskOwner + "'";
				}
				if (!ServicesUtil.isEmpty(processName)) {
					query = query + " AND pe.NAME";
					if(processName.indexOf(",")!=-1){
						query = query +" IN (";
						String[] processes = processName.split(",");
						for(String process:processes){
							query = query +"'"+process+"',";	
						}
						query = query.substring(0, query.length()-1);
						query = query +")";
					}
					else{
						query = query +" = '" + processName + "'";
					}
				}
				if (!ServicesUtil.isEmpty(requestId)) {
					query = query + " AND pe.REQUEST_ID LIKE '%" + requestId + "%'";
				}
				if (!ServicesUtil.isEmpty(createdBy)) {
					query = query + " AND pe.STARTED_BY = '" + createdBy + "'";
				}
				if (!ServicesUtil.isEmpty(createdAt)) {
					query = query + " AND to_char(cast(te.CREATED_AT as date),'MM/DD/YYYY')= '" + createdAt + "'";
				}
				if (!ServicesUtil.isEmpty(completedAt)) {
					query = query + " AND to_char(cast(te.COMPLETED_AT as date),'MM/DD/YYYY')= '" + completedAt + "'";
				}
				else{
					Calendar calendar = GregorianCalendar.getInstance();
					Date startDate = null, endDate = null;
					calendar.add(Calendar.DAY_OF_MONTH, -(PMCConstant.COMPLETED_RANGE - 1));
					startDate = ServicesUtil.setInitialTime(calendar.getTime());
					endDate = new Date();
					DateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a");
					query = query + " AND te.COMPLETED_AT between '" + dateFormatter.format(startDate) + "' and '" + dateFormatter.format(endDate) + "'";
				}


				String countQuery = " SELECT  COUNT(*) AS COUNT FROM TASK_EVENTS te, PROCESS_EVENTS pe, TASK_OWNERS tw WHERE pe.PROCESS_ID = te.PROCESS_ID AND tw.EVENT_ID = te.EVENT_ID AND te.STATUS = 'COMPLETED'" + query; 
				System.err.println("[PMC][WorkBoxFacade][getWorkboxFilterData][countQuery]" + countQuery);
				Query cq = em.createNativeQuery(countQuery.trim(), "workBoxCountResult");
				BigDecimal count = (BigDecimal) cq.getSingleResult();
				System.err.println("[PMC][WorkBoxFacade][getWorkboxFilterData][countQuery][Result]" + count);
				query = query + " ORDER BY 8 DESC";
				System.err.println("[PMC][WorkBoxFacade][getWorkboxCompletedFilterData][query]" + query);


				dataQuery = dataQuery + query;
				System.err.println("[PMC][WorkBoxFacade][getWorkboxCompletedFilterData][dataQuery]" + dataQuery);
				Query q = em.createNativeQuery(dataQuery.trim(), "workBoxCompletedResults");


				if (!ServicesUtil.isEmpty(maxCount) && maxCount > 0 && !ServicesUtil.isEmpty(skipCount)
						&& skipCount >= 0) {
					int first = skipCount;
					int last = maxCount;
					q.setFirstResult(first);
					q.setMaxResults(last);
				}
				if (!ServicesUtil.isEmpty(page) && page > 0) {
					int first = (page - 1) * PMCConstant.PAGE_SIZE;
					int last = PMCConstant.PAGE_SIZE;
					q.setFirstResult(first);
					q.setMaxResults(last);
				}

				@SuppressWarnings("unchecked")
				List<Object[]> resultList = q.getResultList();
				System.err.println("[PMC][WorkBoxFacade][getWorkboxFilterData][countQuery][Result]" + resultList);
				if (ServicesUtil.isEmpty(resultList)) {
					try {
						throw new NoResultFault("NO RECORD FOUND");
					} catch (NoResultFault e) {
						System.err.println("NO RESULT FOUND");
						message.setStatus("NO RESULT FOUND");
						message.setStatusCode("1");
						workboxResponseDto.setResponseMessage(message);
					}
				} else {
					List<WorkBoxDto> workBoxDtos = new ArrayList<WorkBoxDto>();
					System.err.println("ResultList - " + resultList.size());
					for (Object[] obj : resultList) {
						WorkBoxDto workBoxDto = new WorkBoxDto();
						workBoxDto.setRequestId(obj[0] == null ? null : (String) obj[0]);
						workBoxDto.setProcessName(obj[1] == null ? null : (String) obj[1]);
						workBoxDto.setTaskId(obj[2] == null ? null : (String) obj[2]);
						workBoxDto.setTaskDescription(obj[3] == null ? null : (String) obj[3]);
						workBoxDto.setName(obj[4] == null ? null : (String) obj[4]);
						workBoxDto.setSubject(obj[5] == null ? null : (String) obj[5]);
						workBoxDto.setStartedBy(obj[6] == null ? null : (String) obj[6]);
						workBoxDto.setCreatedAt(obj[7] == null ? null : simpleDateFormat1.format(ServicesUtil.resultAsDate(obj[7])));
						workBoxDto.setStatus(obj[8] == null ? null : (String) obj[8]);
						workBoxDto.setCompletedAt(obj[10] == null ? null : simpleDateFormat1.format(ServicesUtil.resultAsDate(obj[10])));
						workBoxDto.setProcessId(obj[11] == null ? null : (String) obj[11]);
						workBoxDto.setSlaDisplayDate(obj[12] == null ? null : simpleDateFormat1.format(ServicesUtil.resultAsDate(obj[12])));
						workBoxDto.setProcessDisplayName(obj[13] == null ? (String)obj[1] : (String) obj[13]);
						workBoxDto.setTaskOwner(taskOwner);
						
						/* Sla Breached Details */
						
						if(!ServicesUtil.isEmpty(obj[12]) && !ServicesUtil.isEmpty(obj[10]) ){
							System.err.println("[PMC][WorkBoxDao][getWorkboxCompletedFilterData][completed]" + obj[10]);
							Calendar completed = ServicesUtil.timeStampToCal(obj[10]);
							Calendar slaDate = ServicesUtil.timeStampToCal(obj[12]);
							//workBoxDto.setSlaDisplayDate(simpleDateFormat1.format(slaDate.getTime()));
							//String timeLeftString = ServicesUtil.getSLATimeLeft(slaDate);
							Integer calCompare = slaDate.compareTo(completed);
							if(calCompare == 1){
								workBoxDto.setBreached(false);
							}
							else if (calCompare == -1){
								workBoxDto.setBreached(true);
								Long diff = completed.getTimeInMillis() - slaDate.getTimeInMillis();
								int diffSeconds = (int) (diff / 1000 % 60);
								int diffMinutes = (int) (diff / (60 * 1000) % 60);
								int diffHours = (int) (diff / (60 * 60 * 1000));
								int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
								String timePassedString = "" + diffDays + " days :" + diffHours + " hrs :" + diffMinutes + " min :" + diffSeconds + "sec";
								System.err.println("[PMC][WorkboxCompletedFilterData][timePassedString] : "+timePassedString);
								/*if(diffMinutes < 60){
									//workBoxDto.setTimeLeftDisplayString("Completed After "+diffMinutes+" Minutes");
								} else if(diffMinutes > 60){
									
									//workBoxDto.setTimeLeftDisplayString("Completed After "+diffHours+" Hours");
								} else if (diffMinutes > 1440){
									
									//workBoxDto.setTimeLeftDisplayString("Completed After "+diffDays+" Days");
								}*/
								//workBoxDto.setTimePercentCompleted(ServicesUtil.getPercntTimeCompleted(completed,slaDate));
								//workBoxDto.setTimeLeftDisplayString(ServicesUtil.getCompletedTimePassed(slaDate, completed));
								//workBoxDto.setTimeLeftDisplayString(timePassedString);
								
							}
						}
						
						workBoxDtos.add(workBoxDto);
					}
					workboxResponseDto.setPageCount(PMCConstant.PAGE_SIZE);
					workboxResponseDto.setCount(count);
					workboxResponseDto.setWorkBoxDtos(workBoxDtos);
					message.setStatus("Success");	
					message.setStatusCode("0");
					message.setMessage("Completed Tasks Fetched Successfully");
					workboxResponseDto.setResponseMessage(message);
				}
				return workboxResponseDto;
			}
		}
		message.setStatus("FAILURE");
		message.setStatusCode("1");
		message.setMessage("NO USER FOUND");
		workboxResponseDto.setResponseMessage(message);
		return workboxResponseDto;

	}
}
